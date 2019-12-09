package com.github.ralmnsk.agregator;

import com.github.ralmnsk.agregator.time.unit.TimeUnit;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class Agregator {

    private String userFilter;
    private long startPeriod;
    private long endPeriod;
    private String userAgregate;
    private TimeUnit timeUnit;
    private ArrayList<JSONObject> json;

    public Agregator(ArrayList<JSONObject> json){
        this.json=json;

    }

    public void setFilterParameters(String userFilter,long s,long e){
        this.userFilter=userFilter;
        this.startPeriod=s;
        this.endPeriod=e;
    }

    public void setAgregateParameters(String userAgregate, TimeUnit timeUnit){
        this.userAgregate=userAgregate;
        this.timeUnit=timeUnit;
    }

    public ArrayList<JSONObject> getAgregatedList(){
        if(userFilter!=null){
            json.stream().filter(j->j.get("username").equals(userFilter));
        }


        LocalDateTime startTime=Instant
                .ofEpochMilli(startPeriod)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime endTime=Instant
                .ofEpochMilli(startPeriod)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        if((endPeriod>startPeriod)&&(endPeriod>0)&&(startPeriod>0)){
            json.stream().filter(j->j.get("username").equals(userFilter));
        }

        return json;
    }
}
