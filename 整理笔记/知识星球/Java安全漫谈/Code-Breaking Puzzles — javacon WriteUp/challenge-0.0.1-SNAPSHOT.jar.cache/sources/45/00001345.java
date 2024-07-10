package org.springframework.aop.target;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/target/PoolingConfig.class */
public interface PoolingConfig {
    int getMaxSize();

    int getActiveCount() throws UnsupportedOperationException;

    int getIdleCount() throws UnsupportedOperationException;
}