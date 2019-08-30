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
import com.dellemc.oe.util.CommonParams;
import com.dellemc.oe.util.Constants;
import com.dellemc.oe.util.DataGenerator;
import com.dellemc.oe.util.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.pravega.client.ClientConfig;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.stream.EventStreamWriter;
import io.pravega.client.stream.EventWriterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

/**
 * A simple example app that uses a Pravega Writer to write to a given scope and stream.
 */
public class JSONWriter {
    // Logger initialization
    private static final Logger LOG = LoggerFactory.getLogger(JSONWriter.class);

    public final String scope;
    public final String streamName;
    public final String dataFile;
    public final URI controllerURI;

    public JSONWriter(String scope, String streamName, URI controllerURI,String dataFile) {
        this.scope = scope;
        this.streamName = streamName;
        this.dataFile = dataFile;
        this.controllerURI = controllerURI;
    }

    public static void main(String[] args) {
        // Get the Program parameters
        CommonParams.init(args);
        final String scope = CommonParams.getParam(Constants.SCOPE);
        final String streamName = CommonParams.getParam(Constants.STREAM_NAME);
        final String routingKey = CommonParams.getParam(Constants.ROUTING_KEY_ATTRIBUTE_NAME);
        final URI controllerURI = URI.create(CommonParams.getParam(Constants.CONTROLLER_URI));
        final String dataFile = CommonParams.getParam(Constants.DATA_FILE);
        JSONWriter ew = new JSONWriter(scope, streamName, controllerURI,dataFile);

        ew.run(routingKey);
    }

    public void run(String routingKey) {

                String streamName = "json-stream";
                ObjectNode message = null;
                // Create client config
                ClientConfig clientConfig = ClientConfig.builder().controllerURI(controllerURI).build();
                //  create stream
                boolean  streamCreated = Utils.createStream(scope, streamName, controllerURI);
                LOG.info(" @@@@@@@@@@@@@@@@ STREAM  =  "+streamName+ "  CREATED = "+ streamCreated);
                // Create EventStreamClientFactory
                try( EventStreamClientFactory clientFactory = EventStreamClientFactory.withScope(scope, clientConfig);

                // Create  Pravega event writer
                EventStreamWriter<JsonNode> writer = clientFactory.createEventWriter(
                        streamName,
                        new JsonNodeSerializer(),
                        EventWriterConfig.builder().build())) {
                    //  Coverst CSV  data to JSON
                    String data = DataGenerator.convertCsvToJson(dataFile);
                    // Deserialize the JSON message.
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonArray = objectMapper.readTree(data);
                    if (jsonArray.isArray()) {
                        for (JsonNode node : jsonArray) {
                            message = (ObjectNode) node;
                            LOG.info("@@@@@@@@@@@@@ DATA  @@@@@@@@@@@@@  "+message.toString());
                            final CompletableFuture writeFuture = writer.writeEvent(routingKey, message);
                            writeFuture.get();
                            Thread.sleep(10000);
                        }

                    }

        }
        catch (Exception e) {
            LOG.error("@@@@@@@@@@@@@ ERROR  @@@@@@@@@@@@@  "+e.getMessage());
            throw new RuntimeException(e);
        }

    }
}
