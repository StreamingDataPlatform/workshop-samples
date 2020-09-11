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
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JSONData implements Serializable {
    public String DateTime;
    public String Latitude;
    public String Longitude;
    public String Depth;
    public double Magnitude;
    public String MagType;
    public String NbStations;
    public String Gap;
    public String Distance;
    public String RMS;
    public String Source;
    public long EventID;

    @Override
    public String toString() {
        return "JSONData{" +
                "DateTime='" + DateTime + '\'' +
                ", latitude='" + Latitude + '\'' +
                ", longitude='" + Longitude + '\'' +
                ", Depth='" + Depth + '\'' +
                ", Magnitude='" + Magnitude + '\'' +
                ", MagType='" + MagType + '\'' +
                ", NbStations='" + NbStations + '\'' +
                ", Gap='" + Gap + '\'' +
                ", Distance='" + Distance + '\'' +
                ", RMS='" + RMS + '\'' +
                ", Source='" + Source + '\'' +
                ", EventID='" + EventID + '\'' +
                '}';
    }
}
