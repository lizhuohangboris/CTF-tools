package org.springframework.beans.factory.parsing;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/parsing/ComponentDefinition.class */
public interface ComponentDefinition extends BeanMetadataElement {
    String getName();

    String getDescription();

    BeanDefinition[] getBeanDefinitions();

    BeanDefinition[] getInnerBeanDefinitions();

    BeanReference[] getBeanReferences();
}