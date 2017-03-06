package com.jimi.java._interview.algorithm._3_LRU_LFU.likeEHCache;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

/**
 * 两种淘汰策略的实现
 * <p>LFU：Less Frequently Used, 直白一点就是讲一直以来最少被使用的。
 * 缓存的元素有一个hit属性，hit值最小的将会被清出缓存。
 * 可以参照ehcache中LfuPolicy的{@link net.sf.ehcache.store.LfuPolicy#compare(net.sf.ehcache.Element, net.sf.ehcache.Element)}实现</p>
 *
 * <p>LRU：Least Recently Used，最近最少使用，缓存的元素有一个时间戳，
 * 当缓存容量满了，而又需要腾出地方来缓存新的元素的时候，那么现有缓存
 * 元素中时间戳离当前时间最远的元素将被清出缓存。
 * 可以参照LruPolicy的{@link net.sf.ehcache.store.LruPolicy#compare(net.sf.ehcache.Element, net.sf.ehcache.Element) compare}实现</p>
 *
 * 使用方法{@link net.sf.ehcache.store.AbstractPolicy#selectedBasedOnPolicy(net.sf.ehcache.Element[], net.sf.ehcache.Element)}
 *
 * @author jimi at 2017-02-24 14:56.
 *
 * @see net.sf.ehcache.store.LfuPolicy
 * @see net.sf.ehcache.store.LruPolicy
 * @see net.sf.ehcache.Element
 *
 * @serial - 说明定义该方法中的序列化字段（也作@serialField或@serialData）；
 *
 * @deprecated - 标记该方法受到批评，这意味着在将来某个时候该方法可能被删除，类的使用者应对此做记录，并停止使用这个方法。
 */

/**
 * @SuppressWarnings("serial")
 * 可以抑制一些能通过编译但是存在有可能运行异常的代码会发出警告，你确定代码运行时不会出现警告提示的情况下，可以使用这个注释。
 * ("serial") 是序列化警告，当实现了序列化接口的类上缺少serialVersionUID属性的定义时，会出现黄色警告。可以使用@SuppressWarnings将警告关闭
 */
public class LRUTest {

    public static void main(String[] args) {
        List<Element> elementList = new ArrayList<>(10);
        elementList.add(new Element(1, 10, System.currentTimeMillis()));
        elementList.add(new Element(2, 20, System.currentTimeMillis()));

        for (Element element : elementList) {
            element.setHit(element.getHit()+1);
        }
        elementList.get(0).updateAccess();

        System.out.println("elementList = " + elementList);
    }
}


/**
 * 元素实体
 *
 * @see net.sf.ehcache.Element
 */
class Element{
    /**
     * 原子累加器
     */
    private static final AtomicLongFieldUpdater ATOMIC_HIT_UPDATER = AtomicLongFieldUpdater.newUpdater(Element.class, "hit");
    private int id;
    private volatile long hit;
    private long lastAccessTime;

    public Element(int id, long hit, long lastAccessTime) {
        this.id = id;
        this.hit = hit;
        this.lastAccessTime = lastAccessTime;
    }

    public void updateAccess() {
        ATOMIC_HIT_UPDATER.incrementAndGet(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getHit() {
        return hit;
    }

    public void setHit(long hit) {
        this.hit = hit;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
