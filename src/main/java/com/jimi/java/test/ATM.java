package com.jimi.java.test;

import java.util.Scanner;

/**
 * Created by lagou on 2017/4/10.
 */
public class ATM {
    public static void main(String[] args) {
        System.out.println("1.取款\t2.转账\t3.存款\t4.退出");
        String operate = null;
        int a;
        Scanner input=new Scanner(System.in);
        while (true) {
            System.out.println("请选择");
            a=input.nextInt();
            if (a != 4) {
                if (a == 1) {
                    operate = "取款";
                } else if (a == 2) {
                    operate = "转账";
                } else if (a == 3) {
                    operate = "存款";
                }
                System.out.println("当前操作是 " + operate);
            }else {
                System.out.println("程序退出 谢谢您的使用");
                return;
            }
        }
    }
}
