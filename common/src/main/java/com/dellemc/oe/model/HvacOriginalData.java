package com.dellemc.oe.model;

import java.io.Serializable;

public class HvacOriginalData implements Serializable {
    public String DateTime;
    public int TargetTemp;
    public int ActualTemp;

    public int System;
    public int SystemAge;
    public int Building_ID;
    @Override
    public String toString() {
        return "HvacOriginalData{" +
                "DateTime='" + DateTime + '\'' +
                ", TargetTemp='" + TargetTemp + '\'' +
                ", ActualTemp='" + ActualTemp + '\'' +
                ", System='" + System + '\'' +
                ", SystemAge='" + SystemAge + '\'' +
                ", Building_ID='" + Building_ID + '\'' +
                '}';
    }
}