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

import com.dellemc.oe.util.Constants;
import io.pravega.client.admin.StreamManager;
import com.dellemc.oe.util.CommonParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
/**
 * A simple example app that creates scope in Pravega
 */
public class ScopeCreator {

    private static Logger LOG = LoggerFactory.getLogger(ScopeCreator.class);
    public final String scope;
    public final URI controllerURI;

    public ScopeCreator(String scope, URI controllerURI) {
        this.scope = scope;
        this.controllerURI = controllerURI;
    }

    public void run() {
        try(StreamManager streamManager = StreamManager.create(controllerURI);) {
            final boolean scopeIsNew = streamManager.createScope(scope);
            if (scopeIsNew) {
                LOG.info("succeed in creating scope  '"+scope);
            }
            else
            {
                LOG.info("already exists scope  '"+scope);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        CommonParams.init(args);
        final String scope = CommonParams.getParam(Constants.SCOPE);
        final URI controllerURI = URI.create(CommonParams.getParam(Constants.CONTROLLER_URI));
        ScopeCreator sc = new ScopeCreator(scope, controllerURI);
        sc.run();
    }
}
