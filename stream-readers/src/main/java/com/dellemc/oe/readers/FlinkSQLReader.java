/**
 * Copyright (c) Dell Inc., or its subsidiaries. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.dellemc.oe.readers;

import com.dellemc.oe.readers.util.EarthQuakeRecord;
import com.dellemc.oe.util.AbstractApp;
import com.dellemc.oe.util.AppConfiguration;
import io.pravega.client.stream.Stream;
import io.pravega.connectors.flink.PravegaConfig;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.Table;
import com.dellemc.oe.model.JSONData;
import com.dellemc.oe.serialization.JsonDeserializationSchema;
import io.pravega.connectors.flink.FlinkPravegaReader;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
 *  This flink application demonstrates the JSON Data reading
 */
public class FlinkSQLReader extends AbstractApp {

    // Logger initialization
    private static final Logger LOG = LoggerFactory.getLogger(FlinkSQLReader.class);

    public FlinkSQLReader(AppConfiguration appConfiguration){
        super(appConfiguration);
    }
    public static void main(String[] args) throws Exception {
        LOG.info("########## FlinkSQLReader START #############");
        AppConfiguration appConfiguration = new AppConfiguration(args);
        FlinkSQLReader flinkSqlReader = new FlinkSQLReader(appConfiguration);
        flinkSqlReader.run();
    }

    public void run() {

        try {

            // Create client config
            PravegaConfig pravegaConfig =  appConfiguration.getPravegaConfig();
            // create the Pravega input stream (if necessary)
            createStream(appConfiguration.getInputStreamConfig());
            Stream stream = appConfiguration.getInputStreamConfig().getStream();

            // Create Flink Execution environment
            StreamExecutionEnvironment env = initializeFlinkStreaming();
            env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

            // Create Stream Table Environment
            StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

            FlinkPravegaReader<EarthQuakeRecord> flinkPravegaReader = FlinkPravegaReader.<EarthQuakeRecord>builder()
                    .withPravegaConfig(appConfiguration.getPravegaConfig())
                    .forStream(appConfiguration.getInputStreamConfig().getStream())
                    .withDeserializationSchema(new JsonDeserializationSchema(JSONData.class))
                    .build();
            DataStream<EarthQuakeRecord> events = env
                    .addSource(flinkPravegaReader)
                    .name("events");
            Table table=tableEnv.fromDataStream(events);

            // Register table source
            tableEnv.registerTable("earthquakes", table);
            String sqlQuery = "SELECT DateTime as eventTime, Latitude, Longitude, Depth, Magnitude, MagType, NbStations, Gap, Distance, RMS, " +
                    "Source, EventID from earthquakes  where Magnitude > 6"; // Magnitude above 6 treated as high/severe earthquake
            Table result = tableEnv.sqlQuery(sqlQuery);

            tableEnv.toDataStream(result, Row.class).printToErr().name("stdout");

            // execute within the Flink environment
            env.execute("FlinkSQL Reader");

            LOG.info("########## FlinkSQLReader END #############");

        } catch (Exception e) {
            LOG.error("########## FlinkSQLReader ERROR  #############  "+e.getMessage());
            e.printStackTrace();
        }

    }

}
