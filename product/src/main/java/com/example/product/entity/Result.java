package com.example.product.entity;

import lombok.Data;

/**
 * @author wsj
 * @implNote 返回结果实体
 * @date 2022/11/3
 */
@Data
public class Result<T> {
    private  Integer code;
    private String msg;
    private  T data;
}
