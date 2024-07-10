package org.springframework.context;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/SmartLifecycle.class */
public interface SmartLifecycle extends Lifecycle, Phased {
    public static final int DEFAULT_PHASE = Integer.MAX_VALUE;

    default boolean isAutoStartup() {
        return true;
    }

    default void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override // org.springframework.context.Phased
    default int getPhase() {
        return Integer.MAX_VALUE;
    }
}