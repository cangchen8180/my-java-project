package com.jimi.java.test;

import java.util.Scanner;

/**
 * on 2017/4/10.
 */
public class CalcDiscount {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入会员积分：");
        double discount = 0;
        int jiFen = scanner.nextInt();

        if (jiFen < 2000) {
            discount = 0.9;
        } else if (jiFen >= 2000 && jiFen < 4000) {
            discount = 0.8;
        } else if (jiFen >= 4000 && jiFen < 8000) {
            discount = 0.7;
        } else if (jiFen >= 8000) {
            discount = 0.6;
        }

        System.out.println("该会员享受的折扣是：" + discount);
    }
}
