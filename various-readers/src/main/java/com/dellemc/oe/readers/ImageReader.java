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

import com.dellemc.oe.serialization.JsonDeserializationSchema;
import com.dellemc.oe.util.AbstractApp;
import com.dellemc.oe.util.AppConfiguration;
import com.dellemc.oe.model.ImageData;
import io.pravega.connectors.flink.FlinkPravegaReader;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A simple example app that uses a Pravega Writer to write to a given scope and stream.
 */
public class ImageReader extends AbstractApp {
    // Logger initialization
    private static final Logger LOG = LoggerFactory.getLogger(ImageReader.class);

    public ImageReader( AppConfiguration appConfiguration) {
       super(appConfiguration);
    }

    public void run() {
        try {

            // create the Pravega input stream (if necessary)
            StreamExecutionEnvironment env = initializeFlinkStreaming();
            createStream(appConfiguration.getInputStreamConfig());
            // create the Pravega source to read a stream of text
            FlinkPravegaReader<ImageData> flinkPravegaReader = FlinkPravegaReader.<ImageData>builder()
                    .withPravegaConfig(appConfiguration.getPravegaConfig())
                    .forStream(appConfiguration.getInputStreamConfig().getStream())
                    .withDeserializationSchema(new JsonDeserializationSchema(ImageData.class))
                    .build();

            DataStream<ImageData> events = env
                    .addSource(flinkPravegaReader)
                    .name("events");

            // create an output sink to print to stdout for verification
            events.printToErr();

            // execute within the Flink environment
            env.execute("IMAGE Reader");

            LOG.info("########## IMAGE READER END #############");
            // Test whether we are able to create  same image or not
            //ImageToByteArray.createImage(result);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        AppConfiguration appConfiguration = new AppConfiguration(args);
        ImageReader imageReader = new ImageReader(appConfiguration);
        imageReader.run();
    }
}
