package com.example.stock_service.config.rule;

import com.alibaba.nacos.client.naming.utils.ThreadLocalRandom;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

import java.util.List;

/**
 * @Description: 自定义负载均衡规则
 * @Author wsj
 * @Date 2022/4/18
 */
public class LoadBalancerRule extends AbstractLoadBalancerRule {

    public void initWithNiwsConfig(IClientConfig iClientConfig) {}

    /**
     * 负载均衡策略规则，此处以随机数为例，可以写其他的规则或者自己创造的新规则
     * @param o 参数
     * @return 返回值
     */
    public Server choose(Object o) {
        ILoadBalancer iLoadBalancer = this.getLoadBalancer();
        //获取当前请求的服务实例
        List<Server> servers = iLoadBalancer.getReachableServers();
        //获取随机数
        int random = ThreadLocalRandom.current().nextInt(servers.size());
        //获取该随机数下的服务
        return servers.get(random);
    }

    @Override
    public ILoadBalancer getLoadBalancer() {
        return super.getLoadBalancer();
    }
}
