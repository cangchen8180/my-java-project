package com.jimi.es.model;

/**
 * @author jimi
 * @version 2016-04-11 14:36.
 */
public class SearchConditionParam {

    private Integer customerId; //客户编号
    private String name;    //客户名称
    private String shortName;   //客户简称

    private Integer linkManUserId;
    private String linkManName; //联系人姓名
    private String linkManPhone;    //联系人电话
    private String linkManFax;  //
    private String linkManSex;  //性别

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
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

    public Integer getLinkManUserId() {
        return linkManUserId;
    }

    public void setLinkManUserId(Integer linkManUserId) {
        this.linkManUserId = linkManUserId;
    }

    public String getLinkManName() {
        return linkManName;
    }

    public void setLinkManName(String linkManName) {
        this.linkManName = linkManName;
    }

    public String getLinkManPhone() {
        return linkManPhone;
    }

    public void setLinkManPhone(String linkManPhone) {
        this.linkManPhone = linkManPhone;
    }

    public String getLinkManFax() {
        return linkManFax;
    }

    public void setLinkManFax(String linkManFax) {
        this.linkManFax = linkManFax;
    }

    public String getLinkManSex() {
        return linkManSex;
    }

    public void setLinkManSex(String linkManSex) {
        this.linkManSex = linkManSex;
    }

    @Override
    public String toString() {
        return "SearchConditionParam{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", linkManUserId=" + linkManUserId +
                ", linkManName='" + linkManName + '\'' +
                ", linkManPhone='" + linkManPhone + '\'' +
                ", linkManFax='" + linkManFax + '\'' +
                ", linkManSex='" + linkManSex + '\'' +
                '}';
    }
}
