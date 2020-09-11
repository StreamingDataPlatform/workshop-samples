/**
 * Copyright (c) Dell Inc., or its subsidiaries. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.dellemc.oe.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageData implements   Serializable{
    // Unique ID for this video stream.
    public int camera;
    // Random source identifier used to avoid corruption if multiple sources use the same camera and timestamp.
    // See https://tools.ietf.org/html/rfc3550.
    public int ssrc;
    // Event time of this frame. We use Timestamp to have nanosecond precision for high-speed cameras.
    public String timestamp;
    // Sequential frame number. This can be used to identify any missing frames.
    public int frameNumber;
    // PNG-encoded image.
    public String data;

    public ImageData(){}

   public ImageData(ImageData frame)
   {
       this.camera = frame.camera;
       this.ssrc = frame.ssrc;
       this.timestamp = frame.timestamp;
       this.frameNumber = frame.frameNumber;
       this.data = frame.data;
   }

    @Override
    public String toString() {
        String dataStr = "";

        return "{" +
                "camera : " + camera +
                ", ssrc : " + ssrc +
                ", timestamp : " + timestamp +
                ", frameNumber : " + frameNumber +
                ", data : " + data +
                "}";
    }

}
