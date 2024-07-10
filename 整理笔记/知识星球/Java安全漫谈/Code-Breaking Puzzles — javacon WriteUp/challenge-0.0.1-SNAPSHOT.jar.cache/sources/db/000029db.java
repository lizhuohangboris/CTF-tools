package org.thymeleaf.templateresolver;

import java.util.Map;
import java.util.Set;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.cache.ICacheEntryValidity;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.PatternSpec;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateresolver/AbstractTemplateResolver.class */
public abstract class AbstractTemplateResolver implements ITemplateResolver {
    public static final boolean DEFAULT_EXISTENCE_CHECK = false;
    public static final boolean DEFAULT_USE_DECOUPLED_LOGIC = false;
    private String name = getClass().getName();
    private Integer order = null;
    private boolean checkExistence = false;
    private boolean useDecoupledLogic = false;
    private final PatternSpec resolvablePatternSpec = new PatternSpec();

    protected abstract ITemplateResource computeTemplateResource(IEngineConfiguration iEngineConfiguration, String str, String str2, Map<String, Object> map);

    protected abstract TemplateMode computeTemplateMode(IEngineConfiguration iEngineConfiguration, String str, String str2, Map<String, Object> map);

    protected abstract ICacheEntryValidity computeValidity(IEngineConfiguration iEngineConfiguration, String str, String str2, Map<String, Object> map);

    @Override // org.thymeleaf.templateresolver.ITemplateResolver
    public final String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override // org.thymeleaf.templateresolver.ITemplateResolver
    public final Integer getOrder() {
        return this.order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public final PatternSpec getResolvablePatternSpec() {
        return this.resolvablePatternSpec;
    }

    public final Set<String> getResolvablePatterns() {
        return this.resolvablePatternSpec.getPatterns();
    }

    public void setResolvablePatterns(Set<String> resolvablePatterns) {
        this.resolvablePatternSpec.setPatterns(resolvablePatterns);
    }

    public final boolean getCheckExistence() {
        return this.checkExistence;
    }

    public void setCheckExistence(boolean checkExistence) {
        this.checkExistence = checkExistence;
    }

    public final boolean getUseDecoupledLogic() {
        return this.useDecoupledLogic;
    }

    public void setUseDecoupledLogic(boolean useDecoupledLogic) {
        this.useDecoupledLogic = useDecoupledLogic;
    }

    @Override // org.thymeleaf.templateresolver.ITemplateResolver
    public final TemplateResolution resolveTemplate(IEngineConfiguration configuration, String ownerTemplate, String template, Map<String, Object> templateResolutionAttributes) {
        ITemplateResource templateResource;
        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(template, "Template Name cannot be null");
        if (!computeResolvable(configuration, ownerTemplate, template, templateResolutionAttributes) || (templateResource = computeTemplateResource(configuration, ownerTemplate, template, templateResolutionAttributes)) == null) {
            return null;
        }
        if (this.checkExistence && !templateResource.exists()) {
            return null;
        }
        return new TemplateResolution(templateResource, this.checkExistence, computeTemplateMode(configuration, ownerTemplate, template, templateResolutionAttributes), this.useDecoupledLogic, computeValidity(configuration, ownerTemplate, template, templateResolutionAttributes));
    }

    protected boolean computeResolvable(IEngineConfiguration configuration, String ownerTemplate, String template, Map<String, Object> templateResolutionAttributes) {
        if (this.resolvablePatternSpec.isEmpty()) {
            return true;
        }
        return this.resolvablePatternSpec.matches(template);
    }
}