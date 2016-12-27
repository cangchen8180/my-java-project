## 一、ArrayList、LinkedList、Vector区别

### 时间上
ArrayList基于动态数组实现，LinkedList基于链表实现。

- 对于随机访问 get和set，ArrayList优于LinkedList。
- 对于增删操作 add和remove，LinkedList优于ArrayList。

### 空间上

- ArrayList内部用一个数组存储数据，但默认容量为10，当数组满了，分配新容量=（旧容量*3/2+1），约为旧容量的50%。
    - 这就意味着，如果你有一个包含大量元素的ArrayList对象，那么最终将有很大的空间会被浪费掉，这个浪费是由ArrayList的工作方式本身造成的。
    - 每次扩容，都要重新拷贝旧数组到新分配数组，将会导致性能急剧下降。
    > **为什么每次扩容要拷贝数组？**
    > 答：因为数组是一块连续的内存空间，扩容是创建新的连续内存区域（大小等于初始大小+步长），也用数组形式封装，并将原来的内存区域数据复制到新的内存区域，然后再用ArrayList中引用原来封装的数组对象的引用变量引用到新的数组对象：elementData = Arrays.copyOf(elementData, newCapacity);
    - 如果我们知道一个ArrayList将会有多少个元素，我们可以通过构造方法来指定容量。我们还可以通过trimToSize方法在ArrayList分配完毕之后去掉浪费掉的空间。
- LinkedList自己有内部类（Entry），每个元素都是一个Entry对象，空间浪费在存储其他Entry对象信息上
    
    ```java
    private static class Entry {   
             Object element;   
             Entry next;   
             Entry previous;   
         }   
    ```     
- 在列表开头和中间插入或删除元素时，ArrayList后面的元素都要后移或前移，LinkedList开销固定。
- Vector和ArrayList唯一区别是，Vector是线程安全的。
    
### 并发操作上
ArrayList和LinkedList都线程不安全，Vector线程安全。

---

## 二、HashSet、HashMap、HashTable、TreeSet和TreeMap区别

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

### TreeSet和TreeMap的区别
- TreeMap对key按红黑树的排序二叉数排序每个元素（Entry），保证所有key都从小到大排序。
- TreeSet基于TreeMap实现，类似HashSet基于HashMap实现。
