/**
 * Copyright (c) Dell Inc., or its subsidiaries. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.dellemc.oe.ingest;


import com.dellemc.oe.model.ImageData;
import com.dellemc.oe.serialization.JsonNodeSerializer;
import com.dellemc.oe.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import io.pravega.client.ClientConfig;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.EventStreamWriter;
import io.pravega.client.stream.EventWriterConfig;
import io.pravega.client.stream.ScalingPolicy;
import io.pravega.client.stream.StreamConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Random;


/**
 * A simple example app that uses a Pravega Writer to write to a given scope and stream.
 */
public class ImageWriter  {
    // Logger initialization
    private static final Logger LOG = LoggerFactory.getLogger(ImageWriter.class);

    public ImageWriter() {

    }

    public static void main(String[] args) {
        ImageWriter ew = new ImageWriter();
        ew.run();
    }

    public void run() {
        try{
            URI controllerURI = Parameters.getControllerURI();
            StreamManager streamManager = StreamManager.create(controllerURI);
            String scope = Parameters.getScope();
            String streamName = Parameters.getStreamName();
            StreamConfiguration streamConfig = StreamConfiguration.builder()
                    .scalingPolicy(ScalingPolicy.byEventRate(
                            Parameters.getTargetRateEventsPerSec(), Parameters.getScaleFactor(), Parameters.getMinNumSegments()))
                    .build();
            streamManager.createStream(scope, streamName, streamConfig);

            ClientConfig config = ClientConfig.builder().controllerURI(controllerURI)
                    .credentials(null).trustStore("").build();
            EventStreamClientFactory clientFactory = EventStreamClientFactory.withScope(scope, config);
            // Create  Pravega event writer
            EventStreamWriter<JsonNode> writer = clientFactory.createEventWriter(
                    streamName,
                    new JsonNodeSerializer(),
                    EventWriterConfig.builder().build());
            while (true) {
                ObjectNode data = createJSONData();
                writer.writeEvent(Parameters.getRoutingKey(), data);
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
