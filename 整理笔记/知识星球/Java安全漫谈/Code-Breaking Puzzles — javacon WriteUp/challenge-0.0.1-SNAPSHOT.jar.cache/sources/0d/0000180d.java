package org.springframework.boot.autoconfigure.task;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.boot.task.TaskSchedulerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.TaskManagementConfigUtils;

@EnableConfigurationProperties({TaskSchedulingProperties.class})
@ConditionalOnClass({ThreadPoolTaskScheduler.class})
@Configuration
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/task/TaskSchedulingAutoConfiguration.class */
public class TaskSchedulingAutoConfiguration {
    @ConditionalOnMissingBean({SchedulingConfigurer.class, TaskScheduler.class})
    @ConditionalOnBean(name = {TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME})
    @Bean
    public ThreadPoolTaskScheduler taskScheduler(TaskSchedulerBuilder builder) {
        return builder.build();
    }

    @ConditionalOnMissingBean
    @Bean
    public TaskSchedulerBuilder taskSchedulerBuilder(TaskSchedulingProperties properties, ObjectProvider<TaskSchedulerCustomizer> taskSchedulerCustomizers) {
        TaskSchedulerBuilder builder = new TaskSchedulerBuilder();
        return builder.poolSize(properties.getPool().getSize()).threadNamePrefix(properties.getThreadNamePrefix()).customizers(taskSchedulerCustomizers);
    }
}