package org.springframework.scripting.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scripting/config/LangNamespaceHandler.class */
public class LangNamespaceHandler extends NamespaceHandlerSupport {
    @Override // org.springframework.beans.factory.xml.NamespaceHandler
    public void init() {
        registerScriptBeanDefinitionParser("groovy", "org.springframework.scripting.groovy.GroovyScriptFactory");
        registerScriptBeanDefinitionParser("bsh", "org.springframework.scripting.bsh.BshScriptFactory");
        registerScriptBeanDefinitionParser("std", "org.springframework.scripting.support.StandardScriptFactory");
        registerBeanDefinitionParser("defaults", new ScriptingDefaultsParser());
    }

    private void registerScriptBeanDefinitionParser(String key, String scriptFactoryClassName) {
        registerBeanDefinitionParser(key, new ScriptBeanDefinitionParser(scriptFactoryClassName));
    }
}