package org.springframework.beans.factory.support;

import java.util.function.Supplier;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/BeanDefinitionBuilder.class */
public final class BeanDefinitionBuilder {
    private final AbstractBeanDefinition beanDefinition;
    private int constructorArgIndex;

    public static BeanDefinitionBuilder genericBeanDefinition() {
        return new BeanDefinitionBuilder(new GenericBeanDefinition());
    }

    public static BeanDefinitionBuilder genericBeanDefinition(String beanClassName) {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder(new GenericBeanDefinition());
        builder.beanDefinition.setBeanClassName(beanClassName);
        return builder;
    }

    public static BeanDefinitionBuilder genericBeanDefinition(Class<?> beanClass) {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder(new GenericBeanDefinition());
        builder.beanDefinition.setBeanClass(beanClass);
        return builder;
    }

    public static <T> BeanDefinitionBuilder genericBeanDefinition(Class<T> beanClass, Supplier<T> instanceSupplier) {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder(new GenericBeanDefinition());
        builder.beanDefinition.setBeanClass(beanClass);
        builder.beanDefinition.setInstanceSupplier(instanceSupplier);
        return builder;
    }

    public static BeanDefinitionBuilder rootBeanDefinition(String beanClassName) {
        return rootBeanDefinition(beanClassName, (String) null);
    }

    public static BeanDefinitionBuilder rootBeanDefinition(String beanClassName, @Nullable String factoryMethodName) {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder(new RootBeanDefinition());
        builder.beanDefinition.setBeanClassName(beanClassName);
        builder.beanDefinition.setFactoryMethodName(factoryMethodName);
        return builder;
    }

    public static BeanDefinitionBuilder rootBeanDefinition(Class<?> beanClass) {
        return rootBeanDefinition(beanClass, (String) null);
    }

    public static BeanDefinitionBuilder rootBeanDefinition(Class<?> beanClass, @Nullable String factoryMethodName) {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder(new RootBeanDefinition());
        builder.beanDefinition.setBeanClass(beanClass);
        builder.beanDefinition.setFactoryMethodName(factoryMethodName);
        return builder;
    }

    public static BeanDefinitionBuilder childBeanDefinition(String parentName) {
        return new BeanDefinitionBuilder(new ChildBeanDefinition(parentName));
    }

    private BeanDefinitionBuilder(AbstractBeanDefinition beanDefinition) {
        this.beanDefinition = beanDefinition;
    }

    public AbstractBeanDefinition getRawBeanDefinition() {
        return this.beanDefinition;
    }

    public AbstractBeanDefinition getBeanDefinition() {
        this.beanDefinition.validate();
        return this.beanDefinition;
    }

    public BeanDefinitionBuilder setParentName(String parentName) {
        this.beanDefinition.setParentName(parentName);
        return this;
    }

    public BeanDefinitionBuilder setFactoryMethod(String factoryMethod) {
        this.beanDefinition.setFactoryMethodName(factoryMethod);
        return this;
    }

    public BeanDefinitionBuilder setFactoryMethodOnBean(String factoryMethod, String factoryBean) {
        this.beanDefinition.setFactoryMethodName(factoryMethod);
        this.beanDefinition.setFactoryBeanName(factoryBean);
        return this;
    }

    public BeanDefinitionBuilder addConstructorArgValue(@Nullable Object value) {
        ConstructorArgumentValues constructorArgumentValues = this.beanDefinition.getConstructorArgumentValues();
        int i = this.constructorArgIndex;
        this.constructorArgIndex = i + 1;
        constructorArgumentValues.addIndexedArgumentValue(i, value);
        return this;
    }

    public BeanDefinitionBuilder addConstructorArgReference(String beanName) {
        ConstructorArgumentValues constructorArgumentValues = this.beanDefinition.getConstructorArgumentValues();
        int i = this.constructorArgIndex;
        this.constructorArgIndex = i + 1;
        constructorArgumentValues.addIndexedArgumentValue(i, new RuntimeBeanReference(beanName));
        return this;
    }

    public BeanDefinitionBuilder addPropertyValue(String name, @Nullable Object value) {
        this.beanDefinition.getPropertyValues().add(name, value);
        return this;
    }

    public BeanDefinitionBuilder addPropertyReference(String name, String beanName) {
        this.beanDefinition.getPropertyValues().add(name, new RuntimeBeanReference(beanName));
        return this;
    }

    public BeanDefinitionBuilder setInitMethodName(@Nullable String methodName) {
        this.beanDefinition.setInitMethodName(methodName);
        return this;
    }

    public BeanDefinitionBuilder setDestroyMethodName(@Nullable String methodName) {
        this.beanDefinition.setDestroyMethodName(methodName);
        return this;
    }

    public BeanDefinitionBuilder setScope(@Nullable String scope) {
        this.beanDefinition.setScope(scope);
        return this;
    }

    public BeanDefinitionBuilder setAbstract(boolean flag) {
        this.beanDefinition.setAbstract(flag);
        return this;
    }

    public BeanDefinitionBuilder setLazyInit(boolean lazy) {
        this.beanDefinition.setLazyInit(lazy);
        return this;
    }

    public BeanDefinitionBuilder setAutowireMode(int autowireMode) {
        this.beanDefinition.setAutowireMode(autowireMode);
        return this;
    }

    public BeanDefinitionBuilder setDependencyCheck(int dependencyCheck) {
        this.beanDefinition.setDependencyCheck(dependencyCheck);
        return this;
    }

    public BeanDefinitionBuilder addDependsOn(String beanName) {
        if (this.beanDefinition.getDependsOn() == null) {
            this.beanDefinition.setDependsOn(beanName);
        } else {
            String[] added = (String[]) ObjectUtils.addObjectToArray(this.beanDefinition.getDependsOn(), beanName);
            this.beanDefinition.setDependsOn(added);
        }
        return this;
    }

    public BeanDefinitionBuilder setRole(int role) {
        this.beanDefinition.setRole(role);
        return this;
    }

    public BeanDefinitionBuilder applyCustomizers(BeanDefinitionCustomizer... customizers) {
        for (BeanDefinitionCustomizer customizer : customizers) {
            customizer.customize(this.beanDefinition);
        }
        return this;
    }
}