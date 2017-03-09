package com.jimi.java._interview.fundamental._3_thread;

import org.apache.commons.collections.map.HashedMap;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * 观察ThreadLocal原理
 * Created by lagou on 2017/3/9.
 */
public class ThreadLocalTest {

    /**
     * 对于一个线程的多个threadLocal变量会保存到一个ThreadLocalMap中（每个线程对应一个ThreadLocalMap）
     * 一个线程的多个变量根据对象的hashcode确定保存在threadLocalMap中数组的位置
     */
    static ThreadLocal<String> strLocal = new ThreadLocal<>();
    static ThreadLocal<Integer> intLocal = new ThreadLocal<>();

    public static void main(String[] args) {

        //================================ 跟踪ThreadLocal原理 ================================
        strLocal.set("123");
        strLocal.set("2222");

        intLocal.set(3);

        System.out.println("strLocal = " + strLocal.get());
        System.out.println("intLocal = " + intLocal.get());


        //===================================== 单对象的弱引用 ====================================
        String test = new String("test");

        WeakReference<String> stringWeakReference = new WeakReference<>(test);

        test = null;
        System.out.println("stringWeakReference value before gc = " + stringWeakReference.get());

        System.gc();

        System.out.println("stringWeakReference value after gc = " + stringWeakReference.get());


        //====================================== 对象放入map中的弱引用，其实和单对象是一样的 ===================================
        Map<WeakReference<StringBuilder>, String> strWeakReferenceMap = new HashedMap();

        StringBuilder stringBuilder = new StringBuilder().append("test builder");

        WeakReference<StringBuilder> stringBuilderWeakReference = new WeakReference<>(stringBuilder);
        strWeakReferenceMap.put(stringBuilderWeakReference, new String("builder value"));

        stringBuilder = null;

        for (WeakReference<StringBuilder> builderWeakReference : strWeakReferenceMap.keySet()) {
            System.out.println("builderWeakReference.get().toString() before gc = " + builderWeakReference.get());
        }

        System.gc();

        //此处，gc后，作为key的弱引用对象变为了null，也就是被回收了，但value的对象还在，如果线程一直不结束，就会造成内存泄漏。
        //因此，ThreadLocal的内存泄漏不是其本身造成的，而是使用线程池，如果不销毁线程才可能导致内存泄漏。
        for (WeakReference<StringBuilder> builderWeakReference : strWeakReferenceMap.keySet()) {
            System.out.println("builderWeakReference.get().toString() after gc = " + builderWeakReference.get());
        }

    }
}
