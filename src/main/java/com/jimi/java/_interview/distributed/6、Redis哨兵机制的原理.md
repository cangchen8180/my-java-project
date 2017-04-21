# Redis哨兵机制的原理


# 疑问

## sentinel（哨兵）实例如何维护sentinel实例、master、slave信息？
每个sentinel实例都会保存其他sentinel实例的列表以及现存的master/slaves列表，各自的列表中不会有重复的信息(不可能出现多个tcp连接)。

sentinel与master的连接中的通信主要是基于`pub/sub`来发布和接收信息。同时，sentinel实例也是"订阅"此主题，以获得其他sentinel实例的信息。

发布的主题名称为"`__sentinel__:hello`"；该主题发布的信息有sentinel实例的信息，slave的信息等。

### 信息同步
#### sentinel实例的信息同步
环境首次构建时,在默认master存活的情况下，所有的sentinel实例可以通过pub/sub即可获得所有的sentinel信息，此后每个sentinel实例即可以根据+sentinel信息中的"ip+port"和其他sentinel逐个建立tcp连接即可。

发布的信息内容包括当前sentinel实例的侦听端口：
```
+sentinel sentinel 127.0.0.1:26579 127.0.0.1 26579 ....
```

#### 已有slave信息同步
根据上文，我们知道在master有效的情况下，即可通过"INFO"指令获得当前master中已有的slave列表。

#### 新增salve信息同步
此后任何slave加入集群，`master`都会向"`主题中`"发布

```
+slave 127.0.0.1:6579 ..
```

此时，那么所有的sentinel也将立即获得slave信息，并和slave建立TCP链接并通过PING命令周期性（1次/秒）发送INFO命令检测其存活性。

#### sentinel实例自身信息间歇性发布
**每个sentinel实例均会间歇性(5秒)向"__sentinel__:hello"主题中发布自己的ip+port，** 目的就是让后续加入集群的sentinel实例也能或得到自己的信息。

### 节点信息存储
对于`sentinel`将使用`ip+port`做唯一性标记，对于`master/slaver`将使用`runid`做唯一性标记,其中redis-server的 _runid在每次启动时都不同_。


## 哨兵机制中master挂了，sentinel实例如何failover？
每个sentinel实例都会保存其他sentinel实例的列表以及现存的master/slaves列表，各自的列表中不会有重复的信息(不可能出现多个tcp连接)。

然后，每个sentinel实例都会和其他所有sentinel实例和master、slave建立TCP连接，用于定时检测集群中节点情况。
在sentinel之间建立连接之前,sentinel将会尽力和配置文件中指定的master建立连接。

当检测到Master异常，则进行失效判定，在所有sentinel实例对master节点失效达成一致（也就是，master是`ODOWN`状态）后，进行leader选举，最后进行failover。

leader选举和failover，参考[Redis Sentinel：集群Failover解决方案](http://shift-alt-ctrl.iteye.com/blog/1884370)


## 附录
### Master失效状态
在失效判定过程中，master可能有两种状态情况：SDOWN和ODOWN。

- SDOWN

    subjectively down，直接翻译的为"主观"失效,即当前sentinel实例认为某个redis服务为"不可用"状态.

- ODOWN

    objectively down，直接翻译为"客观"失效,即多个sentinel实例都认为master处于"SDOWN"状态,那么此时master将处于ODOWN,ODOWN可以简单理解为master已经被集群确定为"不可用",将会开启failover.

SDOWN适合于master和slave,但是ODOWN只会使用于master;当slave失效超过"down-after-milliseconds"后,那么所有sentinel实例都会将其标记为"SDOWN"。

