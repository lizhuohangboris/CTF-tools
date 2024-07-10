package org.springframework.boot.context.properties;

import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySources;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/PropertySourcesDeducer.class */
class PropertySourcesDeducer {
    private static final Log logger = LogFactory.getLog(PropertySourcesDeducer.class);
    private final ApplicationContext applicationContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PropertySourcesDeducer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public PropertySources getPropertySources() {
        PropertySourcesPlaceholderConfigurer configurer = getSinglePropertySourcesPlaceholderConfigurer();
        if (configurer != null) {
            return configurer.getAppliedPropertySources();
        }
        MutablePropertySources sources = extractEnvironmentPropertySources();
        if (sources != null) {
            return sources;
        }
        throw new IllegalStateException("Unable to obtain PropertySources from PropertySourcesPlaceholderConfigurer or Environment");
    }

    private MutablePropertySources extractEnvironmentPropertySources() {
        Environment environment = this.applicationContext.getEnvironment();
        if (environment instanceof ConfigurableEnvironment) {
            return ((ConfigurableEnvironment) environment).getPropertySources();
        }
        return null;
    }

    private PropertySourcesPlaceholderConfigurer getSinglePropertySourcesPlaceholderConfigurer() {
        Map<String, PropertySourcesPlaceholderConfigurer> beans = this.applicationContext.getBeansOfType(PropertySourcesPlaceholderConfigurer.class, false, false);
        if (beans.size() == 1) {
            return beans.values().iterator().next();
        }
        if (beans.size() > 1 && logger.isWarnEnabled()) {
            logger.warn("Multiple PropertySourcesPlaceholderConfigurer beans registered " + beans.keySet() + ", falling back to Environment");
            return null;
        }
        return null;
    }
}