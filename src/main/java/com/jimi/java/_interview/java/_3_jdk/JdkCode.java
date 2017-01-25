package com.jimi.java._interview.java._3_jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * @author jimi
 * @version 2017-01-19 14:15.
 */
public class JdkCode {


    public static void main(String[] args) {

        // ---- 1、组合模式 -----
        //将对象以树形结构组织起来,以达成“部分－整体” 的层次结构，使得客户端对单个对象和组合对象的使用具有一致性。
        ArrayList<String> arrayList = new ArrayList<>(10);
        arrayList.addAll(new LinkedList<String>());
        //类似应用
        /*
        java.util.Map#putAll(Map)
        java.util.List#addAll(Collection)
        java.util.Set#addAll(Collection)
         */

        // ---- 2、适配器模式 -----
        //java.util.Arrays#asList()
        //java.io.InputStreamReader(InputStream)
        //java.io.OutputStreamWriter(OutputStream)
        List<Integer> objects = Arrays.asList(new Integer[]{});

        // ---- 3、代理模式 -----
        // java.lang.reflect.Proxy

        // ---- 4、建造者模式/装饰器 -----
        //java.lang.StringBuilder#append()
        //java.lang.StringBuffer#append()
        StringBuffer stringBuffer = new StringBuffer(new String("2223ss"));

        // ---- 5、工厂模式 -----
        /*
        java.lang.Proxy#newProxyInstance()
        java.lang.Object#toString()
        java.lang.Class#newInstance()
        java.lang.reflect.Array#newInstance()
        java.lang.reflect.Constructor#newInstance()
        java.lang.Boolean#valueOf(String)
        java.lang.Class#forName()
         */
        Proxy.newProxyInstance(JdkCode.class.getClassLoader(), null, new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                return null;
            }
        });
        try {
            Class<?> strInstance = Class.forName("java.lang.String");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // ---- 6、单例模式 -----
        // java.lang.Runtime#getRuntime()
        Runtime runtime = Runtime.getRuntime();
        int processors = runtime.availableProcessors();

        // ---- 7、原型模式 -----
        //java.lang.Object#clone()

        // ---- 8、策略模式 -----
        /*
        java.util.Comparator#compare()
        javax.servlet.http.HttpServlet
        javax.servlet.Filter#doFilter()
         */
        Comparator<Integer> integerComparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer integer, Integer t1) {
                return 0;
            }

            @Override
            public boolean equals(Object o) {
                return false;
            }
        };


        Arrays.copyOf(new int[]{}, 3);
        System.arraycopy(new Object(), 0, new Object(), 1, 2);
    }
}
