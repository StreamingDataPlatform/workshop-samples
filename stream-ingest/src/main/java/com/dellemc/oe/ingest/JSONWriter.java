/*
 * Copyright (c) 2017 Dell Inc., or its subsidiaries. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 */
package com.dellemc.oe.ingest;

import java.net.URI;

import com.dellemc.oe.serialization.JsonNodeSerializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.pravega.client.ClientConfig;
import io.pravega.client.ClientFactory;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.EventStreamWriter;
import io.pravega.client.stream.EventWriterConfig;
import io.pravega.client.stream.StreamConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;;

import com.dellemc.oe.util.CommonParams;
/**
 * A simple example app that uses a Pravega Writer to write to a given scope and stream.
 */
public class JSONWriter {
    // Logger initialization
    private static final Logger LOG = LoggerFactory.getLogger(JSONWriter.class);

    public final String scope;
    public final String streamName;
    public final URI controllerURI;

    public JSONWriter(String scope, String streamName, URI controllerURI) {
        this.scope = scope;
        this.streamName = streamName;
        this.controllerURI = controllerURI;
    }

    public void run(String routingKey)  {

        try {
            String streamName = "json-stream";
            StreamManager streamManager = StreamManager.create(controllerURI);
            // create scope if not exists. This wont work when we try to create scope in nautilus. We need to use other methods to create scope on nautilus.
            streamManager.createScope(scope);
            StreamConfiguration streamConfig = StreamConfiguration.builder().build();
            streamManager.createStream(scope, streamName, streamConfig);

            // Create client config
            ClientConfig clientConfig = ClientConfig.builder().controllerURI(URI.create(controllerURI.toString())).build();
            // Create EventStreamClientFactory
            EventStreamClientFactory clientFactory = EventStreamClientFactory.withScope(scope, clientConfig);
            // Create event writer
            EventStreamWriter<JsonNode> writer = clientFactory.createEventWriter(
                    streamName,
                    new JsonNodeSerializer(),
                    EventWriterConfig.builder().build());
            // same data write every 1 sec
            while(true)
            {
                ObjectNode data = createJSONData();
                writer.writeEvent(routingKey, data);
                Thread.sleep(1000);
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
            LOG.error("====== ERROR ==========");
        }

    }

    // Create a JSON data for testing purpose
    public static ObjectNode createJSONData()  {
        ObjectNode message = null;
        try {
            String data = "{\"id\":"+Math.random()+",\"name\":\"DELL EMC\",\"building\":3,\"location\":\"India\"}";
            // Deserialize the JSON message.
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(data);
            message = (ObjectNode) jsonNode;
            LOG.info("@@@@@@@@@@@@@ DATA >>>  "+message.toString());
            return message;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return  message;
    }

    public static void main(String[] args) {
        final String scope = CommonParams.getScope();
        final String streamName = CommonParams.getStreamName();
        final String routingKey = CommonParams.getRoutingKeyAttributeName();
        final URI controllerURI = CommonParams.getControllerURI();
        JSONWriter ew = new JSONWriter(scope, streamName, controllerURI);
        ew.run(routingKey);
    }
}
