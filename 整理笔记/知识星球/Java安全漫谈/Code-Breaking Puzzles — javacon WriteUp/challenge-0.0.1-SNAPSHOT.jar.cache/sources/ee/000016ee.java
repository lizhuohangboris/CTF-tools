package org.springframework.boot.autoconfigure.jdbc;

import javax.sql.DataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceInitializerPostProcessor.class */
class DataSourceInitializerPostProcessor implements BeanPostProcessor, Ordered {
    @Autowired
    private BeanFactory beanFactory;

    DataSourceInitializerPostProcessor() {
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return -2147483647;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSource) {
            this.beanFactory.getBean(DataSourceInitializerInvoker.class);
        }
        return bean;
    }
}