package org.springframework.ejb.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/ejb/config/JeeNamespaceHandler.class */
public class JeeNamespaceHandler extends NamespaceHandlerSupport {
    @Override // org.springframework.beans.factory.xml.NamespaceHandler
    public void init() {
        registerBeanDefinitionParser("jndi-lookup", new JndiLookupBeanDefinitionParser());
        registerBeanDefinitionParser("local-slsb", new LocalStatelessSessionBeanDefinitionParser());
        registerBeanDefinitionParser("remote-slsb", new RemoteStatelessSessionBeanDefinitionParser());
    }
}