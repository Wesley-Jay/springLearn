package com.example.springevent.event;

import org.springframework.context.ApplicationEvent;

import java.time.Clock;

/**
 * @author wsj
 * @description 订单事件
 * @date 2024/3/5
 */
public class OrderEvent  extends ApplicationEvent {
    private  final String orderName;
    public OrderEvent(Object source, String orderName) {
        super(source);
        this.orderName = orderName;
    }

    public String getOrderName() {
        return orderName;
    }
}
