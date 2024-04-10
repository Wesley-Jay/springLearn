package com.example.product.service.impl;


import com.example.product.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Objects;

/**
 * @author wsj
 * @description 实现类
 * @date 2024/4/10
 */

@Service
@Slf4j
public class OrderServiceImpl {


    @Async("taskExecutor")
    public void userPlaceOrder(String productName) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        UserInfo userDetails = (UserInfo) requestAttributes.getAttribute("userInfo", RequestAttributes.SCOPE_REQUEST);
        if (Objects.isNull(userDetails)) {
            throw new RuntimeException("上下文传递失败");
        }
        log.info("恭喜尊贵的{}用户，下单成功，下单产品为{}", userDetails.getName(), productName);
    }
}
