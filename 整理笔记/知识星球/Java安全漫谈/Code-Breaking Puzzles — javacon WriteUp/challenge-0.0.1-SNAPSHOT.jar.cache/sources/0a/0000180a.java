package org.springframework.boot.autoconfigure.task;

import java.util.concurrent.Executor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.boot.task.TaskExecutorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableConfigurationProperties({TaskExecutionProperties.class})
@ConditionalOnClass({ThreadPoolTaskExecutor.class})
@Configuration
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/task/TaskExecutionAutoConfiguration.class */
public class TaskExecutionAutoConfiguration {
    public static final String APPLICATION_TASK_EXECUTOR_BEAN_NAME = "applicationTaskExecutor";
    private final TaskExecutionProperties properties;
    private final ObjectProvider<TaskExecutorCustomizer> taskExecutorCustomizers;
    private final ObjectProvider<TaskDecorator> taskDecorator;

    public TaskExecutionAutoConfiguration(TaskExecutionProperties properties, ObjectProvider<TaskExecutorCustomizer> taskExecutorCustomizers, ObjectProvider<TaskDecorator> taskDecorator) {
        this.properties = properties;
        this.taskExecutorCustomizers = taskExecutorCustomizers;
        this.taskDecorator = taskDecorator;
    }

    @ConditionalOnMissingBean
    @Bean
    public TaskExecutorBuilder taskExecutorBuilder() {
        TaskExecutionProperties.Pool pool = this.properties.getPool();
        TaskExecutorBuilder builder = new TaskExecutorBuilder();
        return builder.queueCapacity(pool.getQueueCapacity()).corePoolSize(pool.getCoreSize()).maxPoolSize(pool.getMaxSize()).allowCoreThreadTimeOut(pool.isAllowCoreThreadTimeout()).keepAlive(pool.getKeepAlive()).threadNamePrefix(this.properties.getThreadNamePrefix()).customizers(this.taskExecutorCustomizers).taskDecorator(this.taskDecorator.getIfUnique());
    }

    @ConditionalOnMissingBean({Executor.class})
    @Lazy
    @Bean(name = {APPLICATION_TASK_EXECUTOR_BEAN_NAME})
    public ThreadPoolTaskExecutor applicationTaskExecutor(TaskExecutorBuilder builder) {
        return builder.build();
    }
}