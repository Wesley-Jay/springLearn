package com.example.product.service.create_human;

/**
 * @author wsj
 * @implNote 造人工厂
 * @date 2022/10/26
 */
public class CreateHumanFactory implements AbstractCreateHumanFactory{
    @Override
    public <T extends Human> T createHuman(Class<T> clazz) {
        try {
            Human human =  (Human)Class.forName(clazz.getName()).newInstance();
            return (T)human;
        } catch (Exception e) {
           throw  new RuntimeException("造人出错了！！");
        }

    }
}
