package com.github.ralmnsk.file.counter.printer;


import com.github.ralmnsk.agregator.IAgregator;
import com.github.ralmnsk.agregator.message.Message;
import com.github.ralmnsk.file.counter.IFileCounter;
import com.github.ralmnsk.exception.handler.IExceptionHandler;
import com.github.ralmnsk.string.corrector.ICorrector;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.*;

@Service
@NoArgsConstructor
public class FilePrinter implements IFilePrinter, CommandLineRunner {
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
    @Value("${path}")
    private String fileName;
    @Autowired
    private IAgregator agregator;
    @Autowired
    private IFileCounter fileCounter;
    @Autowired
    private IExceptionHandler handler;
    @Autowired
    private ICorrector c;

    private String firstString="";
    private String secondString="";
    private String thirdString="";





    @Override
    public void run(String... args) {
        print();
    }





    public void print(){

        try {
            handler.handle();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

        ArrayList<File> files = fileCounter.getFiles();
        List<CompletableFuture<Object>> futureList = new ArrayList<>();
        for (File file : files) {
            CompletableFuture<Object> agregatedList = agregator.getAgregatedList(file);
            agregatedList.join();
            futureList.add(agregatedList);
        }


        List<Object> objectList = new ArrayList<>();
        for (CompletableFuture<Object> f : futureList) {
            Object o = null;
            try {
                o = f.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            objectList.add(o);
        }





 //-----------------------------------------------------------------------------
        List<String> list=new ArrayList<>();//list for strings to print

        firstString=(" user name:"+userFilter+" | "+"Period: "+startPeriod+" - "+endPeriod+" | pattern:"+pattern+"| groupby: username:"+userAgregate+", time unit:"+timeUnit);

//-----------------------------------------------------------------------------
        if (userAgregate.equals("yes")&&timeUnit.equals("none")){
        secondString=("User name | Count of records");

            List<Map<String,Long>> maps=new ArrayList<>(); //maps where userFilter=true, time unit=none  and   userFilter=no, time unit=month,day,hour
            for(Object o:objectList){
                Map<String, Long> map= (Map<String, Long>)o;
                maps.add(map);
            }

            Map<String,Long> collect=maps.stream()
                    .flatMap(m -> m.entrySet().stream())
                    .collect(groupingBy(Map.Entry::getKey, summingLong(Map.Entry::getValue)));

            for(Map.Entry<String, Long> entry : collect.entrySet()){
                String str=(entry.getKey()+" | "+entry.getValue()); //user and record counts
                list.add(str);
            }
        }

//-----------------------------------------------------------------------------
//Start where userFilter=no
//
            if (userAgregate.equals("no") && timeUnit.equals("hour")) {
                secondString = ("user name:" + userFilter);
                thirdString = ("Hour | Count of records");

                Map<LocalDateTime, Long> collect1 = getLocalDateTimeLongMap(objectList);
                for (Map.Entry<LocalDateTime, Long> entry : collect1.entrySet()) {
                    list.add(entry.getKey().getYear() + "/"
                            + c.correct(entry.getKey().getMonthValue()) + "/" + c.correct(entry.getKey().getDayOfMonth())
                            + "T" + c.correct(entry.getKey().getHour()) + " | " + entry.getValue());
                }
            }

//-----------------------------------------------------------------------------
            if (userAgregate.equals("no") && timeUnit.equals("day")) {
                secondString = ("user name:" + userFilter);
                thirdString = ("Day | Count of records");
                Map<LocalDateTime, Long> collect1 = getLocalDateTimeLongMap(objectList);
                for (Map.Entry<LocalDateTime, Long> entry : collect1.entrySet()) {
                    list.add(entry.getKey().getYear() + "/"
                            + c.correct(entry.getKey().getMonthValue()) + "/" + c.correct(entry.getKey().getDayOfMonth()) +
                            " | " + entry.getValue());
                }
            }


//-----------------------------------------------------------------------------
            if (userAgregate.equals("no") && timeUnit.equals("month")) {
                secondString = ("user name:" + userFilter);
                thirdString = ("Month | Count of records");
                Map<LocalDateTime, Long> collect1 = getLocalDateTimeLongMap(objectList);
                for (Map.Entry<LocalDateTime, Long> entry : collect1.entrySet()) {
                    list.add(entry.getKey().getYear() + "/"
                            + c.correct(entry.getKey().getMonthValue()) +
                            " | " + entry.getValue());
                }
            }

// end where userFilter=no
//userAgregate=yes and timeUnit
//-------------------------------------------------------------------------------
             if (userAgregate.equals("yes") && timeUnit.equals("month")) {
                 secondString = ("User | Month | Count of records");
                 Map<String, Map<LocalDateTime, Long>> collect = getStringMapMap(objectList);

                 collect.entrySet().stream().forEach(s->s.getValue().entrySet()
                        .stream().forEach(t->list.add(s.getKey()+
                                 " | "+t.getKey().getYear()+" / "+c.correct(t.getKey().getMonthValue())+" | "
                                 +t.getValue())));
             }
//-------------------------------------------------------------------------------
             if (userAgregate.equals("yes") && timeUnit.equals("day")) {
                 secondString = ("user | year/month/day/ | Count of records");
                 Map<String, Map<LocalDateTime, Long>> collect = getStringMapMap(objectList);
                 collect.entrySet().stream().forEach(s->s.getValue().entrySet()
                         .stream().forEach(t->list.add(s.getKey()+
                                 " | "+t.getKey().getYear()+" / "+c.correct(t.getKey().getMonthValue())+
                                 "/"+c.correct(t.getKey().getDayOfMonth())+" | "
                                 +t.getValue())));
             }
//-------------------------------------------------------------------------------
             if (userAgregate.equals("yes") && timeUnit.equals("hour")) {
                 secondString = ("user | year/month/day/hour | Count of records");
                 Map<String, Map<LocalDateTime, Long>> collect = getStringMapMap(objectList);
                 collect.entrySet().stream().forEach(s->s.getValue().entrySet()
                         .stream().forEach(t->list.add(s.getKey()+
                                 " | "+t.getKey().getYear()+" / "+c.correct(t.getKey().getMonthValue())+
                                 "/"+c.correct(t.getKey().getDayOfMonth())+"/"+c.correct(t.getKey().getHour())+" | "
                                 +t.getValue())));
             }
//-------------------------------------------------------------------------------
        Collections.sort(list,String::compareTo);
        printInFile(list);
    }





    private Map<LocalDateTime, Long> getLocalDateTimeLongMap(List<Object> objectList) {
        List<Map<LocalDateTime, Long>> maps1 = new ArrayList<>();
        for (Object o : objectList) {
            Map<LocalDateTime, Long> mp =
                    (Map<LocalDateTime, Long>) o;
            maps1.add(mp);
        }

        return maps1.stream()
                .flatMap(m -> m.entrySet().stream())
                .collect(groupingBy(Map.Entry::getKey, summingLong(Map.Entry::getValue)));
    }




    private Map<String, Map<LocalDateTime, Long>> getStringMapMap(List<Object> objectList) {
        List<Map<String, Map<LocalDateTime, Long>>> maps2 = new ArrayList<>();
        for (Object o : objectList) {
            Map<String, Map<LocalDateTime, Long>> m =
                    (Map<String, Map<LocalDateTime, Long>>) o;
            maps2.add(m);
        }

        List<Message> messages = new LinkedList<>();

        for (Map<String, Map<LocalDateTime, Long>> map : maps2) {
            Set<Map.Entry<String, Map<LocalDateTime, Long>>> users = map.entrySet();
            for (Map.Entry<String, Map<LocalDateTime, Long>> user : users) {
                Map<LocalDateTime, Long> times = user.getValue();
                for (Map.Entry<LocalDateTime, Long> time : times.entrySet()) {
                    String name = user.getKey();
                    LocalDateTime t = time.getKey();
                    Long count = time.getValue();
                    Message msg = new Message(name, t, count);
                        Iterator<Message> iterator=messages.iterator();
                    while (iterator.hasNext()) {
                        Message m=iterator.next();
                        if (m.getUser().equals(name) && m.getTime().getYear()==t.getYear()
                        &&m.getTime().getMonthValue()==t.getMonthValue()&&m.getTime().getDayOfMonth()==t.getDayOfMonth()
                        &&m.getTime().getHour()==t.getHour()) {
                            m.setCount(m.getCount() + count);
                            break;
                        } else {
                            messages.add(msg);
                            break;
                        }
                    }
                    if(messages.size()==0){
                        messages.add(msg);
                    }
                }
            }
        }

        return messages
                .stream()
                .collect(groupingBy(m -> m.getUser(),
                        groupingBy(m -> m.getTime(),
                                counting())));
    }




    private void printInFile(List<String> list){
        BufferedWriter writer = null;
        try {
            File file=new File(fileName);
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(firstString);
            writer.newLine();
            writer.write(secondString);
            writer.newLine();
            writer.write(thirdString);
            writer.newLine();
            for(String s:list){
                writer.write(s);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(writer!=null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//
    }

}
