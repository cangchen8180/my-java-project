package com.jimi.java._interview.fundamental._0_string;

import redis.clients.jedis.Jedis;

/**
 * @author jimi
 * @version 2017-02-09 19:29.
 */
public class StringTest {
    public static void main(String[] args) {

        /////////////////// 字符串转字节 ///////////////////////
        Integer id = 20;
        System.out.println("id.byteValue() = " + id.byteValue());

        String idStr = "20";
        byte[] bytes = idStr.getBytes();

        /*
        英文、数字都占一个字节
         */
        String key = "keys20";
        byte[] bytes1 = key.getBytes();

        /*
        utf-8编码下，

        占2个字节的：〇

        占3个字节的：基本等同于GBK，含21000多个汉字
        占4个字节的：中日韩超大字符集里面的汉字，有5万多个

        数字占1个字节

        英文字母占1个字节
         */
        String cnKey = "中国";
        byte[] bytes2 = cnKey.getBytes();
        Jedis jedis = new Jedis("127.0.0.1", 6379);

        /*
        set的字符串型key-value也是转成字节数组存储
        且时间复杂度为 O(1)，
        如何做到的？
        答：参 http://zhangtielei.com/posts/blog-redis-sds.html
         */
        /*String set1 = jedis.set(idStr, idStr);
        String set = jedis.set(bytes, bytes);
        System.out.println("idStr.getBytes() = " + bytes);
        System.out.println("idStr.getBytes() = " + bytes.toString());*/


        ///////////////////// String 字符串取等 ////////////////////
        String str = "hcTest";
        System.out.println("str.hashCode() = " + str.hashCode());

        String s1 = "abc";
        String s2 = "a";
        String s3 = "bc";
        String s4 = s2 + s3;
        //false，因为s2+s3实际上是使用StringBuilder.append来完成，会生成不同的对象。
        System.out.println(s1 == s4);

        String s11 = "abc";
        final String s21 = "a";
        final String s31 = "bc";
        String s41 = s21 + s31;
        //true，因为final变量在编译后会直接替换成对应的值，所以实际上等于s4=”a”+”bc”，而这种情况下，编译器会直接合并为s4=”abc”，所以最终s1==s4。
        System.out.println(s11 == s41);

        String strHellAndO = new String("hell") + new String("o");
        String strIntern = strHellAndO.intern();
        String strHello = "hello";
        String strHello1 = "hell" + new String("o");

        //////////////////// 为什么是false???????? /////////////////////////
        System.out.println("strHello == strHello1 = " + (strHello == strHello1));
        System.out.println("strHello ==strHello1 = " + strHello == strHello1 ? "" : "1");

        System.out.println("strHellAndO == strIntern = " + (strHellAndO == strIntern));

        //////////////////// 为什么是true???????? /////////////////////////
        System.out.println("strHello == strHellAndO = " + (strHello == strHellAndO));
        //////////////////// 为什么是true???????? /////////////////////////
        System.out.println("strHello== strIntern = " + (strIntern == strHello));

        ///////////////////// String hashCode算法 ////////////////////
        System.out.println("\"ji\".hashCode() = " + "ji".hashCode());
        System.out.println("\"gi\".hashCode() = " + "gi".hashCode());
        System.out.println("\"jm\".hashCode() = " + "jm".hashCode());

        System.out.println("\"jim\".hashCode() = " + "jim".hashCode());
        System.out.println("\"jimi\".hashCode() = " + "jimi".hashCode());

    }
}
