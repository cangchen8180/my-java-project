# java
1、hashMap hashTable区别？ 强一致和弱一致问题？
答：[1、Java中重要集合类的对比.md]()
2、hashMap哈希算法是怎样的，如何做到均匀hash的，做了哪些优化？hashMap中hash算法怎样的，什么情况下效率最低、最快？ 
答：[2、HashMap实现详解.md]()
3、treeMap的红黑树
-- 有关红黑树的面试题是怎样的？？？
4、jdk中常用的设计模式，及应用
答：[JDK中常用设计模式的应用.md]()
5、concurrentHashMap的锁分段技术？读时为什么不加锁？为什么是弱一致？
答：[2、HashSet、HashMap、HashTable、TreeSet和TreeMap区别.md]()

6、string的hashcode算法
7、Collections.sort()如何排序的

# jvm
1、jvm运行时问题
2、性能调优
3、堆 栈 何时发生OutOfMemory异常？


# mysql
1、死锁的条件？如何避免死锁？
答：[3、死锁及解决办法.md]()
2、mysql针对大数据量时的性能优化？
答：[2、数据库性能优化.md]()

3、事务 隔离级别
4、倒排索引？索引的优点 缺点


# spring
1、spring启动过程？
1、spring实现原理，用什么数据结构管理bean依赖的？
2、ioc和aop如何实现的？


# 算法
1、快排 冒泡 归并 各自的高级实现，时间复杂度
2、二叉树反转 b树 b+树区别？数据库为什么用btree不用其他树？
3、二分查找
4、lru代码实现
5、快速幂 取模算法
6、如何最少时间复杂度求一个大数组中的第k大的数


# 分布式
1、zk的分布式锁, 和redis的分布式锁的区别？？
2、kafka负载均衡的实现，选举leader的实现
3、分布式事务如何实现的。
4、happens-before机制是什么鬼？

5、分布式session的四种实现方式？

web
1、长连接和短连接区别，如何保证长连接？
2、session的实现原理，与cookie的关系？


# 线程
1、thread 多次start 会怎样？
答：第二次调start()方法时，就会抛IllegalThreadStateException运行时异常。但第一次start的线程还会执行完。