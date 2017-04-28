package com.jimi.java._interview.collection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

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

        Object o = new Object();

        HashSet<String> hashSet = new HashSet<>(1);
        hashSet.add("Jimi");
        hashSet.add("Jimi");
        System.out.println("hashSet = " + hashSet);
        Iterator<String> iteratorSet = hashSet.iterator();

        HashMap<String, String> hashMap1 = new HashMap<>(2);
        hashMap1.put("test", null);
        Set<Map.Entry<String, String>> entries1 = hashMap1.entrySet();
        hashMap1.put("test2", null);
        Set<Map.Entry<String, String>> entries = hashMap1.entrySet();

        Object[] entriesObject = entries.toArray();

        Iterator<Map.Entry<String, String>> entryIterator = entries.iterator();

        System.out.println("null == null = " + (null == null));

        System.out.println("hashMap1 = " + hashMap1);

        int i = 0;

        System.out.println("Integer.SIZE = " + Integer.SIZE);
        System.out.println("Byte.SIZE = " + Byte.SIZE);
        System.out.println("Double.SIZE = " + Double.SIZE);

        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("key1", "value22");

        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();

        ReentrantLock lock = new ReentrantLock();
        // tryLock作用，和lock的区别
        lock.tryLock();
        lock.lock();

        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(1);
        queue.peek();

        Vector<Integer> vector = new Vector<>();
        vector.add(1);

        Stack<Integer> stack = new Stack<>();
        stack.push(1);
        stack.pop();

        System.out.println("1<<5 = " + (1 << 5));

    }
}






















