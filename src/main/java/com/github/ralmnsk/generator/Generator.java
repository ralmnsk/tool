package com.github.ralmnsk.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ralmnsk.agregator.message.Message;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


//ONLY TO GENERATE LOG FILES
public class Generator {
public static void main(String...a){
    ObjectMapper mapper = new ObjectMapper();
    for ( int j=0;j<10;j++){
        try (FileWriter file = new FileWriter("file"+j+".log")) {
            for (int i=0;i<10;i++){
                long milliseconds= Timestamp.valueOf(LocalDateTime.now()).getTime()+1000000000*i;
                LocalDateTime time= Instant
                        .ofEpochMilli(milliseconds)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();;
                Message msg=new Message("user"+i,time,"message of user "+i);


                    String json = mapper.writeValueAsString(msg);
                    file.append(json+System.lineSeparator());
                    file.flush();
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
}
