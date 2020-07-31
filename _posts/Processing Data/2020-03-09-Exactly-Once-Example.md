---
layout: post
category: Processing Data
tags: [stream processing, ingest, flink connector]
subtitle: To demo how 'EXACTLY_ONCE' works
img: ico-once.png
license: Apache
support: Community
author: 
    name: Luis Liu, Youmin Han
    description: I'm focusing on the data stream solution development
    image: 
css: 
js: 
---
This example demonstrates how Pravega EXACTLY_ONCE mode works in conjection with Flink checkpointing and exactly-once mode.
<!--more-->

## Design

The example consists of two applications, a writer and a checker.

```
$ cd flink-connector-examples/build/install/pravega-flink-examples
$ bin/exactlyOnceChecker  [--controller tcp://localhost:9090] [--scope examples] [--stream mystream]
$ bin/exactlyOnceWriter   [--controller tcp://localhost:9090] [--scope examples] [--stream mystream] [--num-events 50] [--exactlyonce true]
```

The writer application generates a set of "integer" events and introduces an artificial exception to 
simulate transaction failure. It takes checkpoints periodically. It is configured to restore 
from the latest Flink checkpoint in case of failures.
For demo purpose, it also generates "start" and "end" events for the checker to detect duplicate events.

## Instructions
#### A. Running example on a local cluster with support from Pravega

##### 1. Setting up the Pravega and Flink environment
Before you start, you need to download the latest Pravega release on the [github releases page](https://github.com/pravega/pravega/releases). See [here](http://pravega.io/docs/latest/getting-started/) for the instructions to build and run Pravega in standalone mode.  

Besides, you also need to get the latest Flink binary from [Apache download page](https://flink.apache.org/downloads.html). Follow [this](https://ci.apache.org/projects/flink/flink-docs-stable/getting-started/tutorials/local_setup.html) tutorial to start a local Flink cluster. 

##### 2. Build `pravega-samples` Repository

`pravega-samples` repository provides code samples to connect analytics engines Flink with Pravega as a storage substrate for data streams. It has divided into sub-projects(`pravega-client-examples`, `flink-connector-examples` and `hadoop-connector-examples`), each one addressed to demonstrate a specific component. To build `pravega-samples` from source, follow the [instructions](https://github.com/pravega/pravega-samples#pravega-samples-build-instructions) to use the built-in gradle wrapper.  

##### 3. Start the Pravega Exactly Once Example program

There are multiple ways to run the program in Flink environment including submitting from terminal or Flink UI. Follow the latest instruction from the [github README](https://github.com/pravega/pravega-samples/tree/master/flink-connector-examples/doc/exactly-once) to learn more about the IDE setup and running process.

#### B. Running example on Dell EMC Streaming Data Platform
##### 1. Change the exactly-once example code to satisfy the SDP running requirements
Since the original flink connector exactly-once example was designed to run on a standalone Pravega and Flink environment, the code used the `createScope` method from the StreamManager Interface in Pravega. However, due to the security reason, Dell EMC Streaming Data Platform does not allow to create a scope from the code. Please **comment out** `createScope` method from your code.   
There is one occurrence of `createScope` method in this exactly-once example which locates on following Java file:   
I. ```pravega-samples/flink-connector-examples/src/main/java/io/pravega/example/flink/Utils.java```  

##### 2. Follow [this post]({{site.baseurl}}/getting started/2020/07/14/create-flink-project-on-streaming-data-platform.html) to learn how to create Flink projects and run on Dell EMC Streaming Data Platform
If you choose to use the Gradle Build Tool, make sure the Maven repo in SDP is available to your development workstation as mentioned in the [post]({{site.baseurl}}/getting started/2020/07/14/create-flink-project-on-streaming-data-platform.html). The following is an example of ```pravega-samples/flink-connector-examples/build.gradle``` file which only shows the modified section. Since the example uses shadow JARs, make sure to add `classifier = ""` and `zip64 true` to the `shadowJar` config. 
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
The preferable **name/namespace/scope** for this project is ```exactlyonce```.  
The **Flink Image** for creating the cluster is ```1.9.0```.  
The **Main Class** for creating the new app is ```io.pravega.example.flink.primer.process.ExactlyOnceChecker```.   
Please also make sure to pass the same parameters as discussed in the [original exactly-once post](https://github.com/pravega/pravega-samples/tree/master/flink-connector-examples/doc/exactly-once). The **controller** address in this case is: ```tcp://nautilus-pravega-controller.nautilus-pravega.svc.cluster.local:9090```   
After finishing the above steps, the **State** for your Flink application should be shown as **Started**.

##### 3. Connect the `ExactlyOnceWriter` application with the Pravega stream on Dell EMC Streaming Data Platform
After setting up the Flink app on SDP, configure Streaming Data Platform authentication by getting the ```keycloak.json``` file. Run the following command in your terminal (you may need to change the name for namespace): 
```
kubectl get secret exactlyonce-pravega -n exactlyonce -o jsonpath="{.data.keycloak\.json}" |base64 -d >  ${HOME}/keycloak.json

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
  "resource": "exactlyonce-pravega",
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

Before running the application, set the following environment variables. This can be done by setting the IntelliJ run configurations. (Go to run -> Edit Configurations -> Select ExactlyOnceWriter application -> Environment Variables -> click Browse icon. Fill the details mentioned below screen. Add all below program environment variables.) Make sure to change the `PRAVEGA_CONTROLLER` to the appropriate `EXTERNAL-IP` address. Notice that the assigned value for `PRAVEGA_SCOPE` and `PRAVEGA_STREAM` may need to be changed based on your settings when created the Flink project and application on SDP.
```
pravega_client_auth_method=Bearer
pravega_client_auth_loadDynamic=true
KEYCLOAK_SERVICE_ACCOUNT_FILE=${HOME}/keycloak.json
PRAVEGA_CONTROLLER=tcp://<pravega controller external-ip>:9090
PRAVEGA_SCOPE=exactlyonce
PRAVEGA_STREAM=mystream
```

In addition, make sure you to pass the same parameters as metioned in [original exactly-once post](https://github.com/pravega/pravega-samples/tree/master/flink-connector-examples/doc/exactly-once).  
Then you can save the configuration and hit Run.

##### 4. Check the result after running exactly-once example
Unlike the standalone mode, the result will not be showing in the console output. You need to access the pod's log by using `kubectl`. The following is an example of using the command (namespace may change based on your settings):
```
kubectl logs -f exactlyonce-taskmanager-0 -n exactlyonce
```
You can find the same results as showed in the [post](https://github.com/pravega/pravega-samples/tree/master/flink-connector-examples/doc/exactly-once).

## Source
[https://github.com/pravega/pravega-samples/tree/master/flink-connector-examples/doc/exactly-once](https://github.com/pravega/pravega-samples/tree/master/flink-connector-examples/doc/exactly-once)

## Documentation 
More information on Pravega EXACTLY_ONCE semantics can be found at [here](http://pravega.io/docs/latest/key-features/#exactly-once-semantics).