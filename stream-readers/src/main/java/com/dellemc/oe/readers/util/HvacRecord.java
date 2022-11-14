package com.dellemc.oe.readers.util;

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Schema;

public class HvacRecord {

    public static Schema getHvacSchema()
    {
        //  DateTime,TargetTemp,ActualTemp,System,SystemAge,Building_ID
        // Read data from the stream using Table reader
        Schema schema = Schema.newBuilder()
                .column("DateTime", DataTypes.STRING())
                .column("TargetTemp", DataTypes.INT())
                .column("ActualTemp", DataTypes.INT())
                .column("System", DataTypes.STRING())
                .column("SystemAge", DataTypes.INT())
                .column("Building_ID", DataTypes.INT()).build();
        return schema;
    }
}

