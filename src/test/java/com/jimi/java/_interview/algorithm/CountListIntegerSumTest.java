package com.jimi.java._interview.algorithm;

import com.jimi.java._interview.algorithm._1_CountListIntegerSum.CountListIntegerSumForCountdownLatch;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jimi
 * @version 2016-12-27 00:11.
 */
public class CountListIntegerSumTest extends TestCase {

    @Test
    public void test(){
        int initCap = 100000;
        List<Integer> integerList = new ArrayList<>(initCap);
        for (int i = 0; i < initCap; i++) {
            integerList.add(i + 1);
        }

        CountListIntegerSumForCountdownLatch countListIntegerSum = new CountListIntegerSumForCountdownLatch();
        long sum = countListIntegerSum.countSum(integerList);
        System.out.println("sum = " + sum);
    }
}