package org.springframework.context;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/LifecycleProcessor.class */
public interface LifecycleProcessor extends Lifecycle {
    void onRefresh();

    void onClose();
}