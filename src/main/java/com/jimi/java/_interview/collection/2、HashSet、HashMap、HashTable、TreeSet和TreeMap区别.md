## HashSet、HashMap、HashTable、TreeSet和TreeMap区别

### HashSet和HashMap的区别
- HashSet内部就是使用HashMap实现的，和HashMap不同的是它不需要Key和Value两个值。

    在HashSet中插入对象其实只不过是内部做了

```java
public boolean add(Object o) {
    return map.put(o, PRESENT)==null;
}
```

- HashMap元素的类结构
```java
static class Node<K,V>{
    final int hash;
    final K key;
    V value;
    Node<K,V> next;
}
```

### HashTable和HashMap的区别
- HashTable是线程安全，HashMap是不安全的。
- HashTable中，key和value都不允许出现null值; HashMap中，null可以作为键，这样的键只有一个；可以有一个或多个键所对应的值为null。

### ConcurrentHashMap能完全替代HashTable吗？
答：HashTable虽然性能上不如ConcurrentHashMap，但并不能完全被取代，两者的迭代器的一致性不同的，HashTable的迭代器是强一致性的，而ConcurrentHashMap是弱一致的。 ConcurrentHashMap的get，clear，iterator 都是弱一致性的。 Doug Lea 也将这个判断留给用户自己决定是否使用ConcurrentHashMap。

### 为什么ConcurrentHashMap是弱一致的？？？
答：ConcurrentHashMap的弱一致性主要是为了提升效率，是一致性与效率之间的一种权衡。要成为强一致性，就得到处使用锁，甚至是全局锁，这就与Hashtable和同步的HashMap一样了。

### ConcurrentHashMap是如何使用分段锁的？？？
答：通过分析Hashtable就知道，synchronized是针对整张Hash表的，即每次锁住整张表让线程独占。
  
ConcurrentHashMap允许多个修改操作并发进行，其关键在于使用了锁分离技术。它使用了多个锁来控制对hash表的不同部分进行的修改。ConcurrentHashMap内部使用段(Segment)来表示这些不同的部分，每个段其实就是一个小的hash table，它们有自己的锁。

当默认值为16时，也就是段的个数，也就是并发级别，hash值的高4位决定分配在哪个段中。

只要多个修改操作发生在不同的段上，它们就可以并发进行。

```java

public ConcurrentHashMap(int initialCapacity) {
    this(initialCapacity, 0.75F, 16);
}

...

int ssize;
for(ssize = 1; ssize < concurrencyLevel; ssize <<= 1) {
    ++sshift;
}

...

ConcurrentHashMap.Segment s0 = new ConcurrentHashMap.Segment(loadFactor, (int)((float)cap * loadFactor), (ConcurrentHashMap.HashEntry[])(new ConcurrentHashMap.HashEntry[cap]));
ConcurrentHashMap.Segment[] ss = (ConcurrentHashMap.Segment[])(new ConcurrentHashMap.Segment[ssize]);
UNSAFE.putOrderedObject(ss, SBASE, s0);
this.segments = ss;

```
  
有些方法需要跨段，比如size()和containsValue()，它们可能需要锁定整个表而而不仅仅是某个段，这需要按顺序锁定所有段，操作完毕后，又按顺序释放所有段的锁。

这里“按顺序”是很重要的，否则极有可能出现死锁，在ConcurrentHashMap内部，段数组是final的，并且其成员变量实际上也是final的，但是，仅仅是将数组声明为final的并不保证数组成员也是final的，这需要实现上的保证。这可以确保不会出现死锁，因为获得锁的顺序是固定的。

### TreeSet和TreeMap的区别
- TreeMap对key按红黑树的排序二叉数排序每个元素（Entry），保证所有key都从小到大排序。
- TreeSet基于TreeMap实现，类似HashSet基于HashMap实现。
