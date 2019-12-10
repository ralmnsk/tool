package com.github.ralmnsk.agregator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ralmnsk.agregator.message.Message;
import com.github.ralmnsk.agregator.time.unit.TimeUnit;
import com.github.ralmnsk.convertor.IConvertor;
import com.github.ralmnsk.file.counter.IFileCounter;
import com.github.ralmnsk.string.matcher.IStringMatcher;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Service
@PropertySource(value="classpath:application.properties")
public class Agregator implements IAgregator{
    @Value("${userFilter}")
    private String userFilter;
    @Value("${startPeriod}")
    private String startPeriod;
    @Value("${endPeriod}")
    private String endPeriod;
    @Value("${pattern}")
    private String pattern;
    @Value("${userAgregate}")
    private String userAgregate;
    @Value("${timeUnit}")
    private String timeUnit;
    @Autowired
    private IFileCounter fileCounter;
    @Autowired
    private IConvertor convertor;
    @Autowired
    private IStringMatcher stringMatcher;

//    private List<JSONObject> json;

    public Agregator(){
    }

    //list of json objects
    public List<Message> getAgregatedList(){
        ObjectMapper mapper = new ObjectMapper();
        List<Message> list=new ArrayList<>();
        List<File> files=fileCounter.getFiles();
        for( File file:files){
            convertor.setFile(file);
//            ArrayList<JSONObject> jsonList = convertor.convert();
//            jsonList.stream().map().forEach(list.add(j));
        }


        return list;
    }

    //json filter and group parameters
    private List<JSONObject> agregate(List<JSONObject> json){
        //FILTER
        if(userFilter!=null){
            json=json.stream().filter(j->j.get("username").equals(userFilter)).collect(Collectors.toList());
        }

        long startLong=timeStrToLong(startPeriod);
        long endLong=timeStrToLong(endPeriod);

        if((endLong>startLong)&&(endLong>0)&&(startLong>0)){
            json=json.stream()
                    .filter(j -> Long.parseLong(j.get("time").toString()) > startLong)
                    .collect(Collectors.toList())
                    .stream()
                    .filter(j->Long.parseLong(j.get("time").toString()) < endLong)
                    .collect(Collectors.toList());
        }

        if((pattern!=null)&&(!pattern.equals(""))){
            json=json.stream()
                    .filter(j->stringMatcher.isExistString(j.get("message").toString()))
                    .collect(Collectors.toList());
        }

        //GROUPING
//        Map<LocalDateTime, List<Data>> byYear = data.stream()
//                .collect(groupingBy(d -> d.getDate().withMonth(1).withDayOfMonth(1)));

        return json;
    }



    public long timeStrToLong(String str){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        long testTime=0L;
        try {
            LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
            testTime = Timestamp.valueOf(dateTime).getTime();
        } catch (DateTimeParseException e){
            System.out.println("Exception:"+e);
        }
        return testTime;
    }
}
