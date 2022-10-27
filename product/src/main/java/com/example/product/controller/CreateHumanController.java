package com.example.product.controller;

import com.example.product.service.create_human.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wsj
 * @implNote 造人控制器
 * @date 2022/10/27
 */
@RestController
@RequestMapping("/human")
public class CreateHumanController {
    @RequestMapping("/create")
    public void createHuman() {
        AbstractCreateHumanFactory humanFactory = new CreateHumanFactory();
        humanFactory.createHuman(YellowHuman.class).speak();
        humanFactory.createHuman(WhiteHuman.class).speak();
        humanFactory.createHuman(BlackHuman.class).speak();
    }
}
