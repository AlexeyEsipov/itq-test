package ru.itq.util.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class AdvancedExecutorConfig {

    @Value("${task.executor.pool.size:5}")
    private int poolSize;

    @Value("${task.executor.keepalive.seconds:60}")
    private int keepAliveSeconds;

    @Bean
    public ScheduledThreadPoolExecutor scheduledExecutor() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(poolSize);
        executor.setKeepAliveTime(keepAliveSeconds, TimeUnit.SECONDS);
        executor.allowCoreThreadTimeOut(true);
        executor.setRemoveOnCancelPolicy(true);
        return executor;
    }
}
