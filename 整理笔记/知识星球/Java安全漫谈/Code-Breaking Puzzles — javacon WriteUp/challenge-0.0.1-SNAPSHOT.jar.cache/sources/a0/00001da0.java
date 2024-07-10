package org.springframework.core;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/Ordered.class */
public interface Ordered {
    public static final int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;
    public static final int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    int getOrder();
}