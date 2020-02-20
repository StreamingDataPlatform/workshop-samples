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

import com.dellemc.oe.serialization.JsonNodeSerializer;
import com.dellemc.oe.util.AbstractApp;
import com.dellemc.oe.util.AppConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.pravega.client.ClientConfig;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.stream.EventStreamWriter;
import io.pravega.client.stream.EventWriterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple example app that uses a Pravega Writer to write to a given scope and stream.
 */
public class EventWithTimestampWriter extends AbstractApp {
    // Logger initialization
    private static final Logger LOG = LoggerFactory.getLogger(JSONWriter.class);

    public EventWithTimestampWriter(AppConfiguration appConfiguration) {
        super(appConfiguration);
    }

    // Create a JSON data for testing purpose
    public static ObjectNode createJSONData() {
        ObjectNode message = null;
        try {
            String data = "{\"sensorid\":" + Math.random() + ",\"time\":" + System.currentTimeMillis() + ",\"value\":" + Math.random();
            // Deserialize the JSON message.
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(data);
            message = (ObjectNode) jsonNode;
            LOG.info("@@@@@@@@@@@@@ DATA >>>  " + message.toString());
            return message;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        AppConfiguration appConfiguration = new AppConfiguration(args);
        JSONWriter ew = new JSONWriter(appConfiguration);

        ew.run();
    }

    public void run() {
        AppConfiguration.StreamConfig streamConfig = appConfiguration.getInputStreamConfig();
        //  create stream
        createStream(appConfiguration.getInputStreamConfig());
        // Create EventStreamClientFactory
        ClientConfig clientConfig = appConfiguration.getPravegaConfig().getClientConfig();
        try (EventStreamClientFactory clientFactory = EventStreamClientFactory.withScope(streamConfig.getStream().getScope(), clientConfig);
             // Create  Pravega event writer
             EventStreamWriter<JsonNode> writer = clientFactory.createEventWriter(
                     streamConfig.getStream().getStreamName(),
                     new JsonNodeSerializer(),
                     EventWriterConfig.builder().build())) {
            // same data write every 1 sec
            while (true) {
                ObjectNode data = createJSONData();
                writer.writeEvent(appConfiguration.getRoutingKey(), data);
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
