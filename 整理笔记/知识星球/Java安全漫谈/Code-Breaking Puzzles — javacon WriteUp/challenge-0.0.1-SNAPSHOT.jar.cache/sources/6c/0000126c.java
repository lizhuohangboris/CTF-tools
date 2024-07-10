package org.springframework.aop.aspectj;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/AspectJMethodBeforeAdvice.class */
public class AspectJMethodBeforeAdvice extends AbstractAspectJAdvice implements MethodBeforeAdvice, Serializable {
    public AspectJMethodBeforeAdvice(Method aspectJBeforeAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aif) {
        super(aspectJBeforeAdviceMethod, pointcut, aif);
    }

    @Override // org.springframework.aop.MethodBeforeAdvice
    public void before(Method method, Object[] args, @Nullable Object target) throws Throwable {
        invokeAdviceMethod(getJoinPointMatch(), null, null);
    }

    @Override // org.springframework.aop.aspectj.AspectJPrecedenceInformation
    public boolean isBeforeAdvice() {
        return true;
    }

    @Override // org.springframework.aop.aspectj.AspectJPrecedenceInformation
    public boolean isAfterAdvice() {
        return false;
    }
}