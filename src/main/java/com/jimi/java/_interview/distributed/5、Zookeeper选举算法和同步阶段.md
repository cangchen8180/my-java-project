## 面试中ZK相关几个典型问题

### zk的leader选举算法
basic paxos，fast paxos。默认使用后者。

### zk临时节点如何编号

### 写请求出来过程
server先发给leader，leader再去分发个Server，只要多数写成功则leader返回成功。

### Server不同类型的作用
Server数为奇数个，如3个Server时，最多容忍挂1个。四个Server时也是最多容忍挂一个。这是有paxos选举算法决定的。

### zk如何实现分布式锁，和redis分布锁有什么区别？
参考 [1、zk和redis的分布式锁的对比]()