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

import com.dellemc.oe.readers.util.EarthQuakeRecord;
import com.dellemc.oe.util.CommonParams;
import com.dellemc.oe.util.Constants;
import io.pravega.client.stream.Stream;
import io.pravega.connectors.flink.*;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.descriptors.Json;
import org.apache.flink.table.descriptors.StreamTableDescriptor;
import org.apache.flink.table.factories.StreamTableSourceFactory;
import org.apache.flink.table.factories.TableFactoryService;
import org.apache.flink.table.sources.TableSource;
import org.apache.flink.table.descriptors.Schema;
import org.apache.flink.types.Row;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;

/*
 *  This flink application demonstrates the JSON Data reading
 */
public class FlinkSQLReader {

    // Logger initialization
    private static final Logger LOG = LoggerFactory.getLogger(FlinkSQLReader.class);

    public static void main(String[] args) throws Exception {
        LOG.info("########## FlinkSQLReader START #############");
        CommonParams.init(args);
        final String scope = CommonParams.getParam(Constants.SCOPE);
        String streamName = CommonParams.getParam(Constants.STREAM_NAME);
        final URI controllerURI = URI.create(CommonParams.getParam(Constants.CONTROLLER_URI));

        LOG.info("#######################     SCOPE   ###################### " + scope);
        LOG.info("#######################     streamName   ###################### " + streamName);
        LOG.info("#######################     controllerURI   ###################### " + controllerURI);
        run(scope, streamName, controllerURI);
    }

    public static void run(String scope, String streamName, URI controllerURI) {

        try {
            // Read from jason stream
            streamName = "json-stream";
            // Create client config
            PravegaConfig pravegaConfig =  PravegaConfig.fromDefaults()
                        .withControllerURI(controllerURI)
                        .withDefaultScope(scope)
                        .withHostnameValidation(false);

            // create the Pravega input stream (if necessary)
            Stream stream = Stream.of(scope, streamName);

            // Create Flink Execution environment
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
            // Read table as stream data
            StreamTableEnvironment tableEnv = TableEnvironment.getTableEnvironment(env);

            // get the scema
            Schema   schema = EarthQuakeRecord.getSchema();

            Pravega pravega = new Pravega();
            pravega.tableSourceReaderBuilder()
                     .forStream(stream)
                    .withPravegaConfig(pravegaConfig);

            // Create Table descriptor
            StreamTableDescriptor desc = tableEnv.connect(pravega)
                    .withFormat(new Json().failOnMissingField(false).deriveSchema())
                    .withSchema(schema)
                    .inAppendMode();

            // Create Table source
            final Map<String, String> propertiesMap = desc.toProperties();
            final TableSource<?> source = TableFactoryService.find(StreamTableSourceFactory.class, propertiesMap)
                    .createStreamTableSource(propertiesMap);

            // Register table resource
            tableEnv.registerTableSource("earthquakes", source);
            String sqlQuery = "SELECT DateTime, Latitude, Longitude, Depth, Magnitude, MagType, NbStations, Gap, Distance, RMS, " +
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
