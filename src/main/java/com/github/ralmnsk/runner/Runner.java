package com.github.ralmnsk.runner;

import com.github.ralmnsk.convertor.Convertor;
import com.github.ralmnsk.file.counter.FileCounter;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Runner {
    public static void main(String[] hello){
        //file list
//        FileCounter fc=new FileCounter();
//        ArrayList<File> list=fc.getFiles("C:\\Users\\rashkevich\\IdeaProjects\\tool\\log");
//        list.stream().map(f->f.toString()).forEach(System.out::println);
        //parse object from file
        String filename="C:\\Users\\rashkevich\\IdeaProjects\\tool\\log\\file0.log";
        File file=new File(filename);
        Convertor convertor=new Convertor(file);
        ArrayList<JSONObject> list = convertor.convert();
    }
}
