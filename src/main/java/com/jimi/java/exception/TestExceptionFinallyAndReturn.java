package com.jimi.java.exception;

/**
 * @author jimi
 * @description
 * @date 2016-01-28 14:26.
 */
public class TestExceptionFinallyAndReturn {

    private int deal1(){
        System.out.println("deal1");
        return 1;
    }
    private int deal2(){
        System.out.println("deal2");
        return 2;
    }

    private int excute(){
        try{
            return deal1();
        }finally {
            return deal2();
        }
    }
    public static void main(String[] args){
        /**
         * 例子的运行结果中可以发现，try中的return语句调用的函数先于
         * finally中调用的函数执行，也就是说return语句先执行，finally语句后执行，
         * 所以，返回的结果是2。Return并不是让函数马上返回，而是return语句执行后，
         * 将把返回结果放置进函数栈中，此时函数并不是马上返回，它要执行finally语句后才真正开始返回。
         */
        System.out.println("[main]result=" + new TestExceptionFinallyAndReturn().excute());
    }
}
