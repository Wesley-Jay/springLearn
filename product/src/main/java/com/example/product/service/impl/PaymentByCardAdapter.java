package com.example.product.service.impl;

import com.example.product.service.PayFee;
import com.example.product.service.PayMethod;

/**
 * @author wsj
 * @implNote 银行卡支付
 * @date 2022/10/26
 */
public class PaymentByCardAdapter implements PayFee {
    PayMethod method;
    public PaymentByCardAdapter(PayMethod payMethod) {
        this.method = payMethod;
    }

    @Override
    public void payment(Double money) {
        method.balancePay(money);
    }
}
