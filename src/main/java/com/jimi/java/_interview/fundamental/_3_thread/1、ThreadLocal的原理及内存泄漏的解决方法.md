# ThreadLocal原理及几个关键问题

## 原理
ThreadLocal的实现是这样的：每个Thread 维护一个 ThreadLocal.ThreadLocalMap实例，这个ThreadLocal.ThreadLocalMap内部用Entry数组存储各个threadLocal里的值，ThreadLocalMap的 key 是 ThreadLocal 实例本身，value 是真正需要存储的 Object。

如图，

![threadlocal.jpg](threadlocal.jpg)

ThreadLocal的set()方法源码如下，get()/remove()类似，

```java
    public void set(T value) {
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value);
    }
```

而ThreadLocalMap是ThreadLocal的内部静态类，其set()方法源码如下，

```java
        /**
         * Set the value associated with key.
         *
         * @param key the thread local object
         * @param value the value to be set
         */
        private void set(ThreadLocal key, Object value) {

            // We don't use a fast path as with get() because it is at
            // least as common to use set() to create new entries as
            // it is to replace existing ones, in which case, a fast
            // path would fail more often than not.

            Entry[] tab = table;
            int len = tab.length;
            int i = key.threadLocalHashCode & (len-1);

            for (Entry e = tab[i];
                 e != null;
                 e = tab[i = nextIndex(i, len)]) {
                ThreadLocal k = e.get();

                if (k == key) {
                    e.value = value;
                    return;
                }

                if (k == null) {
                    replaceStaleEntry(key, value, i);
                    return;
                }
            }

            tab[i] = new Entry(key, value);
            int sz = ++size;
            if (!cleanSomeSlots(i, sz) && sz >= threshold)
                rehash();
        }
```

其中，ThreadLocal实例作为key传入ThreadLocalMap，是为了利用ThreadLocal实例的hash值确定ThreadLocal实例保存的值在table数组中的位置（`int i = key.threadLocalHashCode & (len-1);`）。

> 如果位置发生冲突怎么办？

> ThreadLocalMap会采用线性探测法（不断加1）。

实现是nextIndex()方法，

```java
/**
 * Increment i modulo len.
 */
private static int nextIndex(int i, int len) {
    return ((i + 1 < len) ? i + 1 : 0);
}
```

而在使用Entry保存数据时，对ThreadLocal实例使用的是弱引用。如下，

```java
static class Entry extends WeakReference<ThreadLocal> {
    /** The value associated with this ThreadLocal. */
    Object value;

    Entry(ThreadLocal k, Object v) {
        super(k);
        value = v;
    }
}
```

## 关键问题
### 为什么不是使用Map保存各个实例，而是采用ThreadLocalMap这种方式？
假如我们把ThreadLocalMap做成一个Map<t extends Thread, ?>类型的Map，那么它存储的东西将会非常多（相当于一张全局线程本地变量表），这样的情况下用线性探测法解决哈希冲突的问题效率会非常差。而JDK里的这种利用ThreadLocal作为key，再将ThreadLocalMap与线程相绑定的实现，完美地解决了这个问题。

### 为什么使用弱引用？
由于ThreadLocalMap的生命周期跟Thread一样长，如果都没有手动删除对应key，都会导致内存泄漏，但是使用弱引用可以多一层保障：弱引用ThreadLocal不会内存泄漏，对应的value在下一次ThreadLocalMap调用`set`,`get`,`remove`的时候会被清除。

弱引用的作用，参考[2、强引用、软引用、弱引用和虚引用的区别.md](2、强引用、软引用、弱引用和虚引用的区别.md)

### ThreadLocal是否为造成内存泄漏？
ThreadLocalMap使用ThreadLocal的弱引用作为key，如果一个ThreadLocal没有外部强引用来引用它，那么系统 GC 的时候，这个ThreadLocal势必会被回收，这样一来，ThreadLocalMap中就会出现key为null的Entry，就没有办法访问这些key为null的Entry的value，如果当前线程再迟迟不结束的话，这些key为null的Entry的value就会一直存在一条强引用链：Thread Ref -> Thread -> ThreaLocalMap -> Entry -> value永远无法回收，造成内存泄漏。

> 如何避免内存泄漏？

> 每次使用完ThreadLocal，都调用它的remove()方法，清除数据。

**ThreadLocal的内存泄漏不是其本身造成的，而是使用线程池，如果不销毁线程才可能导致内存泄漏。**

### ThreadLocal只能被当前线程访问吗？

ThreadLocal的子类`InheritableThreadLocal`可以突破这个限制, 父线程的线程局部变量在创建子线程时会传递给子线程。

```java
private void testInheritableThreadLocal() {
    final ThreadLocal<String> threadLocal = new InheritableThreadLocal();
    threadLocal.set("testStr");
    Thread t = new Thread() {
        @Override
        public void run() {
            super.run();
            Log.i(LOGTAG, "testInheritableThreadLocal = " + threadLocal.get());
        }
    };

    t.start();
}

// 输出结果为 testInheritableThreadLocal = testStr
```

Thread类的实现如下，

```java
    /* ThreadLocal values pertaining to this thread. This map is maintained
     * by the ThreadLocal class. */
    ThreadLocal.ThreadLocalMap threadLocals = null;

    /*
     * InheritableThreadLocal values pertaining to this thread. This map is
     * maintained by the InheritableThreadLocal class.
     */
    ThreadLocal.ThreadLocalMap inheritableThreadLocals = null;

    /**
     * Initializes a Thread.
     *
     * @param g the Thread group
     * @param target the object whose run() method gets called
     * @param name the name of the new Thread
     * @param stackSize the desired stack size for the new thread, or
     *        zero to indicate that this parameter is to be ignored.
     */
    private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }

        Thread parent = currentThread();

        ...

        if (parent.inheritableThreadLocals != null)
            this.inheritableThreadLocals =
                ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);
        /* Stash the specified stack size in case the VM cares */
        this.stackSize = stackSize;

        /* Set thread ID */
        tid = nextThreadID();
    }
```