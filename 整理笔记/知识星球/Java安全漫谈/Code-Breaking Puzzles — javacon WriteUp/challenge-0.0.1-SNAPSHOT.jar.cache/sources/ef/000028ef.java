package org.thymeleaf.spring5.templateresolver;

import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.spring5.templateresource.SpringResourceTemplateResource;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/templateresolver/SpringResourceTemplateResolver.class */
public class SpringResourceTemplateResolver extends AbstractConfigurableTemplateResolver implements ApplicationContextAware {
    private ApplicationContext applicationContext = null;

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override // org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver
    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, String resourceName, String characterEncoding, Map<String, Object> templateResolutionAttributes) {
        return new SpringResourceTemplateResource(this.applicationContext, resourceName, characterEncoding);
    }
}