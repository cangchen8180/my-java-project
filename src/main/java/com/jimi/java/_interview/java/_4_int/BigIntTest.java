package com.jimi.java._interview.java._4_int;

/**
 *
 *
 * @author jimi
 * @version 2017-01-25 11:56.
 */
public class BigIntTest {

    public static void main(String[] args) {

        ///////////解决两个int大数相加除2溢出的问题////////////////
        int a = Integer.MAX_VALUE;
        int b = Integer.MAX_VALUE;

        System.out.println("(a+b)/2 = " + (a + b) / 2);
        //有符号右移和直接除2，效果一样
        System.out.println("(a+b)>>1 = " + ((a + b)>>1));

        //想法，分开除，对于两个数都是奇数的情况，加1。
        System.out.println("(a/2 + b/2 + ((a&1)&(b&1))) = " + (a / 2 + b / 2 + ((a & 1) & (b & 1))));
        /*
        对于：>>> 无符号右移，忽略符号位，空位都以0补齐
                value >>> num     --   num 指定要移位值value 移动的位数。
         */
        System.out.println("(a+b)>>>1 = " + ((a + b)>>>1));


        ///////////快速幂////////////////
        //pow方法就是利用快速幂算法实现
        double pow = Math.pow(2, 10);
        System.out.println("pow = " + pow);

    }
}
