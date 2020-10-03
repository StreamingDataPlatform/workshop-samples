# Dell EMC Streaming Data Platform Developer Workshop Code Samples

## Pre-Requisites
External connectivity enabled Dell EMC SDP cluster required to perform these samples. If SDP cluster not exists, applications can be developed using standalone pravega.

Use Ubuntu VM or Ubuntu for desktops

JDK 1.8

Gradle 4.0

Maven 3.6

## Setting up IntelliJ
-  	Clone workshop-samples main branch from https://github.com/pravega/workshop-samples.git  
-  	Start Intellij and File -> Open –> Select the cloned workshop-sample folder
![Open Project](/images/IntelliJ_1.png)

- 	Select Project -> File -> settings -> plugins and install Lombok plugin.
-	Select Project -> File -> settings -> Build,Execution,Deployment Build Tools -> Gradle -> Select checkbox Download external annotations for dependencies
![Enable annotation](/images/IntelliJ_2.png)

-	It will take some time to download dependencies and complete build.
-	Go to Build -> Build Project
-	Go to the SDP UI and create a project workshop-samples. If you are running samples with SDP cluster.

## Configuring Standalone Pravega and running
-	Clone Pravega from https://github.com/pravega/pravega.git and get required version.
-	Get proper release version Ex: pravega-r0.8

-	Run standalone Pravega 
./gradlew startStandalone

## Running the Samples from IntelliJ with Standalone Pravega

###	Running a JSONWriter
- Go to run -> Edit Configurations -> Select application and click + icon. Fill the details mentioned below screen. Add all below program environment variables. 

```
PRAVEGA_CONTROLLER=tcp://localhost:9090
PRAVEGA_SCOPE=workshop-samples
PRAVEGA_STREAM=json-stream
```

Click ok and Run JSONWriter

Configure other samples and run.

## Running the Samples with SDP cluster

### Configure SDP Authentication

- Create a project `workshop-samples` in SDP UI
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

## Running [JSONWriter](/stream-ingest/src/main/java/com/dellemc/oe/ingest/JSONWriter.java) from Intelij

- Set the following environment variables. This can be done by setting the IntelliJ run configurations.
  (-> Go to run -> Edit Configurations -> Select JSONWriter application and click + icon. Fill the details mentioned below screen. Add all below program environment variables.)
```
pravega_client_auth_method=Bearer
pravega_client_auth_loadDynamic=true
KEYCLOAK_SERVICE_ACCOUNT_FILE=${HOME}/keycloak.json
PRAVEGA_CONTROLLER=tcp://<pravega controller>:9090
PRAVEGA_SCOPE=workshop-samples
PRAVEGA_STREAM=json-stream
```
- Save configuration and hit Run

## Running [JSONWReader](/stream-readers/src/main/java/com/dellemc/oe/readers/JSONReader.java) from Intelij

- Set the following environment variables. This can be done by setting the IntelliJ run configurations.

```
pravega_client_auth_method=Bearer
pravega_client_auth_loadDynamic=true
```

- set the following under parameters in Intelij
```$xslt
--controller tcp://<pravega controller>:9090
--scope workshop-samples
--stream json-stream
```

- Save configuration and hit Run

## Running [JSONWReader](/stream-readers/src/main/java/com/dellemc/oe/readers/JSONReader.java) in Dell EMC SDP

-  You must make the Maven repo in SDP available to your development workstation.
```
kubectl port-forward service/repo 9092:80 --namespace workshop-samples
```
-   Set the environment variable for maven user and password.
```
export MAVEN_USER=desdp
export MAVEN_PASSWORD=password
```
-   Build and publish your application JAR file. Make sure to replace the `release name` and `application values file` with appropriate values.
```
./gradlew publish
helm upgrade --install --timeout 600s --wait \
    <Release Name> \
    charts/flink-app \
    --namespace workshop-samples \
    -f values/<application values file> 
```

Here is an example for building the json reader application by using Helm Chart.
```
helm upgrade --install --timeout 600s --wait \
    jsonreader \
    charts/flink-app \
    --namespace workshop-samples \
    -f values/flink-json-reader.yaml
```

## About Samples
-  `stream-ingest` shows how to ingest data into a Pravega stream.   
``
\workshop-samples\stream-ingest\src\main\java\com\dellemc\oe\ingest
``  
    1.  `JSONWriter` demonstrates streaming a JSON data which convert from CSV file.  
    2.	`EventWriter` demonstrate streaming a String Event  
    3.	`ImageWriter` demonstrate streaming ImagaeData as a JSON

- `stream-processing` illustrates how to read from a Pravega stream and write to a different one.  
``
\workshop-samples\stream-processing\src\main\java\com\dellemc\oe\flink\wordcount
``  
This wordcount reads data from a stream written by EventWriter as a String and do some transformations and write to another stream.

- `stream-readers` exemplifies how to read the data from a Pravega stream.  
``\workshop-samples\stream-readers\src\main\java\com\dellemc\oe\readers``  
    1.  `JSONReader` reads the data generated by `JSONWriter` and prints to standard output.
    2.	`ImageReader` reads the data generated by `ImageWriter` and prints the image data.
    3.	`FlinkSQLReader` reads the data generated by `JSONWriter` and converts to a SQL table source.
    4.  `FlinkSQLJOINReader` reads the data generated by `JSONWriter` and uses SQL to join another table.
