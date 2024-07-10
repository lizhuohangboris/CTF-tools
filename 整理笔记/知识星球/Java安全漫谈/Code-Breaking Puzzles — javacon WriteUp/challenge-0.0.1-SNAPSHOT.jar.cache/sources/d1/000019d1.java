package org.springframework.boot.jta.atomikos;

import com.atomikos.icatch.jta.UserTransactionManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/jta/atomikos/AtomikosDependsOnBeanFactoryPostProcessor.class */
public class AtomikosDependsOnBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {
    private static final String[] NO_BEANS = new String[0];
    private int order = Integer.MAX_VALUE;

    @Override // org.springframework.beans.factory.config.BeanFactoryPostProcessor
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] transactionManagers = beanFactory.getBeanNamesForType(UserTransactionManager.class, true, false);
        for (String transactionManager : transactionManagers) {
            addTransactionManagerDependencies(beanFactory, transactionManager);
        }
        addMessageDrivenContainerDependencies(beanFactory, transactionManagers);
    }

    private void addTransactionManagerDependencies(ConfigurableListableBeanFactory beanFactory, String transactionManager) {
        BeanDefinition bean = beanFactory.getBeanDefinition(transactionManager);
        Set<String> dependsOn = new LinkedHashSet<>(asList(bean.getDependsOn()));
        int initialSize = dependsOn.size();
        addDependencies(beanFactory, "javax.jms.ConnectionFactory", dependsOn);
        addDependencies(beanFactory, "javax.sql.DataSource", dependsOn);
        if (dependsOn.size() != initialSize) {
            bean.setDependsOn(StringUtils.toStringArray(dependsOn));
        }
    }

    private void addMessageDrivenContainerDependencies(ConfigurableListableBeanFactory beanFactory, String[] transactionManagers) {
        String[] messageDrivenContainers = getBeanNamesForType(beanFactory, "com.atomikos.jms.extra.MessageDrivenContainer");
        for (String messageDrivenContainer : messageDrivenContainers) {
            BeanDefinition bean = beanFactory.getBeanDefinition(messageDrivenContainer);
            Set<String> dependsOn = new LinkedHashSet<>(asList(bean.getDependsOn()));
            dependsOn.addAll(asList(transactionManagers));
            bean.setDependsOn(StringUtils.toStringArray(dependsOn));
        }
    }

    private void addDependencies(ConfigurableListableBeanFactory beanFactory, String type, Set<String> dependsOn) {
        dependsOn.addAll(asList(getBeanNamesForType(beanFactory, type)));
    }

    private String[] getBeanNamesForType(ConfigurableListableBeanFactory beanFactory, String type) {
        try {
            return beanFactory.getBeanNamesForType(Class.forName(type), true, false);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return NO_BEANS;
        }
    }

    private List<String> asList(String[] array) {
        return array != null ? Arrays.asList(array) : Collections.emptyList();
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}