package org.thymeleaf.templateresolver;

import java.util.Map;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresource.ClassLoaderTemplateResource;
import org.thymeleaf.templateresource.ITemplateResource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateresolver/ClassLoaderTemplateResolver.class */
public class ClassLoaderTemplateResolver extends AbstractConfigurableTemplateResolver {
    private final ClassLoader classLoader;

    public ClassLoaderTemplateResolver() {
        this(null);
    }

    public ClassLoaderTemplateResolver(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override // org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver
    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, String resourceName, String characterEncoding, Map<String, Object> templateResolutionAttributes) {
        return new ClassLoaderTemplateResource(this.classLoader, resourceName, characterEncoding);
    }
}