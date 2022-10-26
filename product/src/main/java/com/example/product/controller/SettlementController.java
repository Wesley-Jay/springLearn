package com.example.product.controller;

import com.example.product.service.SettlementExecute;
import com.example.product.service.impl.AlipayPayment;
import com.example.product.service.impl.PaymentByCardAdapter;
import com.example.product.service.impl.WechatPay;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wsj
 * @apiNote 结算web层
 * @date 2022/10/24
 */
@RestController
@RequestMapping("/settlement")
public class SettlementController {
    @Resource
    private SettlementExecute execute;
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
}

