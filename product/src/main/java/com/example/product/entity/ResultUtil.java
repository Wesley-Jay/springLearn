package com.example.product.entity;

import org.apache.poi.ss.formula.functions.T;

import java.util.Objects;

/**
 * @author wsj
 * @implNote 返回结果工具类
 * @date 2022/11/3
 */
public class ResultUtil {
    public static Result<T> success(T o) {
        Result<T> result  = new Result<>();
        result.setCode(ResultEnum.SUCCESS.getCode());
        result.setMsg(ResultEnum.SUCCESS.getDescribe());
        result.setData(o);
        return  result;
    }
    public static Result<T> success() {
        return  success(null);
    }

    public static Result<T> error(Integer code, String msg,T o) {
        Result<T> result  = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(o);
        return  result;
    }

    public static Result<T> error(Integer code, String msg) {
        return  error( code,  msg,null);
    }
}
