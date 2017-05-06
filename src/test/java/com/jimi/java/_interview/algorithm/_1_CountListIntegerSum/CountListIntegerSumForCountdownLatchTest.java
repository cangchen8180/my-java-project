package com.jimi.java._interview.algorithm._1_CountListIntegerSum;

import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 发现bug后，就编写响应的单元测试，防止bug出现漏网之虫
 * Created by Jimi on 2017/5/4.
 */
public class CountListIntegerSumForCountdownLatchTest {

    private CountListIntegerSumForCountdownLatch countListIntegerSum;
    private List<Integer> integerList;

    @Before
    public void setUp() throws Exception {
        int initCap = 1000;
        integerList = new ArrayList<>(initCap);
        for (int i = 0; i < initCap; i++) {
            integerList.add(i + 1);
        }

        countListIntegerSum = new CountListIntegerSumForCountdownLatch();

    }

    @After
    public void tearDown() throws Exception {
        integerList = null;
        countListIntegerSum = null;
    }

    @org.junit.Test
    public void countSum() throws Exception {
        long sum = countListIntegerSum.countSum(integerList);
        System.out.println("sum = " + sum);

        assert (sum == 500500);
        // or
        assertEquals(sum, 500500);
        assertEquals("测试结果是否正确", sum, 500500);

    }

}