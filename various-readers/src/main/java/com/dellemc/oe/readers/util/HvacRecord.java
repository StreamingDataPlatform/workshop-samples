package com.dellemc.oe.readers.util;

import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.descriptors.Schema;

public class HvacRecord {

    public static Schema getHvacSchema()
    {
        //  DateTime,TargetTemp,ActualTemp,System,SystemAge,Building_ID
        // Read data from the stream using Table reader
        Schema schema = new Schema()
                .field("DateTime", Types.STRING())
                .field("TargetTemp", Types.INT())
                .field("ActualTemp", Types.INT())
                .field("System", Types.STRING())
                .field("SystemAge", Types.INT())
                .field("Building_ID", Types.INT());
        return schema;
    }
}

