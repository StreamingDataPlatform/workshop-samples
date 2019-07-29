/*
 * Copyright (c) 2018 Dell Inc., or its subsidiaries. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 */
package com.dellemc.oe.readers;

import com.dellemc.oe.serialization.JsonNodeSerializer;
import com.dellemc.oe.util.CommonParams;

import com.fasterxml.jackson.databind.JsonNode;
import io.pravega.client.ClientFactory;
import io.pravega.client.admin.ReaderGroupManager;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.UUID;
;

/*
 * 
 */
public class JSONReader {

    // Logger initialization
    private static final Logger LOG = LoggerFactory.getLogger(JSONReader.class);
    private static final int READER_TIMEOUT_MS = 3000;

    public static void main(String[] args) throws Exception {
       LOG.info("########## READER START #############");

        final String scope = CommonParams.getScope();
        String streamName = CommonParams.getStreamName();
        final URI controllerURI = CommonParams.getControllerURI();

        LOG.info("#######################     SCOPE   ###################### "+scope);
        LOG.info("#######################     streamName   ###################### "+streamName);
        LOG.info("#######################     controllerURI   ###################### "+controllerURI);
        run(scope , streamName,controllerURI );
    }

    public static void run(String scope , String streamName,URI controllerURI )  {

        try {
            /*String scope = "json-scope";
            String streamName = "json-stream";
            URI controllerURI =  new URI("tcp://localhost:9090");*/
            streamName = "json-stream";
            StreamManager streamManager = StreamManager.create(controllerURI);
            streamManager.createScope(scope);
            StreamConfiguration streamConfig = StreamConfiguration.builder().build();
            streamManager.createStream(scope, streamName, streamConfig);

            final String readerGroup = UUID.randomUUID().toString().replace("-", "");
            final ReaderGroupConfig readerGroupConfig = ReaderGroupConfig.builder()
                    .stream(Stream.of(scope, streamName))
                    .build();
            try (ReaderGroupManager readerGroupManager = ReaderGroupManager.withScope(scope, controllerURI)) {
                readerGroupManager.createReaderGroup(readerGroup, readerGroupConfig);
            }

            try (ClientFactory clientFactory = ClientFactory.withScope(scope, controllerURI);
                 EventStreamReader<JsonNode> reader = clientFactory.createReader("reader",
                         readerGroup,
                         new JsonNodeSerializer(),
                         ReaderConfig.builder().build())) {
                System.out.format("@@@@@@@@@@@@@@@@ Reading all the events from %s/%s%n", scope, streamName);
                EventRead<JsonNode> event = null;
                while (reader.hashCode() > 0) {
                    try {
                        event = reader.readNextEvent(READER_TIMEOUT_MS);
                        if (event.getEvent() != null) {
                            System.out.format("@@@@@@@@@@@@@@ Read event '%s'%n", event.getEvent());
                        }
                    } catch (ReinitializationRequiredException e) {
                        //There are certain circumstances where the reader needs to be reinitialized
                        e.printStackTrace();
                    }
                } //while (event.getEvent() != null);
                System.out.format("@@@@@@@@@@@@@@ No more events from %s/%s%n", scope, streamName);
            }


            System.out.println("########## READER END #############");

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }



}
