package com.dellemc.oe.readers.util;

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Schema;
import java.io.Serializable;

public class EarthQuakeRecord implements Serializable {
    public static Schema getSchema()
    {
        // Read data from the stream using Table reader
        Schema schema = Schema.newBuilder()
                .column("DateTime", DataTypes.STRING()) // TODO: Change Date Time fields to Types.SQL_TIMESTAMP()
                .column("Latitude", DataTypes.DOUBLE())
                .column("Longitude", DataTypes.DOUBLE())
                .column("Depth", DataTypes.DOUBLE())
                .column("Magnitude", DataTypes.DOUBLE())
                .column("MagType", DataTypes.STRING())
                .column("NbStations", DataTypes.STRING())
                .column("Gap", DataTypes.STRING())
                .column("Distance", DataTypes.STRING())
                .column("RMS", DataTypes.STRING())
                .column("Source", DataTypes.STRING())
                .column("EventID", DataTypes.BIGINT()).build();
        return schema;
    }
}

