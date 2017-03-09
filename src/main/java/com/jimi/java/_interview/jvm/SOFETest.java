package com.jimi.java._interview.jvm;

/**
 * 发生栈溢出{@link StackOverflowError}的实例
 *
 * Created by lagou on 2017/3/7.
 */
public class SOFETest {

    /**
     * 无限递归方法
     */
    public void stackOverFlowMethod() {
        stackOverFlowMethod();
    }

    public static void main(String[] args) {
        SOFETest sofeTest = new SOFETest();
        sofeTest.stackOverFlowMethod();
    }
}
