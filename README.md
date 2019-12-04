# Nautilus Developer Workshop Code Samples

## Pre-Requisites
External connectivity enabled Nautilus cluster required to perform these samples. If Nautilus cluster not exists, applications can be developed using standalone pravega.

Use Ubuntu VM or Ubuntu for desktops

JDK 1.8

Gradle 4.0

Maven 3.6

##Setting up IntelliJ
	Clone workshop-samples main branch from https://github.com/pravega/workshop-samples.git  
	Start Intellij and File  Open –> Select the cloned workshop-sample folder
![Open Project](/images/IntelliJ_1.png)


	Select Project  File  settings  plugins and install Lombok plugin.
	Select Project  File  settings  Build,Execution,Deployment  Annotation Process  Select checkbox of Enable annotation processing

![Enable annotation](/images/IntelliJ_2.png)

	Select Project  File  settings  Build Tools  Gradle  enable auto import and select gradle wrapper

![gradle wrapper](/images/IntelliJ_3.png)

![gradle installDist](/images/IntelliJ_4.png)

	It will take some time to download dependencies and complete build.
	Go to Build  Build Project
	Go to the Nautilus UI and create a project workshop-samples.If you are running samples with Nautilus cluster.

## Configuring Standalone Pravega and running
	Clone Pravega from https://github.com/pravega/pravega.git and get required version.
	Get proper release version Ex: pravega-r0.5

	Run standalone Pravega 
./gradlew startStandalone

## Running the Samples from IntelliJ with Standalone Pravega

	Running a JSONWriter
	Go to run  Edit Configurations  Select application and click + icon. Fill the details mentioned below screen. Add all below program parameters. 

```
--pravega_scope	workshop-samples
--stream_name workshop-stream
--pravega_controller_uri tcp://localhost:9090
--pravega_standalone true
--data_file earthquakes1970-2014.csv
```

![gradle installDist](/images/IntelliJ_5.png)


Click ok and Run JSONWriter

	Configure other samples and run.

## Running the Samples with Nautilus cluster

### Configure Nautilus Authentication

- Create a project `workshop-samples` in Nautilus UI
- This will automatically create a scope `workshop-samples`
-  Get the `keycloak.json` file by executing this command
```
kubectl get secret workshop-samples-pravega -n workshop-samples 
-o jsonpath="{.data.keycloak\.json}" |base64 -d >  ${HOME}/keycloak.json
chmod go-rw ${HOME}/keycloak.json
```
  output looks like the following:
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
When running the example applications, you must set the following environment variables. This can be done by setting the IntelliJ run configurations. If you set this in IntelliJ, you must manually replace ${HOME} with your actual home directory.
```
export pravega_client_auth_method=Bearer
export pravega_client_auth_loadDynamic=true
export KEYCLOAK_SERVICE_ACCOUNT_FILE=${HOME}/keycloak.json

And also need to set following parameters according to your project and provide as program params.
--pravega_scope	workshop-samples
--stream_name workshop-stream
--pravega_controller_uri tcp://localhost:9090
--pravega_standalone true
```


## Running JSON Reader in Nautilus

- You must make the Maven repo in Nautilus available to your development workstation.
```
kubectl port-forward service/repo 9090:80 --namespace workshop-samples &
```
- Build and publish your application JAR file.
```
./gradlew publish
helm upgrade --install --timeout 600 jsonreader \
--wait --namespace workshop-samples charts

```

## About Samples
	Go to the ingest module and find various writers.

	Run JSONWriter,ImageWriter and EventWriter from IntelliJ

$\workshop-samples\stream-ingest\src\main\java\com\dellemc\oe\ingest

	JSONWriter demonstrates streaming a JSON data.

	EventWriter demonstrate streaming a String Event

	ImageWriter demonstrate streaming ImagaeData as a JSON

	Go to various-readers module and run JSONReader and ImageReader Flink 
apps

$\workshop-samples\various-readers\src\main\java\com\dellemc\oe\readers

	Go to stream to stream module and run WordCountReader

$\workshop-samples\stream-to-stream\src\main\java\com\dellemc\oe\flink\wordcount

This sample reads data from a stream written by EventWriter as a String and do some transformations and write to another stream.
