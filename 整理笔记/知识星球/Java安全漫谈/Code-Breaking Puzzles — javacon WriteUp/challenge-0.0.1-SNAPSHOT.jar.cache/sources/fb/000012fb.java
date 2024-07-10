package org.springframework.aop.interceptor;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.Ordered;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/interceptor/AsyncExecutionInterceptor.class */
public class AsyncExecutionInterceptor extends AsyncExecutionAspectSupport implements MethodInterceptor, Ordered {
    public AsyncExecutionInterceptor(@Nullable Executor defaultExecutor) {
        super(defaultExecutor);
    }

    public AsyncExecutionInterceptor(@Nullable Executor defaultExecutor, AsyncUncaughtExceptionHandler exceptionHandler) {
        super(defaultExecutor, exceptionHandler);
    }

    @Override // org.aopalliance.intercept.MethodInterceptor
    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> targetClass = invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null;
        Method specificMethod = ClassUtils.getMostSpecificMethod(invocation.getMethod(), targetClass);
        Method userDeclaredMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
        AsyncTaskExecutor executor = determineAsyncExecutor(userDeclaredMethod);
        if (executor == null) {
            throw new IllegalStateException("No executor specified and no default executor set on AsyncExecutionInterceptor either");
        }
        Callable<Object> task = () -> {
            try {
                Object result = invocation.proceed();
                if (result instanceof Future) {
                    return ((Future) result).get();
                }
                return null;
            } catch (ExecutionException ex) {
                handleError(ex.getCause(), userDeclaredMethod, invocation.getArguments());
                return null;
            } catch (Throwable ex2) {
                handleError(ex2, userDeclaredMethod, invocation.getArguments());
                return null;
            }
        };
        return doSubmit(task, executor, invocation.getMethod().getReturnType());
    }

    @Override // org.springframework.aop.interceptor.AsyncExecutionAspectSupport
    @Nullable
    protected String getExecutorQualifier(Method method) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.aop.interceptor.AsyncExecutionAspectSupport
    @Nullable
    public Executor getDefaultExecutor(@Nullable BeanFactory beanFactory) {
        Executor defaultExecutor = super.getDefaultExecutor(beanFactory);
        return defaultExecutor != null ? defaultExecutor : new SimpleAsyncTaskExecutor();
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}