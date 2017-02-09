package com.jimi.java._interview.java._0_string;

import redis.clients.jedis.Jedis;

/**
 * @author jimi
 * @version 2017-02-09 19:29.
 */
public class StringTest {
    public static void main(String[] args) {
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
        String set1 = jedis.set(idStr, idStr);
        String set = jedis.set(bytes, bytes);
        System.out.println("idStr.getBytes() = " + bytes);
        System.out.println("idStr.getBytes() = " + bytes.toString());
    }
}
