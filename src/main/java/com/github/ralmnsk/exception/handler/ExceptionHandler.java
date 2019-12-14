package com.github.ralmnsk.exception.handler;

import com.github.ralmnsk.agregator.IAgregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ExceptionHandler implements IExceptionHandler {
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

    @Override
    public void handle() throws ParameterException {
        int filterFlag=0;
        if(!userFilter.equals("")){
         filterFlag++;
        }
        if(!startPeriod.equals("")&&(!endPeriod.equals(""))){
            try {
                long startLong = agregator.timeStrToLong(startPeriod);
                long endLong = agregator.timeStrToLong(endPeriod);
                if(startLong>=endLong){
                    throw new ParameterException("start >= end");
                }
                filterFlag++;
            } catch (Exception e){
                System.out.println(e+"Incorrect start and end date");
                try {
                    throw new ParameterException("start >= end");
                } catch(Exception e1){
                    System.exit(0);
                }
            }

        }
        if(!pattern.equals("")){
            filterFlag++;
        }

        if(filterFlag==0){
            throw new ParameterException("userFilter, dates or pattern is incorrect, at least one parameter should be specified");

        }

        int groupingFlag=0;
        if(!userAgregate.equals("yes")&&!userAgregate.equals("no")){
            throw new ParameterException("userAgregate have to be 'yes' or 'no'");
        }

        if(!timeUnit.equals("month")&&!timeUnit.equals("day")
        &&(!timeUnit.equals("hour")&&(!timeUnit.equals("none")))){
            throw new ParameterException("userAgregate have to be 'yes' or 'no'");
        }

        if(userAgregate.equals("no")&&timeUnit.equals("none")){
            throw new ParameterException("At least one parameter should be specified: userAgregate:yes or no, timeUnit: month, day or hour");
        }
    }
}
