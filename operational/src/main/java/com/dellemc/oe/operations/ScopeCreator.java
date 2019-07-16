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

import io.pravega.client.admin.StreamManager;
import com.dellemc.oe.util.CommonParams;
import java.net.URI;
/**
 * A simple example app that creates scope in Pravega
 */
public class ScopeCreator {

    public final String scope;
    public final URI controllerURI;

    public ScopeCreator(String scope, URI controllerURI) {
        this.scope = scope;
        this.controllerURI = controllerURI;
    }

    public void run() {
        StreamManager streamManager = StreamManager.create(controllerURI);
        final boolean scopeIsNew = streamManager.createScope(scope);
        if (scopeIsNew) {
            System.out.format("succeed in creating scope '%s'", scope);
        }
    }

    public static void main(String[] args) {
        final URI controllerURI = CommonParams.getControllerURI();
        String scope = CommonParams.getScope();
        ScopeCreator sc = new ScopeCreator(scope, controllerURI);
        sc.run();
    }
}
