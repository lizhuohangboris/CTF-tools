package org.springframework.aop.support;

import java.io.Serializable;
import org.aopalliance.aop.Advice;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/support/AbstractPointcutAdvisor.class */
public abstract class AbstractPointcutAdvisor implements PointcutAdvisor, Ordered, Serializable {
    @Nullable
    private Integer order;

    public void setOrder(int order) {
        this.order = Integer.valueOf(order);
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        if (this.order != null) {
            return this.order.intValue();
        }
        Advice advice = getAdvice();
        if (advice instanceof Ordered) {
            return ((Ordered) advice).getOrder();
        }
        return Integer.MAX_VALUE;
    }

    @Override // org.springframework.aop.Advisor
    public boolean isPerInstance() {
        return true;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PointcutAdvisor)) {
            return false;
        }
        PointcutAdvisor otherAdvisor = (PointcutAdvisor) other;
        return ObjectUtils.nullSafeEquals(getAdvice(), otherAdvisor.getAdvice()) && ObjectUtils.nullSafeEquals(getPointcut(), otherAdvisor.getPointcut());
    }

    public int hashCode() {
        return PointcutAdvisor.class.hashCode();
    }
}