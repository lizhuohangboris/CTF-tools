package org.springframework.aop.framework.autoproxy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.aop.TargetSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/autoproxy/BeanNameAutoProxyCreator.class */
public class BeanNameAutoProxyCreator extends AbstractAutoProxyCreator {
    @Nullable
    private List<String> beanNames;

    public void setBeanNames(String... beanNames) {
        Assert.notEmpty(beanNames, "'beanNames' must not be empty");
        this.beanNames = new ArrayList(beanNames.length);
        for (String mappedName : beanNames) {
            this.beanNames.add(StringUtils.trimWhitespace(mappedName));
        }
    }

    @Override // org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator
    @Nullable
    protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName, @Nullable TargetSource targetSource) {
        if (this.beanNames != null) {
            Iterator<String> it = this.beanNames.iterator();
            while (it.hasNext()) {
                String mappedName = it.next();
                if (FactoryBean.class.isAssignableFrom(beanClass)) {
                    if (mappedName.startsWith(BeanFactory.FACTORY_BEAN_PREFIX)) {
                        mappedName = mappedName.substring(BeanFactory.FACTORY_BEAN_PREFIX.length());
                    } else {
                        continue;
                    }
                }
                if (isMatch(beanName, mappedName)) {
                    return PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS;
                }
                BeanFactory beanFactory = getBeanFactory();
                if (beanFactory != null) {
                    String[] aliases = beanFactory.getAliases(beanName);
                    for (String alias : aliases) {
                        if (isMatch(alias, mappedName)) {
                            return PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS;
                        }
                    }
                    continue;
                } else {
                    continue;
                }
            }
        }
        return DO_NOT_PROXY;
    }

    protected boolean isMatch(String beanName, String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, beanName);
    }
}