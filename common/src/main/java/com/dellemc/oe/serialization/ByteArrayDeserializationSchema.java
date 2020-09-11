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
