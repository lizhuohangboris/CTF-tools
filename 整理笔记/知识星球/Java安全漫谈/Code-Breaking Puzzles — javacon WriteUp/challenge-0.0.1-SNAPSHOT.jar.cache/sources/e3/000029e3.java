package org.thymeleaf.templateresolver;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.regex.Pattern;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.cache.ICacheEntryValidity;
import org.thymeleaf.cache.NonCacheableCacheEntryValidity;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.UrlTemplateResource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateresolver/UrlTemplateResolver.class */
public class UrlTemplateResolver extends AbstractConfigurableTemplateResolver {
    private static final Pattern JSESSIONID_PATTERN = Pattern.compile("(.*?);jsessionid(.*?)");

    @Override // org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver
    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, String resourceName, String characterEncoding, Map<String, Object> templateResolutionAttributes) {
        try {
            return new UrlTemplateResource(resourceName, characterEncoding);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver, org.thymeleaf.templateresolver.AbstractTemplateResolver
    public ICacheEntryValidity computeValidity(IEngineConfiguration configuration, String ownerTemplate, String template, Map<String, Object> templateResolutionAttributes) {
        if (JSESSIONID_PATTERN.matcher(template.toLowerCase()).matches()) {
            return NonCacheableCacheEntryValidity.INSTANCE;
        }
        return super.computeValidity(configuration, ownerTemplate, template, templateResolutionAttributes);
    }
}