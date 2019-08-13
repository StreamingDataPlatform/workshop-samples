package com.dellemc.oe.serialization;

import com.dellemc.oe.model.ImageData;
import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;

import java.nio.ByteBuffer;

public class ByteArrayDeserializationSchema extends AbstractDeserializationSchema<ImageData> {

    @Override
    public ImageData deserialize(byte[] message) {
        //ByteBuffer buf = ByteBuffer.wrap(payload);
        return null;
    }

}
