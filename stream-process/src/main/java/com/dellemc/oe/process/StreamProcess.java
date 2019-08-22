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

import com.dellemc.oe.serialization.UTF8StringDeserializationSchema;
import com.dellemc.oe.util.Constants;
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

        CommonParams.init(args);
        final String scope = CommonParams.getParam(Constants.SCOPE);
        final String streamName = CommonParams.getParam(Constants.STREAM_NAME);
        final URI controllerURI = URI.create(CommonParams.getParam(Constants.CONTROLLER_URI));

        // initialize the parameter utility tool in order to retrieve input parameters
         PravegaConfig pravegaConfig = PravegaConfig.fromDefaults()
                .withControllerURI(controllerURI)
                .withDefaultScope(scope)
                .withHostnameValidation(false);

        // Retrieve the Pravega  stream (if necessary)
        Stream stream = pravegaConfig.resolve(streamName);
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@"+stream);


        // initialize the Flink execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // create the Pravega source to read a stream of text
        FlinkPravegaReader<String> source = FlinkPravegaReader.<String>builder()
                .withPravegaConfig(pravegaConfig)
                .forStream(stream)
                .withDeserializationSchema(new UTF8StringDeserializationSchema())
                .build();

        //Reading from the stream and then converting the string to UpperCase
		DataStream<String> dataStream = env.addSource(source).name(streamName).map(String::toUpperCase);
		//Printing it to the Error stream, as it may be easy to read while debugging.
	    dataStream.printToErr();

		env.execute("Reading an exisitng stream");
	}

}