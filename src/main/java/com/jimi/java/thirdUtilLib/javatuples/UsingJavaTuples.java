package com.jimi.java.thirdUtilLib.javatuples;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.ArrayList;

/**
 * javaTuples 是一个很简单的 lib，
 * 它没有什么华丽的功能，就是提供了支持返回多个元素的一些类，每个类返回元素个数固定，但元素间的类型可以不同。
 *
 * Unit (1 element)
 * Pair<A,B> (2 elements)
 * Triplet<A,B,C> (3 elements)
 * Quartet<A,B,C,D> (4 elements)
 * Quintet<A,B,C,D,E> (5 elements)
 * Sextet<A,B,C,D,E,F> (6 elements)
 * Septet<A,B,C,D,E,F,G> (7 elements)
 * Octet<A,B,C,D,E,F,G,H> (8 elements)
 * Ennead<A,B,C,D,E,F,G,H,I> (9 elements)
 * Decade<A,B,C,D,E,F,G,H,I,J> (10 elements)
 *
 * @author jimi
 * @version 2017-01-19 20:53.
 */
public class UsingJavaTuples {

    public static void main(String[] args) {

        Pair<String, Integer> pair = Pair.with("jimi", 3);
        String value0 = pair.getValue0();
        Integer value1 = pair.getValue1();

        System.out.println("value0 = " + value0);
        System.out.println("value1 = " + value1);

        ArrayList<Integer> list = new ArrayList<>();
        list.add(21);
        list.add(32);
        Triplet<String, Integer, ArrayList<Integer>> triplet = Triplet.with("小明", 344, list);

        String value01 = triplet.getValue0();
        Integer value11 = triplet.getValue1();
        ArrayList<Integer> value2 = triplet.getValue2();

        System.out.println("value01 = " + value01);
        System.out.println("value11 = " + value11);
        System.out.println("value2 = " + value2.toString());
    }
}
