# Nautilus Developer Workshop Code Samples

## Pre-Requisites
External connectivity enabled Nautilus cluster required to perform these samples.

## Running the Samples
1. Create a project `workshop-samples` in Nautilus UI
    1. This will automatically create a scope `workshop-samples`
1. Get the `keycloak.json` file by executing this command
```
kubectl get secret workshop-samples-pravega -n workshop-samples -o jsonpath="{.data.keycloak\.json}" |base64 -d | jq .
```
  output:
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
1. Import the project directory into IntelliJ IDE. It will automatically detect the gradle project and import things correctly.
2. Enable `Annotation Processing` by going to `Build, Execution, Deployment` -> `Compiler` > `Annotation Processors` and checking 'Enable annotation processing'.
3. Install the `Lombok Plugin`. This can be found in `Preferences` -> `Plugins`. Restart your IDE.

## Running in CLI

```
1. Edit the common/set-workshop-env.sh environment variables
1. run the set-workshop-env
```
source common/set-workshop-env.sh
```
1. Now build the workshop samples
```
gradlew clean installDist
```
  1. Go to the folder workshop-samples and run the auth-demo
```
sh auth-demo/build/install/auth-demo/bin/auth-demo
```
  1. To Create Scope
```
sh operational/build/install/operational/bin/scopeCreator
```
  1. To create Stream
```
sh operational/build/install/operational/bin/streamCreator
```
  1. To ingest data
```
sh stream-ingest/build/install/stream-ingest/bin/eventWriter
```
