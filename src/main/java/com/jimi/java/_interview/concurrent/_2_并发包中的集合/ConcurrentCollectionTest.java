package com.jimi.java._interview.concurrent._2_并发包中的集合;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author jimi
 * @version 2016-12-29 16:56.
 */
public class ConcurrentCollectionTest {

    public static void main(String[] args) {
        //-------------- 并发相关 ----------------
        ConcurrentHashMap<String, String> currentHashMap = new ConcurrentHashMap<>();
        //get时，不加锁
        String value1 = currentHashMap.get("key1");
        //put时，加锁
        currentHashMap.put("key22", "value2222");

        CopyOnWriteArrayList<String> currentArrayList = new CopyOnWriteArrayList<>();

        CopyOnWriteArraySet currentSet = new CopyOnWriteArraySet();



    }
}
