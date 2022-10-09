package com.example.common.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;


/**
 * @Description: 热刷新
 * @Author wsj
 * @Date 2022/4/8
 */
@RestController
@RefreshScope
public class ProviderController {
    Logger logger = LoggerFactory.getLogger(ProviderController.class);
    @Value("${username}")
    private String userName;
    @Value("${address}")
    private String address;

    @RequestMapping("/getInfo")
    public String getInfo(){
        return address + "  " + userName;
    }


    public static void main(String[] args) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.println(format.format(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
    }
}
