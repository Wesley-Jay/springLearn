package com.example.elasticsearch.utils;

/**
 * @author wsj
 * @description 查询条件
 * @date 2023/7/7
 */

public class Condition {
    /** 数据库字段名 */
    private String field;
    /** 数据匹配类型 */
    private String type;
    /** 值 */
    private Object value;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Condition(String field, String type, Object value) {
        this.field = field;
        this.type = type;
        this.value = value;
    }

    public Condition() {
    }
}
