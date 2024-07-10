package org.springframework.aop.aspectj;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.weaver.tools.JoinPointMatch;
import org.springframework.aop.ProxyMethodInvocation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/AspectJAroundAdvice.class */
public class AspectJAroundAdvice extends AbstractAspectJAdvice implements MethodInterceptor, Serializable {
    public AspectJAroundAdvice(Method aspectJAroundAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aif) {
        super(aspectJAroundAdviceMethod, pointcut, aif);
    }

    @Override // org.springframework.aop.aspectj.AspectJPrecedenceInformation
    public boolean isBeforeAdvice() {
        return false;
    }

    @Override // org.springframework.aop.aspectj.AspectJPrecedenceInformation
    public boolean isAfterAdvice() {
        return false;
    }

    @Override // org.springframework.aop.aspectj.AbstractAspectJAdvice
    protected boolean supportsProceedingJoinPoint() {
        return true;
    }

    @Override // org.aopalliance.intercept.MethodInterceptor
    public Object invoke(MethodInvocation mi) throws Throwable {
        if (!(mi instanceof ProxyMethodInvocation)) {
            throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
        }
        ProxyMethodInvocation pmi = (ProxyMethodInvocation) mi;
        ProceedingJoinPoint pjp = lazyGetProceedingJoinPoint(pmi);
        JoinPointMatch jpm = getJoinPointMatch(pmi);
        return invokeAdviceMethod(pjp, jpm, null, null);
    }

    protected ProceedingJoinPoint lazyGetProceedingJoinPoint(ProxyMethodInvocation rmi) {
        return new MethodInvocationProceedingJoinPoint(rmi);
    }
}