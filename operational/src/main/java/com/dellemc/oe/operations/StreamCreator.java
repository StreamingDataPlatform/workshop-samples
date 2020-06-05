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
package com.dellemc.oe.operations;

import com.dellemc.oe.util.AbstractApp;
import com.dellemc.oe.util.AppConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple example app that creates stream in Pravega
 */
public class StreamCreator extends AbstractApp {

    private static Logger LOG = LoggerFactory.getLogger(StreamCreator.class);
    public StreamCreator(AppConfiguration appConfiguration) {
        super(appConfiguration);
    }

    public void run() {
        //  create stream
        String streamName = appConfiguration.getInputStreamConfig().getStream().getStreamName();
        String scope = appConfiguration.getInputStreamConfig().getStream().getScope();
        Boolean streamCreated = createStream(appConfiguration.getInputStreamConfig());
        LOG.info(" @@@@@@@@@@@@@@@@ STREAM  =  "+streamName+ "  CREATED = "+ streamCreated);
        if (streamCreated) {
            LOG.info("succeed in creating stream '%s' under scope '%s'", streamName, scope);
        }
    }

    public static void main(String[] args) {
        AppConfiguration appConfiguration = new AppConfiguration(args);
        StreamCreator sc = new StreamCreator(appConfiguration);
        sc.run();
    }
}
