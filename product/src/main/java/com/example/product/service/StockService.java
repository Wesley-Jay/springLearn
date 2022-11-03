package com.example.product.service;

import com.example.product.entity.CustomException;
import org.springframework.stereotype.Service;

/**
 * @author wsj
 * @implNote 库存服务
 * @date 2022/11/3
 */
@Service
public class StockService {
    public void getStock(Integer id) {
        if (id == 3) {
            throw new CustomException(1,"未存在");
        }
        System.out.println("查询库存成功了");
    }
}
