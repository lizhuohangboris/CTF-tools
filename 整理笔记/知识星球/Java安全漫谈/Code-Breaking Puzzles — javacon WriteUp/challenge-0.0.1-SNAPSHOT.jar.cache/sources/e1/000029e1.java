package org.thymeleaf.templateresolver;

import java.util.Map;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.cache.AlwaysValidCacheEntryValidity;
import org.thymeleaf.cache.ICacheEntryValidity;
import org.thymeleaf.cache.NonCacheableCacheEntryValidity;
import org.thymeleaf.cache.TTLCacheEntryValidity;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.StringTemplateResource;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateresolver/StringTemplateResolver.class */
public class StringTemplateResolver extends AbstractTemplateResolver {
    public static final boolean DEFAULT_CACHEABLE = false;
    private TemplateMode templateMode = DEFAULT_TEMPLATE_MODE;
    private boolean cacheable = false;
    private Long cacheTTLMs = DEFAULT_CACHE_TTL_MS;
    public static final TemplateMode DEFAULT_TEMPLATE_MODE = TemplateMode.HTML;
    public static final Long DEFAULT_CACHE_TTL_MS = null;

    public final TemplateMode getTemplateMode() {
        return this.templateMode;
    }

    public final void setTemplateMode(TemplateMode templateMode) {
        Validate.notNull(templateMode, "Cannot set a null template mode value");
        this.templateMode = TemplateMode.parse(templateMode.toString());
    }

    public final void setTemplateMode(String templateMode) {
        Validate.notNull(templateMode, "Cannot set a null template mode value");
        this.templateMode = TemplateMode.parse(templateMode);
    }

    public final boolean isCacheable() {
        return this.cacheable;
    }

    public final void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    public final Long getCacheTTLMs() {
        return this.cacheTTLMs;
    }

    public final void setCacheTTLMs(Long cacheTTLMs) {
        this.cacheTTLMs = cacheTTLMs;
    }

    @Override // org.thymeleaf.templateresolver.AbstractTemplateResolver
    public void setUseDecoupledLogic(boolean useDecoupledLogic) {
        if (useDecoupledLogic) {
            throw new ConfigurationException("The 'useDecoupledLogic' flag is not allowed for String template resolution");
        }
        super.setUseDecoupledLogic(useDecoupledLogic);
    }

    @Override // org.thymeleaf.templateresolver.AbstractTemplateResolver
    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, Map<String, Object> templateResolutionAttributes) {
        return new StringTemplateResource(template);
    }

    @Override // org.thymeleaf.templateresolver.AbstractTemplateResolver
    protected TemplateMode computeTemplateMode(IEngineConfiguration configuration, String ownerTemplate, String template, Map<String, Object> templateResolutionAttributes) {
        return this.templateMode;
    }

    @Override // org.thymeleaf.templateresolver.AbstractTemplateResolver
    protected ICacheEntryValidity computeValidity(IEngineConfiguration configuration, String ownerTemplate, String template, Map<String, Object> templateResolutionAttributes) {
        if (isCacheable()) {
            if (this.cacheTTLMs != null) {
                return new TTLCacheEntryValidity(this.cacheTTLMs.longValue());
            }
            return AlwaysValidCacheEntryValidity.INSTANCE;
        }
        return NonCacheableCacheEntryValidity.INSTANCE;
    }
}