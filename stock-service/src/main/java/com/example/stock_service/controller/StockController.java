package com.example.stock_service.controller;

import com.example.stock_service.entity.Stock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.logging.Logger;

/**
 * @deprecated escription: 库存服务
 * @author wsj
 * @date ate 2022/4/15
 */
@Slf4j
@RestController
@RequestMapping("/stock")
public class StockController {
    @Value("${server.port}")
    private  String port;


    /**
     * 增加库存
     * @return 返回
     */
    @RequestMapping("/addStock")
    public String addStock(){
        log.info("增加库存成功！！！");
        return "库存服务增加库存成功";
    }

    /**
     * 增加库存
     * @return 返回
     */
    @RequestMapping("/deductionStock")
    public String deductionStock(){
        log.info("减库存成功！！！");
        return "抠库存成功-端口:" + port;
    }

    /**
     * 增加库存
     * @return 返回
     */
    @RequestMapping("/getStock")
    public Stock getStock(){
        Stock stock = new Stock();
        stock.setMoney(3333.33);
        stock.setPaymentDate(new Date());
        stock.setStockId("stock");
        stock.setTotal(3330000);
        return stock;
    }
}
