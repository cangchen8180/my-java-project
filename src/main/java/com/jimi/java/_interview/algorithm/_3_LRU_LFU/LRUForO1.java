package com.jimi.java._interview.algorithm._3_LRU_LFU;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.util.Date;
import java.util.HashMap;

/**
 * 实现lru，插入、删除和查找时间复杂度都做到o(1)
 *
 * Created by lagou on 2017/3/6.
 */
public class LRUForO1 {

    /**
     * 缓存最大长度
     */
    private static final int CACHE_MAX_SIZE = 5;
    private HashMap<Integer, Element> elementMap = new HashMap<>(CACHE_MAX_SIZE);
    /**
     * 头结点
     */
    private Element head;
    /**
     * 尾结点
     */
    private Element end;

    /**
     * 查找元素，即访问元素
     * 当访问到元素时，会先调用{@linkplain #remove(Element) 删除方法}删除元素原来的位置，
     * 然后再调用{@linkplain #setHead(Element) 插入方法}将元素插入到链表头部。
     * @param id 元素id
     * @return 访问的元素
     */
    public Element get(int id) {

        if (elementMap.containsKey(id)) {
            Element element = elementMap.get(id);

            remove(element);
            setHead(element);

            return element;
        }

        return null;
    }

    /**
     * 删除元素
     * 满时，删除的是最少访问的
     * 不满时，刚被访问元素原来的位置
     * @param element 元素
     */
    public void remove(Element element) {

        if (null == element.pre) {
            head = element.next;
        } else {
            element.pre.next = element.next;
        }

        if (null == element.next) {
            element.next.pre = element.pre;
        } else {
            end = element.pre;
        }

    }


    /**
     * 将元素添加到双向链表头部
     * @param element
     */
    public void setHead(Element element) {

        element.next = head;
        element.pre = null;

        if (null != head) {
            head.pre = element;
        }

        head = element;

        if (null == end) {
            end = head;
        }
    }

    /**
     * 插入元素
     * 对于新元素或刚访问的元素，则重新添加到链表最前面
     * @param id 元素
     */
    public void set(int id) {
        if (elementMap.containsKey(id)) {
            Element element = elementMap.get(id);
            remove(element);
            setHead(element);

        } else {
            Element newElement = new Element();
            if (elementMap.size() >= CACHE_MAX_SIZE) {
                remove(end);
                setHead(newElement);
            } else {
                setHead(newElement);
            }

            elementMap.put(id, newElement);
        }
    }
}


class Element {
    int id;
    Date accessTime;
    Element pre;
    Element next;

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}