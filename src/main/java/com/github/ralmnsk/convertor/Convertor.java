package com.github.ralmnsk.convertor;



import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;

public class Convertor {
    private File file;
    public Convertor(File file){
        this.file=file;
    }

//    @SuppressWarnings("unchecked")
    public ArrayList<JSONObject> convert(){
        JSONParser parser = new JSONParser();
        String line=null;
        JSONObject obj;
        ArrayList<JSONObject> json=new ArrayList<JSONObject>();
        try {
            FileReader fileReader = new FileReader(file.getAbsolutePath());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                obj = (JSONObject) new JSONParser().parse(line);
                json.add(obj);
            }
            // Always close files.
            bufferedReader.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return json;
    }
}
