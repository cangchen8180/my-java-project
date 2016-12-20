package com.jimi.java.fundamental;

/**
 * @author jimi
 * @description
 * @date 2016-02-03 16:53.
 */
public class TestClassDeliver {

    private static class TestStringBuffer{
        private String str;

        public TestStringBuffer(){}

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }
    }

    public static void changeTestStringBuffer(TestStringBuffer buffer){
        buffer.setStr("changeTestStringBuffer set str = '888'");
    }

    public static void main(String[] args){
        TestStringBuffer testStringBuffer = new TestStringBuffer();
        testStringBuffer.setStr("main set str='123'");
        System.out.println("[testStringBuffer]=" + testStringBuffer.getStr());
        changeTestStringBuffer(testStringBuffer);
        System.out.println("[testStringBuffer]=" + testStringBuffer.getStr());
    }
}
