package com.github.ralmnsk.agregator;

import com.github.ralmnsk.agregator.message.Message;
import com.github.ralmnsk.agregator.time.unit.TimeUnit;
import org.json.simple.JSONObject;

import java.util.List;

public interface IAgregator {
    List<Message> getAgregatedList();
}
