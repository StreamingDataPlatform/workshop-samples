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
package com.dellemc.oe.readers;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import io.pravega.client.ByteStreamClientFactory;
import io.pravega.client.ClientConfig;
import io.pravega.client.admin.ReaderGroupManager;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.byteStream.ByteStreamReader;
import io.pravega.client.stream.*;
import com.dellemc.oe.util.CommonParams;
import io.pravega.client.stream.impl.DefaultCredentials;
import io.pravega.common.io.StreamHelpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A simple example app that uses a Pravega Writer to write to a given scope and stream.
 */
public class ImageReader {
    // Logger initialization
    private static final Logger LOG = LoggerFactory.getLogger(ImageReader.class);
    private static final int READER_TIMEOUT_MS = 3000;

    public final String scope;
    public final String streamName;
    public final URI controllerURI;

    public ImageReader(String scope, String streamName, URI controllerURI) {
        this.scope = scope;
        this.streamName = streamName;
        this.controllerURI = controllerURI;
    }

    public void run()  {

             //String scope = "image-scope";
            String streamName = "image-stream";
            // Create client config
            ClientConfig clientConfig = null;
            if(CommonParams.isPravegaStandaloneAuth())
            {
                clientConfig = ClientConfig.builder().controllerURI(controllerURI)
                        .credentials(new DefaultCredentials(CommonParams.getPassword(), CommonParams.getUser()))
                        .build();
            }
            else
            {
                clientConfig = ClientConfig.builder().controllerURI(controllerURI).build();
            }

            StreamManager streamManager = StreamManager.create(clientConfig);
            StreamConfiguration streamConfig = StreamConfiguration.builder().build();
            streamManager.createStream(scope, streamName, streamConfig);

            // Create reader group and reader config
            final String readerGroup = UUID.randomUUID().toString().replace("-", "");
            final ReaderGroupConfig readerGroupConfig = ReaderGroupConfig.builder()
                    .stream(Stream.of(scope, streamName))
                    .build();
            try (ReaderGroupManager readerGroupManager = ReaderGroupManager.withScope(scope, controllerURI)) {
                readerGroupManager.createReaderGroup(readerGroup, readerGroupConfig);
            }

            ByteStreamReader reader = null;
            // Create  EventStreamClientFactory and  create reader to get stream data
            try
            {
                ByteStreamClientFactory clientFactory = ByteStreamClientFactory.withScope(scope, clientConfig);
                reader = clientFactory.createByteStreamReader(streamName);
                byte[] readBuffer = new byte[1024];

                CompletableFuture<Integer>  count   =   reader. onDataAvailable();
                LOG.info("#######################     count.get()   ######################  "+count.get());
                while (count.get() > 0) {
                    //int  result  =StreamHelpers.readAll(reader, readBuffer, 0, readBuffer.length);
                    byte[]  result  =StreamHelpers.readAll(reader, count.get());
                    LOG.info("#######################     RECEIVED IMAGE DATA   ######################  "+result);
                    // Test whether we are able to create  same image or not
                    //ImageToByteArray.createImage(result);
               }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                reader.close();
            }

    }



    public static void main(String[] args) {
        final String scope = CommonParams.getScope();
        final String streamName = CommonParams.getStreamName();
        final URI controllerURI = CommonParams.getControllerURI();
        LOG.info("#######################     SCOPE   ###################### "+scope);
        LOG.info("#######################     streamName   ###################### "+streamName);
        LOG.info("#######################     controllerURI   ###################### "+controllerURI);
        ImageReader imageReader = new ImageReader(scope, streamName, controllerURI);
        imageReader.run();
    }
}
