package org.springframework.aop.interceptor;

import com.jamonapi.MonKey;
import com.jamonapi.MonKeyImp;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.jamonapi.utils.Misc;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/interceptor/JamonPerformanceMonitorInterceptor.class */
public class JamonPerformanceMonitorInterceptor extends AbstractMonitoringInterceptor {
    private boolean trackAllInvocations = false;

    public JamonPerformanceMonitorInterceptor() {
    }

    public JamonPerformanceMonitorInterceptor(boolean useDynamicLogger) {
        setUseDynamicLogger(useDynamicLogger);
    }

    public JamonPerformanceMonitorInterceptor(boolean useDynamicLogger, boolean trackAllInvocations) {
        setUseDynamicLogger(useDynamicLogger);
        setTrackAllInvocations(trackAllInvocations);
    }

    public void setTrackAllInvocations(boolean trackAllInvocations) {
        this.trackAllInvocations = trackAllInvocations;
    }

    @Override // org.springframework.aop.interceptor.AbstractTraceInterceptor
    protected boolean isInterceptorEnabled(MethodInvocation invocation, Log logger) {
        return this.trackAllInvocations || isLogEnabled(logger);
    }

    @Override // org.springframework.aop.interceptor.AbstractTraceInterceptor
    protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
        String name = createInvocationTraceName(invocation);
        Monitor monitor = MonitorFactory.start(new MonKeyImp(name, name, "ms."));
        try {
            Object proceed = invocation.proceed();
            monitor.stop();
            if (!this.trackAllInvocations || isLogEnabled(logger)) {
                writeToLog(logger, "JAMon performance statistics for method [" + name + "]:\n" + monitor);
            }
            return proceed;
        } finally {
        }
    }

    protected void trackException(MonKey key, Throwable ex) {
        String stackTrace = "stackTrace=" + Misc.getExceptionTrace(ex);
        key.setDetails(stackTrace);
        MonitorFactory.add(new MonKeyImp(ex.getClass().getName(), stackTrace, "Exception"), 1.0d);
        MonitorFactory.add(new MonKeyImp("com.jamonapi.Exceptions", stackTrace, "Exception"), 1.0d);
    }
}