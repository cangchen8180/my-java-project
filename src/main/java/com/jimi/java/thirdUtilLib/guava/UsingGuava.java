package com.jimi.java.thirdUtilLib.guava;

import com.google.common.base.*;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Ordering;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.math.RandomUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @author jimi
 * @version 2017-01-14 12:25.
 */
public class UsingGuava {

    public static void main(String[] args) throws InterruptedException {

        //--------------- 前置条件-使用和避免null ------------
        //赋值
//        Optional<StudentTest> student  = Optional.of(new StudentTest());
        /*Optional<StudentTest> student  = Optional.fromNullable(null);
        //判断对象是否是有效对象
        if (student.isPresent()) {
            //如果为空，则报IllegalStateException异常
            StudentTest studentTest = student.get();
            //如果为空，返回参数对象
            StudentTest or = student.or(new StudentTest());
        }*/
        /*
        注：Optional<T>的最常用价值在于，
        例如，假设一个方法返回某一个数据类型，调用这个方法的代码来根据这个方法的返回值来做下一步的动作，
        若该方法可以返回一个null值表示成功，或者表示失败，在这里看来都是意义含糊的，所以使用Optional<T>作为返回值，
        则后续代码可以通过isPresent()来判断是否返回了期望的值（原本期望返回null或者返回不为null，其意义不清晰），
        并且可以使用get()来获得实际的返回值。
         */

        //--------------- 前置条件-简化方法参数判断 ------------
        /*List<Integer> listForTest = new ArrayList<>(20);
        //用于判断某个参数是否合法
        int id = 0;
        Preconditions.checkArgument(id > 0, "%s 必须大于 0", id);
        //用于判断某个对象的某种状态
        int p1 = 3;
        Preconditions.checkState(listForTest.contains(p1), "%s 不包含 %s", "listForTest", p1);*/


        //--------------- Object常用方法封装 ------------
        String a = "a";
        String a2 = "a";
        System.out.println("(a == \"a\") = " + (a == "a"));
        System.out.println("(\"a\" == \"a\") = " + ("a" == "a"));
        System.out.println("(a == a2) = " + (a == a2));
        System.out.println("(null == null) = " + (null == null));
        System.out.println("Objects.equal(null, \"a\") = " + Objects.equal(null, "a"));


        //--------------- 排序工具 ------------
        Ordering<Integer> orderUtil = new Ordering<Integer>() {
            @Override
            public int compare(Integer left, Integer right) {
                Preconditions.checkNotNull(left, "left 不能为空");
                Preconditions.checkNotNull(right, "right 不能为空");
                return left.compareTo(right);
            }
        };
        List<Integer> intList = new ArrayList<>(10);
        intList.add(2);
        intList.add(20);
        intList.add(3);
        intList.add(8);
        List<Integer> topK = orderUtil.greatestOf(intList, 3);
        System.out.println("topK = " + topK);
        /*
        Q：Ordering的greatestOf()方法怎么找出最大/小的k个元素？
        答：用到一个特殊算法。但如果要找的元素个数超过总数一半，则不用算法，而是直接排序截取，这样更快。算法适用于k远小n的情况。
        算法流程：
            保持一个2k大小的buffer；每次满了时，清掉较大的一半，剩下k位。
            *剪枝优化：维护一个第k小的阈值，大于它的可以直接忽略了
            *清掉一半的方法：快速选择。
                定一个标志位，比它小的挪到左边，比它大的挪到右边
        复杂度：
            时间O(n + k log k) 存储O(k)
         */

        //--------------- 异常捕获 ------------
        /*try {
            int i = 2 / 1;
            throw new RuntimeException("runtime exception");
        } catch (Exception e) {
            e.printStackTrace();

            //guava三个好用的工具方法
            *//*String stackTraceAsString = Throwables.getStackTraceAsString(e);
            System.out.println("stackTraceAsString = " + stackTraceAsString);

            List<Throwable> causalChain = Throwables.getCausalChain(e);

            Throwable rootCause = Throwables.getRootCause(e);*//*
        }
        System.out.println("i end = " + "i end");*/

        //--------------- 数组转list注意问题 ------------
        Integer[] ints = new Integer[]{1, 2, 4};
//        int[] ints = new int[5];
        ints[0] = 2;
        ints[2] = 3;
        /*
        asList使用注意：
        1、避免使用基本数据类型数组转换为列表。
        2、asList 产生的列表不可操作
         */
        List<Integer> integers = Arrays.asList(ints);
//        integers.add(5);

        //--------------- String工具类 ------------
        //guava连接器
        System.out.println("ints = " + Joiner.on(", ").join(integers));

        //guava分割器
        Iterable<String> split = Splitter.on(",").trimResults().omitEmptyStrings().split("3, 4, , 66,");

        //字符串处理，只保留数字
        String sequence = "23你00001好4";
        // --- guava 提供 ---
        String digit = CharMatcher.DIGIT.retainFrom(sequence);
        System.out.println("digit = " + digit);
        // --- 自己实现 ---
        UsingGuava useGuava = new UsingGuava();
        String digit1 = useGuava.getDigit(sequence);
        System.out.println("digit1 = " + digit1);

        //用*号替换所有数字
        String s = CharMatcher.DIGIT.replaceFrom(sequence, "*");
        System.out.println("s = " + s);

        ConcurrentMap<String, String> map = new MapMaker()
                /*.concurrencyLevel(8)
                .softKeys()
                .weakValues()
                .maximumSize(100)*/
                .expireAfterWrite(5, TimeUnit.SECONDS)
                .makeComputingMap(new Function<String, String>() {
                    @Override
                    public String apply(String input) {
                        return RandomUtils.nextInt()+"";
                    }
                });

        for (int i = 0; i < 5; ++i) {
            String value = map.get(i + "");
            System.out.println("key=" + i + ", value = " + value);
            Thread.sleep(2000);
        }

        System.out.println("map = " + map.toString());


    }

    /**
     * 筛选出字符串中的数字
     * <p/>
     * 注意：
     * 1、字符串无能改变顺序
     * 2、不能额外增加数组
     *
     * @param str
     * @return
     */
    public String getDigit(String str) {
        char[] chars = str.toCharArray();
        int length = chars.length;

        /*
        *** todo: 忘记考虑1： ***
        先检测一遍，看是否包含数字，否则就不执行下面算法，因为执行也是白浪费时间，时间复杂度o(n)，此处并不影响o(n2)的数量级
         */
        //这部分参CharMatcher.javaDigit().retainFrom()源码。
        ;;

        /*
        *** 错误想法1： ***
        本意单独考虑字符串为一个字符情况，但其实这种不属特殊情况，没必要单独考虑。
         */
        /*if (length == 1) {
            if (isDigit(chars[0])){
                return new String(chars);
            }else {
                return new String();
            }
        }*/

        /*
        int pos = length-1;   //扫描位置从后向前，因为从前往后时，在循环中不好在前移时判断后面数据中数字的位置
        int spread = 0;
        for (int i = 0; i < pos; i++) {
            if (!isDigit(chars[i])) {
                spread = i;
                break;
            }
        }
        for (int i = pos; i > spread; ) {

            if (isDigit(chars[i])) {
                char key = chars[i];
                for (int j = pos; j >= spread; j--) {
                    chars[j] = chars[j - 1];
                }
                chars[spread++] = key;
            }else {
                pos--;
            }
            i = pos;
        }*/


        //////////////////////////方法二////////////////////////
        /*
        因为重点是获取数字，所以非数字字符就是无关紧要的，所以不要纠结在o(n)情况下，将两者完整的左右分开。
         */
        int pos = 0;
        for (int i = 0; i < length; i++) {
            if (!isDigit(chars[i])) {
                pos = i;
                break;
            }
        }
        int spread = 1;

        // This unusual loop comes from extensive benchmarking
        //OUT:为循环标签，表示为某个循环定义一个名字，break时跳出规定的循环。
        OUT:
        while (true) {
            pos++;
            while (true) {
                if (pos == chars.length) {
                    break OUT;
                }
                if (!isDigit(chars[pos])) {
                    break;
                }
                chars[pos - spread] = chars[pos];
                pos++;
            }
            spread++;
        }
        System.out.println("chars = " + ToStringBuilder.reflectionToString(chars));
        String s = new String(chars, 0, pos - spread);
        return s;
    }

    /**
     * 判断字符串是否是数字
     *
     * @param c
     * @return
     */
    public boolean isDigit(char c) {
        boolean digit = Character.isDigit(c);
        return digit;
    }

    static class StudentTest {
        private int id;
        private String name;
        private String sex;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }
}
