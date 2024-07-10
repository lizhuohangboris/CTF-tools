package ch.qos.logback.core;

import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.spi.PropertyContainer;
import ch.qos.logback.core.status.StatusManager;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/Context.class */
public interface Context extends PropertyContainer {
    StatusManager getStatusManager();

    Object getObject(String str);

    void putObject(String str, Object obj);

    @Override // ch.qos.logback.core.spi.PropertyContainer
    String getProperty(String str);

    void putProperty(String str, String str2);

    @Override // ch.qos.logback.core.spi.PropertyContainer
    Map<String, String> getCopyOfPropertyMap();

    String getName();

    void setName(String str);

    long getBirthTime();

    Object getConfigurationLock();

    ScheduledExecutorService getScheduledExecutorService();

    ExecutorService getExecutorService();

    void register(LifeCycle lifeCycle);

    void addScheduledFuture(ScheduledFuture<?> scheduledFuture);
}