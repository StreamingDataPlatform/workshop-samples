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
package com.dellemc.oe.process;

import java.net.URI;

import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.Stream;
import io.pravega.connectors.flink.FlinkPravegaReader;
import io.pravega.connectors.flink.PravegaConfig;
import io.pravega.connectors.flink.serialization.PravegaSerialization;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.dellemc.oe.util.CommonParams;
/**
 * A simple example app that reads from an exisiting Pravega stream using the FlinkConnector
 */
public class StreamProcess {

	
	public static void main(String[] args) throws Exception {
 
        final String scope = CommonParams.getScope();
        final String streamName = CommonParams.getStreamName();
        final URI controllerURI = CommonParams.getControllerURI();

        // initialize the parameter utility tool in order to retrieve input parameters
         PravegaConfig pravegaConfig = PravegaConfig.fromDefaults()
                .withControllerURI(controllerURI)
                .withDefaultScope(scope)
                .withHostnameValidation(false);
        if (CommonParams.isPravegaStandalone()) {
            try (StreamManager streamManager = StreamManager.create(pravegaConfig.getClientConfig())) {
                // create the requested scope (if necessary)
                streamManager.createScope(scope);
            }
        }
        // Retrieve the Pravega  stream (if necessary)
        Stream stream = pravegaConfig.resolve(streamName);
      		

        // initialize the Flink execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // create the Pravega source to read a stream of text
        FlinkPravegaReader<String> source = FlinkPravegaReader.<String>builder()
                .withPravegaConfig(pravegaConfig)
                .forStream(stream)
                .withDeserializationSchema(PravegaSerialization.deserializationFor(String.class))
                .build();		
		
		DataStream<String> dataStream = env.addSource(source).name(streamName).map(String::toUpperCase);
	    dataStream.print();
		
		env.execute("Reading an exisitng stream");
	}

}