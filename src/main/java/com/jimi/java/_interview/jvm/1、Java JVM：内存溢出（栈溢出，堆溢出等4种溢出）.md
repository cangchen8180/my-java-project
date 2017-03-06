## Java JVM：内存溢出（栈溢出，堆溢出等4种溢出）

### 栈溢出(StackOverflowError)

栈溢出抛出StackOverflowError错误，也属于内存溢出， 出现此种情况是`因为方法运行的时候栈的深度超过了虚拟机容许的最大深度所致`。 出现这种情况，一般情况下是程序错误所致的，比如写了一个死递归，就有可能造成此种情况。

实例代码如下，

```java
package com.jimi.java._interview.jvm;

/**
 * Created by lagou on 2017/3/7.
 */
public class SOFETest {

    /**
     * 无限递归方法
     */
    public void stackOverFlowMethod() {
        stackOverFlowMethod();
    }

    public static void main(String[] args) {
        SOFETest sofeTest = new SOFETest();
        sofeTest.stackOverFlowMethod();
    }
}
```

异常

```java
Exception in thread "main" java.lang.StackOverflowError
	at com.jimi.java._interview.jvm.SOFETest.stackOverFlowMethod(SOFETest.java:9)
	at com.jimi.java._interview.jvm.SOFETest.stackOverFlowMethod(SOFETest.java:9)
	at com.jimi.java._interview.jvm.SOFETest.stackOverFlowMethod(SOFETest.java:9)
	...
	at com.jimi.java._interview.jvm.SOFETest.stackOverFlowMethod(SOFETest.java:9)

Process finished with exit code 1
```

注：出现StackOverflowError异常时，有错误堆栈信息可以阅读，比较容易找到问题的所在。

**如果使用虚拟机默认参数，栈深度在大多数情况下达到1000-2000完全没有问题，对于正常的方法调用（包括递归），这个深度应该完全够用了。但是，如果是建立过多线程导致的OutOfMemoryError异常，在不能减少线程数或者更换64位虚拟机的情况下，就只能通过减少最大堆和减少栈容量来换取更多的线程。**

### 堆溢出(OutOfMemoryError:java heap space)
Java堆内存的OOM是实际应用中最常见的内存溢出异常情况。出现Java堆内存溢出时，异常堆栈信息“java.lang.OutOfMemoryError”会跟着进一步提示“Java Heap space”。

要解决这个区域的异常，一般是首先通过内存映像分析工具（如Eclipse Memory Analyzer）对dump出来的堆转储快照进行分析，重点是确认内存中的对象是否是必要的，也就是要先分清楚到底是出现了内存泄漏（Memory Leak）还是内存溢出（Memory Overflow）。

- 内存泄漏

    可进一步用通过工具查看泄漏对象到GC Roots的引用链。就能找到泄漏对象是通过怎样的路径与GC Roots相关联并导致垃圾收集器无法自动回收它们的。掌握了泄漏对象的类型信息，以及GC Roots引用链的信息，就可以比较准确地定位出泄漏代码的位置。

- 内存溢出

    检查虚拟机的对参数（-Xmx和-Xms），与机器物理内存对比看是否还可以调大，从代码上检查是否存在某些对象生命周期过长、持有状态时间过长的情况，尝试减少程序运行期的内存消耗。

实例代码如下，

```java
import java.util.*;
import java.lang.*;
public class OOMTest{
        public static void main(String... args){
                List<byte[]> buffer = new ArrayList<byte[]>();
                //申请10M堆空间
                buffer.add(new byte[10*1024*1024]);
        }
}
```

通过如下的命令运行上面的代码，

```
java -verbose:gc -Xmn10M -Xms20M -Xmx20M -XX:+PrintGCOOMTest
```

执行结果，

```
[GC 1180K->366K(19456K), 0.0037311 secs]
[Full GC 366K->330K(19456K), 0.0098740 secs]
[Full GC 330K->292K(19456K), 0.0090244 secs]
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
        at OOMTest.main(OOMTest.java:7)
 ```

### 关键点
>1、堆内存明明是20M，为什么会发生内存溢出？

答：-Xms和-Xmx是设置初始堆和最大堆的大小都是20M，-Xmn是设置年轻代大小为10M。
而JVM堆内存大小=年轻代+年老代大小+持久代大小。所以上述命令下，年老代内存不到10M。

jvm参数作用，参考[2、jvm参数说明及调优.md](2、jvm参数说明及调优.md)。

>2、通过上面的实验其实也从侧面验证了一个结论

当对象大于新生代剩余内存的时候，将直接放入老年代，当老年代剩余内存还是无法放下的时候，触发垃圾收集，收集后还是不能放下就会抛出内存溢出异常了。

jvm垃圾回收算法，参考[3、jvm内存模型及垃圾回收算法.md](3、jvm内存模型及垃圾回收算法.md)

### 持久代溢出(OutOfMemoryError: PermGen space)
Hotspot jvm通过持久代实现了Java虚拟机规范中的方法区，而运行时的常量池就是保存在方法区中的，因此持久带溢出有可能是运行时常量池溢出，也有可能是方法区中保存的class对象没有被及时回收掉或者class信息占用的内存超过了我们配置。 当持久带溢出的时候抛出java.lang.OutOfMemoryError: PermGen space。

可能在如下几种场景下出现，

1. 使用一些应用服务器的热部署的时候，我们就会遇到热部署几次以后发现内存溢出了，这种情况就是因为每次热部署的后，原来的class没有被卸载掉。

2. 如果应用程序本身比较大，涉及的类库比较多，但是我们分配给持久带的内存（通过-XX:PermSize和-XX:MaxPermSize来设置）比较小的时候也可能出现此种问题。

3. 一些第三方框架，比如spring,hibernate都通过字节码生成技术（比如CGLib）来实现一些增强的功能，这种情况可能需要更大的方法区来存储动态生成的Class文件。

Java中字符串常量是放在常量池中的，而String.intern()这个方法运行的时候，会检查常量池中是否存和本字符串相等的对象，如果存在直接返回对常量池中对象的引用，不存在的话，先把此字符串加入常量池，然后再返回字符串的引用。那么我们就可以通过String.intern方法来模拟一下运行时常量区的溢出。

实例代码如下，

```java
import java.util.*;
import java.lang.*;
public class OOMTest{
        public static void main(String... args){
                List<String> list = new ArrayList<String>();
                while(true){
                      list.add(UUID.randomUUID().toString().intern());
                }
        }
}
```

通过如下的命令运行上面代码，

```
java -verbose:gc -Xmn5M -Xms10M -Xmx10M -XX:MaxPermSize=1M -XX:+PrintGC OOMTest
```

运行结果如下，

```
Exception in thread "main" java.lang.OutOfMemoryError: PermGen space
        at java.lang.String.intern(Native Method)
        at OOMTest.main(OOMTest.java:8)
```

### OutOfMemoryError:unable to create native thread
java.lang.OutOfMemoryError:unable to create natvie thread这种错误，出现这种情况的时候，一般是下面两种情况导致的：

1. 程序创建的线程数超过了操作系统的限制。对于Linux系统，我们可以通过ulimit -u来查看此限制。

2. 给虚拟机分配的内存过大，导致创建线程的时候需要的native内存太少。

操作系统对每个进程的内存是有限制的，我们启动Jvm,相当于启动了一个进程，假如我们一个进程占用了4G的内存，那么通过下面的公式计算出来的剩余内存就是建立线程栈的时候可以用的内存。

 > 线程栈总可用内存=4G-（-Xmx的值）- （-XX:MaxPermSize的值）- 程序计数器占用的内存

 通过上面的公式我们可以看出，-Xmx 和 MaxPermSize的值越大，那么留给线程栈可用的空间就越小，在-Xss参数配置的栈容量不变的情况下，可以创建的线程数也就越小。

 因此如果是因为这种情况导致的unable to create native thread,那么要么我们增大进程所占用的总内存，或者减少-Xmx或者-Xss来达到创建更多线程的目的。

 参考[Java JVM：内存溢出（栈溢出，堆溢出等4种溢出）](http://www.tuicool.com/articles/EviAnmF)