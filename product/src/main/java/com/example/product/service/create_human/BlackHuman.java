package com.example.product.service.create_human;

/**
 * @author wsj
 * @implNote 黑人
 * @date 2022/10/26
 */
public class BlackHuman implements Human{
    @Override
    public void skinColor() {
        System.out.println("I'm BlackHuman");
    }

    @Override
    public void speak() {
        System.out.println("I'm the king of the black");
    }
}
