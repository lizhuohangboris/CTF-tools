package org.springframework.beans.factory.groovy;

import groovy.lang.GroovyObjectSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.util.CollectionUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/groovy/GroovyBeanDefinitionWrapper.class */
public class GroovyBeanDefinitionWrapper extends GroovyObjectSupport {
    private static final String PARENT = "parent";
    private static final String AUTOWIRE = "autowire";
    private static final String CONSTRUCTOR_ARGS = "constructorArgs";
    private static final String FACTORY_BEAN = "factoryBean";
    private static final String FACTORY_METHOD = "factoryMethod";
    private static final String INIT_METHOD = "initMethod";
    private static final String DESTROY_METHOD = "destroyMethod";
    private static final String SINGLETON = "singleton";
    private static final List<String> dynamicProperties = new ArrayList(8);
    private String beanName;
    private Class<?> clazz;
    private Collection<?> constructorArgs;
    private AbstractBeanDefinition definition;
    private BeanWrapper definitionWrapper;
    private String parentName;

    static {
        dynamicProperties.add("parent");
        dynamicProperties.add("autowire");
        dynamicProperties.add(CONSTRUCTOR_ARGS);
        dynamicProperties.add(FACTORY_BEAN);
        dynamicProperties.add(FACTORY_METHOD);
        dynamicProperties.add(INIT_METHOD);
        dynamicProperties.add(DESTROY_METHOD);
        dynamicProperties.add("singleton");
    }

    public GroovyBeanDefinitionWrapper(String beanName) {
        this.beanName = beanName;
    }

    public GroovyBeanDefinitionWrapper(String beanName, Class<?> clazz) {
        this.beanName = beanName;
        this.clazz = clazz;
    }

    public GroovyBeanDefinitionWrapper(String beanName, Class<?> clazz, Collection<?> constructorArgs) {
        this.beanName = beanName;
        this.clazz = clazz;
        this.constructorArgs = constructorArgs;
    }

    public String getBeanName() {
        return this.beanName;
    }

    public void setBeanDefinition(AbstractBeanDefinition definition) {
        this.definition = definition;
    }

    public AbstractBeanDefinition getBeanDefinition() {
        if (this.definition == null) {
            this.definition = createBeanDefinition();
        }
        return this.definition;
    }

    protected AbstractBeanDefinition createBeanDefinition() {
        AbstractBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(this.clazz);
        if (!CollectionUtils.isEmpty(this.constructorArgs)) {
            ConstructorArgumentValues cav = new ConstructorArgumentValues();
            for (Object constructorArg : this.constructorArgs) {
                cav.addGenericArgumentValue(constructorArg);
            }
            bd.setConstructorArgumentValues(cav);
        }
        if (this.parentName != null) {
            bd.setParentName(this.parentName);
        }
        this.definitionWrapper = new BeanWrapperImpl(bd);
        return bd;
    }

    public void setBeanDefinitionHolder(BeanDefinitionHolder holder) {
        this.definition = (AbstractBeanDefinition) holder.getBeanDefinition();
        this.beanName = holder.getBeanName();
    }

    public BeanDefinitionHolder getBeanDefinitionHolder() {
        return new BeanDefinitionHolder(getBeanDefinition(), getBeanName());
    }

    public void setParent(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Parent bean cannot be set to a null runtime bean reference!");
        }
        if (obj instanceof String) {
            this.parentName = (String) obj;
        } else if (obj instanceof RuntimeBeanReference) {
            this.parentName = ((RuntimeBeanReference) obj).getBeanName();
        } else if (obj instanceof GroovyBeanDefinitionWrapper) {
            this.parentName = ((GroovyBeanDefinitionWrapper) obj).getBeanName();
        }
        getBeanDefinition().setParentName(this.parentName);
        getBeanDefinition().setAbstract(false);
    }

    public GroovyBeanDefinitionWrapper addProperty(String propertyName, Object propertyValue) {
        if (propertyValue instanceof GroovyBeanDefinitionWrapper) {
            propertyValue = ((GroovyBeanDefinitionWrapper) propertyValue).getBeanDefinition();
        }
        getBeanDefinition().getPropertyValues().add(propertyName, propertyValue);
        return this;
    }

    public Object getProperty(String property) {
        if (this.definitionWrapper.isReadableProperty(property)) {
            return this.definitionWrapper.getPropertyValue(property);
        }
        if (dynamicProperties.contains(property)) {
            return null;
        }
        return super.getProperty(property);
    }

    public void setProperty(String property, Object newValue) {
        if ("parent".equals(property)) {
            setParent(newValue);
            return;
        }
        AbstractBeanDefinition bd = getBeanDefinition();
        if ("autowire".equals(property)) {
            if (BeanDefinitionParserDelegate.AUTOWIRE_BY_NAME_VALUE.equals(newValue)) {
                bd.setAutowireMode(1);
            } else if (BeanDefinitionParserDelegate.AUTOWIRE_BY_TYPE_VALUE.equals(newValue)) {
                bd.setAutowireMode(2);
            } else if (BeanDefinitionParserDelegate.AUTOWIRE_CONSTRUCTOR_VALUE.equals(newValue)) {
                bd.setAutowireMode(3);
            } else if (Boolean.TRUE.equals(newValue)) {
                bd.setAutowireMode(1);
            }
        } else if (CONSTRUCTOR_ARGS.equals(property) && (newValue instanceof List)) {
            ConstructorArgumentValues cav = new ConstructorArgumentValues();
            List args = (List) newValue;
            for (Object arg : args) {
                cav.addGenericArgumentValue(arg);
            }
            bd.setConstructorArgumentValues(cav);
        } else if (FACTORY_BEAN.equals(property)) {
            if (newValue != null) {
                bd.setFactoryBeanName(newValue.toString());
            }
        } else if (FACTORY_METHOD.equals(property)) {
            if (newValue != null) {
                bd.setFactoryMethodName(newValue.toString());
            }
        } else if (INIT_METHOD.equals(property)) {
            if (newValue != null) {
                bd.setInitMethodName(newValue.toString());
            }
        } else if (DESTROY_METHOD.equals(property)) {
            if (newValue != null) {
                bd.setDestroyMethodName(newValue.toString());
            }
        } else if ("singleton".equals(property)) {
            bd.setScope(Boolean.TRUE.equals(newValue) ? "singleton" : "prototype");
        } else if (this.definitionWrapper.isWritableProperty(property)) {
            this.definitionWrapper.setPropertyValue(property, newValue);
        } else {
            super.setProperty(property, newValue);
        }
    }
}