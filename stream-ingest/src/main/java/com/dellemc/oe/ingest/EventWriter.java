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
import java.util.concurrent.ExecutionException;

import io.pravega.client.ClientConfig;
import io.pravega.client.ClientFactory;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.EventStreamWriter;
import io.pravega.client.stream.EventWriterConfig;
import io.pravega.client.stream.StreamConfiguration;
import io.pravega.client.stream.impl.DefaultCredentials;
import io.pravega.client.stream.impl.UTF8StringSerializer;

import com.dellemc.oe.util.CommonParams;
/**
 * A simple example app that uses a Pravega Writer to write to a given scope and stream.
 */
public class EventWriter {

    public final String scope;
    public final String streamName;
    public final URI controllerURI;

    public EventWriter(String scope, String streamName, URI controllerURI) {
        this.scope = scope;
        this.streamName = streamName;
        this.controllerURI = controllerURI;
    }

    public void run(String routingKey, String message) {
        System.out.println(" @@@@@@@@@@@@@@@@ URI " + controllerURI.toString());

        // Create client config
        ClientConfig clientConfig = null;
        if (CommonParams.isPravegaStandaloneAuth()) {
            clientConfig = ClientConfig.builder().controllerURI(controllerURI)
                    .credentials(new DefaultCredentials(CommonParams.getPassword(), CommonParams.getUser()))
                    .build();
        } else {
            clientConfig = ClientConfig.builder().controllerURI(controllerURI).build();
        }

        StreamManager streamManager = StreamManager.create(clientConfig);
        StreamConfiguration streamConfig = StreamConfiguration.builder().build();
        streamManager.createStream(scope, streamName, streamConfig);
        // Create EventStreamClientFactory
        try (ClientFactory clientFactory = ClientFactory.withScope(scope, controllerURI);
             EventStreamWriter<String> writer = clientFactory.createEventWriter(streamName,
                     new UTF8StringSerializer(),
                     EventWriterConfig.builder().build())) {

            System.out.format("Writing message: '%s' with routing-key: '%s' to stream '%s / %s'%n",
                    message, routingKey, scope, streamName);
            final CompletableFuture writeFuture = writer.writeEvent(routingKey, message);
            writeFuture.get();
        } catch(Exception e){
            e.printStackTrace();
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
