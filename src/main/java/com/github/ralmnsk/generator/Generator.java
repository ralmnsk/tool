package com.github.ralmnsk.generator;

import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


public class Generator {
public static void main(String...a){
    for ( int j=0;j<10;j++){
        try (FileWriter file = new FileWriter("file"+j+".log")) {
            for (int i=0;i<10;i++){
                JSONObject userDetails = new JSONObject();
                userDetails.put("username","user"+i);
                userDetails.put("time",LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
                userDetails.put("message", "message of user "+i);

                    file.append(userDetails.toJSONString());
                    file.flush();
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
}
