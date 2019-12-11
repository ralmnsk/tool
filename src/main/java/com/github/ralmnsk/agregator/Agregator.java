package com.github.ralmnsk.agregator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ralmnsk.agregator.message.Message;
import com.github.ralmnsk.comparator.SortByTime;
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
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

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
    public Object getAgregatedList(){
        ObjectMapper mapper = new ObjectMapper();
        List<Message> list=new ArrayList<>();
        List<File> files=fileCounter.getFiles();
        for( File file:files){
            convertor.setFile(file);
            List<Message> listFromFile = convertor.convert();
            listFromFile.stream().forEach(m->list.add(m));
        }

        return agregate(list);
    }

    //json filter and group parameters
    private Object agregate(List<Message> messages){
        //FILTER
        if(!userFilter.equals("")){
            messages=messages
                    .stream()
                    .filter(message->message.getUser().equals(userFilter))
                    .collect(Collectors.toList());
        }

        long startLong=timeStrToLong(startPeriod);
        long endLong=timeStrToLong(endPeriod);

        if((endLong>startLong)&&(endLong>0)&&(startLong>0)){
            messages=messages.stream()
                    .filter(message -> Timestamp.valueOf(message.getTime()).getTime() > startLong)
                    .collect(Collectors.toList())
                    .stream()
                    .filter(message->Timestamp.valueOf(message.getTime()).getTime() < endLong)
                    .collect(Collectors.toList());
        }

        if((pattern!=null)&&(!pattern.equals(""))){
            messages=messages.stream()
                    .filter(m->stringMatcher.isExistString(m.getText()))
                    .collect(Collectors.toList());
        }

//        Collections.sort(messages,new SortByTime());
        //GROUPING
        if (userAgregate.equals("yes")&&timeUnit.equals("none")){
            Map<String, Long> collect = messages.stream()
                    .sorted(Comparator.comparing(Message::getUser))
                    .collect(groupingBy(Message::getUser,
                            Collectors.counting()));
            return collect;
        }

        if(userAgregate.equals("no")&&timeUnit.equals("hour")){
            Map<Integer, Map<Integer, Map<Integer, Map<Integer, Long>>>> collect = messages
                    .stream()
                    .collect(groupingBy(m -> m.getTime().getYear(),
                            groupingBy(m -> m.getTime().getMonthValue(),
                                    groupingBy(m -> m.getTime().getDayOfMonth(),
                                            groupingBy(m -> m.getTime().getHour(),
                                                    Collectors.counting())))));
            return collect;
        }

        if(userAgregate.equals("no")&&timeUnit.equals("day")){
            Map<Integer, Map<Integer, Map<Integer, Long>>> collect = messages
                    .stream()
                    .collect(groupingBy(m -> m.getTime().getYear(),
                            groupingBy(m -> m.getTime().getMonthValue(),
                                    groupingBy(m -> m.getTime().getDayOfMonth(),
                                            Collectors.counting()))));
            return collect;
        }

        if(userAgregate.equals("no")&&timeUnit.equals("month")){
            Map<Integer, Map<Integer, Long>> collect = messages
                    .stream()
                    .collect(groupingBy(m -> m.getTime().getYear(),
                            groupingBy(m -> m.getTime().getMonthValue(),
                                    Collectors.counting())));
            return collect;
        }

//        if(userAgregate.equals("yes")&&timeUnit.equals("year")){
//            Map<String, Map<Integer, Long>> collect = messages
//                    .stream()
//                    .collect(groupingBy(m -> m.getUser(),
//                            groupingBy(m -> m.getTime().getYear(),
//                                    Collectors.counting())));
//            return collect;
//        }

        if(userAgregate.equals("yes")&&timeUnit.equals("month")){
            Map<String, Map<Integer, Map<Integer, Long>>> collect = messages.stream()
                    .collect(groupingBy(m -> m.getUser(),
                            groupingBy(m -> m.getTime().getYear(),
                                    groupingBy(m -> m.getTime().getMonthValue(),
                                            Collectors.counting()))));
            return collect;
        }

        if(userAgregate.equals("yes")&&timeUnit.equals("day")){
            Map<String, Map<Integer, Map<Integer, Map<Integer, Long>>>> collect = messages.stream()
                    .collect(groupingBy(m -> m.getUser(),
                            groupingBy(m -> m.getTime().getYear(),
                                    groupingBy(m -> m.getTime().getMonthValue(),
                                            groupingBy(m -> m.getTime().getDayOfMonth(),
                                                    Collectors.counting())))));
            return collect;
        }

        if(userAgregate.equals("yes")&&timeUnit.equals("hour")){
            Map<String, Map<Integer, Map<Integer, Map<Integer, Map<Integer, Long>>>>> collect = messages.stream()
                    .collect(groupingBy(m -> m.getUser(),
                            groupingBy(m -> m.getTime().getYear(),
                                    groupingBy(m -> m.getTime().getMonthValue(),
                                            groupingBy(m -> m.getTime().getDayOfMonth(),
                                                    groupingBy(m -> m.getTime().getHour(),
                                                            Collectors.counting()))))));
            return collect;
        }

        return null;
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
