package org.springframework.aop.framework.autoproxy;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/autoproxy/DefaultAdvisorAutoProxyCreator.class */
public class DefaultAdvisorAutoProxyCreator extends AbstractAdvisorAutoProxyCreator implements BeanNameAware {
    public static final String SEPARATOR = ".";
    private boolean usePrefix = false;
    @Nullable
    private String advisorBeanNamePrefix;

    public void setUsePrefix(boolean usePrefix) {
        this.usePrefix = usePrefix;
    }

    public boolean isUsePrefix() {
        return this.usePrefix;
    }

    public void setAdvisorBeanNamePrefix(@Nullable String advisorBeanNamePrefix) {
        this.advisorBeanNamePrefix = advisorBeanNamePrefix;
    }

    @Nullable
    public String getAdvisorBeanNamePrefix() {
        return this.advisorBeanNamePrefix;
    }

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(String name) {
        if (this.advisorBeanNamePrefix == null) {
            this.advisorBeanNamePrefix = name + ".";
        }
    }

    @Override // org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator
    protected boolean isEligibleAdvisorBean(String beanName) {
        if (!isUsePrefix()) {
            return true;
        }
        String prefix = getAdvisorBeanNamePrefix();
        return prefix != null && beanName.startsWith(prefix);
    }
}