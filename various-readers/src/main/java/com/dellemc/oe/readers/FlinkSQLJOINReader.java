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

import com.dellemc.oe.readers.model.HvacData;
import com.dellemc.oe.readers.util.HvacRecord;
import com.dellemc.oe.util.AbstractApp;
import com.dellemc.oe.util.AppConfiguration;
import io.pravega.client.stream.Stream;
import io.pravega.connectors.flink.Pravega;
import io.pravega.connectors.flink.PravegaConfig;
import org.apache.flink.api.java.tuple.Tuple2;

import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.Json;
import org.apache.flink.table.descriptors.Schema;
import org.apache.flink.table.descriptors.StreamTableDescriptor;
import org.apache.flink.table.factories.StreamTableSourceFactory;
import org.apache.flink.table.factories.TableFactoryService;
import org.apache.flink.table.sources.CsvTableSource;
import org.apache.flink.table.sources.TableSource;
import org.apache.flink.types.Row;


import java.net.*;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
 *  This flink application demonstrates the joining of two data sets with SQL. To execute this sample run the JSONWriter with data_file HVAC.csv as a input param along with other params.
 */
public class FlinkSQLJOINReader extends AbstractApp {

    // Logger initialization
    private static final Logger LOG = LoggerFactory.getLogger(FlinkSQLJOINReader.class);

    public FlinkSQLJOINReader(AppConfiguration appConfiguration){
        super(appConfiguration);
    }

    public void run() {

        try {
            LOG.info("################## RUN START ################  ");
            // Create Flink Execution environment
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
            // Read table as stream data
            StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

            // Create client config
            PravegaConfig pravegaConfig =  appConfiguration.getPravegaConfig();

            // create the Pravega input stream (if necessary)
            createStream(appConfiguration.getInputStreamConfig());
            Stream stream = appConfiguration.getInputStreamConfig().getStream();

            // get the schema
            Schema   schema = HvacRecord.getHvacSchema();

            Pravega pravega = new Pravega();
            pravega.tableSourceReaderBuilder()
                     .forStream(stream)
                    .withPravegaConfig(pravegaConfig);

            // Create Table descriptor
            StreamTableDescriptor desc = (StreamTableDescriptor) tableEnv.connect(pravega)
                    .withFormat(new Json().failOnMissingField(false).deriveSchema())
                    .withSchema(schema)
                    .inAppendMode();

            // Create Table source
            final Map<String, String> propertiesMap = desc.toProperties();
            final TableSource<?> source = TableFactoryService.find(StreamTableSourceFactory.class, propertiesMap)
                    .createStreamTableSource(propertiesMap);

            // Register table resource
            tableEnv.registerTableSource("hvac", source);
            // calculate and add additional params  diff, temp_range and  extreme_temp
            String sqlQuery = "SELECT DateTime, TargetTemp , ActualTemp,SystemAge,Building_ID ,  (TargetTemp - ActualTemp )  as diff , " +
                    "(CASE WHEN (TargetTemp-ActualTemp)>5 THEN 'HOT' WHEN (TargetTemp-ActualTemp)<-5 THEN 'COLD' ELSE 'NORMAL' END) AS temp_range," +
                    "(CASE WHEN (TargetTemp-ActualTemp)>5 THEN 1 WHEN (TargetTemp-ActualTemp)<-5 THEN 1 ELSE 0 END) AS extreme_temp  from hvac " ;

            Table result = tableEnv.sqlQuery(sqlQuery);
            //  Execute query and get DataStream Object
            DataStream<HvacData>   dataStream = tableEnv.toAppendStream(result, HvacData.class);
            LOG.info("################## RUN 7 ################  ");

            // Print the data
            //dataStream.printToErr();
            //   START seconf source
            // Register as a table in order to run SQL queries against it.
           tableEnv.registerDataStream("hvacStream", dataStream);

            // load the static data from CSV.
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            URL resourceUrl = loader.getResource("building.csv");
            String filePath = resourceUrl.getPath();
            LOG.info("################## FILE PATH ################  "+filePath);

            // This is a finite list of items which has an BuildingID,BuildingMgr,BuildingAge,HVACproduct,Country
            CsvTableSource buildingSource = new CsvTableSource.Builder()
                    .path(filePath)
                    .ignoreParseErrors()
                    .field("BuildingID", Types.INT())
                    .field("BuildingMgr", Types.STRING())
                    .field("BuildingAge", Types.INT())
                    .field("HVACproduct", Types.STRING())
                    .field("Country", Types.STRING())
                    .build();


            // Similarly, register this list of items as a table.
            tableEnv.registerTableSource("building", buildingSource);

            // Apply a join  HVAC amd building  and store the results in a dynamic (stream table) table
            Table streamTableJoinResult = tableEnv.sqlQuery("SELECT H.DateTime,  H.TargetTemp ,  H.ActualTemp, H.SystemAge, H.Building_ID, " +
                    "H.diff, H.temp_range, H.extreme_temp, B.BuildingMgr, B.BuildingAge, B.HVACproduct, B.Country " +
                    "FROM hvacStream H  JOIN building B ON H.Building_ID = B.BuildingID");


            // Transform the results again to a stream so that we can print it or use it elsewhere.
            DataStream<Tuple2<Boolean, Row>> finalResult = tableEnv.toRetractStream(streamTableJoinResult, Row.class );

            //  Print final result
            finalResult.printToErr();

            //  END
            // execute within the Flink environment
            env.execute("FlinkSQLJOINReader Reader");

            LOG.info("########## FlinkSQLJOINReader END #############");

        } catch (Exception e) {
            LOG.error("########## HvacSQLReader ERROR  #############  "+e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) throws Exception {
        LOG.info("########## FlinkSQLJOINReader START #############");
        AppConfiguration appConfiguration = new AppConfiguration(args);
        FlinkSQLJOINReader sqlJoinReader = new FlinkSQLJOINReader(appConfiguration);
        sqlJoinReader.run();
    }


}
