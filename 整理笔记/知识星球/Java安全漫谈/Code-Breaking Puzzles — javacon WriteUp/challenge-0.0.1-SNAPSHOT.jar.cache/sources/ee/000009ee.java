package org.apache.commons.logging;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-jcl-5.1.2.RELEASE.jar:org/apache/commons/logging/LogFactory.class */
public abstract class LogFactory {
    public static Log getLog(Class<?> clazz) {
        return getLog(clazz.getName());
    }

    public static Log getLog(String name) {
        return LogAdapter.createLog(name);
    }

    @Deprecated
    public static LogFactory getFactory() {
        return new LogFactory() { // from class: org.apache.commons.logging.LogFactory.1
        };
    }

    @Deprecated
    public Log getInstance(Class<?> clazz) {
        return getLog(clazz);
    }

    @Deprecated
    public Log getInstance(String name) {
        return getLog(name);
    }
}