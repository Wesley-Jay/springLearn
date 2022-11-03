package com.example.product.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wsj
 * @implNote 返回结果实体
 * @date 2022/11/3
 */
@Setter
@Getter
public class Result<T> {
    private  Integer code;
    private String msg;
    private  T data;
}
