# Redis cluster分区的原理


## 疑问
### 集群中master节点、slave节点维护的key和slot的关系？

### 既然键与slot的映射关系是固定的，client如何快速查询键的值？
client发送查询请求，如果redis节点不维护待查询key对应的slot，则给client 返回对应slot所在节点的IP和端口。

返回client的信息如下：

```
GET msg
-MOVED 254 127.0.0.1:6381
```

其中，`msg`是待查询数据的key；`254`是`msg`对应的slot编号；客户端想要的254槽由运行在IP为`127.0.0.1`，端口为`6381`的Master实例服务。

**_没有代理的Redis Cluster** 可能会导致客户端两次连接急群中的节点才能找到正确的服务，推荐客户端缓存连接，这样最坏的情况是两次往返通信。

> 那这种情况下，如何实现高效查询的？


### 如何实现重新分片，重新分片时如何在不影响服务的情况下，实现slot的平滑迁移？
两个master节点（A -> B）进行数据迁移时，A为进入migrating状态，B进入importing状态，迁移过程中的查询，如果A有，返回数据，否则move到B。

#### 当客户端请求的某个Key所属的槽处于 MIGRATING状态 的 A 时候，影响有下面几条：

- 如果Key存在则成功处理
- 如果`Key不存在`，则返回客户端`ASK命令`，仅当这次请求会转向另一个节点，并不会刷新客户端中node的映射关系，也就是说下次该客户端请求该Key的时候，还会选择MasterA节点
- 如果Key包含多个命令，如果都存在则成功处理，如果`都不存在`，则返回客户端`ASK命令`，如果`一部分存在`，则返回客户端`TRYAGAIN`（稍后重试），通知客户端稍后重试，这样当所有的Key都迁移完毕的时候客户端重试请求的时候回得到ASK，然后经过一次重定向就可以获取这批键。

#### 当客户端请求的某个Key所属的槽处于 IMPORTING状态 的 B 时候

本例中的IMPORTING状态是发生在MasterB节点中的一种槽的状态，预备将槽从MasterA节点迁移到MasterB节点的时候，槽的状态会首先变为IMPORTING。IMPORTING状态的槽对客户端的行为有下面一些影响：

`正常命令`会`被MOVED重定向`，如果是`ASKING命令`（该命令，说明这个请求是Master A转发过来的）则命令会`被执行`。这执行ASKING命令的key时：

- Key没有在老的节点已经被迁移到新的节点的情况，可以被顺利处理；
- _如果Key不存在，则新建_；
- 没有ASKING的请求和正常请求一样被MOVED，这保证客户端node映射关系出错的情况下不会发生写错；

## 参考

- [Redis Cluster分区实现原理](https://my.oschina.net/andylucc/blog/704440)