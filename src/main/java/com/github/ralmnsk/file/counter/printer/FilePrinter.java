package com.github.ralmnsk.file.counter.printer;

import com.github.ralmnsk.agregator.Agregator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@NoArgsConstructor
public class FilePrinter implements IFilePrinter{
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
    private Agregator agregator;

    public void print(){
        Object obj=agregator.getAgregatedList();
        List<String> list=new ArrayList<>();
        if (userAgregate.equals("yes")&&timeUnit.equals("none")){
            System.out.println("User name | Count of records");
            Map<String, Long> collect= (Map<String, Long>)obj;
            for(Map.Entry<String, Long> entry : collect.entrySet()){
                System.out.println(entry.getKey()+" | "+entry.getValue());
            }
        }

        if(userAgregate.equals("no")&&timeUnit.equals("hour")){
            System.out.println("user name:"+userFilter);
            System.out.println("Hour | Count of records");
            Map<Integer, Map<Integer, Map<Integer, Map<Integer, Long>>>> collect=
                    (Map<Integer, Map<Integer, Map<Integer, Map<Integer, Long>>>>)obj;
            for(Map.Entry<Integer, Map<Integer, Map<Integer, Map<Integer, Long>>>> entry:collect.entrySet()){
//                    System.out.print(entry.getKey()+"/");
                Map<Integer, Map<Integer, Map<Integer, Long>>> months = entry.getValue();
                for(Map.Entry<Integer, Map<Integer, Map<Integer, Long>>> entry1:months.entrySet()){
//                    System.out.print(entry1.getKey()+"/");
                    Map<Integer, Map<Integer, Long>> days = entry1.getValue();
                    for(Map.Entry<Integer, Map<Integer, Long>> entry2:days.entrySet()){
//                        System.out.print(entry2.getKey()+"/");
                        Map<Integer, Long> hours = entry2.getValue();
                        for(Map.Entry<Integer, Long> entry3:hours.entrySet()){
                            list.add(entry.getKey()+"/"+entry1.getKey()+"/"+entry2.getKey()+"/"+entry3.getKey()+" | "+entry3.getValue());

                        }
                    }

                }
            }
        }

        if(userAgregate.equals("no")&&timeUnit.equals("day")){
            Map<Integer, Map<Integer, Map<Integer, Long>>> collect =
                    (Map<Integer, Map<Integer, Map<Integer, Long>>>)obj;
            System.out.println("user name:"+userFilter);
            System.out.println("Day | Count of records");
            for(Map.Entry<Integer, Map<Integer, Map<Integer, Long>>> entry1:collect.entrySet()){
//                    System.out.print(entry1.getKey()+"/");
                Map<Integer, Map<Integer, Long>> days = entry1.getValue();
                for(Map.Entry<Integer, Map<Integer, Long>> entry2:days.entrySet()){
//                        System.out.print(entry2.getKey()+"/");
                    Map<Integer, Long> hours = entry2.getValue();
                    for(Map.Entry<Integer, Long> entry3:hours.entrySet()){
                        System.out.println(entry1.getKey()+"/"+entry2.getKey()+"/"+entry3.getKey()+" | "+entry3.getValue());
                    }
                }

            }
        }

        if(userAgregate.equals("no")&&timeUnit.equals("month")){
            Map<Integer, Map<Integer, Long>> collect =
                    ( Map<Integer, Map<Integer, Long>>)obj;
            System.out.println("user name:"+userFilter);
            System.out.println("Month | Count of records");
            for(Map.Entry<Integer, Map<Integer, Long>> entry2:collect.entrySet()){
//                        System.out.print(entry2.getKey()+"/");
                Map<Integer, Long> hours = entry2.getValue();
                for(Map.Entry<Integer, Long> entry3:hours.entrySet()){
                    System.out.println(entry2.getKey()+"/"+entry3.getKey()+" | "+entry3.getValue());
                }
            }
        }

        if(userAgregate.equals("yes")&&timeUnit.equals("month")){
            Map<String, Map<Integer, Map<Integer, Long>>> collect =
                    (Map<String, Map<Integer, Map<Integer, Long>>>)obj;
//            for(Map.Entry<String, Map<Integer, Map<Integer, Long>>> entry:collect.entrySet()){
//
//            }
            for (Map.Entry<String, Map<Integer, Map<Integer, Long>>> entry : collect.entrySet()) {
                Map<Integer, Map<Integer, Long>> years = entry.getValue();
                for(Map.Entry<Integer, Map<Integer, Long>> entry1:years.entrySet()){
                    Map<Integer, Long> month = entry1.getValue();
                    for(Map.Entry<Integer,Long> entry2:month.entrySet()){
                        String str=(entry.getKey()+"/"+entry1.getKey()+"/"+entry2.getValue()+" | "+entry2.getValue());
                        list.add(str);
                    }
                }
            }
        }

        if(userAgregate.equals("yes")&&timeUnit.equals("day")){
            Map<String, Map<Integer, Map<Integer, Map<Integer, Long>>>> collect =
                    (Map<String, Map<Integer, Map<Integer, Map<Integer, Long>>>>)obj;
        }

        if(userAgregate.equals("yes")&&timeUnit.equals("hour")){
            Map<String, Map<Integer, Map<Integer, Map<Integer, Map<Integer, Long>>>>> collect =
                    (Map<String, Map<Integer, Map<Integer, Map<Integer, Map<Integer, Long>>>>>)obj;
        }

        list.sort(Comparator.comparing(String::toString));
        list.forEach(System.out::println);
        System.out.println();
    }

}
