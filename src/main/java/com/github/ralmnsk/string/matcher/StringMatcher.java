package com.github.ralmnsk.string.matcher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StringMatcher implements IStringMatcher{

    private String inputString; //string from file
    @Value("${pattern}")
    private String userStr; //string from user


    public boolean isExistString(String inputString){
        String REGEX=userStr;
        this.inputString=inputString;
        Pattern p=Pattern.compile(REGEX);
        Matcher m=p.matcher(inputString);
        if(m.find()){
            return true;
        }
        return false;
    }

}
