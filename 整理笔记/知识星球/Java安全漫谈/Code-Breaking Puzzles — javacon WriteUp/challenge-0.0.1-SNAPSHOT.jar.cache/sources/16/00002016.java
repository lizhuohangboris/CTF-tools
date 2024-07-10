package org.springframework.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogDelegateFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/HttpLogging.class */
public abstract class HttpLogging {
    private static final Log fallbackLogger = LogFactory.getLog("org.springframework.web." + HttpLogging.class.getSimpleName());

    public static Log forLogName(Class<?> primaryLoggerClass) {
        Log primaryLogger = LogFactory.getLog(primaryLoggerClass);
        return forLog(primaryLogger);
    }

    public static Log forLog(Log primaryLogger) {
        return LogDelegateFactory.getCompositeLog(primaryLogger, fallbackLogger, new Log[0]);
    }
}