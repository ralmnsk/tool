package com.github.ralmnsk.agregator;

import com.github.ralmnsk.agregator.message.Message;


import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IAgregator {
    CompletableFuture<Object> getAgregatedList(File file);
    long timeStrToLong(String str);
}
