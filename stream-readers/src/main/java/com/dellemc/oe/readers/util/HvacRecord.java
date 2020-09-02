package com.dellemc.oe.readers.util;

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.descriptors.Schema;

public class HvacRecord {

    public static Schema getHvacSchema()
    {
        //  DateTime,TargetTemp,ActualTemp,System,SystemAge,Building_ID
        // Read data from the stream using Table reader
        Schema schema = new Schema()
                .field("DateTime", DataTypes.STRING())
                .field("TargetTemp", DataTypes.INT())
                .field("ActualTemp", DataTypes.INT())
                .field("System", DataTypes.STRING())
                .field("SystemAge", DataTypes.INT())
                .field("Building_ID", DataTypes.INT());
        return schema;
    }
}

