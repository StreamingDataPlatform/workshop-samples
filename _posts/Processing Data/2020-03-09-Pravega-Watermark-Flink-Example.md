---
layout: post
category: Processing Data
tags: [stream processing, ingest, flink connector]
subtitle: To demo how to use 'Watermark'
img: ico-infinite.png
license: Apache
support: Community
author: 
    name: Luis Liu, Youmin Han
    description: I'm focusing on the data stream solution development.  
    image: 
css: 
js: 
---
This sample demonstrates the use of Pravega Watermarks in Flink applications.
<!--more-->

## Purpose
Flink offers [event-time characteristic](https://ci.apache.org/projects/flink/flink-docs-stable/dev/event_time.html).
The mechanism in Flink to measure progress in event time is watermarks.
Watermarks flow as part of the data stream and carry a timestamp `t`.
A `Watermark(t)` declares that event time has reached time `t` in that stream, meaning that there should be no more elements from the stream with a timestamp `t’` <= `t` 
(i.e. events with timestamps older or equal to the watermark).

Pravega has innovated a strategy to generate the watermark within the stream in 0.6 release.
The main design is close to the concept in Flink. The new Pravega watermark API enables the writer to provide event time information, and provide a time bound from the writers to the readers so that they can identify where they are in the stream.

The following example will show how to utilize Pravega watermark in Flink applications. In addition, you can also follow the instruction to set up this project running on Dell EMC Streaming Data Platform.

## Design
#### Application: Raw Data Ingestion
`PravegaWatermarkIngestion` is a Pravega writer to generate synthetic sensor data with event time and ingest into a Pravega stream.
It mocks a sine wave for three sensors and emits data every second in event time.

#### Application: Event Time Window Average
##### A. Usage of Pravega source with watermark 
There is a slight difference that Flink forces that event timestamp can be extracted from each record, while Pravega as a streaming storage doesn't have that limitation.

In order to enable Pravega watermark to transfer into Flink, Flink readers accepts an implementation of  
1: How to extract timestamp from each record  
2: How to leverage the watermark timestamp given the time bound from Pravega readers.

This thought is abstracted into an interface called `AssignerWithTimeWindows`. It's up to you to implement it.
While building the reader, please use `withTimestampAssigner(new MyAssignerWithTimeWindows())` to register the assigner. 

##### B. Usage of Pravega sink with watermark 
The application reads text from a socket, assigns the event time and then propagating the Flink watermark into Pravega with `enableWatermark(true)` in the Flink writer.

##### C. Our Recursive Application
`EventTimeAverage` reads sensor data from the stream with Pravega watermark, calculates an average value for each sensor under an fixed-length event-time window and generates the summary sensor data back into another Pravega stream.
You can run it recursively by reusing the result of the smaller window.

## Instructions
#### A. Running example on a local cluster with support from Pravega

##### 1. Setting up the Pravega and Flink environment
Before you start, you need to download the latest Pravega release on the [github releases page](https://github.com/pravega/pravega/releases). See [here](http://pravega.io/docs/latest/getting-started/) for the instructions to build and run Pravega in standalone mode.  

Besides, you also need to get the latest Flink binary from [Apache download page](https://flink.apache.org/downloads.html). Follow [this](https://ci.apache.org/projects/flink/flink-docs-stable/getting-started/tutorials/local_setup.html) tutorial to start a local Flink cluster. 

##### 2. Build `pravega-samples` Repository

`pravega-samples` repository provides code samples to connect analytics engines Flink with Pravega as a storage substrate for data streams. It has divided into sub-projects(`pravega-client-examples`, `flink-connector-examples` and `hadoop-connector-examples`), each one addressed to demonstrate a specific component. To build `pravega-samples` from source, follow the [instructions](https://github.com/pravega/pravega-samples#pravega-samples-build-instructions) to use the built-in gradle wrapper.  

##### 3. Start the Pravega Watermark Flink Example program

There are multiple ways to run the program in Flink environment including submitting from terminal or Flink UI. Follow the latest instruction from the [github README](https://github.com/pravega/pravega-samples/tree/master/flink-connector-examples/doc/watermark) to learn more about the IDE setup and running process.

#### B. Running example on Dell EMC Streaming Data Platform
##### 1. Change the watermark example code to satisfy the SDP running requirements
Since the original flink connector watermark example was designed to run on a standalone Pravega and Flink environment, the code used the `createScope` method from the StreamManager Interface in Pravega. However, due to the security reason, Dell EMC Streaming Data Platform does not allow to create a scope from the code. Please **comment out** `createScope` method from your code.   
There are two occurrences of `createScope` method in this watermark example which locate on following Java file:   
I. ```pravega-samples/flink-connector-examples/src/main/java/io/pravega/example/flink/Utils.java```  
II. ```pravega-samples/flink-connector-examples/src/main/java/io/pravega/example/flink/watermark/PravegaWatermarkIngestion.java```

##### 2. Follow the [this post]({{site.baseurl}}/getting started/2020/07/14/create-flink-project-on-streaming-data-platform.html) to learn how to create Flink projects and run on Dell EMC Streaming Data Platform
If you choose to use the Gradle Build Tool, make sure the Maven repo in SDP available to your development workstation as mentioned in the [post]({{site.baseurl}}/getting started/2020/07/14/create-flink-project-on-streaming-data-platform.html). The following is an example of ```pravega-samples/flink-connector-examples/build.gradle``` file which only shows the modified section. Since the example uses shadow JARs, make sure to add `classifier = ""` and `zip64 true` to the `shadowJar` config. 
```
shadowJar {
    dependencies {
        include dependency("org.scala-lang.modules:scala-java8-compat_${flinkScalaVersion}")
        include dependency("io.pravega:pravega-connectors-flink-${flinkMajorMinorVersion}_${flinkScalaVersion}")
        include dependency("io.pravega:pravega-keycloak-client:${pravegaKeycloakVersion}")
    }
    classifier = ""
    zip64 true
}

publishing {
    repositories {
        maven {
            url = "http://localhost:9090/maven2"
            credentials {
                username "desdp"
                password "password"
            }
            authentication {
                basic(BasicAuthentication)
            }
        }
    }

    publications {
        shadow(MavenPublication) { publication ->
                project.shadow.component(publication)
        }
    }
}
```
The preferable name/namespace for this project is ```watermark-examples```.  
The Flink Image for creating the cluster is ```1.9.0```.  
The Main Class for creating the new app is ```io.pravega.example.flink.watermark.EventTimeAverage```. Please also make sure to pass the same parameters as discussed in the [original watermark post](https://github.com/pravega/pravega-samples/tree/master/flink-connector-examples/doc/watermark).  
After finishing the above steps, the **State** for your Flink application should be shown as **Started**.

##### 3. Connect the `PravegaWatermarkIngestion` application with the Pravega stream on Dell EMC Streaming Data Platform
After setting up the Flink app on SDP, configure Streaming Data Platform authentication by getting the ```keycloak.json``` file. Run the following command in your terminal (you may need to change the name for namespace): 
```
kubectl get secret watermark-examples-pravega -n watermark-examples -o jsonpath="{.data.keycloak\.json}" |base64 -d >  ${HOME}/keycloak.json

chmod go-rw ${HOME}/keycloak.json
```
The output should look like the following:
```
{
  "realm": "nautilus",
  "auth-server-url": "https://keycloak.p-test.nautilus-lab-wachusett.com/auth",
  "ssl-required": "external",
  "bearer-only": false,
  "public-client": false,
  "resource": "workshop-samples-pravega",
  "confidential-port": 0,
  "credentials": {
    "secret": "c72c45f8-76b0-4ca2-99cf-1f1a03704c4f"
  }
}
```
In order to connect with Pravega controller, use `kubectl` to get the `EXTERNAL-IP` of the nautilus-pravega-controller service.
```
kubectl get service nautilus-pravega-controller -n nautilus-pravega

NAME                          TYPE           CLUSTER-IP       EXTERNAL-IP     PORT(S)                          AGE
nautilus-pravega-controller   LoadBalancer   10.100.200.243   11.111.11.111   10080:32217/TCP,9090:30808/TCP   6d5h
```

Before running the application, set the following environment variables. This can be done by setting the IntelliJ run configurations. (Go to run -> Edit Configurations -> Select PravegaWatermarkIngestion application -> Environment Variables -> click Browse icon. Fill the details mentioned below screen. Add all below program environment variables.) Make sure to change the `PRAVEGA_CONTROLLER` to the appropriate `EXTERNAL-IP` address. Notice that the assigned value for `PRAVEGA_SCOPE` and `PRAVEGA_STREAM` may need to be changed based on your settings when created the Flink project and application on SDP.
```
pravega_client_auth_method=Bearer
pravega_client_auth_loadDynamic=true
KEYCLOAK_SERVICE_ACCOUNT_FILE=${HOME}/keycloak.json
PRAVEGA_CONTROLLER=tcp://<pravega controller external-ip>:9090
PRAVEGA_SCOPE=watermark-examples
PRAVEGA_STREAM=raw-data
```

Then you can save the configuration and hit Run.

##### 4. Check the result after running watermark example
Unlike the standalone mode, the result will not be showing in the console output. You need to access the pod's log by using `kubectl`. The following is an example of using the command (namespace may change based on your settings):
```
kubectl logs -f watermark-examples-taskmanager-0 -n watermark-examples
```
You can find the same results as showed in the [post](https://github.com/pravega/pravega-samples/tree/master/flink-connector-examples/doc/watermark).

## Source
[https://github.com/pravega/pravega-samples/tree/master/flink-connector-examples/doc/watermark](https://github.com/pravega/pravega-samples/tree/master/flink-connector-examples/doc/watermark)

## Documentation
Flink implements many techniques from the Dataflow Model, and Pravega aligns with it.
For better knowledge about event time and watermarks, the following articles can be helpful.

- [Streaming 101](https://www.oreilly.com/ideas/the-world-beyond-batch-streaming-101) by Tyler Akidau
- The [Dataflow Model paper](https://research.google.com/pubs/archive/43864.pdf)
- Flink introductions for [event time](https://ci.apache.org/projects/flink/flink-docs-stable/dev/event_time.html).
- Pravega design detail [PDP-33](https://github.com/pravega/pravega/wiki/PDP-33:-Watermarking)