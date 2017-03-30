## HashMap学习
HashMap提供了三个构造函数：

- HashMap()：构造一个具有默认初始容量 (16) 和默认加载因子 (0.75) 的空 HashMap。
- HashMap(int initialCapacity)：构造一个带指定初始容量和默认加载因子 (0.75) 的空 HashMap。
- HashMap(int initialCapacity, float loadFactor)：构造一个带指定初始容量和加载因子的空 HashMap。

在这里提到了两个参数：初始容量，加载因子。这两个参数是影响HashMap性能的重要参数，其中容量表示哈希表中桶的数量，初始容量是创建哈希表时的容量，加载因子是哈希表在其容量自动增加之前可以达到多满的一种尺度，它衡量的是一个散列表的空间的使用程度，负载因子越大表示散列表的装填程度越高，反之愈小。对于使用链表法的散列表来说，查找一个元素的平均时间是O(1+a)，因此如果负载因子越大，对空间的利用更充分，然而后果是查找效率的降低；如果负载因子太小，那么散列表的数据将过于稀疏，对空间造成严重浪费。系统默认负载因子为0.75，一般情况下我们是无需修改的。

## HashMap中几个重要的点
> HashMap哈希算法是怎样的？如何做到均匀hash的？做了哪些优化？16个桶满了怎么避免报错的？

### put方法
> 如何保证元素均匀分布？如何高效写入？

源码如下：

```java
public V put(K key, V value) {  
        //当key为null，调用putForNullKey方法，保存null与table第一个位置中，这是HashMap允许为null的原因  
        if (key == null)  
            return putForNullKey(value);  
        //计算key的hash值  
        int hash = hash(key.hashCode());                  ------(1)  
        //计算key hash 值在 table 数组中的位置  
        int i = indexFor(hash, table.length);             ------(2)  
        //从i出开始迭代 e,找到 key 保存的位置  
        for (Entry<K, V> e = table[i]; e != null; e = e.next) {  
            Object k;  
            //判断该条链上是否有hash值相同的(key相同)  
            //若存在相同，则直接覆盖value，返回旧value  
            if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {  
                V oldValue = e.value;    //旧值 = 新值  
                e.value = value;  
                e.recordAccess(this);  
                return oldValue;     //返回旧值  
            }  
        }  
        //修改次数增加1  
        modCount++;  
        //将key、value添加至i位置处  
        addEntry(hash, key, value, i);  
        return null;  
    }  
```    

（1）、（2）处。这里是HashMap的精华所在。

#### 1、hash算法
> 如何保证hash值均匀分布？

该方法为一个纯粹的数学计算，就是计算h的hash值。

```java
static int hash(int h) {  
        h ^= (h >>> 20) ^ (h >>> 12);  
        return h ^ (h >>> 7) ^ (h >>> 4);  
    } 
```    

#### 2、取模优化
计算hash值后，怎么才能保证table元素分布均与呢？我们会想到取模，但是由于取模的消耗较大，HashMap是这样处理的：调用indexFor方法。

由于取模（除完取整）本身耗能太大（因为取模是多次的加法或减法，所以耗能很大），HashMap采用下面方式实现取模，效率极大提高。

```java
static int indexFor(int h, int length) {  
        return h & (length-1);  
    } 
```

HashMap的底层数组长度总是2的n次方，在构造函数中存在：capacity <<= 1;这样做总是能够保证HashMap的底层数组长度为2的n次方。

当length为2的n次方时，h&(length - 1)就相当于对length取模，而且速度比直接取模快得多，这是HashMap在速度上的一个优化。 

注：indexFor方法，仅有一条语句：h&(length - 1)，这句话除了上面的取模运算外还有一个非常重要的责任：均匀分布table数据和充分利用空间。

**当length = 2^n时，不同的hash值发生碰撞的概率比较小，这样就会使得数据在table数组中分布较均匀，查询速度也较快。**
    
#### 3、如何扩容时，保证数组容量为2的整数次幂？？？
由上面可知，数组容量（即length）为2的n次方时，HashMap hash出的元素分布才比较均匀，性能才比较高。

>那如何保证每次扩容后，容量都是2的整数次幂？

>答：每次扩容为原来长度的两倍。

```java
void addEntry(int hash, K key, V value, int bucketIndex) {  
        
        ...
        
        //若HashMap中元素的个数超过极限了，则容量扩大两倍  
        if (size++ >= threshold)  
            resize(2 * table.length);  
    }

    /**
         * Rehashes the contents of this map into a new array with a
         * larger capacity.  This method is called automatically when the
         * number of keys in this map reaches its threshold.
         *
         * If current capacity is MAXIMUM_CAPACITY, this method does not
         * resize the map, but sets threshold to Integer.MAX_VALUE.
         * This has the effect of preventing future calls.
         *
         * @param newCapacity the new capacity, MUST be a power of two;
         *        must be greater than current capacity unless current
         *        capacity is MAXIMUM_CAPACITY (in which case value
         *        is irrelevant).
         */
        void resize(int newCapacity) {
            Entry[] oldTable = table;
            int oldCapacity = oldTable.length;
            if (oldCapacity == MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return;
            }

            Entry[] newTable = new Entry[newCapacity];
            boolean oldAltHashing = useAltHashing;
            useAltHashing |= sun.misc.VM.isBooted() &&
                    (newCapacity >= Holder.ALTERNATIVE_HASHING_THRESHOLD);
            boolean rehash = oldAltHashing ^ useAltHashing;
            transfer(newTable, rehash);
            table = newTable;
            threshold = (int)Math.min(newCapacity * loadFactor, MAXIMUM_CAPACITY + 1);
        }

        /**
         * Transfers all entries from current table to newTable.
         */
        void transfer(Entry[] newTable, boolean rehash) {
            int newCapacity = newTable.length;
            for (Entry<K,V> e : table) {
                while(null != e) {
                    Entry<K,V> next = e.next;
                    if (rehash) {
                        e.hash = null == e.key ? 0 : hash(e.key);
                    }
                    int i = indexFor(e.hash, newCapacity);
                    e.next = newTable[i];
                    newTable[i] = e;
                    e = next;
                }
            }
        }
```

随着HashMap中元素的数量越来越多，发生碰撞的概率就越来越大，所产生的链表长度就会越来越长，这样势必会影响HashMap的速度，为了保证HashMap的效率，系统必须要在某个临界点进行扩容处理。该临界点在当HashMap中元素的数量等于table数组长度*加载因子。但是扩容是一个非常耗时的过程，因为它需要重新计算这些数据在新table数组中的位置并进行复制处理。所以如果我们已经预知HashMap中元素的个数，那么预设元素的个数能够有效的提高HashMap的性能。

#### 4、负载因子（loadFactor）的作用
loadFactor默认是0.75。HashMap扩容的阈值是元素个数大于容量*loadFactor时。

>为什么要有个负载因子？

答：也就是说，如果负载因子是0.75，HashMap(16)最多可以存储12个元素，想存第16个就得扩容成32。

这样可以减少hash冲突的概率，如果hash值相同的过多，那元素都会存在一个链表里，会降低查询key的速度。

#### 5、hash相同的，链表存储，新元素总是插入到表头
系统总是将新的Entry对象添加到bucketIndex处。如果bucketIndex处已经有了对象，那么新添加的Entry对象将指向原有的Entry对象，形成一条Entry链，但是若bucketIndex处没有Entry对象，也就是e==null,那么新添加的Entry对象指向null，也就不会产生Entry链了。

 bucketIndex：即hash后存储到数组的位置，也就是0-15其中之一。（HashMap初始容量是16）

 **注意和ConcurrentHashMap的16个桶的区别，它的桶是指Segment。**

```java
void addEntry(int hash, K key, V value, int bucketIndex) {  
        //获取bucketIndex处的Entry  
        Entry<K, V> e = table[bucketIndex];  
        //将新创建的 Entry 放入 bucketIndex 索引处，并让新的 Entry 指向原来的 Entry   
        table[bucketIndex] = new Entry<K, V>(hash, key, value, e);  
        //若HashMap中元素的个数超过极限了，则容量扩大两倍  
        if (size++ >= threshold)  
            resize(2 * table.length);  
    } 
```        

### get方法
先找到hash对应桶索引，再循环链表。

```java
public V get(Object key) {  
        // 若为null，调用getForNullKey方法返回相对应的value  
        if (key == null)  
            return getForNullKey();  
        // 根据该 key 的 hashCode 值计算它的 hash 码    
        int hash = hash(key.hashCode());  
        // 取出 table 数组中指定索引处的值  
        for (Entry<K, V> e = table[indexFor(hash, table.length)]; e != null; e = e.next) {  
            Object k;  
            //若搜索的key与查找的key相同，则返回相对应的value  
            if (e.hash == hash && ((k = e.key) == key || key.equals(k)))  
                return e.value;  
        }  
        return null;  
    }  
```
