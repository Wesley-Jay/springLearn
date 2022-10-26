package com.example.product.service.impl;

import com.example.product.service.PayFee;
import com.example.product.service.PayMethod;

/**
 * @author wsj
 * @apiNote PayFee 适配器
 * @date 2022/10/24
 */
public class PayFeeAdapter implements PayFee {
    PayFee payFee;

    /**
     * 根据传入的支付方式实例化适配器
     * @param payFee 支付
     */
    public PayFeeAdapter(PayFee payFee) {
        this.payFee = payFee;
    }


    @Override
    public void payment(Double money) {
        payFee.payment(money);
    }
}
