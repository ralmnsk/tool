package com.github.ralmnsk.file.counter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

@Service
@PropertySource(value="classpath:application.properties")
public class FileCounter implements IFileCounter{
    @Value("${directory}")
    private String directory;

    public ArrayList<File> getFiles(){
            File dir=new File(directory);
            File[] files = dir.listFiles();
            ArrayList<File> list = new ArrayList<File>(Arrays.asList(files));

        return list;
    }
}
