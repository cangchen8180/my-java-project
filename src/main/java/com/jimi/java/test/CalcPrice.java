package com.jimi.java.test;

import java.util.Scanner;

/**
 * on 2017/4/10.
 */
public class CalcPrice {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入是否是会员：是（y）/否（其他字符）");
        String isHuiYuan = scanner.next();
        System.out.println("请输入购物金额：");
        int price = scanner.nextInt();

        double realPrice = 0;
        if (!isHuiYuan.equals("y") && price >= 100) {
            realPrice = price * 0.9;
        } else if (isHuiYuan.equals("y") && price < 200) {
            realPrice = price * 0.8;
        } else if (isHuiYuan.equals("y") && price >= 200) {
            realPrice = price * 0.75;
        }

        System.out.println("实际支付：" + realPrice);
    }
}
