package com.github.ralmnsk.file.counter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class FileCounter {

    public ArrayList<File> getFiles(String directory){
            File dir=new File(directory);
            File[] files = dir.listFiles();
        ArrayList<File> list = new ArrayList<File>(Arrays.asList(files));
        return list;
    }
}
