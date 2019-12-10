package com.github.ralmnsk.agregator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ralmnsk.agregator.message.Message;
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
import java.util.Map;
import java.util.TreeMap;
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
    public List<Message> getAgregatedList(){
        ObjectMapper mapper = new ObjectMapper();
        List<Message> list=new ArrayList<>();
        List<File> files=fileCounter.getFiles();
        for( File file:files){
            convertor.setFile(file);
            List<Message> listFromFile = convertor.convert();
            listFromFile.stream().forEach(m->list.add(m));
        }
        List<Message> messages=agregate(list);
        return messages;
    }

    //json filter and group parameters
    private List<Message> agregate(List<Message> messages){
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

        //GROUPING
//        Map<LocalDateTime, List<Data>> byYear = data.stream()
//                .collect(groupingBy(d -> d.getDate().withMonth(1).withDayOfMonth(1)));
        if (!userAgregate.equals("username")){
            Map<String, Long> messageByUser = messages.stream()
                    .collect(groupingBy(Message::getUser,
                            Collectors.counting()));
//            System.out.println(map);
        }
        if(timeUnit.equals("hour")){
            Map<String, List<Message>> messageByHour = messages.stream()
                    .sorted((m1,m2)->Timestamp.valueOf(m1.getTime()).compareTo(Timestamp.valueOf(m2.getTime())))
                    .collect(groupingBy(x -> DateTimeFormatter
                            .ofPattern("YYYY-MM-dd HH").format(x.getTime())));
        }


        return messages;
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
