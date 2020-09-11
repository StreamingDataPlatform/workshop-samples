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
import io.pravega.connectors.flink.table.descriptors.Pravega;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.descriptors.ConnectTableDescriptor;
import org.apache.flink.table.descriptors.*;
import org.apache.flink.table.factories.StreamTableSourceFactory;
import org.apache.flink.table.factories.TableFactoryService;
import org.apache.flink.table.sources.TableSource;
import org.apache.flink.table.descriptors.Schema;
import org.apache.flink.types.Row;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

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

            // get the Schema
            Schema   schema = EarthQuakeRecord.getSchema();

            Pravega pravega = new Pravega();
            pravega.tableSourceReaderBuilder()
                    .forStream(stream)
                    .withPravegaConfig(pravegaConfig);
            // Create Table descriptor
            ConnectTableDescriptor desc = tableEnv.connect(pravega)
                    .withFormat(new Json().failOnMissingField(false).deriveSchema())
                    .withSchema(schema)
                    .inAppendMode();

            // Create Table source
            final Map<String, String> propertiesMap = desc.toProperties();
            final TableSource<?> source = TableFactoryService.find(StreamTableSourceFactory.class, propertiesMap)
                    .createStreamTableSource(propertiesMap);


            // Register table source
            tableEnv.registerTableSource("earthquakes", source);
            String sqlQuery = "SELECT DateTime as eventTime, Latitude, Longitude, Depth, Magnitude, MagType, NbStations, Gap, Distance, RMS, " +
                    "Source, EventID from earthquakes  where Magnitude > 6"; // Magnitude above 6 treated as high/severe earthquake
            Table result = tableEnv.sqlQuery(sqlQuery);

            tableEnv.toAppendStream(result, Row.class).printToErr().name("stdout");

            // execute within the Flink environment
            env.execute("FlinkSQL Reader");

            LOG.info("########## FlinkSQLReader END #############");

        } catch (Exception e) {
            LOG.error("########## FlinkSQLReader ERROR  #############  "+e.getMessage());
            e.printStackTrace();
        }

    }

}
