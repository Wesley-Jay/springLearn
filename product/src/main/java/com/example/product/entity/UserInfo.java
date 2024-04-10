package com.example.product.entity;

import lombok.Data;

import java.util.function.Predicate;

/**
 * @author wsj
 * @description 用户
 * @date 2024/4/10
 */
@Data
public class UserInfo {
    private Long id;
    private String name;
    private String gender;
}
