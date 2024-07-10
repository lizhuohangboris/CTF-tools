package org.springframework.web.context.support;

import java.io.IOException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/support/XmlWebApplicationContext.class */
public class XmlWebApplicationContext extends AbstractRefreshableWebApplicationContext {
    public static final String DEFAULT_CONFIG_LOCATION = "/WEB-INF/applicationContext.xml";
    public static final String DEFAULT_CONFIG_LOCATION_PREFIX = "/WEB-INF/";
    public static final String DEFAULT_CONFIG_LOCATION_SUFFIX = ".xml";

    @Override // org.springframework.context.support.AbstractRefreshableApplicationContext
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        beanDefinitionReader.setEnvironment(getEnvironment());
        beanDefinitionReader.setResourceLoader(this);
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));
        initBeanDefinitionReader(beanDefinitionReader);
        loadBeanDefinitions(beanDefinitionReader);
    }

    protected void initBeanDefinitionReader(XmlBeanDefinitionReader beanDefinitionReader) {
    }

    protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws IOException {
        String[] configLocations = getConfigLocations();
        if (configLocations != null) {
            for (String configLocation : configLocations) {
                reader.loadBeanDefinitions(configLocation);
            }
        }
    }

    @Override // org.springframework.context.support.AbstractRefreshableConfigApplicationContext
    protected String[] getDefaultConfigLocations() {
        return getNamespace() != null ? new String[]{"/WEB-INF/" + getNamespace() + DEFAULT_CONFIG_LOCATION_SUFFIX} : new String[]{DEFAULT_CONFIG_LOCATION};
    }
}