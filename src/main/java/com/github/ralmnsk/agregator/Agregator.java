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
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
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
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;

@Service
@Scope(value="prototype")
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
//    @Autowired
//    private IFileCounter fileCounter;
    @Autowired
    private IConvertor convertor;
    @Autowired
    private IStringMatcher stringMatcher;

//    private List<JSONObject> json;

    public Agregator(){
    }

    //list
    @Async
    public CompletableFuture<Object> getAgregatedList(File file){
//
        System.out.println(Thread.currentThread().getName()+" start:"+LocalDateTime.now());
//        List<Message> list=new LinkedList<>();
            convertor.setFile(file);
            List<Message> listFromFile = convertor.convert();
//            listFromFile.stream().forEach(m->list.add(m));
        CompletableFuture<Object> agregate = agregate(listFromFile);
        return agregate;
    }


    //json filter and group parameters
    private CompletableFuture<Object> agregate(List<Message> messages){
        //FILTER
        if(!userFilter.equals("")){
            messages=messages
                    .stream()
                    .filter(message->message.getUser().equals(userFilter))
                    .collect(Collectors.toList());
        }

        long startLong=0L;
        long endLong=0L;

        if(!startPeriod.equals("")&&(!endPeriod.equals(""))){
            startLong=timeStrToLong(startPeriod);
            endLong=timeStrToLong(endPeriod);
        }

        if(!startPeriod.equals("")&&(!endPeriod.equals("")
                &&(endLong>startLong)&&(endLong>0)&&(startLong>0))){
            long start=startLong;
            long end=endLong;
            messages=messages.stream()
                    .filter(message -> Timestamp.valueOf(message.getTime()).getTime() > start)
                    .collect(Collectors.toList())
                    .stream()
                    .filter(message->Timestamp.valueOf(message.getTime()).getTime() < end)
                    .collect(Collectors.toList());
        }

        if((pattern!=null)&&(!pattern.equals(""))){
            List<Message> messagesByPattern=new LinkedList<>();
            for(Message m:messages){
                if(stringMatcher.isExistString(m.getText())){
                    messagesByPattern.add(m);
                }
            }
            messages=messagesByPattern;
//            messages=messages.stream()
//                    .filter(m->stringMatcher.isExistString(m.getText()))
//                    .collect(Collectors.toList());
        }

//
        //GROUPING
        if (userAgregate.equals("yes")&&timeUnit.equals("none")){
            Map<String, Long> collect = messages.stream()
                    .sorted(Comparator.comparing(Message::getUser))
                    .collect(groupingBy(Message::getUser,
                            Collectors.counting()));

            return CompletableFuture.completedFuture(collect);
        }



        if(userAgregate.equals("no")&&timeUnit.equals("hour")){

            messages.stream()
                    .forEach(m->m.setTime(m.getTime().withSecond(0)
                            .withMinute(0)));

            Map<LocalDateTime, Long> collect = messages
                    .stream()
                    .collect(groupingBy(m -> m.getTime(),
                            Collectors.counting()));

            return CompletableFuture.completedFuture(collect);
        }



        if(userAgregate.equals("no")&&timeUnit.equals("day")){
            messages.stream()
                    .forEach(m->m.setTime(m.getTime().withSecond(0)
                            .withMinute(0).withHour(0)));

            Map<LocalDateTime, Long> collect = messages
                    .stream()
                    .collect(groupingBy(m -> m.getTime(),
                            Collectors.counting()));

            return CompletableFuture.completedFuture(collect);
        }



        if(userAgregate.equals("no")&&timeUnit.equals("month")){
            messages.stream()
                    .forEach(m->m.setTime(m.getTime().withSecond(0)
                            .withMinute(0).withHour(0).withDayOfMonth(1)));

            Map<LocalDateTime, Long> collect = messages
                    .stream()
                    .collect(groupingBy(m -> m.getTime(),
                            Collectors.counting()));

            return CompletableFuture.completedFuture(collect);
        }



        if(userAgregate.equals("yes")&&timeUnit.equals("month")){

            messages.stream()
                    .forEach(m->m.setTime(m.getTime().withSecond(0)
                            .withMinute(0).withHour(0).withDayOfMonth(1)));

            Map<String, Map<LocalDateTime, Long>> collect = messages
                    .stream()
                    .collect(groupingBy(m -> m.getUser(),
                            groupingBy(m -> m.getTime(),
                                    Collectors.counting())));

            return CompletableFuture.completedFuture(collect);
        }



        if(userAgregate.equals("yes")&&timeUnit.equals("day")){

            messages.stream()
                    .forEach(m->m.setTime(m.getTime().withSecond(0)
                            .withMinute(0).withHour(0)));

            Map<String, Map<LocalDateTime, Long>> collect = messages
                    .stream()
                    .collect(groupingBy(m -> m.getUser(),
                            groupingBy(m -> m.getTime(),
                                    Collectors.counting())));

            return CompletableFuture.completedFuture(collect);
        }



        if(userAgregate.equals("yes")&&timeUnit.equals("hour")){
            messages.stream()
                    .forEach(m->m.setTime(m.getTime().withSecond(0)
                            .withMinute(0)));

            Map<String, Map<LocalDateTime, Long>> collect = messages
                    .stream()
                    .collect(groupingBy(m -> m.getUser(),
                            groupingBy(m -> m.getTime(),
                                    Collectors.counting())));

            return CompletableFuture.completedFuture(collect);
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
