package com.example.product.service;

/**
 * @author wsj
 * @apiNote 支付方式
 * @date 2022/10/21
 */
public interface PayMethod {
    /**
     * 银行卡支付
     * @param cardNumber 卡号
     * @param money 金额
     */
    void bankCardPay(String cardNumber, Double money);

    /**
     * 余额支付
     * @param amount 金额
     */
    void balancePay(Double amount);
}
