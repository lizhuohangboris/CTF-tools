package org.springframework.aop.interceptor;

import java.lang.reflect.Method;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/interceptor/SimpleAsyncUncaughtExceptionHandler.class */
public class SimpleAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {
    private static final Log logger = LogFactory.getLog(SimpleAsyncUncaughtExceptionHandler.class);

    @Override // org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        if (logger.isErrorEnabled()) {
            logger.error("Unexpected exception occurred invoking async method: " + method, ex);
        }
    }
}