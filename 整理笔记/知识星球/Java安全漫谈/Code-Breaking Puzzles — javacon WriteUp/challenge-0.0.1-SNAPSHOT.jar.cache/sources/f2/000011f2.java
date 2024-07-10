package org.jboss.logging;

import org.jboss.logging.Logger;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/BasicLogger.class */
public interface BasicLogger {
    boolean isEnabled(Logger.Level level);

    boolean isTraceEnabled();

    void trace(Object obj);

    void trace(Object obj, Throwable th);

    void trace(String str, Object obj, Throwable th);

    void trace(String str, Object obj, Object[] objArr, Throwable th);

    void tracev(String str, Object... objArr);

    void tracev(String str, Object obj);

    void tracev(String str, Object obj, Object obj2);

    void tracev(String str, Object obj, Object obj2, Object obj3);

    void tracev(Throwable th, String str, Object... objArr);

    void tracev(Throwable th, String str, Object obj);

    void tracev(Throwable th, String str, Object obj, Object obj2);

    void tracev(Throwable th, String str, Object obj, Object obj2, Object obj3);

    void tracef(String str, Object... objArr);

    void tracef(String str, Object obj);

    void tracef(String str, Object obj, Object obj2);

    void tracef(String str, Object obj, Object obj2, Object obj3);

    void tracef(Throwable th, String str, Object... objArr);

    void tracef(Throwable th, String str, Object obj);

    void tracef(Throwable th, String str, Object obj, Object obj2);

    void tracef(Throwable th, String str, Object obj, Object obj2, Object obj3);

    void tracef(String str, int i);

    void tracef(String str, int i, int i2);

    void tracef(String str, int i, Object obj);

    void tracef(String str, int i, int i2, int i3);

    void tracef(String str, int i, int i2, Object obj);

    void tracef(String str, int i, Object obj, Object obj2);

    void tracef(Throwable th, String str, int i);

    void tracef(Throwable th, String str, int i, int i2);

    void tracef(Throwable th, String str, int i, Object obj);

    void tracef(Throwable th, String str, int i, int i2, int i3);

    void tracef(Throwable th, String str, int i, int i2, Object obj);

    void tracef(Throwable th, String str, int i, Object obj, Object obj2);

    void tracef(String str, long j);

    void tracef(String str, long j, long j2);

    void tracef(String str, long j, Object obj);

    void tracef(String str, long j, long j2, long j3);

    void tracef(String str, long j, long j2, Object obj);

    void tracef(String str, long j, Object obj, Object obj2);

    void tracef(Throwable th, String str, long j);

    void tracef(Throwable th, String str, long j, long j2);

    void tracef(Throwable th, String str, long j, Object obj);

    void tracef(Throwable th, String str, long j, long j2, long j3);

    void tracef(Throwable th, String str, long j, long j2, Object obj);

    void tracef(Throwable th, String str, long j, Object obj, Object obj2);

    boolean isDebugEnabled();

    void debug(Object obj);

    void debug(Object obj, Throwable th);

    void debug(String str, Object obj, Throwable th);

    void debug(String str, Object obj, Object[] objArr, Throwable th);

    void debugv(String str, Object... objArr);

    void debugv(String str, Object obj);

    void debugv(String str, Object obj, Object obj2);

    void debugv(String str, Object obj, Object obj2, Object obj3);

    void debugv(Throwable th, String str, Object... objArr);

    void debugv(Throwable th, String str, Object obj);

    void debugv(Throwable th, String str, Object obj, Object obj2);

    void debugv(Throwable th, String str, Object obj, Object obj2, Object obj3);

    void debugf(String str, Object... objArr);

    void debugf(String str, Object obj);

    void debugf(String str, Object obj, Object obj2);

    void debugf(String str, Object obj, Object obj2, Object obj3);

    void debugf(Throwable th, String str, Object... objArr);

    void debugf(Throwable th, String str, Object obj);

    void debugf(Throwable th, String str, Object obj, Object obj2);

    void debugf(Throwable th, String str, Object obj, Object obj2, Object obj3);

    void debugf(String str, int i);

    void debugf(String str, int i, int i2);

    void debugf(String str, int i, Object obj);

    void debugf(String str, int i, int i2, int i3);

    void debugf(String str, int i, int i2, Object obj);

    void debugf(String str, int i, Object obj, Object obj2);

    void debugf(Throwable th, String str, int i);

    void debugf(Throwable th, String str, int i, int i2);

    void debugf(Throwable th, String str, int i, Object obj);

    void debugf(Throwable th, String str, int i, int i2, int i3);

    void debugf(Throwable th, String str, int i, int i2, Object obj);

    void debugf(Throwable th, String str, int i, Object obj, Object obj2);

    void debugf(String str, long j);

    void debugf(String str, long j, long j2);

    void debugf(String str, long j, Object obj);

    void debugf(String str, long j, long j2, long j3);

    void debugf(String str, long j, long j2, Object obj);

    void debugf(String str, long j, Object obj, Object obj2);

    void debugf(Throwable th, String str, long j);

    void debugf(Throwable th, String str, long j, long j2);

    void debugf(Throwable th, String str, long j, Object obj);

    void debugf(Throwable th, String str, long j, long j2, long j3);

    void debugf(Throwable th, String str, long j, long j2, Object obj);

    void debugf(Throwable th, String str, long j, Object obj, Object obj2);

    boolean isInfoEnabled();

    void info(Object obj);

    void info(Object obj, Throwable th);

    void info(String str, Object obj, Throwable th);

    void info(String str, Object obj, Object[] objArr, Throwable th);

    void infov(String str, Object... objArr);

    void infov(String str, Object obj);

    void infov(String str, Object obj, Object obj2);

    void infov(String str, Object obj, Object obj2, Object obj3);

    void infov(Throwable th, String str, Object... objArr);

    void infov(Throwable th, String str, Object obj);

    void infov(Throwable th, String str, Object obj, Object obj2);

    void infov(Throwable th, String str, Object obj, Object obj2, Object obj3);

    void infof(String str, Object... objArr);

    void infof(String str, Object obj);

    void infof(String str, Object obj, Object obj2);

    void infof(String str, Object obj, Object obj2, Object obj3);

    void infof(Throwable th, String str, Object... objArr);

    void infof(Throwable th, String str, Object obj);

    void infof(Throwable th, String str, Object obj, Object obj2);

    void infof(Throwable th, String str, Object obj, Object obj2, Object obj3);

    void warn(Object obj);

    void warn(Object obj, Throwable th);

    void warn(String str, Object obj, Throwable th);

    void warn(String str, Object obj, Object[] objArr, Throwable th);

    void warnv(String str, Object... objArr);

    void warnv(String str, Object obj);

    void warnv(String str, Object obj, Object obj2);

    void warnv(String str, Object obj, Object obj2, Object obj3);

    void warnv(Throwable th, String str, Object... objArr);

    void warnv(Throwable th, String str, Object obj);

    void warnv(Throwable th, String str, Object obj, Object obj2);

    void warnv(Throwable th, String str, Object obj, Object obj2, Object obj3);

    void warnf(String str, Object... objArr);

    void warnf(String str, Object obj);

    void warnf(String str, Object obj, Object obj2);

    void warnf(String str, Object obj, Object obj2, Object obj3);

    void warnf(Throwable th, String str, Object... objArr);

    void warnf(Throwable th, String str, Object obj);

    void warnf(Throwable th, String str, Object obj, Object obj2);

    void warnf(Throwable th, String str, Object obj, Object obj2, Object obj3);

    void error(Object obj);

    void error(Object obj, Throwable th);

    void error(String str, Object obj, Throwable th);

    void error(String str, Object obj, Object[] objArr, Throwable th);

    void errorv(String str, Object... objArr);

    void errorv(String str, Object obj);

    void errorv(String str, Object obj, Object obj2);

    void errorv(String str, Object obj, Object obj2, Object obj3);

    void errorv(Throwable th, String str, Object... objArr);

    void errorv(Throwable th, String str, Object obj);

    void errorv(Throwable th, String str, Object obj, Object obj2);

    void errorv(Throwable th, String str, Object obj, Object obj2, Object obj3);

    void errorf(String str, Object... objArr);

    void errorf(String str, Object obj);

    void errorf(String str, Object obj, Object obj2);

    void errorf(String str, Object obj, Object obj2, Object obj3);

    void errorf(Throwable th, String str, Object... objArr);

    void errorf(Throwable th, String str, Object obj);

    void errorf(Throwable th, String str, Object obj, Object obj2);

    void errorf(Throwable th, String str, Object obj, Object obj2, Object obj3);

    void fatal(Object obj);

    void fatal(Object obj, Throwable th);

    void fatal(String str, Object obj, Throwable th);

    void fatal(String str, Object obj, Object[] objArr, Throwable th);

    void fatalv(String str, Object... objArr);

    void fatalv(String str, Object obj);

    void fatalv(String str, Object obj, Object obj2);

    void fatalv(String str, Object obj, Object obj2, Object obj3);

    void fatalv(Throwable th, String str, Object... objArr);

    void fatalv(Throwable th, String str, Object obj);

    void fatalv(Throwable th, String str, Object obj, Object obj2);

    void fatalv(Throwable th, String str, Object obj, Object obj2, Object obj3);

    void fatalf(String str, Object... objArr);

    void fatalf(String str, Object obj);

    void fatalf(String str, Object obj, Object obj2);

    void fatalf(String str, Object obj, Object obj2, Object obj3);

    void fatalf(Throwable th, String str, Object... objArr);

    void fatalf(Throwable th, String str, Object obj);

    void fatalf(Throwable th, String str, Object obj, Object obj2);

    void fatalf(Throwable th, String str, Object obj, Object obj2, Object obj3);

    void log(Logger.Level level, Object obj);

    void log(Logger.Level level, Object obj, Throwable th);

    void log(Logger.Level level, String str, Object obj, Throwable th);

    void log(String str, Logger.Level level, Object obj, Object[] objArr, Throwable th);

    void logv(Logger.Level level, String str, Object... objArr);

    void logv(Logger.Level level, String str, Object obj);

    void logv(Logger.Level level, String str, Object obj, Object obj2);

    void logv(Logger.Level level, String str, Object obj, Object obj2, Object obj3);

    void logv(Logger.Level level, Throwable th, String str, Object... objArr);

    void logv(Logger.Level level, Throwable th, String str, Object obj);

    void logv(Logger.Level level, Throwable th, String str, Object obj, Object obj2);

    void logv(Logger.Level level, Throwable th, String str, Object obj, Object obj2, Object obj3);

    void logv(String str, Logger.Level level, Throwable th, String str2, Object... objArr);

    void logv(String str, Logger.Level level, Throwable th, String str2, Object obj);

    void logv(String str, Logger.Level level, Throwable th, String str2, Object obj, Object obj2);

    void logv(String str, Logger.Level level, Throwable th, String str2, Object obj, Object obj2, Object obj3);

    void logf(Logger.Level level, String str, Object... objArr);

    void logf(Logger.Level level, String str, Object obj);

    void logf(Logger.Level level, String str, Object obj, Object obj2);

    void logf(Logger.Level level, String str, Object obj, Object obj2, Object obj3);

    void logf(Logger.Level level, Throwable th, String str, Object... objArr);

    void logf(Logger.Level level, Throwable th, String str, Object obj);

    void logf(Logger.Level level, Throwable th, String str, Object obj, Object obj2);

    void logf(Logger.Level level, Throwable th, String str, Object obj, Object obj2, Object obj3);

    void logf(String str, Logger.Level level, Throwable th, String str2, Object obj);

    void logf(String str, Logger.Level level, Throwable th, String str2, Object obj, Object obj2);

    void logf(String str, Logger.Level level, Throwable th, String str2, Object obj, Object obj2, Object obj3);

    void logf(String str, Logger.Level level, Throwable th, String str2, Object... objArr);
}