package com.github.ralmnsk.string.corrector;

import org.springframework.stereotype.Service;

@Service
public class Corrector implements ICorrector {
    @Override
    public String correct(int n) {
        String str=String.valueOf(n);
        if(str.length()<2){
            str=0+str;
        }
        return str;
    }
}
