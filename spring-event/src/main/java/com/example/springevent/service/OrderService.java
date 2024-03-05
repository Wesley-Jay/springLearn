package com.example.springevent.service;

import com.example.springevent.event.OrderEvent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * @author wsj
 * @description 订单业务
 * @date 2024/3/5
 */
@Service
@Slf4j
public class OrderService {
    @Resource
    private ApplicationEventPublisher publisher;

    public void placeOrder(String orderName) {
        log.info("下订单成功，名称：{}",orderName);
        OrderEvent event = new OrderEvent(this,orderName);
        publisher.publishEvent(event);
    }

}
