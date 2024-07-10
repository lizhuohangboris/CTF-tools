package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.w3c.dom.Node;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/xml/BeanDefinitionDecorator.class */
public interface BeanDefinitionDecorator {
    BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder beanDefinitionHolder, ParserContext parserContext);
}