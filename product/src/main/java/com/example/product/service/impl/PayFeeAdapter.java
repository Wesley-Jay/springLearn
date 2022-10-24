package com.example.product.service.impl;

import com.example.product.service.PayFee;
import com.example.product.service.PayMethod;

/**
 * @author wsj
 * @apiNote PayFee 适配器
 * @date 2022/10/24
 */
public class PayFeeAdapter implements PayFee {
    PayMethod payMethod;

    /**
     * 根据传入的支付方式实例化适配器
     * @param method 支付方式
     */
    public PayFeeAdapter(PayMethod method) {
        payMethod = method;
    }


    @Override
    public void payment(String type, Double money) {
        payMethod.balancePay(money);
    }
}
