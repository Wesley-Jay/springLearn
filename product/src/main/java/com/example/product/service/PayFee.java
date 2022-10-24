package com.example.product.service;

/**
 * @author wsj
 * @apiNote  支付费用
 * @date 2022/10/21
 */
public interface PayFee {
    /**
     * 支付
     * @param type 支付类型
     * @param money 钱
     */
    void payment(String type, Double money);
}
