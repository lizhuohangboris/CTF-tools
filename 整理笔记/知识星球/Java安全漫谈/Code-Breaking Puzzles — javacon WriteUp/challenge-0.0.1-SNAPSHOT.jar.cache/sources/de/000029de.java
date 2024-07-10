package org.thymeleaf.templateresolver;

import java.util.Map;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresource.FileTemplateResource;
import org.thymeleaf.templateresource.ITemplateResource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateresolver/FileTemplateResolver.class */
public class FileTemplateResolver extends AbstractConfigurableTemplateResolver {
    @Override // org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver
    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, String resourceName, String characterEncoding, Map<String, Object> templateResolutionAttributes) {
        return new FileTemplateResource(resourceName, characterEncoding);
    }
}