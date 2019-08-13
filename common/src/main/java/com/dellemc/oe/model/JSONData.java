package com.dellemc.oe.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JSONData implements Serializable {
    public String id;
    public String name;
    public String building;
    public String location;
   
    @Override
    public String toString() {
        return "JSONData{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", building='" + building + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
