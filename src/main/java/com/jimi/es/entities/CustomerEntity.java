package com.jimi.es.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author jimi
 * @description 客户信息
 * @date 2016-04-09 10:53.
 */
public class CustomerEntity implements Serializable {

    /**
     * id
     */
    private Integer id;
    /**
     * 客户名称
     */
    private String name;
    /**
     * 客户简称
     */
    //配置属性转换前后对应关系
    //alternate：需要转换的字段名
    //value：转换后字段名
    //作用：short_name和shortName两种字段的值都会转换为shortName的值
    @SerializedName(value = "shortName", alternate={"short_name"})
    private String shortName;

    /**
     * 客户性别
     */
    private String sex;
    /**
     * 客户注册时间
     */
    private Date registerTime;

    /**
     * 联系人列表
     */
    private List<LinkManEntity> linkManList;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public List<LinkManEntity> getLinkManList() {
        return linkManList;
    }

    public void setLinkManList(List<LinkManEntity> linkManList) {
        this.linkManList = linkManList;
    }

    @Override
    public String toString() {
        return "CustomerEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", registerTime=" + registerTime +
                ", linkManList=" + linkManList +
                '}';
    }
}
