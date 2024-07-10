package org.apache.juli.logging;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/juli/logging/Log.class */
public interface Log {
    boolean isDebugEnabled();

    boolean isErrorEnabled();

    boolean isFatalEnabled();

    boolean isInfoEnabled();

    boolean isTraceEnabled();

    boolean isWarnEnabled();

    void trace(Object obj);

    void trace(Object obj, Throwable th);

    void debug(Object obj);

    void debug(Object obj, Throwable th);

    void info(Object obj);

    void info(Object obj, Throwable th);

    void warn(Object obj);

    void warn(Object obj, Throwable th);

    void error(Object obj);

    void error(Object obj, Throwable th);

    void fatal(Object obj);

    void fatal(Object obj, Throwable th);
}