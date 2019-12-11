package com.github.ralmnsk.comparator;

import com.github.ralmnsk.agregator.message.Message;
import java.sql.Timestamp;
import java.util.Comparator;

public class SortByTime implements Comparator<Message> {
    @Override
    public int compare(Message m1, Message m2) {
         if(Timestamp.valueOf(m1.getTime()).getTime()>Timestamp.valueOf(m2.getTime()).getTime()){
             return -1;
         } else if(Timestamp.valueOf(m1.getTime()).getTime()<Timestamp.valueOf(m2.getTime()).getTime()){
             return 1;
         } else {

                return 0;
         }
    }

}
