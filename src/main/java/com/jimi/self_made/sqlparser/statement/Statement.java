package com.jimi.self_made.sqlparser.statement;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Created by lixinjian on 17/6/27.
 */
public class Statement {

    /**
     * 操作类型
      */
    private String operateType;
    /**
     * 表名
     */
    private String tableName;
    /**
     * sql语句
     */
    private String content;

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
