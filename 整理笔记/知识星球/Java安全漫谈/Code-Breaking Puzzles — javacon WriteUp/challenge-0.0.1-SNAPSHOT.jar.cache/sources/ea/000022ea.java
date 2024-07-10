package org.springframework.scripting.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.scripting.support.ScriptFactoryPostProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scripting/config/LangNamespaceUtils.class */
public abstract class LangNamespaceUtils {
    private static final String SCRIPT_FACTORY_POST_PROCESSOR_BEAN_NAME = "org.springframework.scripting.config.scriptFactoryPostProcessor";

    public static BeanDefinition registerScriptFactoryPostProcessorIfNecessary(BeanDefinitionRegistry registry) {
        BeanDefinition beanDefinition;
        if (registry.containsBeanDefinition(SCRIPT_FACTORY_POST_PROCESSOR_BEAN_NAME)) {
            beanDefinition = registry.getBeanDefinition(SCRIPT_FACTORY_POST_PROCESSOR_BEAN_NAME);
        } else {
            beanDefinition = new RootBeanDefinition(ScriptFactoryPostProcessor.class);
            registry.registerBeanDefinition(SCRIPT_FACTORY_POST_PROCESSOR_BEAN_NAME, beanDefinition);
        }
        return beanDefinition;
    }
}