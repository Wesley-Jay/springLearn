package com.example.orderservice.controller;

import com.example.orderservice.service.PlaceOrderInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description: 订单处理
 * @Author wsj
 * @Date 2022/4/15
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private PlaceOrderInterface orderInterface;
    /**
     * 下订单
     * @return 返回
     */
    @RequestMapping("/placeOrder")
    public String placeOrder() {
        return orderInterface.deductionStock();
    }
}
