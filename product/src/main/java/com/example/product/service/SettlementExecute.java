package com.example.product.service;

import com.example.product.service.impl.AlipayPayment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wsj
 * @apiNote 结算执行器
 * @date 2022/10/24
 * @implNote
 */
@Service
public class SettlementExecute {
    public void settlePayment(PayMethod method,Double amount){
        method.balancePay(amount);
        method.bankCardPay("100086",amount);
    }
}
