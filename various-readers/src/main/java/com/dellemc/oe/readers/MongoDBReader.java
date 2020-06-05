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
import com.dellemc.oe.util.AbstractApp;
import com.dellemc.oe.util.AppConfiguration;
import io.pravega.client.stream.Stream;
import io.pravega.connectors.flink.FlinkPravegaReader;
import io.pravega.connectors.flink.PravegaConfig;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.io.MongoUpdateWritable;
import com.mongodb.hadoop.MongoOutput;*/

/*
 *  This flink application demonstrates the JSON Data reading
 */
public class MongoDBReader extends AbstractApp {

    // Logger initialization
    private static final Logger LOG = LoggerFactory.getLogger(MongoDBReader.class);
    private static final int READER_TIMEOUT_MS = 3000;

    public MongoDBReader(AppConfiguration appConfiguration){
        super(appConfiguration);
    }
    public void run() {

        try {

            // Create client config
            PravegaConfig pravegaConfig = appConfiguration.getPravegaConfig();
            LOG.info("==============  pravegaConfig  =============== " + pravegaConfig);

            // create the Pravega input stream (if necessary)
            createStream(appConfiguration.getInputStreamConfig());
            Stream stream = appConfiguration.getInputStreamConfig().getStream();

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

    public static void main(String[] args) throws Exception {
        LOG.info("########## MongoDBReader START #############");
        AppConfiguration appConfiguration = new AppConfiguration(args);
        MongoDBReader mongoDbReader = new MongoDBReader(appConfiguration);
        mongoDbReader.run();
    }
}
