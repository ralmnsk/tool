package com.github.ralmnsk.file.counter.printer;


import com.github.ralmnsk.agregator.IAgregator;

import com.github.ralmnsk.file.counter.IFileCounter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.expression.TypedValue;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private String firstString="";
    private String secondString="";
    private String thirdString="";

    @Override
    public void run(String... args) throws Exception {
        print();
    }

    public void print(){
        ArrayList<File> files = fileCounter.getFiles();
        List<CompletableFuture<Object>> futureList = files
                .stream()
                .map(f -> agregator.getAgregatedList(f))
                .collect(toList());

        futureList
                .stream()
                .map(CompletableFuture::join).collect(Collectors.toList());

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

//        ObjectMapper mapper=new ObjectMapper();

//----------------------------------------------------------------
//        Object obj=agregator.getAgregatedList(files.get(0));
        List<String> list=new ArrayList<>();
        firstString=(" user name:"+userFilter+" | "+"Period: "+startPeriod+" - "+endPeriod+" | pattern:"+pattern+"| groupby: username:"+userAgregate+", time unit:"+timeUnit);
        if (userAgregate.equals("yes")&&timeUnit.equals("none")){

            List<Map<String,Long>> maps=new ArrayList<>();
            for(Object o:objectList){
                Map<String, Long> m= (Map<String, Long>)o;
                maps.add(m);
            }
            Map<String,Long> collect=maps.stream()
                    .flatMap(m -> m.entrySet().stream())
                    .collect(groupingBy(Map.Entry::getKey, summingLong(Map.Entry::getValue)));

            System.out.println("User name | Count of records");
//            Map<String, Long> collect= (Map<String, Long>)obj;
            for(Map.Entry<String, Long> entry : collect.entrySet()){
                System.out.println(entry.getKey()+" | "+entry.getValue());
            }
        }


        if(userAgregate.equals("no")&&timeUnit.equals("hour")){
            secondString=("user name:"+userFilter);
            thirdString=("Hour | Count of records");

            List<Map<LocalDateTime, Long>> maps=new ArrayList<>();
            for(Object o:objectList){
                Map<LocalDateTime, Long> m=
                        (Map<LocalDateTime, Long>)o;
                maps.add(m);
            }

            Map<LocalDateTime, Long> collect = maps.stream()
                    .flatMap(m -> m.entrySet().stream())
                    .collect(groupingBy(Map.Entry::getKey, summingLong(Map.Entry::getValue)));

            for(Map.Entry<LocalDateTime, Long> entry : collect.entrySet()){
                System.out.println(entry.getKey().getYear()+"/"
                        +entry.getKey().getMonthValue()+"/"+entry.getKey().getDayOfMonth()
                        +"T"+entry.getKey().getHour()+" | "+entry.getValue());
            }


        }

        if(userAgregate.equals("no")&&timeUnit.equals("day")){

            List<Map<LocalDateTime, Long>> maps1=new ArrayList<>();
            for(Object o:objectList){
                Map<LocalDateTime, Long> m=
                        (Map<LocalDateTime, Long>)o;
                maps1.add(m);
            }

            Map<LocalDateTime, Long> collect1 = maps1.stream()
                    .flatMap(m -> m.entrySet().stream())
                    .collect(groupingBy(Map.Entry::getKey, summingLong(Map.Entry::getValue)));

            for(Map.Entry<LocalDateTime, Long> entry : collect1.entrySet()){
                System.out.println(entry.getKey().getYear()+"/"
                        +entry.getKey().getMonthValue()+"/"+entry.getKey().getDayOfMonth()+
                        " | "+entry.getValue());
            }
        }

        if(userAgregate.equals("no")&&timeUnit.equals("month")){
            List<Map<LocalDateTime, Long>> maps2=new ArrayList<>();
            for(Object o:objectList){
                Map<LocalDateTime, Long> m=
                        (Map<LocalDateTime, Long>)o;
                maps2.add(m);
            }

            Map<LocalDateTime, Long> collect1 =
                    new HashMap<>();
            for (Map<LocalDateTime, Long> m : maps2) {
                for (Map.Entry<LocalDateTime, Long> localDateTimeLongEntry : m.entrySet()) {
                    collect1.merge(localDateTimeLongEntry.getKey(), localDateTimeLongEntry.getValue(), Long::sum);
                }
            }

            for(Map.Entry<LocalDateTime, Long> entry : collect1.entrySet()){
                System.out.println(entry.getKey().getYear()+"/"
                        +entry.getKey().getMonthValue()+
                        " | "+entry.getValue());
            }
        }

        if(userAgregate.equals("yes")&&timeUnit.equals("month")){
            secondString=("User | Month | Count of records");
            List<Map<String, Map<LocalDateTime, Long>>> maps=new ArrayList<>();
            for(Object o:objectList){
                Map<String, Map<LocalDateTime, Long>> m=
                        (Map<String, Map<LocalDateTime, Long>>)o;
                maps.add(m);
            }


            Map<String, Map<LocalDateTime, Long>> collect = new HashMap<>();

            //////////////////////////////////////////////////////////////////////////////////
                    ;

//                    .collect(groupingBy(s->s.getValue().entrySet()
//                            .stream().collect(groupingBy(Map.Entry::getKey, summingLong(Map.Entry::getValue)))));


            collect.forEach((key, dates) -> {
                for (Map.Entry<LocalDateTime, Long> d : dates.entrySet()) {
                    System.out.println(key + " | " + d.getKey().getYear() + "/"
                            + d.getKey().getMonthValue() +
                            " | " + d.getValue());
                }
            });


        }

        if(userAgregate.equals("yes")&&timeUnit.equals("day")){
            secondString=("user | year/month/day/ | Count of records");
//            Map<String, Map<Integer, Map<Integer, Map<Integer, Long>>>> collect =
//                    (Map<String, Map<Integer, Map<Integer, Map<Integer, Long>>>>)obj;
//            for(Map.Entry<String, Map<Integer, Map<Integer, Map<Integer, Long>>>> entry:collect.entrySet()){
//                Map<Integer, Map<Integer, Map<Integer, Long>>> users = entry.getValue();
//                for(Map.Entry<Integer, Map<Integer, Map<Integer, Long>>> entry1:users.entrySet()){
//                    Map<Integer, Map<Integer, Long>> months = entry1.getValue();
//                    for(Map.Entry<Integer, Map<Integer, Long>> entry2:months.entrySet()){
//                        Map<Integer, Long> days = entry2.getValue();
//                        for(Map.Entry<Integer, Long> entry3: days.entrySet()){
//                            list.add(entry.getKey()+" | "+entry1.getKey()+"/"+entry2.getKey()+"/"+entry3.getKey()+" | "+entry3.getValue());
//                        }
//                    }
//                }
//            }
        }
//
//        if(userAgregate.equals("yes")&&timeUnit.equals("hour")){
//            secondString=("user | year/month/day/hour | Count of records");
//            Map<String, Map<Integer, Map<Integer, Map<Integer, Map<Integer, Long>>>>> collect =
//                    (Map<String, Map<Integer, Map<Integer, Map<Integer, Map<Integer, Long>>>>>)obj;
//            for(Map.Entry<String, Map<Integer, Map<Integer, Map<Integer, Map<Integer, Long>>>>> user:collect.entrySet()){
//                Map<Integer, Map<Integer, Map<Integer, Map<Integer, Long>>>> years = user.getValue();
//                for(Map.Entry<Integer, Map<Integer, Map<Integer, Map<Integer, Long>>>> year:years.entrySet()){
//                    Map<Integer, Map<Integer, Map<Integer, Long>>> months = year.getValue();
//                    for(Map.Entry<Integer, Map<Integer, Map<Integer, Long>>> month:months.entrySet()){
//                        Map<Integer, Map<Integer, Long>> days = month.getValue();
//                        for(Map.Entry<Integer,Map<Integer,Long>> day:days.entrySet()){
//                            Map<Integer, Long> hours = day.getValue();
//                            for(Map.Entry<Integer,Long> hour:hours.entrySet()){
//                                list.add(user.getKey()+" | "+year.getKey()+"/"+month.getKey()+"/"+day.getKey()+" h:"+hour.getKey()+" | "+hour.getValue());
//
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        if (userAgregate.equals("yes")&&timeUnit.equals("none")){
//            secondString="User name   |   Count of records";
//            Map<String, Long> collect = (Map<String, Long>)obj;
//            for(Map.Entry<String,Long> user:collect.entrySet()){
//                list.add(user.getKey()+" | "+user.getValue());
//            }
//        }
//
//        list.sort(Comparator.comparing(String::toString));
//        printInFile(list);
//        System.out.println();
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
