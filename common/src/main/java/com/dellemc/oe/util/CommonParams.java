package com.dellemc.oe.util;

import java.net.URI;

// All parameters will come from environment variables. This makes it easy
// to configure on Docker, Mesos, Kubernetes, etc.
public class CommonParams {
    // By default, we will connect to a standalone Pravega running on localhost.
    public static URI getControllerURI() {
        return URI.create(getEnvVar("PRAVEGA_CONTROLLER_URI", "tcp://10.247.118.176:9090"));
    }

    public static String getScope() {
        return getEnvVar("PRAVEGA_SCOPE", "workshop-samples");
    }

    public static String getStreamName() {
        return getEnvVar("STREAM_NAME", "workshop-stream");
    }

    public static String getRoutingKeyAttributeName() {
        return getEnvVar("ROUTING_KEY_ATTRIBUTE_NAME", "test");
    }

    public static String getMessage() {
        return getEnvVar("MESSAGE", "This is Nautilus OE team workshop samples.");
    }

    private static String getEnvVar(String name, String defaultValue) {
        String value = System.getenv(name);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return value;
    }
}
