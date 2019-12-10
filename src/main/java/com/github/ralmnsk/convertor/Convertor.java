package com.github.ralmnsk.convertor;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ralmnsk.agregator.message.Message;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.*;
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

//                Message msg=new Message();
//                msg.setUser(json.get("username").toString());
//                msg.setText(json.get("message").toString());
//                json.get("time");
            }
            // Always close files.
            bufferedReader.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return list;
    }
}
