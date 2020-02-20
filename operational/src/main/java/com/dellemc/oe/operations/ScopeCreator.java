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
import io.pravega.client.ClientConfig;
import io.pravega.client.admin.StreamManager;
import com.dellemc.oe.util.AppConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple example app that creates scope in Pravega
 */
public class ScopeCreator extends AbstractApp {

    private static Logger LOG = LoggerFactory.getLogger(ScopeCreator.class);

    public ScopeCreator(AppConfiguration appConfiguration) {
        super(appConfiguration);
    }

    public void run() {
        ClientConfig clientConfig = appConfiguration.getPravegaConfig().getClientConfig();
        String scope = appConfiguration.getInputStreamConfig().getStream().getScope();
        try(StreamManager streamManager = StreamManager.create(clientConfig)) {
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
        AppConfiguration appConfiguration = new AppConfiguration(args);
        ScopeCreator sc = new ScopeCreator(appConfiguration);
        sc.run();
    }
}
