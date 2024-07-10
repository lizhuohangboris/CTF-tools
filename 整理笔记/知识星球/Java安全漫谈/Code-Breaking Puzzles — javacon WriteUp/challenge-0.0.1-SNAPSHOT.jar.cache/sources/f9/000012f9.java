package org.springframework.aop.interceptor;

import java.io.Serializable;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/interceptor/AbstractTraceInterceptor.class */
public abstract class AbstractTraceInterceptor implements MethodInterceptor, Serializable {
    @Nullable
    protected transient Log defaultLogger = LogFactory.getLog(getClass());
    private boolean hideProxyClassNames = false;
    private boolean logExceptionStackTrace = true;

    protected abstract Object invokeUnderTrace(MethodInvocation methodInvocation, Log log) throws Throwable;

    public void setUseDynamicLogger(boolean useDynamicLogger) {
        this.defaultLogger = useDynamicLogger ? null : LogFactory.getLog(getClass());
    }

    public void setLoggerName(String loggerName) {
        this.defaultLogger = LogFactory.getLog(loggerName);
    }

    public void setHideProxyClassNames(boolean hideProxyClassNames) {
        this.hideProxyClassNames = hideProxyClassNames;
    }

    public void setLogExceptionStackTrace(boolean logExceptionStackTrace) {
        this.logExceptionStackTrace = logExceptionStackTrace;
    }

    @Override // org.aopalliance.intercept.MethodInterceptor
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Log logger = getLoggerForInvocation(invocation);
        if (isInterceptorEnabled(invocation, logger)) {
            return invokeUnderTrace(invocation, logger);
        }
        return invocation.proceed();
    }

    protected Log getLoggerForInvocation(MethodInvocation invocation) {
        if (this.defaultLogger != null) {
            return this.defaultLogger;
        }
        Object target = invocation.getThis();
        return LogFactory.getLog(getClassForLogging(target));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Class<?> getClassForLogging(Object target) {
        return this.hideProxyClassNames ? AopUtils.getTargetClass(target) : target.getClass();
    }

    protected boolean isInterceptorEnabled(MethodInvocation invocation, Log logger) {
        return isLogEnabled(logger);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isLogEnabled(Log logger) {
        return logger.isTraceEnabled();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writeToLog(Log logger, String message) {
        writeToLog(logger, message, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writeToLog(Log logger, String message, @Nullable Throwable ex) {
        if (ex != null && this.logExceptionStackTrace) {
            logger.trace(message, ex);
        } else {
            logger.trace(message);
        }
    }
}