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
import java.util.concurrent.CompletableFuture;
import com.dellemc.oe.util.Parameters;
import io.pravega.client.ClientFactory;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.EventStreamWriter;
import io.pravega.client.stream.EventWriterConfig;
import io.pravega.client.stream.ScalingPolicy;
import io.pravega.client.stream.StreamConfiguration;
import io.pravega.client.stream.impl.UTF8StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple example app that uses a Pravega Writer to write to a given scope and stream.
 */
public class EventWriter {

    private static Logger LOG = LoggerFactory.getLogger(EventWriter.class);

    public EventWriter() {

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

            ClientFactory clientFactory = ClientFactory.withScope(scope, controllerURI);
            // Create  Pravega event writer
            EventStreamWriter<String> writer = clientFactory.createEventWriter(
                    streamName,
                    new UTF8StringSerializer(),
                    EventWriterConfig.builder().build());
            while(true) {
                final CompletableFuture writeFuture = writer.writeEvent( Parameters.getRoutingKey(), Parameters.getMessage());
                writeFuture.get();
                Thread.sleep(1000);
            }
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        EventWriter ew = new EventWriter();
        ew.run();
    }
}
