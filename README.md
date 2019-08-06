# Nautilus Developer Workshop Code Samples

## Pre-Requisites
External connectivity enabled Nautilus cluster required to perform these samples.

## Running the Samples

### Configure Nautilus Authentication
- Obtain the file pravega-keycloak-credentials-*.jar and place it in the lib directory.
```
PRAVEGA_CREDENTIALS_VERSION=0.5.0-2305.31c34b9-0.11.10-001.e597251
sudo apt install maven
mvn install:install-file \
-Dfile=lib/pravega-keycloak-credentials-${PRAVEGA_CREDENTIALS_VERSION}-shadow.jar \
-DgroupId=io.pravega -DartifactId=pravega-keycloak-credentials \
-Dversion=${PRAVEGA_CREDENTIALS_VERSION} -Dpackaging=jar
```
- Create a project `workshop-samples` in Nautilus UI
- This will automatically create a scope `workshop-samples`
-  Get the `keycloak.json` file by executing this command
```
kubectl get secret workshop-samples-pravega -n workshop-samples -o jsonpath="{.data.keycloak\.json}" |base64 -d >  ${HOME}/keycloak.json
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
```

## Setting up your IDE

First, we need to build pravega client and pravega flink connector
ans install it in local maven

```
git clone https://github.com/pravega/pravega
cd pravega
git checkout r0.5
./gradlew install
cd ..
git clone https://github.com/pravega/flink-connectors
cd flink-connectors
git checkout r0.5
./gradlew install
```

To import the source into IntelliJ:
1. create a project from existing source
2. Enable auto import
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
