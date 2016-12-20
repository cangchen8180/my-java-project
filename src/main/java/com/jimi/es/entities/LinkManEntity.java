package com.jimi.es.entities;

import java.io.Serializable;

/**
 * @author jimi
 * @description 联系人信息
 * @date 2016-04-09 10:59.
 */
public class LinkManEntity implements Serializable {

    /**
     * id
     */
    private Integer id;
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 联系人姓名
     */
    private String name;
    /**
     * 性别
     */
    private String sex;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 传真
     */
    private String fax;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    @Override
    public String toString() {
        return "LinkManEntity{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", phone='" + phone + '\'' +
                ", fax='" + fax + '\'' +
                '}';
    }
}
