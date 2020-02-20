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

import java.util.concurrent.CompletableFuture;

import com.dellemc.oe.util.AbstractApp;
import io.pravega.client.ClientConfig;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.stream.EventStreamWriter;
import io.pravega.client.stream.EventWriterConfig;
import io.pravega.client.stream.impl.UTF8StringSerializer;

import com.dellemc.oe.util.AppConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple example app that uses a Pravega Writer to write to a given scope and stream.
 */
public class EventWriter extends AbstractApp {

    private static Logger LOG = LoggerFactory.getLogger(EventWriter.class);

    public EventWriter(AppConfiguration appConfiguration) {
        super(appConfiguration);
    }

    public void run() {
        //  create stream
        createStream(appConfiguration.getInputStreamConfig());
        AppConfiguration.StreamConfig streamConfig = appConfiguration.getInputStreamConfig();
        // Create EventStreamClientFactory
        ClientConfig clientConfig = appConfiguration.getPravegaConfig().getClientConfig();

        try (EventStreamClientFactory clientFactory = EventStreamClientFactory.withScope(streamConfig.getStream().getScope(), clientConfig);
             EventStreamWriter<String> writer = clientFactory.createEventWriter(
                     streamConfig.getStream().getStreamName(),
                     new UTF8StringSerializer(),
                     EventWriterConfig.builder().build())) {
            while(true) {
                System.out.format("Writing message: '%s' with routing-key: '%s' to stream '%s / %s'%n",
                        appConfiguration.getMessage(), appConfiguration.getRoutingKey(),
                        streamConfig.getStream().getScope(), streamConfig.getStream().getStreamName());
                final CompletableFuture writeFuture = writer.writeEvent( appConfiguration.getRoutingKey(), appConfiguration.getMessage());
                writeFuture.get();
                Thread.sleep(1000);
            }
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        AppConfiguration appConfiguration = new AppConfiguration(args);
        EventWriter ew = new EventWriter(appConfiguration);
        ew.run();
    }
}
