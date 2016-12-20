package com.jimi.es.enums;

/**
 * @author jimi
 * @version 2016-04-12 17:45.
 */
public enum EsParamTypeEnum {
    AND("must"),
    OR("should"),
    NOT("mustNot");

    private String paramType;

    EsParamTypeEnum(String unionType) {
        this.paramType = unionType;
    }

    public String getParamType() {
        return paramType;
    }
}
