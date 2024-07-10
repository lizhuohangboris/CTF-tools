package org.thymeleaf.templateresolver;

import java.util.Map;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.cache.AlwaysValidCacheEntryValidity;
import org.thymeleaf.cache.ICacheEntryValidity;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.StringTemplateResource;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateresolver/DefaultTemplateResolver.class */
public class DefaultTemplateResolver extends AbstractTemplateResolver {
    public static final TemplateMode DEFAULT_TEMPLATE_MODE = TemplateMode.HTML;
    private TemplateMode templateMode = DEFAULT_TEMPLATE_MODE;
    private String template = "";

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

    public String getTemplate() {
        return this.template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    @Override // org.thymeleaf.templateresolver.AbstractTemplateResolver
    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, Map<String, Object> templateResolutionAttributes) {
        return new StringTemplateResource(this.template);
    }

    @Override // org.thymeleaf.templateresolver.AbstractTemplateResolver
    protected TemplateMode computeTemplateMode(IEngineConfiguration configuration, String ownerTemplate, String template, Map<String, Object> templateResolutionAttributes) {
        return this.templateMode;
    }

    @Override // org.thymeleaf.templateresolver.AbstractTemplateResolver
    protected ICacheEntryValidity computeValidity(IEngineConfiguration configuration, String ownerTemplate, String template, Map<String, Object> templateResolutionAttributes) {
        return AlwaysValidCacheEntryValidity.INSTANCE;
    }
}