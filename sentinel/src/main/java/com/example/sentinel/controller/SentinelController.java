package com.example.sentinel.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.Rule;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.example.sentinel.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description: sentinel服务熔断
 * @Author wsj
 * @Date 2022/4/19
 */
@Slf4j
@RestController
public class SentinelController {
    private  static final String RESOURCE_NAME = "user";
    private  static final String DEGRADE_RESOURCE_NAME = "user";


    @RequestMapping("/sentinel")
    public String sentinelTest() {
        Entry entry = null;
        try {
            //sentinel针对资源名做控制
            entry = SphU.entry(RESOURCE_NAME);
            log.info("接口正常------------");
            return "接口正常";
        }catch (BlockException e){
            e.printStackTrace();
            log.info("接口被流控了");
            return "接口被流控了";
        }finally {
            if (Objects.nonNull(entry)) {
                entry.exit();
            }
        }
    }

    /**
     * 定义规则
     */
    @PostConstruct
    private  static void initFlowRules(){
        //流控规则
        List<FlowRule> rules = new ArrayList<>();
        FlowRule flowRule = new FlowRule();
        //设置保护的资源
        flowRule.setResource(RESOURCE_NAME);
        //设置规则，qps
        flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        //设置qps量
        flowRule.setCount(1);
        rules.add(flowRule);
        //加载控制
        FlowRuleManager.loadRules(rules);
    }

    /**
     * 使用@SentinelResource进行Sentinel流控
     * 使用@SentinelResource 注解改善接口钟资源定义和被流控降级后的处理方法
     * 使用方法：1、添加依赖
     *           2、配置bean-SentinelResourceAspect
     *  value:定义流控资源
     *  blockHandler：设置流控降级后的处理方法（默认该方法必须声明在同一个类）
     *      如果不想在同一个类中，可以使用 blockHandlerClass 指定，但是方法必须是static
     *  fallback：当接口出现异常，就可以交给fallback指定的方法进行处理
     *      如果不想在同一个类中，可以使用 fallbackClass 指定，但是方法必须是static
     *
     *  注意：如果blockHandler和fallback方法同时指定了，则blockHandler优先级更高
     * @param id 编码
     * @return 用户
     */
    @RequestMapping(value = "/user")
    @SentinelResource(value = RESOURCE_NAME,blockHandler = "flowHandlerForTest",fallback = "flowBackForUser")
    public User userTest(String id){
        int i = 10;
        User user = new User();
        user.setId(String.valueOf(i));
        user.setName("不知道");
        return user;
    }

    /**
     * userTest流控降级后的处理方法
     * 注意：
     * 1、一定要是public
     * 2、返回值一定要和源方法（userTest）保证一致，包含源方法的参数
     * 3、可以在参数最后添加BlockException，可以区分是什么规则的处理方法
     * @param id 传参
     * @param exception 报错类型
     * @return 返回
     */
    public User flowHandlerForTest(String id ,BlockException exception){
        exception.printStackTrace();
        User user = new User();
        user.setName("流控！");
        user.setId(id);
        return user;
    }

    /**
     * userTest异常后的处理方法
     * 注意：
     * 1、一定要是public
     * 2、返回值一定要和源方法（userTest）保证一致，包含源方法的参数
     * 3、可以在参数最后添加Throwable，可以区分是什么异常
     * @param id 参数
     * @param e 异常
     * @return
     */
    public User flowBackForUser(String id,Throwable e){
        e.printStackTrace();
        User user = new User();
        user.setName("异常处理！");
        user.setId(id);
        return user;
    }

    /**
     * 流量降级规则
     */
    @PostConstruct
    public  static  void initDegradeRules(){
        List<DegradeRule> degradeRules = new ArrayList<>();
        DegradeRule degradeRule = new DegradeRule();
        //设置要保护的资源
        degradeRule.setResource(DEGRADE_RESOURCE_NAME);
        //设置规则测率： 异常数
        degradeRule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT);
        //设置异常数
        degradeRule.setCount(2);
        //设置频率
        degradeRule.setTimeWindow(10);
        //设置最小频率
        degradeRule.setMinRequestAmount(2);
        degradeRules.add(degradeRule);
        DegradeRuleManager.loadRules(degradeRules);
    }
}
