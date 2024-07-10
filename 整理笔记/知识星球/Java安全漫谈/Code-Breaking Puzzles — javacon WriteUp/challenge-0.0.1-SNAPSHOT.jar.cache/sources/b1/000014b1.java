package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.lang.Nullable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/xml/NamespaceHandler.class */
public interface NamespaceHandler {
    void init();

    @Nullable
    BeanDefinition parse(Element element, ParserContext parserContext);

    @Nullable
    BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder beanDefinitionHolder, ParserContext parserContext);
}