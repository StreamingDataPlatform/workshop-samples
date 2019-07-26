#!/bin/bash
export pravega_client_auth_method=Bearer
echo 'pravega_client_auth_method :' $pravega_client_auth_method
export pravega_client_auth_loadDynamic=true
echo 'pravega_client_auth_loadDynamic :' $pravega_client_auth_loadDynamic
export KEYCLOAK_SERVICE_ACCOUNT_FILE="/mnt/c/Projects/Nautilus/samples/beta1/workshop-samples/common/keycloak.json"
echo 'KEYCLOAK_SERVICE_ACCOUNT_FILE : ' $KEYCLOAK_SERVICE_ACCOUNT_FILE

# workshop Properties
export NAUTILUS_PROJECT_NAME=workshop-samples
echo 'NAUTILUS_PROJECT_NAME : ' $NAUTILUS_PROJECT_NAME
export PRAVEGA_SCOPE=workshop-samples
echo 'PRAVEGA_SCOPE : '$PRAVEGA_SCOPE
export STREAM_NAME=workshop-stream
echo 'STREAM_NAME : '$STREAM_NAME
export PRAVEGA_CONTROLLER_URI="tcp://localhost:9090"
#export PRAVEGA_CONTROLLER_URI="tcp://a4d4f2d23ac9411e994ef0218f612d13-52557933.us-west-2.elb.amazonaws.com:9090"
echo 'PRAVEGA_CONTROLLER_URI : '$PRAVEGA_CONTROLLER_URI
