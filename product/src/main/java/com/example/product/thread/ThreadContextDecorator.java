package com.example.product.thread;


import org.jetbrains.annotations.NotNull;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Executor;


/**
 * @author wsj
 * @description 线程装饰器
 * @date 2024/4/10
 */
public class ThreadContextDecorator implements TaskDecorator {
    @Override
    public @NotNull Runnable decorate(@NotNull Runnable runnable) {
        RequestAttributes context = RequestContextHolder.currentRequestAttributes();
        return () -> {
            try {
                RequestContextHolder.setRequestAttributes(context);
                runnable.run();
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        };
    }

    // 使用装饰器包装线程池，确保任务执行时可以获取到主线程的请求上下文信息
    public static Executor decorateExecutor(Executor executor) {
        return (runnable) -> {
            Runnable decoratedRunnable = new ThreadContextDecorator().decorate(runnable);
            executor.execute(decoratedRunnable);
        };
    }
}
