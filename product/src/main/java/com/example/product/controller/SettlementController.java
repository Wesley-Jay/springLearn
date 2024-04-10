package com.example.product.controller;

import com.example.product.entity.Result;
import com.example.product.entity.ResultUtil;
import com.example.product.entity.UserInfo;
import com.example.product.service.SettlementExecute;
import com.example.product.service.StockService;
import com.example.product.service.impl.AlipayPayment;
import com.example.product.service.impl.OrderServiceImpl;
import com.example.product.service.impl.PaymentByCardAdapter;
import com.example.product.service.impl.WechatPay;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * @author wsj
 * @apiNote 结算web层
 * @date 2022/10/24
 */
@RestController
@RequestMapping("/settlement")
@Slf4j
public class SettlementController {
    @Resource
    private SettlementExecute execute;
    @Resource
    private StockService service;

    @Resource
    private OrderServiceImpl orderService;

    @Resource
    private Executor taskExecutor;

    private final Double amount = 5000.0;
    @RequestMapping("/payFee/wechat")
    public String wechatPayFee() {
        execute.settlePayment(new WechatPay(),amount);
        return "结算ok";
    }
    @RequestMapping("/payFee/alipay")
    public String alipayPayFee() {
        execute.settlePayment(new AlipayPayment(), amount);
        return "结算ok";
    }

    @RequestMapping("/payMethod/alipay")
    public String alipayPay() {
        PaymentByCardAdapter cardAdapter = new PaymentByCardAdapter(new AlipayPayment());
        cardAdapter.payment(3000.0);
        return "结算ok";
    }

    @RequestMapping("/getStock")
    public Result<T> getStock() {
        service.getStock(3);
        return ResultUtil.success();
    }

    @PostMapping("/user-place-order")
    public void userPlaceOrder(@RequestBody List<String> productName) {
        // 获取当前请求的 RequestAttributes 对象
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        UserInfo userInfo = new UserInfo();
        userInfo.setName("魏先生");
        userInfo.setId(88888L);
        // 将用户信息添加到 RequestAttributes 中
        assert attributes != null;
        attributes.setAttribute("userInfo", userInfo, RequestAttributes.SCOPE_REQUEST);
        for (String product : productName) {
            orderService.userPlaceOrder(product);
        }

        execute.settlePayment(new AlipayPayment(), amount);
    }
}

