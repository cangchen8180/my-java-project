## zk的分布式锁, 和redis的分布式锁

### 区别
redis实现分布式锁，是设置一个key，同时设置一个超时时间。但这种方式不能重入，没有本地锁，并发性能交差，不能实现等待排序（可以通过redis的list实现,但缺点是list下每个子节点无超时时间. redis也无法进行模糊查询 key）。而zookeeper实现方式也会遇到性能瓶颈，适合小并发量场景（每个leader的写操作并发大约为每秒1万次，而一个集群只有leader进行写操作）。


### 基于zk实现分布式锁
zookeeper是由Yahoo开发，它的吞吐量标准已经达到大约每秒10000基于写操作的工作量。对于读操作的工作量来说，它的吞吐量标准还要高几倍。 

#### 思路
线程需要访问共享资源时，就在特定路径下，创建一个临时节点（EPHEMERAL_SEQUENTIAL）。

- 当最小节点不是自己的节点时，watcher排序后在其前一位的节点，关注它锁释放的操作;
- 当最小节点为当前线程节点时，说明该线程获得了锁。进程获得锁之后，就可以访问共享资源，访问完成后，删除该节点，锁由最新的最小节点获得。

#### 算法

- lock操作过程
    
    首先为一个lock场景，在zookeeper中指定对应的一个根节点，用于记录资源竞争的内容；
    
    每个lock创建后，会lazy在zookeeper中创建一个node节点，表明对应的资源竞争标识。 (小技巧：node节点为EPHEMERAL_SEQUENTIAL，自增长的临时节点)；
    
    进行lock操作时，获取对应lock根节点下的所有子节点，也即处于竞争中的资源标识；
    
    按照Fair（公平）竞争的原则，按照对应的自增内容做排序，取出编号最小的一个节点做为lock的owner，判断自己的节点id是否就为owner id，如果是则返回，lock成功。
    
    如果自己非owner id，按照排序的结果找到序号比自己前一位的id，关注它锁释放的操作(也就是exist watcher)，形成一个链式的触发过程；

- unlock操作过程：

    将自己id对应的节点删除即可，对应的下一个排队的节点就可以收到Watcher事件，从而被唤醒得到锁后退出；

- 其中的几个关键点：

    node节点选择为EPHEMERAL_SEQUENTIAL很重要。
    
    自增长的特性，可以方便构建一个基于Fair特性的锁，前一个节点唤醒后一个节点，形成一个链式的触发过程。可以有效的避免"惊群效应"(一个锁释放，所有等待的线程都被唤醒)，有针对性的唤醒，提升性能。
    
    选择一个EPHEMERAL临时节点的特性。因为和zookeeper交互是一个网络操作，不可控因素过多，比如网络断了，上一个节点释放锁的操作会失败。临时节点是和对应的session挂接的，session一旦超时或者异常退出其节点就会消失，类似于ReentrantLock中等待队列Thread的被中断处理。
    
    获取lock操作是一个阻塞的操作，而对应的Watcher是一个异步事件，所以需要使用互斥信号共享锁BooleanMutex进行通知，可以比较方便的解决锁重入的问题。(锁重入可以理解为多次读操作，锁释放为写抢占操作)

- 注意：
    
    使用EPHEMERAL会引出一个风险：在非正常情况下，网络延迟比较大会出现session timeout，zookeeper就会认为该client已关闭，从而销毁其id标示，竞争资源的下一个id就可以获取锁。这时可能会有两个process同时拿到锁在跑任务，所以设置好session timeout很重要。
    
    同样使用PERSISTENT同样会存在一个死锁的风险，进程异常退出后，对应的竞争资源id一直没有删除，下一个id一直无法获取到锁对象。

#### 代码

参考[ZooKeeper 分布式锁实现](https://my.oschina.net/xianggao/blog/532010)


### redis实现分布式锁
#### 思路

- 先定义好一个锁等待时间和锁超时时间，我们使用中分别设置了300ms和5min。
- 线程执行进方法后，设置锁到期时间为当前时间加5分钟，就是System.currentTimeMillis() + 1000*60*5 + 1
- 执行setnx，执行完返回1就是获取锁成功。返回0时，表示未获取到锁，执行下面一步
- 判断是否已经锁到期，currentValueStr和当前时间比较，如果未过期，锁等待时间减100ms，sleep一下继续重复这个动作。如果已经到期，执行下面一步。
- 通过getSet把redis中的锁超时时间设为自己的时间（因此自己要获取锁），这个方法是同步的。保证只能有个线程能拿到锁。执行这步返回的值是旧值，
- 判断旧值和之前的锁超时的值是否一致。一致则获取锁成功。失败的话锁等待时间减1，sleep一下继续重复之前的逻辑。

#### 代码

```java
import java.util.List;
import java.util.UUID;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

/**
 * Jedis实现分布式锁
 * 
 * @author 三文鱼
 *
 */
public class DistributionLock {
    private final JedisPool jedisPool;

    public DistributionLock(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * 获取分布式锁
     * 
     * @param lockName
     *            竞争获取锁key
     * @param acquireTimeoutInMS
     *            获取锁超时时间
     * @param lockTimeoutInMS
     *            锁的超时时间
     * @return 获取锁标识
     */
    public String acquireLockWithTimeout(String lockName,
            long acquireTimeoutInMS, long lockTimeoutInMS) {
        Jedis conn = null;
        boolean broken = false;
        String retIdentifier = null;
        try {
            conn = jedisPool.getResource();
            String identifier = UUID.randomUUID().toString();
            String lockKey = "lock:" + lockName;
            int lockExpire = (int) (lockTimeoutInMS / 1000);

            long end = System.currentTimeMillis() + acquireTimeoutInMS;
            while (System.currentTimeMillis() < end) {
                if (conn.setnx(lockKey, identifier) == 1) {
                    conn.expire(lockKey, lockExpire);
                    retIdentifier = identifier;
                }
                if (conn.ttl(lockKey) == -1) {
                    conn.expire(lockKey, lockExpire);
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (JedisException je) {
            if (conn != null) {
                broken = true;
                jedisPool.returnBrokenResource(conn);
            }
        } finally {
            if (conn != null && !broken) {
                jedisPool.returnResource(conn);
            }
        }
        return retIdentifier;
    }

    /**
     * 释放锁
     * @param lockName 竞争获取锁key
     * @param identifier 释放锁标识
     * @return
     */
    public boolean releaseLock(String lockName, String identifier) {
        Jedis conn = null;
        boolean broken = false;
        String lockKey = "lock:" + lockName;
        boolean retFlag = false;
        try {
            conn = jedisPool.getResource();
            while (true) {
                conn.watch(lockKey);
                if (identifier.equals(conn.get(lockKey))) {
                    Transaction trans = conn.multi();
                    trans.del(lockKey);
                    List<Object> results = trans.exec();
                    if (results == null) {
                        continue;
                    }
                    retFlag = true;
                }
                conn.unwatch();
                break;
            }

        } catch (JedisException je) {
            if (conn != null) {
                broken = true;
                jedisPool.returnBrokenResource(conn);
            }
        } finally {
            if (conn != null && !broken) {
                jedisPool.returnResource(conn);
            }
        }
        return retFlag;
    }

}
```

#### 基于Redis的分布式可重入锁
实现参考[Java 实现基于Redis的分布式可重入锁](http://lixiaohui.iteye.com/blog/2328067)