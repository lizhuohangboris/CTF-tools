package org.apache.catalina;

import java.util.concurrent.TimeUnit;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/Executor.class */
public interface Executor extends java.util.concurrent.Executor, Lifecycle {
    String getName();

    void execute(Runnable runnable, long j, TimeUnit timeUnit);
}