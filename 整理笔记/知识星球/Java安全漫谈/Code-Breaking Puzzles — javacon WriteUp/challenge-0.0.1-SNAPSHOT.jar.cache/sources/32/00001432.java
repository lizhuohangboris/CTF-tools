package org.springframework.beans.factory.parsing;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/parsing/BeanComponentDefinition.class */
public class BeanComponentDefinition extends BeanDefinitionHolder implements ComponentDefinition {
    private BeanDefinition[] innerBeanDefinitions;
    private BeanReference[] beanReferences;

    public BeanComponentDefinition(BeanDefinition beanDefinition, String beanName) {
        this(new BeanDefinitionHolder(beanDefinition, beanName));
    }

    public BeanComponentDefinition(BeanDefinition beanDefinition, String beanName, @Nullable String[] aliases) {
        this(new BeanDefinitionHolder(beanDefinition, beanName, aliases));
    }

    public BeanComponentDefinition(BeanDefinitionHolder beanDefinitionHolder) {
        super(beanDefinitionHolder);
        PropertyValue[] propertyValues;
        List<BeanDefinition> innerBeans = new ArrayList<>();
        List<BeanReference> references = new ArrayList<>();
        PropertyValues propertyValues2 = beanDefinitionHolder.getBeanDefinition().getPropertyValues();
        for (PropertyValue propertyValue : propertyValues2.getPropertyValues()) {
            Object value = propertyValue.getValue();
            if (value instanceof BeanDefinitionHolder) {
                innerBeans.add(((BeanDefinitionHolder) value).getBeanDefinition());
            } else if (value instanceof BeanDefinition) {
                innerBeans.add((BeanDefinition) value);
            } else if (value instanceof BeanReference) {
                references.add((BeanReference) value);
            }
        }
        this.innerBeanDefinitions = (BeanDefinition[]) innerBeans.toArray(new BeanDefinition[0]);
        this.beanReferences = (BeanReference[]) references.toArray(new BeanReference[0]);
    }

    @Override // org.springframework.beans.factory.parsing.ComponentDefinition
    public String getName() {
        return getBeanName();
    }

    @Override // org.springframework.beans.factory.parsing.ComponentDefinition
    public String getDescription() {
        return getShortDescription();
    }

    @Override // org.springframework.beans.factory.parsing.ComponentDefinition
    public BeanDefinition[] getBeanDefinitions() {
        return new BeanDefinition[]{getBeanDefinition()};
    }

    @Override // org.springframework.beans.factory.parsing.ComponentDefinition
    public BeanDefinition[] getInnerBeanDefinitions() {
        return this.innerBeanDefinitions;
    }

    @Override // org.springframework.beans.factory.parsing.ComponentDefinition
    public BeanReference[] getBeanReferences() {
        return this.beanReferences;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinitionHolder
    public String toString() {
        return getDescription();
    }

    @Override // org.springframework.beans.factory.config.BeanDefinitionHolder
    public boolean equals(Object other) {
        return this == other || ((other instanceof BeanComponentDefinition) && super.equals(other));
    }
}