package com.github.ralmnsk.runner;

import com.github.ralmnsk.convertor.Convertor;
import com.github.ralmnsk.file.counter.FileCounter;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Runner {
    @Autowired
    private FileCounter fc;
    public static void main(String[] hello){

        //file list
//        FileCounter fc=new FileCounter();
//        ArrayList<File> list=fc.getFiles("C:\\Users\\rashkevich\\IdeaProjects\\tool\\log");
//        list.stream().map(f->f.toString()).forEach(System.out::println);

        //parse object from file
//        String filename="C:\\Users\\iland\\IdeaProjects\\tool\\log\\file0.log";
//        File file=new File(filename);
//        Convertor convertor=new Convertor(file);
//        ArrayList<JSONObject> list = convertor.convert();

//Date
//        LocalDateTime now = LocalDateTime.now();
//        System.out.println(now);
//        long time=Timestamp.valueOf(now).getTime();
//        LocalDateTime test=Instant
//                .ofEpochMilli(time)
//                .atZone(ZoneId.systemDefault())
//                .toLocalDateTime();
//        System.out.println(test);

////        Pattern String
//        String inputString="bsdfasd 1йuser9e цasdfasdf";
//        String str="user9";
//        String regex=" \\S{0,}["+str+"]\\S{0,}";
//        Pattern p=Pattern.compile(regex);
//        Matcher m=p.matcher(inputString);
//        m.find();
//        System.out.println(m.find());

        try {
            String str = "2016-03-04 11:x30";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
            System.out.println(dateTime);
            long testTime = Timestamp.valueOf(dateTime).getTime();
            LocalDateTime test = Instant
                    .ofEpochMilli(testTime)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            System.out.println(test);
        } catch (DateTimeParseException e){
            System.out.println("Exception:"+e);
        }

    }
}
