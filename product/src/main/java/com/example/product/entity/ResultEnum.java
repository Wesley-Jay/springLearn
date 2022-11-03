package com.example.product.entity;

/**
 * @author wsj
 * @implNote 返回结果枚举
 * @date 2022/11/3
 */
public enum ResultEnum {
    /** 枚举 */
    SUCCESS(200,"返回成功"),
    ERROR(400,"失败"),
    SystemException(-1,"系统异常"),
    CustomException(1, "自定义异常");
    private final Integer code;
    private final String describe;
    ResultEnum(Integer code,String describe ) {
        this.code = code;
        this.describe = describe;
    }
    public Integer getCode() {
        return code;
    }

    public String getDescribe() {
        return describe;
    }
}
