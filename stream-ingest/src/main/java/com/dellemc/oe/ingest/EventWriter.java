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

import com.dellemc.oe.util.Utils;
import io.pravega.client.ClientConfig;
import io.pravega.client.ClientFactory;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.EventStreamWriter;
import io.pravega.client.stream.EventWriterConfig;
import io.pravega.client.stream.StreamConfiguration;
import io.pravega.client.stream.impl.UTF8StringSerializer;

import com.dellemc.oe.util.CommonParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple example app that uses a Pravega Writer to write to a given scope and stream.
 */
public class EventWriter {

    private static Logger LOG = LoggerFactory.getLogger(EventWriter.class);
    public final String scope;
    public final String streamName;
    public final URI controllerURI;

    public EventWriter(String scope, String streamName, URI controllerURI) {
        this.scope = scope;
        this.streamName = streamName;
        this.controllerURI = controllerURI;
    }

    public void run(String routingKey, String message) {
        LOG.info(" @@@@@@@@@@@@@@@@ URI " + controllerURI.toString());

        //  create stream
        boolean  streamCreated = Utils.createStream(scope, streamName, controllerURI);
        LOG.info(" @@@@@@@@@@@@@@@@ STREAM  =  "+streamName+ "  CREATED = "+ streamCreated);

        // Create EventStreamClientFactory
        try (ClientFactory clientFactory = ClientFactory.withScope(scope, controllerURI);
             EventStreamWriter<String> writer = clientFactory.createEventWriter(streamName,
                     new UTF8StringSerializer(),
                     EventWriterConfig.builder().build())) {
            while(true) {
                System.out.format("Writing message: '%s' with routing-key: '%s' to stream '%s / %s'%n",
                        message, routingKey, scope, streamName);
                final CompletableFuture writeFuture = writer.writeEvent(routingKey, message);
                writeFuture.get();
                Thread.sleep(1000);
            }
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        final String scope = CommonParams.getScope();
        final String streamName = CommonParams.getStreamName();
        final String routingKey = CommonParams.getRoutingKeyAttributeName();
        final String message = CommonParams.getMessage();;
        final URI controllerURI = CommonParams.getControllerURI();
        EventWriter ew = new EventWriter(scope, streamName, controllerURI);
        ew.run(routingKey, message);
    }
}
