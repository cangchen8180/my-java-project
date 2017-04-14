package com.jimi.java.test;

/**
 * 计算周数
 *
 *  on 2017/4/5.
 */
public class CalculateWeek {

    public int calculateWeek(int days) {
        int week = days / 7;
        return week;
    }

    public int calculateLeftDay(int days) {
        int leftDay = days % 7;
        return leftDay;
    }

    public static void main(String[] args) {
        CalculateWeek calculateWeek = new CalculateWeek();
        int week = calculateWeek.calculateWeek(46);
        int leftDay = calculateWeek.calculateLeftDay(46);

        System.out.println("周数 = " + week + "， 剩余天数 = " + leftDay);

    }
}
