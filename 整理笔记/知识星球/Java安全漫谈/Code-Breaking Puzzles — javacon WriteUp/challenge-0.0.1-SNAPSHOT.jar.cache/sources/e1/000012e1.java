package org.springframework.aop.framework.adapter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/adapter/GlobalAdvisorAdapterRegistry.class */
public final class GlobalAdvisorAdapterRegistry {
    private static AdvisorAdapterRegistry instance = new DefaultAdvisorAdapterRegistry();

    private GlobalAdvisorAdapterRegistry() {
    }

    public static AdvisorAdapterRegistry getInstance() {
        return instance;
    }

    static void reset() {
        instance = new DefaultAdvisorAdapterRegistry();
    }
}