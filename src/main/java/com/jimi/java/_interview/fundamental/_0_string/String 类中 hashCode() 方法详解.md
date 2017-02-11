## String 类中 hashCode() 方法详解

### hash值
#### 含义
Hash，一般翻译做“散列”，也有直接音译为“哈希”的，就是把任意长度的输入（又叫做预映射， pre-image），通过散列算法，变换成固定长度的输出，该输出就是散列值。这种转换是一种压缩映射，也就是，散列值的空间通常远小于输入的空间，不同的输入可能会散列成相同的输出，所以不可能从散列值来唯一的确定输入值。简单的说就是一种将任意长度的消息压缩到某一固定长度的消息摘要的函数。

#### 用途
hash 值主要是用来在散列存储结构中确定对象的存储地址的，提高对象的查询效率，如HashMap、HashTable等；

但是需要注意：

- 如果两个对象相同，那么这两个对象的 hash 值一定相等；
- 如果要重写对象的 equals 方法，那么尽量重写对象的 hashCode 方法；
- 两个对象的 hash 值相等，并不一定表示两个对象相同。

### String中hashCode算法
在进行 hash 计算的时候，我们希望尽量减小生产重复 hash 值的概率，使得数据更离散一些，如果重复 hash 值太多，散列存储结构中同一 hash 值映射的对象也会很多，导致降低查询效率。而且 equals() 计算的准确性也会降低。

#### 代码

```java
public int hashCode() {
        int h = this.hash;
        if(h == 0 && this.value.length > 0) {
            char[] val = this.value;

            for(int i = 0; i < this.value.length; ++i) {
                h = 31 * h + val[i];
            }

            this.hash = h;
        }

        return h;
    }
```

**实例**

```java
///////////////////// String hashCode算法 ////////////////////
System.out.println("\"ji\".hashCode() = " + "ji".hashCode());
System.out.println("\"gi\".hashCode() = " + "gi".hashCode());
System.out.println("\"jm\".hashCode() = " + "jm".hashCode());

System.out.println("\"jim\".hashCode() = " + "jim".hashCode());
System.out.println("\"jimi\".hashCode() = " + "jimi".hashCode());
```

**结果：**

```java
"ji".hashCode() = 3391
"gi".hashCode() = 3298
"jm".hashCode() = 3395
"jim".hashCode() = 105230
"jimi".hashCode() = 3262235
```

#### 为什么hashCode方法是这样的？

##### Q1：计算是为什么要判断是否 h==0？
答：是用于判断是否计算过hash。
因为h是一个int类型的值，默认值为0，因此0可以表示可能未执行过hash计算，但不能表示一定未执行过hash计算，原因是我们现在还不确定hash计算后是否会产生0值； 
那么执行hash计算后，会不会产生值为0的hash呢？根据hash的计算逻辑，当val[0]=0时，根据公式h=31*h+val[i];进行计算，h的值等于0。
val[0]=0怎么解释呢？查看ASCII表发现，null的ASCII值为0。显然val[0]中永远不可能存放null，因此hash计算后不会产生0值，h==0可以作为是否进行过hash计算的判定条件。

#### Q2：为什么累加时，要加 31*h ？
答：可以看作是一种权重的算法，在前面的字符的权重大。就是前缀相同的字符串的hash值都落在邻近的区间。

优点：

1. 可以节省内存，因为hash值在相邻，这样hash的数组可以比较小。比如当用HashMap，以String为key时。
2. hash值相邻，如果存放在容器，比好HashSet，HashMap中时，实际存放的内存的位置也相邻，则存取的效率也高。（程序局部性原理）

#### Q3：那为什么用31，不用32/17等？
答：
为什么不用32，而用31？
因为31是素数，与素数乘出来的值更容易唯一，也就是hash值重复的概率更小。

为什么不用17等其他素数，而用31？
因为31的二进制全是1，计算机运算时可以转换成 (x<<5)-1 。
可以提高乘法效率，也不易产生内存溢出问题。

PS：乘法、除法是很耗性能，因为计算机运算时要转成n个加/减运算。位运算效率更好，这也是为什么HashMap的hash也采用位运算计算hash值。

### 附加
#### 素数
素数又称质数：指在一个大于1的自然数中，除了1和此整数自身外，没法被其他自然数整除的数。

> 算法，就是存的快，查的快。
  如何存的快？
  1、减少某些重复计算，利用空间保存临时值。
  2、减少耗性能的计算，尽量利用位运算这种。[参考HashMap的hash算法]()
  如何取的快？
  1、尽量通过hash方式/重hash等方式，转换值或index为指定值，可以在o(1)内定位到值的内存地址。
  2、尽量让相同或相似数据，存在一起，操作起来效率更高。



