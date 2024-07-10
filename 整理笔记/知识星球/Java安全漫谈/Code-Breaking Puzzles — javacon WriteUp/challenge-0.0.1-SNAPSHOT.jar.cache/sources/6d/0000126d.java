package org.springframework.aop.aspectj;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/AspectJPointcutAdvisor.class */
public class AspectJPointcutAdvisor implements PointcutAdvisor, Ordered {
    private final AbstractAspectJAdvice advice;
    private final Pointcut pointcut;
    @Nullable
    private Integer order;

    public AspectJPointcutAdvisor(AbstractAspectJAdvice advice) {
        Assert.notNull(advice, "Advice must not be null");
        this.advice = advice;
        this.pointcut = advice.buildSafePointcut();
    }

    public void setOrder(int order) {
        this.order = Integer.valueOf(order);
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        if (this.order != null) {
            return this.order.intValue();
        }
        return this.advice.getOrder();
    }

    @Override // org.springframework.aop.Advisor
    public boolean isPerInstance() {
        return true;
    }

    @Override // org.springframework.aop.Advisor
    public Advice getAdvice() {
        return this.advice;
    }

    @Override // org.springframework.aop.PointcutAdvisor
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    public String getAspectName() {
        return this.advice.getAspectName();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AspectJPointcutAdvisor)) {
            return false;
        }
        AspectJPointcutAdvisor otherAdvisor = (AspectJPointcutAdvisor) other;
        return this.advice.equals(otherAdvisor.advice);
    }

    public int hashCode() {
        return (AspectJPointcutAdvisor.class.hashCode() * 29) + this.advice.hashCode();
    }
}