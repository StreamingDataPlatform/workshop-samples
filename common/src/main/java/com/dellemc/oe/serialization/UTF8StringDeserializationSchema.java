package com.dellemc.oe.serialization;

import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;

import java.nio.charset.StandardCharsets;

public class UTF8StringDeserializationSchema extends AbstractDeserializationSchema<String> {
    @Override
    public String deserialize(byte[] message) {
        return new String(message, StandardCharsets.UTF_8);
    }
}