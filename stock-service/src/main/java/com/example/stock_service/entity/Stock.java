package com.example.stock_service.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import java.util.Date;

/**
 * @Description 库存实体
 * @Author wsj
 * @Date 2022/10/9
 */
@Getter
@Setter
@Data
public class Stock {
    private  String stockId;
    @NumberFormat(style= NumberFormat.Style.NUMBER,pattern="#,###")
    private int total;

    @NumberFormat(style= NumberFormat.Style.PERCENT)
    private double discount;

    @NumberFormat(style= NumberFormat.Style.CURRENCY)
    private double money;

    @DateTimeFormat()
    private Date paymentDate;
}
