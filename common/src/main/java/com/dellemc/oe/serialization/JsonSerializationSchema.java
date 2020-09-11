/**
 * Copyright (c) Dell Inc., or its subsidiaries. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.dellemc.oe.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.serialization.SerializationSchema;

public class JsonSerializationSchema<T> implements SerializationSchema<T> {
    @Override
    public byte[] serialize(T o) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            byte[] result = objectMapper.writeValueAsBytes(o);
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
