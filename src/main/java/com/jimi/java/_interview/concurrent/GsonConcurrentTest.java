package com.jimi.java._interview.concurrent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jimi
 * @version 2017-01-06 00:29.
 */
public class GsonConcurrentTest implements Runnable{

    static ThreadLocal<Gson> gsonThreadLocal = new ThreadLocal<>();
    static {
        gsonThreadLocal.set(new GsonBuilder().create());
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);

    }

    @Override
    public void run() {

    }
}
