package com.jimi.java.test;

import java.util.Scanner;

/**
 *  on 2017/4/10.
 */
public class DayCounter {

    public static void main(String[] arguments){
        int yearIn=2017;
        int monthIn=2;
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入年份：");
        yearIn = scanner.nextInt();
        System.out.println("请输入月份：");
        monthIn = scanner.nextInt();

        System.out.println(yearIn+"年"+ monthIn +"月共有"
                +countDays(monthIn,yearIn)+"天");
    }

    public static int countDays(int month,int year)
    {
        int count=0;
        switch(month){
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                count=31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                count=30;
                break;
            case 2:

                /*
                闰年二月份29天
                平年二月份28天
                 */

                //普通年能被4整除的为闰年。（如2004年就是闰年,1901年不是闰年）
                if(year%4==0)
                    count=29;
                else
                    count=28;

                //世纪年能被400整除的是闰年。(如2000年是闰年，1900年不是闰年)
                if((year%100==0)&&(year%400!=0))
                    count=28;
        }
        return count;
    }
}
