# AQS的原理


## 疑问
### 1、ReentrantLock如何实现重入的，AQS中exclusiveOwnerThread的作用？
如果判断lock()的线程是exclusiveOwnerThread，也就是持有锁的线程，则让state加1，来实现锁的重入。

### 2、等待列表中header节点不绑定线程，其作用是什么？
header只是一个傀儡node，他代表上次获得的线程，只到header之后的一个线程获得锁之后，才会重新设置header，被设置为新header的是header的next node。

在获得锁的线程释放锁时，唤醒header的next节点，next节点的线程会去尝试加锁（lock()）。而header会赋null，一方面，方便内存回收，释放资源；另一方面，也是FIFO的体现。


在加锁的死循环中，如果加锁的线程对应等待队列的节点的awaitStatus是SIGNAL(-1)时，则线程挂起。


### 3、等待队列中每个Waiter节点的waitStatus的作用？

## 参考

- [扒一扒ReentrantLock以及AQS实现原理](https://my.oschina.net/andylucc/blog/651982)
- [AQS的原理浅析](http://ifeve.com/java-special-troops-aqs/)