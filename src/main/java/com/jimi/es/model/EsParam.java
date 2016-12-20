package com.jimi.es.model;

import com.jimi.es.enums.EsParamTypeEnum;

/**
 * @author jimi
 * @version 2016-04-12 17:42.
 */
public class EsParam {
    private String field;
    private String value;
    private EsParamTypeEnum paramType;

    public EsParam() {
    }

    public EsParam(String field, String value, EsParamTypeEnum paramType) {
        this.field = field;
        this.value = value;
        this.paramType = paramType;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public EsParamTypeEnum getParamType() {
        return paramType;
    }

    public void setParamType(EsParamTypeEnum paramType) {
        this.paramType = paramType;
    }

    @Override
    public String toString() {
        return "EsParam{" +
                "field='" + field + '\'' +
                ", value='" + value + '\'' +
                ", paramType=" + paramType +
                '}';
    }
}
