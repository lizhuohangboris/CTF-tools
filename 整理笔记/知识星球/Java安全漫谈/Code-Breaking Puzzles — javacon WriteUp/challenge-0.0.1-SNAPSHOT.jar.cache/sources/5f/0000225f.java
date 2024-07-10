package org.springframework.remoting.httpinvoker;

import java.io.InvalidClassException;
import java.net.ConnectException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.lang.Nullable;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteInvocationFailureException;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationBasedAccessor;
import org.springframework.remoting.support.RemoteInvocationResult;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/remoting/httpinvoker/HttpInvokerClientInterceptor.class */
public class HttpInvokerClientInterceptor extends RemoteInvocationBasedAccessor implements MethodInterceptor, HttpInvokerClientConfiguration {
    @Nullable
    private String codebaseUrl;
    @Nullable
    private HttpInvokerRequestExecutor httpInvokerRequestExecutor;

    public void setCodebaseUrl(@Nullable String codebaseUrl) {
        this.codebaseUrl = codebaseUrl;
    }

    @Override // org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration
    @Nullable
    public String getCodebaseUrl() {
        return this.codebaseUrl;
    }

    public void setHttpInvokerRequestExecutor(HttpInvokerRequestExecutor httpInvokerRequestExecutor) {
        this.httpInvokerRequestExecutor = httpInvokerRequestExecutor;
    }

    public HttpInvokerRequestExecutor getHttpInvokerRequestExecutor() {
        if (this.httpInvokerRequestExecutor == null) {
            SimpleHttpInvokerRequestExecutor executor = new SimpleHttpInvokerRequestExecutor();
            executor.setBeanClassLoader(getBeanClassLoader());
            this.httpInvokerRequestExecutor = executor;
        }
        return this.httpInvokerRequestExecutor;
    }

    @Override // org.springframework.remoting.support.UrlBasedRemoteAccessor, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        getHttpInvokerRequestExecutor();
    }

    @Override // org.aopalliance.intercept.MethodInterceptor
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        if (AopUtils.isToStringMethod(methodInvocation.getMethod())) {
            return "HTTP invoker proxy for service URL [" + getServiceUrl() + "]";
        }
        RemoteInvocation invocation = createRemoteInvocation(methodInvocation);
        try {
            RemoteInvocationResult result = executeRequest(invocation, methodInvocation);
            try {
                return recreateRemoteInvocationResult(result);
            } catch (Throwable ex) {
                if (result.hasInvocationTargetException()) {
                    throw ex;
                }
                throw new RemoteInvocationFailureException("Invocation of method [" + methodInvocation.getMethod() + "] failed in HTTP invoker remote service at [" + getServiceUrl() + "]", ex);
            }
        } catch (Throwable ex2) {
            RemoteAccessException rae = convertHttpInvokerAccessException(ex2);
            if (rae != null) {
                throw rae;
            }
            throw ex2;
        }
    }

    protected RemoteInvocationResult executeRequest(RemoteInvocation invocation, MethodInvocation originalInvocation) throws Exception {
        return executeRequest(invocation);
    }

    protected RemoteInvocationResult executeRequest(RemoteInvocation invocation) throws Exception {
        return getHttpInvokerRequestExecutor().executeRequest(this, invocation);
    }

    @Nullable
    protected RemoteAccessException convertHttpInvokerAccessException(Throwable ex) {
        if (ex instanceof ConnectException) {
            return new RemoteConnectFailureException("Could not connect to HTTP invoker remote service at [" + getServiceUrl() + "]", ex);
        }
        if ((ex instanceof ClassNotFoundException) || (ex instanceof NoClassDefFoundError) || (ex instanceof InvalidClassException)) {
            return new RemoteAccessException("Could not deserialize result from HTTP invoker remote service [" + getServiceUrl() + "]", ex);
        }
        if (ex instanceof Exception) {
            return new RemoteAccessException("Could not access HTTP invoker remote service at [" + getServiceUrl() + "]", ex);
        }
        return null;
    }
}