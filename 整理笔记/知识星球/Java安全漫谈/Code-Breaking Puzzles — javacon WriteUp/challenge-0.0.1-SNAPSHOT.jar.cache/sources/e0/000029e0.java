package org.thymeleaf.templateresolver;

import java.util.Map;
import javax.servlet.ServletContext;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.ServletContextTemplateResource;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateresolver/ServletContextTemplateResolver.class */
public class ServletContextTemplateResolver extends AbstractConfigurableTemplateResolver {
    private final ServletContext servletContext;

    public ServletContextTemplateResolver(ServletContext servletContext) {
        Validate.notNull(servletContext, "ServletContext cannot be null");
        this.servletContext = servletContext;
    }

    @Override // org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver
    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, String resourceName, String characterEncoding, Map<String, Object> templateResolutionAttributes) {
        return new ServletContextTemplateResource(this.servletContext, resourceName, characterEncoding);
    }
}