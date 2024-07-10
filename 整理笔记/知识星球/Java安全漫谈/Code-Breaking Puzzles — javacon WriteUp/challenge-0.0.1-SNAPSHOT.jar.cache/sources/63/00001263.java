package org.springframework.aop.aspectj;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.AfterAdvice;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/AspectJAfterThrowingAdvice.class */
public class AspectJAfterThrowingAdvice extends AbstractAspectJAdvice implements MethodInterceptor, AfterAdvice, Serializable {
    public AspectJAfterThrowingAdvice(Method aspectJBeforeAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aif) {
        super(aspectJBeforeAdviceMethod, pointcut, aif);
    }

    @Override // org.springframework.aop.aspectj.AspectJPrecedenceInformation
    public boolean isBeforeAdvice() {
        return false;
    }

    @Override // org.springframework.aop.aspectj.AspectJPrecedenceInformation
    public boolean isAfterAdvice() {
        return true;
    }

    @Override // org.springframework.aop.aspectj.AbstractAspectJAdvice
    public void setThrowingName(String name) {
        setThrowingNameNoCheck(name);
    }

    @Override // org.aopalliance.intercept.MethodInterceptor
    public Object invoke(MethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        } catch (Throwable ex) {
            if (shouldInvokeOnThrowing(ex)) {
                invokeAdviceMethod(getJoinPointMatch(), null, ex);
            }
            throw ex;
        }
    }

    private boolean shouldInvokeOnThrowing(Throwable ex) {
        return getDiscoveredThrowingType().isAssignableFrom(ex.getClass());
    }
}