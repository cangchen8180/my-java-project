## 解决两个int大数相加除2溢出的问题

首先，int型是有符号型。

解决：
法一：分开除，然后对两个数都是奇数的情况做加1。
法二：使用无符号右移运算，它计算时忽略符号位，右移高位补0。

```java
public class IntTest {

    public static void main(String[] args) {
        int a = Integer.MAX_VALUE;
        int b = Integer.MAX_VALUE;

        System.out.println("(a+b)/2 = " + (a + b) / 2);
        //有符号右移和直接除2，效果一样
        System.out.println("(a+b)>>1 = " + ((a + b)>>1));

        //想法，分开除，对于两个数都是奇数的情况，加1。
        System.out.println("(a/2 + b/2 + ((a&1)&(b&1))) = " + (a / 2 + b / 2 + ((a & 1) & (b & 1))));
        /*
        对于：>>> 无符号右移，忽略符号位，空位都以0补齐
                value >>> num     --   num 指定要移位值value 移动的位数。
         */
        System.out.println("(a+b)>>>1 = " + ((a + b)>>>1));
    }
}
```

### JAVA中位运算符

```
~ 按位非（NOT）（一元运算） 
& 按位与（AND） 
| 按位或（OR） 
^ 按位异或（XOR） 
>> 右移 
>>> 右移，左边空出的位以0填充 
<< 左移 
&= 按位与赋值 
|= 按位或赋值 
^= 按位异或赋值 
>>= 右移赋值 
>>>= 右移赋值，左边空出的位以0填充 
<<= 左移赋值
```