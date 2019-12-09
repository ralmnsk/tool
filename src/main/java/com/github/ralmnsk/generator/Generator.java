package com.github.ralmnsk.generator;

import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


public class Generator {
public static void main(String...a){
    for ( int j=0;j<10;j++){
        try (FileWriter file = new FileWriter("file"+j+".log")) {
            for (int i=0;i<10;i++){
                JSONObject userDetails = new JSONObject();
                LocalDateTime time=LocalDateTime.now();
                long milliseconds= Timestamp.valueOf(time).getTime()+10000*i;
                userDetails.put("username","user"+i);
                userDetails.put("time",milliseconds);
                userDetails.put("message", "message of user "+i);

                    file.append(userDetails.toJSONString()+System.lineSeparator());
                    file.flush();
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
}
