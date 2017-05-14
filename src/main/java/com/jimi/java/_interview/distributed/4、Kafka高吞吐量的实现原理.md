

## 面试中Kafka相关几个典型问题

### kafka吞吐量为什么这么大，如何做到的
系统的pagesize

sendfile技术

### kafka机器挂掉如何保证pagecache消息不丢
修改确认为-1，保证写入所有follower成功，即使master挂了，新的master也有挂掉master未持久化的数据。保证数据不丢失。

### kafka如何保存数据
分区文件夹 .index文件索引offset位置
前一个最大offset命名下一个.log段文件

### leader选举算法
isr（in—sync replica） 列表中选取

_Kafka的选举算法和zk的选举算法有何不同，为什么这种选择？_

### kafka consumer负载均衡算法
