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


import com.dellemc.oe.util.ImageToByteArray;
import io.pravega.client.ByteStreamClientFactory;
import io.pravega.client.ClientConfig;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.byteStream.ByteStreamWriter;
import com.dellemc.oe.util.CommonParams;
import io.pravega.client.stream.StreamConfiguration;
import io.pravega.client.stream.impl.DefaultCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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

    public void run(String routingKey, String message)  {

        try {
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

            // Create ByteStreamClientFactory
            ByteStreamClientFactory clientFactory = ByteStreamClientFactory.withScope(scope, clientConfig);

             ByteStreamWriter writer = clientFactory.createByteStreamWriter(streamName);
             //  Read a image and convert to byte[]
             byte[] payload = ImageToByteArray.readImage();


            while(true)
            {
                // write image data.
                writer.write(payload);
                LOG.info("@@@@@@@@@@@@@ DATA >>>  "+payload);
                Thread.sleep(5000);
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        final String scope = CommonParams.getScope();
        final String streamName = CommonParams.getStreamName();
        final String routingKey = CommonParams.getRoutingKeyAttributeName();
        final String message = CommonParams.getMessage();;
        final URI controllerURI = CommonParams.getControllerURI();
        ImageWriter ew = new ImageWriter(scope, streamName, controllerURI);
        ew.run(routingKey, message);
    }
}
