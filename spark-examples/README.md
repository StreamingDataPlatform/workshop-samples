# Spark Connector Examples for Pravega on SDP

The battery of code examples to demonstrate the capabilities of Pravega as a data stream storage system for Apache Spark.

## Deployment

1. Create a project on SDP and upload the maven dependencies artifacts 
    - Method A: 
        - Git clone the repository for [spark-connectors](https://github.com/pravega/spark-connectors) and [pravega-keycloak](https://github.com/pravega/pravega-keycloak) 
        - Using `gradlew` to build the artifacts
          ```bash
          ./gradlew install
          ```
        - Upload the jar through UI in SDP
    - Method B 
        - Download pre-built artifacts from [JFrog](http://oss.jfrog.org/jfrog-dependencies/io/pravega/) and/or [Maven Central](https://repo1.maven.org/maven2/io/pravega/)
        - Upload the jar through UI in SDP
    
    ![SDP Maven](images/maven.png)

2. Upload the Python or Scala application file to the SDP
    ![SDP File](images/files.png)

3. Change any configurations such as spark version, dependencies, application name of the deployment `yaml` file

4. Use `kubectl` to deploy the application
    ```bash
    kubectl apply -f generate_data_to_pravega.yaml -n namespace
    kubectl apply -f pravega_to_console_python.yaml -n namespace
    ```