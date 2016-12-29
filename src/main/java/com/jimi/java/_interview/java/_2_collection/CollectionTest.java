package com.jimi.java._interview.java._2_collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 *  常用集合对比
 *  ArrayList，LinkedList和HashMap
 *
 * @author jimi
 * @version 2016-12-25 19:11.
 */
public class CollectionTest {

    public static void main(String[] args) {

        int[] nums = {1, 2, 3};

        ArrayList<String> arrayList = new ArrayList<>();

        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("keyy", "valueeee");

        //get时，如何做到o(1)？
        // -->> 参源码，会对key的hash处理，计算出在数组中位置。
        String value = hashMap.get("keyy");

        //如何获取所有元素，数组中，包括相同位置的链表中的元素？？？
        Set<String> keySet = hashMap.keySet();


    }
}
