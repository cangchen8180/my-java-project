package com.jimi.java._interview.collection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

//        hashMap.put()
        ConcurrentHashMap<String, Integer> curtHashMap = new ConcurrentHashMap<>(16);

        List<Integer> iList = new ArrayList<>();
        iList.add(3);
        iList.add(4);
        iList.add(1);
        iList.add(8);
        iList.add(9);
        iList.add(10);
        iList.add(10);
        iList.add(2);
        iList.add(5);
        System.out.println("=============排序前=============");
        System.out.println("iList = " + iList);

        System.out.println("");
        Collections.sort(iList);
        System.out.println("=============排序后=============");
        System.out.println("iList = " + iList);


        System.out.println("16 >> 1 = " + (16 >> 1));

        //=================== TreeSet源码 ====================
        Set<String> treeSet = new TreeSet<>();


        int i = 0;

        System.out.println("Integer.SIZE = " + Integer.SIZE);
        System.out.println("Byte.SIZE = " + Byte.SIZE);
        System.out.println("Double.SIZE = " + Double.SIZE);

        HashMap<String, String> stringStringHashMap = new HashMap<>();

        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();

        System.out.println("1<<5 = " + (1 << 5));
    }
}






















