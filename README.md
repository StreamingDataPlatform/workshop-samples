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
-	Clone Pravega ([getting-started-guide](https://pravega.io/docs/latest/getting-started/quick-start/)) from https://github.com/pravega/pravega.git and get required version.
-	Get proper release version Ex: pravega-r0.12.0

-	Run standalone Pravega 
```
./gradlew startStandalone
```
```
wget https://github.com/pravega/pravega/releases/download/v0.12.0/pravega-0.12.0.tgz
tar zxvf pravega-0.12.0.tgz
cd pravega-0.12.0
```
-   and run CLI and create scope
```
./bin/pravega-cli
> scope create workshop-sample
```
## Running the Samples from IntelliJ with Standalone Pravega

###	Running a [JSONWriter](/stream-ingest/src/main/java/com/dellemc/oe/ingest/JSONWriter.java)

- Go to run -> Edit Configurations -> Select application and click + icon. Fill the details mentioned below screen. Add all below program environment variables. 

```
pravega_client_auth_method=Bearer;
pravega_client_auth_loadDynamic=false;
PRAVEGA_CONTROLLER=tcp://localhost:9090;
PRAVEGA_SCOPE=workshop-sample;
PRAVEGA_STREAM=json-stream;
DATA_FILE=earthquakes1970-2014.csv;
```

Click ok and Run JSONWriter

Configure other samples and run.

###	Running a [JSONReader](/stream-readers/src/main/java/com/dellemc/oe/readers/JSONReader.java)
- Set the following environment variables. This can be done by setting the IntelliJ run configurations.

```
pravega_client_auth_method=Bearer;
pravega_client_auth_loadDynamic=false;
```
- Go to run -> Edit Configurations -> Select application set the following under parameters(Program arguments) in Intelij
```
--controller tcp://localhost:9090
--scope workshop-sample
--input-stream json-stream
```
Click ok and Run JSONReader

Configure other samples and run.
## Running the Samples with SDP cluster
### Configure SDP Authentication

- Create a project `workshop-sample` in SDP UI
- This will automatically create a scope `workshop-sample`
-  Get the `keycloak.json` file by executing this command
```
kubectl get secret workshop-sample-ext-pravega -n workshop-sample -o jsonpath="{.data.keycloak\.json}" |base64 -d >  ${HOME}/keycloak.json
chmod go-rw ${HOME}/keycloak.json
```
output looks like the following:
```
{
    "realm":"nautilus",
    "auth-server-url":"https://keycloak.host.ns.sdp.hop.lab.emc.com/auth",
    "ssl-required":"none",
    "bearer-only":false,
    "public-client":false,
    "resource":"workshop-sample-ext-pravega",
    "confidential-port":0,
    "credentials":{
        "secret":"4ldBpio0SFI10bUevqvJvDdEVZl49WYM"
    }
}
```

### Getting Pravega controller and Port
```
kubectl get ingress pravega-controller -n nautilus-pravega
```
OUTPUT:-

| NAME               | CLASS          | HOSTS                                          | ADDRESS     | PORTS  |
|--------------------|----------------|------------------------------------------------|-------------|--------|
| pravega-controller | nginx-nautilus | pravega-controller.host.ns.sdp.hop.lab.emc.com | 10.10.10.10 | 80,443 |

- Use pravega-controller.host.ns.sdp.hop.lab.emc.com as **pravega controller** and 443 as **port**
### Adding required certificates
```
kubectl get secret pravega-controller-tls -n nautilus-pravega -o jsonpath="{.data.tls\.crt}" | base64 --decode > pravega.crt
kubectl get secret keycloak-tls -n nautilus-system -o jsonpath="{.data.tls\.crt}" | base64 --decode > keycloak.crt
```

```
keytool -import -trustcacerts -keystore /etc/ssl/certs/java/cacerts -storepass changeit -alias mykeycloakcert -file ./keycloak.crt
keytool -import -trustcacerts -keystore /etc/ssl/certs/java/cacerts -storepass changeit -alias mypravegacert -file ./pravega.crt
```
### Running [JSONWriter](/stream-ingest/src/main/java/com/dellemc/oe/ingest/JSONWriter.java) from Intelij and Write to SDP

- Set the following environment variables. This can be done by setting the IntelliJ run configurations.
  (-> Go to run -> Edit Configurations -> Select JSONWriter application and click + icon. Fill the details mentioned below screen. Add all below program environment variables.)
```
pravega_client_auth_method=Bearer;
pravega_client_auth_loadDynamic=true;
KEYCLOAK_SERVICE_ACCOUNT_FILE=${HOME}/keycloak.json;
PRAVEGA_CONTROLLER=tls://<pravega controller>:<port>;
PRAVEGA_SCOPE=workshop-sample;
PRAVEGA_STREAM=json-stream;
DATA_FILE=earthquakes1970-2014.csv;
```
- Save configuration and hit Run

### Running [JSONWReader](/stream-readers/src/main/java/com/dellemc/oe/readers/JSONReader.java) in Dell EMC SDP

-  You must make the Maven repo in SDP available to your development workstation.
```
kubectl port-forward service/repo 9092:80 --namespace workshop-sample
```
-   Set the environment variable for maven user and password.
```
export MAVEN_USER=desdp
export MAVEN_PASSWORD=password
```
-   Build and publish your application JAR file. Make sure to replace the `release name` and `application values file` with appropriate values.
```    
./gradlew publish
```
```
helm upgrade --install --timeout 600s --wait \
    <Release Name> \
    charts/flink-app \
    --namespace workshop-sample \
    -f values/<application values file> 
```

Here is an example for building the json reader application by using Helm Chart.
```
helm upgrade --install --timeout 600s --wait \
    jsonreader \
    charts/flink-app \
    --namespace workshop-sample \
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
    1.  `JSONReader` reads the data generated by `JSONWriter` and prints to standard output use `DATA_FILE=earthquakes1970-2014.csv`.
    2.	`ImageReader` reads the data generated by `ImageWriter` and prints the image data no `DATA_FILE` required.
    3.	`FlinkSQLReader` reads the data generated by `JSONWriter` and converts to a SQL table source use `DATA_FILE=earthquakes1970-2014.csv`.
    4.  `FlinkSQLJOINReader` reads the data generated by `JSONWriter` and uses SQL to join another table use `DATA_FILE=HVAC.csv`.
