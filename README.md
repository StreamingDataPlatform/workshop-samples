# Nautilus Developer Workshop Code Samples

## Pre-Requisites
External connectivity enabled Nautilus cluster required to perform these samples.

## Running the Samples
1. Create a project workshop-samples in Nautilus UI
1. Get the keycloak.json by executing this command
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
1. Edit the common/set-workshop-env.sh environment variables
1. run the set-workshop-env
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
