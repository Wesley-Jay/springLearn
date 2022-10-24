package com.example.product.service.impl;

import com.example.product.service.PayMethod;

/**
 * @author wsj
 * @apiNote 支付宝支付
 * @date 2022/10/24
 */
public class AlipayPayment implements PayMethod {

    @Override
    public void balancePay(Double amount) {
        System.out.println("Alipay余额支付" + amount);
    }

    @Override
    public void bankCardPay(String cardNumber, Double money) {
        System.out.println("Alipay支付使用银行卡" + cardNumber + "支付：" + money);
    }
}
