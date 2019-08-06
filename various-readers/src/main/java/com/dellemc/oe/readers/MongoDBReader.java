/*
 * Copyright (c) 2018 Dell Inc., or its subsidiaries. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 */
package com.dellemc.oe.readers;

import com.dellemc.oe.db.MongoDBSink;
import com.dellemc.oe.model.JSONData;
import com.dellemc.oe.serialization.JsonDeserializationSchema;
import com.dellemc.oe.util.CommonParams;
import com.dellemc.oe.util.Utils;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.Stream;
import io.pravega.client.stream.impl.DefaultCredentials;
import io.pravega.connectors.flink.FlinkPravegaReader;
import io.pravega.connectors.flink.PravegaConfig;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

import org.bson.Document;
import org.bson.BSONObject;
import org.bson.types.ObjectId;
import com.mongodb.DBObject;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
/*import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.io.MongoUpdateWritable;
import com.mongodb.hadoop.MongoOutput;*/

import org.apache.flink.configuration.Configuration;
import org.apache.flink.api.common.functions.MapFunction;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

/*
 *  This flink application demonstrates the JSON Data reading
 */
public class MongoDBReader {

    // Logger initialization
    private static final Logger LOG = LoggerFactory.getLogger(MongoDBReader.class);
    private static final int READER_TIMEOUT_MS = 3000;

    public static void main(String[] args) throws Exception {
        LOG.info("########## MongoDBReader START #############");

        final String scope = CommonParams.getScope();
        String streamName = CommonParams.getStreamName();
        final URI controllerURI = CommonParams.getControllerURI();

        LOG.info("#######################     SCOPE   ###################### " + scope);
        LOG.info("#######################     streamName   ###################### " + streamName);
        LOG.info("#######################     controllerURI   ###################### " + controllerURI);
        run(scope, streamName, controllerURI);
    }

    public static void run(String scope, String streamName, URI controllerURI) {

        try {
            streamName = "json-stream";
            // Create client config
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

            LOG.info("==============  pravegaConfig  =============== " + pravegaConfig);

            // create the Pravega input stream (if necessary)
            Stream stream = Utils.createStream(
                    pravegaConfig,
                    streamName);
            LOG.info("==============  stream  =============== " + stream);

            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            // create the Pravega source to read a stream of text
            FlinkPravegaReader<JSONData> flinkPravegaReader = FlinkPravegaReader.builder()
                    .withPravegaConfig(pravegaConfig)
                    .forStream(stream)
                    .withDeserializationSchema(new JsonDeserializationSchema(JSONData.class))
                    .build();

            DataStream<JSONData> events = env
                    .addSource(flinkPravegaReader)
                    .name("events");

            events.addSink(new MongoDBSink());
            // create an output sink to print to stdout for verification
            events.printToErr();

            // execute within the Flink environment
            env.execute("MONGO-DB Reader");

            LOG.info("########## JSON READER END #############");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
