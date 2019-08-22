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

import java.net.URI;

import com.dellemc.oe.util.Constants;
import com.dellemc.oe.util.Utils;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.ScalingPolicy;
import io.pravega.client.stream.StreamConfiguration;
import com.dellemc.oe.util.CommonParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple example app that creates stream in Pravega
 */
public class StreamCreator {

    private static Logger LOG = LoggerFactory.getLogger(StreamCreator.class);
    public final String scope;
    public final String streamName;
    public final URI controllerURI;

    public StreamCreator(String scope, String streamName, URI controllerURI) {
        this.scope = scope;
        this.streamName = streamName;
        this.controllerURI = controllerURI;
    }

    public void run() {
        //  create stream
        boolean  streamCreated = Utils.createStream(scope, streamName, controllerURI);
        LOG.info(" @@@@@@@@@@@@@@@@ STREAM  =  "+streamName+ "  CREATED = "+ streamCreated);
        if (streamCreated) {
            LOG.info("succeed in creating stream '%s' under scope '%s'", streamName, scope);
        }
    }

    public static void main(String[] args) {
        CommonParams.init(args);
        final String scope = CommonParams.getParam(Constants.SCOPE);
        final String streamName = CommonParams.getParam(Constants.STREAM_NAME);
        final URI controllerURI = URI.create(CommonParams.getParam(Constants.CONTROLLER_URI));
        StreamCreator sc = new StreamCreator(scope, streamName, controllerURI);
        sc.run();
    }
}
