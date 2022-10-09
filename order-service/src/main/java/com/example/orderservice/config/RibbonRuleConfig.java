package com.example.orderservice.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: ribbon配置
 * @Author wsj
 * @Date 2022/4/18
 */

@Configuration
public class RibbonRuleConfig {
    @Bean
    public IRule iRule(){
        //使用随机负载均衡策略
        return  new RandomRule();
        //使用Nacos提供的负载均衡策略
        //return  new NacosRule();
    }
}
