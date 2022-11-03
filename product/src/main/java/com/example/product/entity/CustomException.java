package com.example.product.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wsj
 * @implNote 自定义异常
 * @date 2022/11/3
 */
@Getter
@Setter
public class CustomException extends RuntimeException{
    private Integer code;

    /**
     * 构造器
     * @param msg 消息
     */
    public CustomException(String msg) {
        super(msg);
    }

    /**
     * 构造器重构方便自定义
     * @param code 码
     * @param msg 消息
     */
    public CustomException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }
}
