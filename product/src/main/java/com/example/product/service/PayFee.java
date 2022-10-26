package com.example.product.service;

/**
 * @author wsj
 * @apiNote  支付费用
 * @date 2022/10/21
 */
public interface PayFee {
    /**
     * 支付
     * @param money 钱
     */
    void payment(Double money);
}
