package org.springframework.web.method;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.OrderUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/method/ControllerAdviceBean.class */
public class ControllerAdviceBean implements Ordered {
    private final Object bean;
    @Nullable
    private final BeanFactory beanFactory;
    private final int order;
    private final HandlerTypePredicate beanTypePredicate;

    public ControllerAdviceBean(Object bean) {
        this(bean, (BeanFactory) null);
    }

    public ControllerAdviceBean(String beanName, @Nullable BeanFactory beanFactory) {
        this((Object) beanName, beanFactory);
    }

    private ControllerAdviceBean(Object bean, @Nullable BeanFactory beanFactory) {
        Class<?> beanType;
        this.bean = bean;
        this.beanFactory = beanFactory;
        if (bean instanceof String) {
            String beanName = (String) bean;
            Assert.hasText(beanName, "Bean name must not be null");
            Assert.notNull(beanFactory, "BeanFactory must not be null");
            if (!beanFactory.containsBean(beanName)) {
                throw new IllegalArgumentException("BeanFactory [" + beanFactory + "] does not contain specified controller advice bean '" + beanName + "'");
            }
            beanType = this.beanFactory.getType(beanName);
            this.order = initOrderFromBeanType(beanType);
        } else {
            Assert.notNull(bean, "Bean must not be null");
            beanType = bean.getClass();
            this.order = initOrderFromBean(bean);
        }
        ControllerAdvice annotation = beanType != null ? (ControllerAdvice) AnnotatedElementUtils.findMergedAnnotation(beanType, ControllerAdvice.class) : null;
        if (annotation != null) {
            this.beanTypePredicate = HandlerTypePredicate.builder().basePackage(annotation.basePackages()).basePackageClass(annotation.basePackageClasses()).assignableType(annotation.assignableTypes()).annotation(annotation.annotations()).build();
        } else {
            this.beanTypePredicate = HandlerTypePredicate.forAnyHandlerType();
        }
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Nullable
    public Class<?> getBeanType() {
        Class<?> beanType = this.bean instanceof String ? obtainBeanFactory().getType((String) this.bean) : this.bean.getClass();
        if (beanType != null) {
            return ClassUtils.getUserClass(beanType);
        }
        return null;
    }

    public Object resolveBean() {
        return this.bean instanceof String ? obtainBeanFactory().getBean((String) this.bean) : this.bean;
    }

    private BeanFactory obtainBeanFactory() {
        Assert.state(this.beanFactory != null, "No BeanFactory set");
        return this.beanFactory;
    }

    public boolean isApplicableToBeanType(@Nullable Class<?> beanType) {
        return this.beanTypePredicate.test(beanType);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ControllerAdviceBean)) {
            return false;
        }
        ControllerAdviceBean otherAdvice = (ControllerAdviceBean) other;
        return this.bean.equals(otherAdvice.bean) && this.beanFactory == otherAdvice.beanFactory;
    }

    public int hashCode() {
        return this.bean.hashCode();
    }

    public String toString() {
        return this.bean.toString();
    }

    public static List<ControllerAdviceBean> findAnnotatedBeans(ApplicationContext context) {
        return (List) Arrays.stream(BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context, Object.class)).filter(name -> {
            return context.findAnnotationOnBean(name, ControllerAdvice.class) != null;
        }).map(name2 -> {
            return new ControllerAdviceBean(name2, (BeanFactory) context);
        }).collect(Collectors.toList());
    }

    private static int initOrderFromBean(Object bean) {
        return bean instanceof Ordered ? ((Ordered) bean).getOrder() : initOrderFromBeanType(bean.getClass());
    }

    private static int initOrderFromBeanType(@Nullable Class<?> beanType) {
        Integer order = null;
        if (beanType != null) {
            order = OrderUtils.getOrder(beanType);
        }
        if (order != null) {
            return order.intValue();
        }
        return Integer.MAX_VALUE;
    }
}