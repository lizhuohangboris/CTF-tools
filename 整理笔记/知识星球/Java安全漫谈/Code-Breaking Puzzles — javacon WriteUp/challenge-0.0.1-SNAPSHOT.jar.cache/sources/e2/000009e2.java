package org.apache.commons.logging;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-jcl-5.1.2.RELEASE.jar:org/apache/commons/logging/Log.class */
public interface Log {
    boolean isFatalEnabled();

    boolean isErrorEnabled();

    boolean isWarnEnabled();

    boolean isInfoEnabled();

    boolean isDebugEnabled();

    boolean isTraceEnabled();

    void fatal(Object obj);

    void fatal(Object obj, Throwable th);

    void error(Object obj);

    void error(Object obj, Throwable th);

    void warn(Object obj);

    void warn(Object obj, Throwable th);

    void info(Object obj);

    void info(Object obj, Throwable th);

    void debug(Object obj);

    void debug(Object obj, Throwable th);

    void trace(Object obj);

    void trace(Object obj, Throwable th);
}