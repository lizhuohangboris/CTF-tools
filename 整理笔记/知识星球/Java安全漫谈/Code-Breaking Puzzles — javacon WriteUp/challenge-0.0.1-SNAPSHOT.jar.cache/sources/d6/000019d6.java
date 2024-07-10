package org.springframework.boot.jta.bitronix;

import javax.transaction.TransactionManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/jta/bitronix/BitronixDependentBeanFactoryPostProcessor.class */
public class BitronixDependentBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {
    private static final String[] NO_BEANS = new String[0];
    private int order = Integer.MAX_VALUE;

    @Override // org.springframework.beans.factory.config.BeanFactoryPostProcessor
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] transactionManagers = beanFactory.getBeanNamesForType(TransactionManager.class, true, false);
        for (String transactionManager : transactionManagers) {
            addTransactionManagerDependencies(beanFactory, transactionManager);
        }
    }

    private void addTransactionManagerDependencies(ConfigurableListableBeanFactory beanFactory, String transactionManager) {
        String[] beanNamesForType;
        String[] beanNamesForType2;
        for (String dependentBeanName : getBeanNamesForType(beanFactory, "javax.jms.ConnectionFactory")) {
            beanFactory.registerDependentBean(transactionManager, dependentBeanName);
        }
        for (String dependentBeanName2 : getBeanNamesForType(beanFactory, "javax.sql.DataSource")) {
            beanFactory.registerDependentBean(transactionManager, dependentBeanName2);
        }
    }

    private String[] getBeanNamesForType(ConfigurableListableBeanFactory beanFactory, String type) {
        try {
            return beanFactory.getBeanNamesForType(Class.forName(type), true, false);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return NO_BEANS;
        }
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}