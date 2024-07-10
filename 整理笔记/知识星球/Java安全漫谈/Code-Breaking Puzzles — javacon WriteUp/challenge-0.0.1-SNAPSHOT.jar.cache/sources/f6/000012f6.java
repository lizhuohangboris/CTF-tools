package org.springframework.aop.framework.autoproxy.target;

import org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource;
import org.springframework.aop.target.CommonsPool2TargetSource;
import org.springframework.aop.target.PrototypeTargetSource;
import org.springframework.aop.target.ThreadLocalTargetSource;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/autoproxy/target/QuickTargetSourceCreator.class */
public class QuickTargetSourceCreator extends AbstractBeanFactoryBasedTargetSourceCreator {
    public static final String PREFIX_COMMONS_POOL = ":";
    public static final String PREFIX_THREAD_LOCAL = "%";
    public static final String PREFIX_PROTOTYPE = "!";

    @Override // org.springframework.aop.framework.autoproxy.target.AbstractBeanFactoryBasedTargetSourceCreator
    @Nullable
    protected final AbstractBeanFactoryBasedTargetSource createBeanFactoryBasedTargetSource(Class<?> beanClass, String beanName) {
        if (beanName.startsWith(":")) {
            CommonsPool2TargetSource cpts = new CommonsPool2TargetSource();
            cpts.setMaxSize(25);
            return cpts;
        } else if (beanName.startsWith(PREFIX_THREAD_LOCAL)) {
            return new ThreadLocalTargetSource();
        } else {
            if (beanName.startsWith("!")) {
                return new PrototypeTargetSource();
            }
            return null;
        }
    }
}