package com.github.ralmnsk.convertor;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.github.ralmnsk.agregator.message.Message;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class Convertor implements IConvertor{

    private File file;
    public Convertor(){
    }

    public void setFile(File file) {
        this.file = file;
    }

    //    @SuppressWarnings("unchecked")
    public ArrayList<Message> convert(){
        JSONParser parser = new JSONParser();
        String line=null;
        ArrayList<Message> list=new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            FileReader fileReader = new FileReader(file.getAbsolutePath());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                JSONObject json = (JSONObject) parser.parse(line);

                Message msg=new Message();
                msg.setUser(json.get("user").toString());
                msg.setText(json.get("text").toString());
                String jsonTimeString = json.get("time").toString();
                JSONObject jsonTime = (JSONObject) parser.parse(jsonTimeString);
                int year=Integer.parseInt(jsonTime.get("year").toString());
                int month=Integer.parseInt(jsonTime.get("monthValue").toString());
                int day=Integer.parseInt(jsonTime.get("dayOfMonth").toString());
                int hour=Integer.parseInt(jsonTime.get("hour").toString());
                int minute=Integer.parseInt(jsonTime.get("minute").toString());
                int second=Integer.parseInt(jsonTime.get("second").toString());
                LocalDateTime time=LocalDateTime.of(year,month,day,hour,minute,second);
                msg.setTime(time);
                list.add(msg);
            }
            // Always close files.
            bufferedReader.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return list;
    }
}
