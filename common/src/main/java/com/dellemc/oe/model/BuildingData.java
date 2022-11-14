package com.dellemc.oe.model;

import java.io.Serializable;

public class BuildingData implements Serializable {
    public int BuildingID;
    public String BuildingMgr;
    public String BuildingAge;
    public String HVACproduct;
    public String Country;
    public BuildingData getParams(String line){
        String[] tokens = line.split(",");
        BuildingData event = new BuildingData();
        try {
            event.BuildingID = Integer.parseInt(tokens[0]);
            event.BuildingMgr = tokens[1].length() > 0 ? tokens[1] : null;
            event.BuildingAge = tokens[2].length() > 0 ? tokens[2] : null;
            event.HVACproduct = tokens[3].length() > 0 ? tokens[3] : null;
            event.Country = tokens[4].length() > 0 ? tokens[4] : null;

        } catch (NumberFormatException nfe) {
            System.out.println("ERROR+++++++++++++++++++++++++++++++"+nfe.getMessage()+"+++++++++++++++++++++++++++++++");
        }

        return event;
    }
    @Override
    public String toString() {
        return "BuildingData{" +
                "BuildingID='" + BuildingID + '\'' +
                ", BuildingMgr='" + BuildingMgr + '\'' +
                ", BuildingAge='" + BuildingAge + '\'' +
                ", HVACproduct='" + HVACproduct + '\'' +
                ", Country='" + Country + '\'' +
                '}';
    }

}