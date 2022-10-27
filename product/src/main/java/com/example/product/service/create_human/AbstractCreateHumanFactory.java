package com.example.product.service.create_human;

/**
 * @author wsj
 * @implNote 造人工厂
 * @date 2022/10/26
 */
public interface AbstractCreateHumanFactory {
    /** 创造人类
     * @param clazz 类
     * @return 数据
     */
    <T extends  Human> T createHuman(Class<T> clazz);
}
