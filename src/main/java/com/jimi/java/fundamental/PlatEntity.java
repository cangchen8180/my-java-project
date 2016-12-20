package com.jimi.java.fundamental;

import java.util.Date;

/**
 * @author jimi
 * @description 测试实体
 * @date 2016-03-18 17:30.
 */
public class PlatEntity {

    private int id;
    private String name;
    private String type;
    private Date createTime;
    private boolean isDel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public boolean isDel() {
        return isDel;
    }

    public void setIsDel(boolean isDel) {
        this.isDel = isDel;
    }

    @Override
    public String toString() {
        return "PlatEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", createTime=" + createTime +
                ", isDel=" + isDel +
                '}';
    }
}
