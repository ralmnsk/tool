package com.github.ralmnsk.convertor;

import com.github.ralmnsk.agregator.message.Message;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayList;

public interface IConvertor {
    ArrayList<Message> convert();
    void setFile(File file);
}
