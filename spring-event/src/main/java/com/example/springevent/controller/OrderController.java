package com.example.springevent.controller;

/**
 * @author wsj
 * @description 接口请求层
 * @date 2024/3/5
 */

import com.example.springevent.service.OrderService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;


    @GetMapping("/placeOrder")
    public void  placeOrder() {
        orderService.placeOrder("IPhone 15 Pro Max");
    }
}
