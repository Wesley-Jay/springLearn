package com.example.orderservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author wsj
 * @Description: 订单接口
 * @date 2022/4/15
 * name:指定调用rest接口所对应的服务名
 * path:指定调用rest接口所在的StockController指定的@RequestMapping
 */

@FeignClient(value = "stock-service",path = "stock")
public interface PlaceOrderInterface {
    /**
     * 远程调用扣减库存
     * @return 返回值
     */
    @PostMapping(value = "/deductionStock")
    String  deductionStock();
}
