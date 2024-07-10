package org.springframework.boot.autoconfigure.quartz;

import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/quartz/SchedulerFactoryBeanCustomizer.class */
public interface SchedulerFactoryBeanCustomizer {
    void customize(SchedulerFactoryBean schedulerFactoryBean);
}