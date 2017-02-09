package com.jimi.java._interview.fundamental._1_double;

import java.math.BigDecimal;

/**
 * float和double型运算时精度的问题
 *
 * @author jimi
 * @version 2016-12-23 17:41.
 */
public class DoubleTest {

    public static void main(String[] args) {
        double t1 = 0.7d;
        double t2 = 0.1d;
        System.out.println("t1+t2 = " + (t1 + t2));

        double t3 = 0.8d;
        double t4 = 0.2d;
        System.out.println("t3 + t4 = " + (t3 + t4));

        double t5 = 0.42d;
        double t6 = 1d;
        System.out.println("t6 - t5 = " + (t6 - t5));

        BigDecimal bigDecimal1 = new BigDecimal(1.0);
        BigDecimal bigDecimal2 = new BigDecimal(0.42);
        System.out.println(bigDecimal1.subtract(bigDecimal2).doubleValue());

        BigDecimal bigDecimal3 = new BigDecimal(Double.toString(1.0));
        BigDecimal bigDecimal4 = new BigDecimal(Double.toString(0.42));
        System.out.println(bigDecimal3.subtract(bigDecimal4).doubleValue());
    }
}
