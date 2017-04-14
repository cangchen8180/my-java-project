package com.jimi.java.test;

/**
 * 根据半径求园的面积
 *
 *  on 2017/4/5.
 */
public class Circle {

    final double PI = 3.14159;

    public double area(double radius) {

        return PI * radius * radius;
    }

    public static void main(String[] args) {
        Circle circle = new Circle();
        double area = circle.area(1.5);

        System.out.println("面积 = " + area);
    }
}
