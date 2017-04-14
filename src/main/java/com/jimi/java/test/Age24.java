package com.jimi.java.test;

import java.util.Scanner;

/**
 * on 2017/4/10.
 */
public class Age24 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入一个年龄值：");
        int age = scanner.nextInt();
        String is24;
        if (age == 24) {
            is24 = "yes";
        }else {
            is24 = "no";
        }
        System.out.println("输入年龄等于24：" + is24);
    }
}
