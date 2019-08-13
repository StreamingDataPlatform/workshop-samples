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


import com.dellemc.oe.model.ImageData;
import com.dellemc.oe.serialization.JsonNodeSerializer;
import com.dellemc.oe.util.CommonParams;
import com.dellemc.oe.util.ImageToByteArray;
import com.dellemc.oe.util.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import io.pravega.client.ClientConfig;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.stream.EventStreamWriter;
import io.pravega.client.stream.EventWriterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Random;


/**
 * A simple example app that uses a Pravega Writer to write to a given scope and stream.
 */
public class ImageWriter {
    // Logger initialization
    private static final Logger LOG = LoggerFactory.getLogger(ImageWriter.class);

    public final String scope;
    public final String streamName;
    public final URI controllerURI;

    public ImageWriter(String scope, String streamName, URI controllerURI) {
        this.scope = scope;
        this.streamName = streamName;
        this.controllerURI = controllerURI;
    }

    public static void main(String[] args) {
        final String scope = CommonParams.getScope();
        final String streamName = CommonParams.getStreamName();
        final String routingKey = CommonParams.getRoutingKeyAttributeName();
        final String message = CommonParams.getMessage();
        final URI controllerURI = CommonParams.getControllerURI();
        ImageWriter ew = new ImageWriter(scope, streamName, controllerURI);
        ew.run(routingKey, message);
    }

    public void run(String routingKey, String message) {

            //String scope = "image-scope";
            String streamName = "image-stream";
            // Create client config
            ClientConfig clientConfig = ClientConfig.builder().controllerURI(controllerURI).build();
            //  create stream
            boolean  streamCreated = Utils.createStream(scope, streamName, controllerURI);
            LOG.info(" @@@@@@@@@@@@@@@@ STREAM  =  "+streamName+ "  CREATED = "+ streamCreated);

        try(EventStreamClientFactory clientFactory = EventStreamClientFactory.withScope(scope, clientConfig);

                // Create event writer
                EventStreamWriter<JsonNode> writer = clientFactory.createEventWriter(
                         streamName,
                        new JsonNodeSerializer(),
                         EventWriterConfig.builder().build())) {
                // same data write every 1 sec
                while (true) {
                    ObjectNode data = createJSONData();
                    writer.writeEvent(routingKey, data);
                    Thread.sleep(5000);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

    }

    // Create a JSON data for testing purpose
    public static ObjectNode createJSONData() {
        ObjectNode message = null;
        try {
            int ssrc = new Random().nextInt();
            int camera = new Random().nextInt();
            ImageData   imageData   =   new ImageData();
            imageData.camera = camera;
            imageData.ssrc = ssrc + camera;
            imageData.timestamp = new Timestamp(System.currentTimeMillis()).toString();
            //Convert byte[] to String
            String encodedData = Base64.getEncoder().encodeToString( ImageToByteArray.readImage());
            imageData.data = encodedData;
           // imageData.hash = imageData.calculateHash();

            JsonObject obj = new JsonObject();
            obj.addProperty("camera", camera);
            obj.addProperty("ssrc", ssrc);
            obj.addProperty("timestamp",  imageData.timestamp);
            obj.addProperty("data", encodedData);
            String data = obj.toString();
            LOG.info("@@@@@@@@@@@@@ DATA BEFORE >>>  " + data);

            // Deserialize the JSON message.
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(data);
            message = (ObjectNode) jsonNode;
            LOG.info("@@@@@@@@@@@@@ DATA >>>  " + message.toString());
            return message;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

}
