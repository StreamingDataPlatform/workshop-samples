package com.dellemc.oe.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JSONData implements Serializable {
    public String Date;
    public String Time;
    public String Latitude;
    public String Longitude;
    public String Type;
    public String Depth;
   @JsonProperty("Depth Error")
    public String DepthError;
    @JsonProperty("Depth Seismic Stations")
    public String DepthSeismicStations;
    public String Magnitude;
    @JsonProperty("Magnitude Type")
    public String MagnitudeType;
    @JsonProperty("Magnitude Error")
    public String MagnitudeError;
    @JsonProperty("Magnitude Seismic Stations")
    public String MagnitudeSeismicStations;
    @JsonProperty("Azimuthal Gap")
    public String AzimuthalGap;
    @JsonProperty("Horizontal Distance")
    public String HorizontalDistance;
    @JsonProperty("Horizontal Error")
    public String HorizontalError;
    @JsonProperty("RootMean Square")
    public String RootMeanSquare;
    public String ID;
    public String Source;
    @JsonProperty("Location Source")
    public String LocationSource;
    @JsonProperty("Magnitude Source")
    public String MagnitudeSource;
    public String Status;

    @Override
    public String toString() {
        return "JSONData{" +
                "date='" + Date + '\'' +
                ", time='" + Time + '\'' +
                ", latitude='" + Latitude + '\'' +
                ", longitude='" + Longitude + '\'' +
                ", Type='" + Type + '\'' +
                ", Depth='" + Depth + '\'' +
                ", DepthError='" + DepthError + '\'' +
                ", DepthSeismicStations='" + DepthSeismicStations + '\'' +
                ", Magnitude='" + Magnitude + '\'' +
                ", MagnitudeType='" + MagnitudeType + '\'' +
                ", MagnitudeError='" + MagnitudeError + '\'' +
                ", MagnitudeSeismicStations='" + MagnitudeSeismicStations + '\'' +
                ", AzimuthalGap='" + AzimuthalGap + '\'' +
                ", HorizontalDistance='" + HorizontalDistance + '\'' +
                ", HorizontalError='" + HorizontalError + '\'' +
                ", RootMeanSquare='" + RootMeanSquare + '\'' +
                ", ID='" + ID + '\'' +
                ", Source='" + Source + '\'' +
                ", LocationSource='" + LocationSource + '\'' +
                ", MagnitudeSource='" + MagnitudeSource + '\'' +
                ", Status='" + Status + '\'' +
                '}';
    }
}
