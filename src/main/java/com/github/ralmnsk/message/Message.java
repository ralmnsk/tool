package com.github.ralmnsk.message;

import lombok.Data;

@Data
public class Message {
    private String user;
    private long time;
    private String message;
}
