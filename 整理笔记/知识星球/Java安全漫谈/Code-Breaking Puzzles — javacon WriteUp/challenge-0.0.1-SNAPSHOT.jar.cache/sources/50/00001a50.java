package org.springframework.boot.task;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/task/TaskExecutorBuilder.class */
public class TaskExecutorBuilder {
    private final Integer queueCapacity;
    private final Integer corePoolSize;
    private final Integer maxPoolSize;
    private final Boolean allowCoreThreadTimeOut;
    private final Duration keepAlive;
    private final String threadNamePrefix;
    private final TaskDecorator taskDecorator;
    private final Set<TaskExecutorCustomizer> customizers;

    public TaskExecutorBuilder() {
        this.queueCapacity = null;
        this.corePoolSize = null;
        this.maxPoolSize = null;
        this.allowCoreThreadTimeOut = null;
        this.keepAlive = null;
        this.threadNamePrefix = null;
        this.taskDecorator = null;
        this.customizers = null;
    }

    private TaskExecutorBuilder(Integer queueCapacity, Integer corePoolSize, Integer maxPoolSize, Boolean allowCoreThreadTimeOut, Duration keepAlive, String threadNamePrefix, TaskDecorator taskDecorator, Set<TaskExecutorCustomizer> customizers) {
        this.queueCapacity = queueCapacity;
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        this.keepAlive = keepAlive;
        this.threadNamePrefix = threadNamePrefix;
        this.taskDecorator = taskDecorator;
        this.customizers = customizers;
    }

    public TaskExecutorBuilder queueCapacity(int queueCapacity) {
        return new TaskExecutorBuilder(Integer.valueOf(queueCapacity), this.corePoolSize, this.maxPoolSize, this.allowCoreThreadTimeOut, this.keepAlive, this.threadNamePrefix, this.taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder corePoolSize(int corePoolSize) {
        return new TaskExecutorBuilder(this.queueCapacity, Integer.valueOf(corePoolSize), this.maxPoolSize, this.allowCoreThreadTimeOut, this.keepAlive, this.threadNamePrefix, this.taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder maxPoolSize(int maxPoolSize) {
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, Integer.valueOf(maxPoolSize), this.allowCoreThreadTimeOut, this.keepAlive, this.threadNamePrefix, this.taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder allowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, this.maxPoolSize, Boolean.valueOf(allowCoreThreadTimeOut), this.keepAlive, this.threadNamePrefix, this.taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder keepAlive(Duration keepAlive) {
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, this.maxPoolSize, this.allowCoreThreadTimeOut, keepAlive, this.threadNamePrefix, this.taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder threadNamePrefix(String threadNamePrefix) {
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, this.maxPoolSize, this.allowCoreThreadTimeOut, this.keepAlive, threadNamePrefix, this.taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder taskDecorator(TaskDecorator taskDecorator) {
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, this.maxPoolSize, this.allowCoreThreadTimeOut, this.keepAlive, this.threadNamePrefix, taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder customizers(TaskExecutorCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return customizers(Arrays.asList(customizers));
    }

    public TaskExecutorBuilder customizers(Iterable<TaskExecutorCustomizer> customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, this.maxPoolSize, this.allowCoreThreadTimeOut, this.keepAlive, this.threadNamePrefix, this.taskDecorator, append(null, customizers));
    }

    public TaskExecutorBuilder additionalCustomizers(TaskExecutorCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return additionalCustomizers(Arrays.asList(customizers));
    }

    public TaskExecutorBuilder additionalCustomizers(Iterable<TaskExecutorCustomizer> customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, this.maxPoolSize, this.allowCoreThreadTimeOut, this.keepAlive, this.threadNamePrefix, this.taskDecorator, append(this.customizers, customizers));
    }

    public ThreadPoolTaskExecutor build() {
        return build(ThreadPoolTaskExecutor.class);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T extends ThreadPoolTaskExecutor> T build(Class<T> taskExecutorClass) {
        return (T) configure((ThreadPoolTaskExecutor) BeanUtils.instantiateClass(taskExecutorClass));
    }

    public <T extends ThreadPoolTaskExecutor> T configure(T taskExecutor) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        PropertyMapper.Source from = map.from((PropertyMapper) this.queueCapacity);
        taskExecutor.getClass();
        from.to((v1) -> {
            r1.setQueueCapacity(v1);
        });
        PropertyMapper.Source from2 = map.from((PropertyMapper) this.corePoolSize);
        taskExecutor.getClass();
        from2.to((v1) -> {
            r1.setCorePoolSize(v1);
        });
        PropertyMapper.Source from3 = map.from((PropertyMapper) this.maxPoolSize);
        taskExecutor.getClass();
        from3.to((v1) -> {
            r1.setMaxPoolSize(v1);
        });
        PropertyMapper.Source<Integer> asInt = map.from((PropertyMapper) this.keepAlive).asInt((v0) -> {
            return v0.getSeconds();
        });
        taskExecutor.getClass();
        asInt.to((v1) -> {
            r1.setKeepAliveSeconds(v1);
        });
        PropertyMapper.Source from4 = map.from((PropertyMapper) this.allowCoreThreadTimeOut);
        taskExecutor.getClass();
        from4.to((v1) -> {
            r1.setAllowCoreThreadTimeOut(v1);
        });
        PropertyMapper.Source whenHasText = map.from((PropertyMapper) this.threadNamePrefix).whenHasText();
        taskExecutor.getClass();
        whenHasText.to(this::setThreadNamePrefix);
        PropertyMapper.Source from5 = map.from((PropertyMapper) this.taskDecorator);
        taskExecutor.getClass();
        from5.to(this::setTaskDecorator);
        if (!CollectionUtils.isEmpty(this.customizers)) {
            this.customizers.forEach(customizer -> {
                customizer.customize(taskExecutor);
            });
        }
        return taskExecutor;
    }

    private <T> Set<T> append(Set<T> set, Iterable<? extends T> additions) {
        Set<T> result = new LinkedHashSet<>(set != null ? set : Collections.emptySet());
        result.getClass();
        additions.forEach(this::add);
        return Collections.unmodifiableSet(result);
    }
}