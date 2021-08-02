package io.bio.threadPool;

import javafx.concurrent.Task;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HandlerExecutePool {
    ExecutorService executor;

    public HandlerExecutePool(int maxPoolSize,int queueSize) {
        this.executor =new ThreadPoolExecutor(maxPoolSize,maxPoolSize,10L, TimeUnit.SECONDS,new ArrayBlockingQueue<>(queueSize));
    }

    public void execute(Runnable task){
        executor.execute(task);
    }
}
