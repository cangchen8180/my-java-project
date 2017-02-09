package com.jimi.java._interview.java._5_thread;

/**
 * @author jimi
 * @version 2017-02-09 11:03.
 */
public class ThreadTest {
    public static void main(String[] args) {

        /////////////////// thread 多次start 会怎样？？？ //////////////////
        //答：第二次调start()方法时，就会抛IllegalThreadStateException运行时异常。但第一次start的线程还会执行完。
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    try {
                        Thread.sleep(2000);
                        System.out.println(i + "-running...");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        System.out.println("start_1");
        thread.start();
        System.out.println("start_2");
        thread.start();
        System.out.println("start_3");
    }
}
