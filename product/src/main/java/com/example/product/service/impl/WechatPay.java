package com.example.product.service.impl;

import com.example.product.service.PayMethod;

/**
 * @author wsj
 * @apiNote 微信支付
 * @date 2022/10/24
 */
public class WechatPay implements PayMethod {
    @Override
    public void bankCardPay(String cardNumber, Double money) {
        System.out.println("微信使用银行卡" + cardNumber + "支付：" + money);

    }

    @Override
    public void balancePay(Double amount) {
        System.out.println("微信余额支付" + amount);
    }
}
