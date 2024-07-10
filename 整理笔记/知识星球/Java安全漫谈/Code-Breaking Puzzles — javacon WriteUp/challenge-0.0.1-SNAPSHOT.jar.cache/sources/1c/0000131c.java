package org.springframework.aop.support;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.DynamicIntroductionAdvice;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.IntroductionInfo;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/support/DefaultIntroductionAdvisor.class */
public class DefaultIntroductionAdvisor implements IntroductionAdvisor, ClassFilter, Ordered, Serializable {
    private final Advice advice;
    private final Set<Class<?>> interfaces;
    private int order;

    public DefaultIntroductionAdvisor(Advice advice) {
        this(advice, advice instanceof IntroductionInfo ? (IntroductionInfo) advice : null);
    }

    public DefaultIntroductionAdvisor(Advice advice, @Nullable IntroductionInfo introductionInfo) {
        this.interfaces = new LinkedHashSet();
        this.order = Integer.MAX_VALUE;
        Assert.notNull(advice, "Advice must not be null");
        this.advice = advice;
        if (introductionInfo != null) {
            Class<?>[] introducedInterfaces = introductionInfo.getInterfaces();
            if (introducedInterfaces.length == 0) {
                throw new IllegalArgumentException("IntroductionAdviceSupport implements no interfaces");
            }
            for (Class<?> ifc : introducedInterfaces) {
                addInterface(ifc);
            }
        }
    }

    public DefaultIntroductionAdvisor(DynamicIntroductionAdvice advice, Class<?> intf) {
        this.interfaces = new LinkedHashSet();
        this.order = Integer.MAX_VALUE;
        Assert.notNull(advice, "Advice must not be null");
        this.advice = advice;
        addInterface(intf);
    }

    public void addInterface(Class<?> intf) {
        Assert.notNull(intf, "Interface must not be null");
        if (!intf.isInterface()) {
            throw new IllegalArgumentException("Specified class [" + intf.getName() + "] must be an interface");
        }
        this.interfaces.add(intf);
    }

    @Override // org.springframework.aop.IntroductionInfo
    public Class<?>[] getInterfaces() {
        return ClassUtils.toClassArray(this.interfaces);
    }

    @Override // org.springframework.aop.IntroductionAdvisor
    public void validateInterfaces() throws IllegalArgumentException {
        for (Class<?> ifc : this.interfaces) {
            if ((this.advice instanceof DynamicIntroductionAdvice) && !((DynamicIntroductionAdvice) this.advice).implementsInterface(ifc)) {
                throw new IllegalArgumentException("DynamicIntroductionAdvice [" + this.advice + "] does not implement interface [" + ifc.getName() + "] specified for introduction");
            }
        }
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Override // org.springframework.aop.Advisor
    public Advice getAdvice() {
        return this.advice;
    }

    @Override // org.springframework.aop.Advisor
    public boolean isPerInstance() {
        return true;
    }

    @Override // org.springframework.aop.IntroductionAdvisor
    public ClassFilter getClassFilter() {
        return this;
    }

    @Override // org.springframework.aop.ClassFilter
    public boolean matches(Class<?> clazz) {
        return true;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DefaultIntroductionAdvisor)) {
            return false;
        }
        DefaultIntroductionAdvisor otherAdvisor = (DefaultIntroductionAdvisor) other;
        return this.advice.equals(otherAdvisor.advice) && this.interfaces.equals(otherAdvisor.interfaces);
    }

    public int hashCode() {
        return (this.advice.hashCode() * 13) + this.interfaces.hashCode();
    }

    public String toString() {
        return ClassUtils.getShortName(getClass()) + ": advice [" + this.advice + "]; interfaces " + ClassUtils.classNamesToString(this.interfaces);
    }
}