package com.example.springevent.listener;

import com.example.springevent.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author wsj
 * @description 订单监听事件
 * @date 2024/3/5
 */
@Component
@Slf4j
public class OrderListener  implements ApplicationListener<OrderEvent> {
    @Override
    public void onApplicationEvent(OrderEvent event) {
        String orderName = event.getOrderName();
        log.info("监听到订单:{} 创建成功,准备执行相关的逻辑",orderName);
    }
}
