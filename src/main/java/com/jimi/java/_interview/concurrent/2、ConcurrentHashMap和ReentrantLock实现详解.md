# ConcurrentHashMap


# ReentrantLock
## tryLock作用，和lock的区别
1）lock(), 拿不到lock就不罢休，不然线程就一直block。 比较无赖的做法。
2）tryLock()，马上返回，拿到lock就返回true，不然返回false。 比较潇洒的做法。