package com.example.elasticsearch.utils;

/**
 * @author wsj
 * @description 通用时间
 * @date 2023/11/15
 */
public enum DateCommonEnum {
    PAST_WEEK("1", 7, "day"),
    PAST_MONTH("2", 1, "month"),
    PAST_THREE_MONTH("3", 3, "month"),
    PAST_SIX_MONTH("4", 6, "month"),
    PAST_YEAR("5", 1, "year");

    public String getCode() {
        return code;
    }

    public Integer getNumber() {
        return number;
    }

    public String getUnit() {
        return unit;
    }

    private final String code;
    private final Integer number;
    private final String unit;

    DateCommonEnum(String code, Integer number, String unit) {
        this.code = code;
        this.number = number;
        this.unit = unit;
    }

    public static DateCommonEnum getByCode(String code) {
        for (DateCommonEnum item : DateCommonEnum.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        throw new RuntimeException("文件枚举没有该类型");
    }
}
