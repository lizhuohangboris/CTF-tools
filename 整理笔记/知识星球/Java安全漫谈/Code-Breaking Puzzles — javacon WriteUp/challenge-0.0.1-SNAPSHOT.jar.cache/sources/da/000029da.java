package org.thymeleaf.templateresolver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.cache.AlwaysValidCacheEntryValidity;
import org.thymeleaf.cache.ICacheEntryValidity;
import org.thymeleaf.cache.NonCacheableCacheEntryValidity;
import org.thymeleaf.cache.TTLCacheEntryValidity;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.ContentTypeUtils;
import org.thymeleaf.util.PatternSpec;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateresolver/AbstractConfigurableTemplateResolver.class */
public abstract class AbstractConfigurableTemplateResolver extends AbstractTemplateResolver {
    public static final boolean DEFAULT_CACHEABLE = true;
    private String prefix = null;
    private String suffix = null;
    private boolean forceSuffix = false;
    private String characterEncoding = null;
    private TemplateMode templateMode = DEFAULT_TEMPLATE_MODE;
    private boolean forceTemplateMode = false;
    private boolean cacheable = true;
    private Long cacheTTLMs = DEFAULT_CACHE_TTL_MS;
    private final HashMap<String, String> templateAliases = new HashMap<>(8);
    private final PatternSpec xmlTemplateModePatternSpec = new PatternSpec();
    private final PatternSpec htmlTemplateModePatternSpec = new PatternSpec();
    private final PatternSpec textTemplateModePatternSpec = new PatternSpec();
    private final PatternSpec javaScriptTemplateModePatternSpec = new PatternSpec();
    private final PatternSpec cssTemplateModePatternSpec = new PatternSpec();
    private final PatternSpec rawTemplateModePatternSpec = new PatternSpec();
    private final PatternSpec cacheablePatternSpec = new PatternSpec();
    private final PatternSpec nonCacheablePatternSpec = new PatternSpec();
    public static final TemplateMode DEFAULT_TEMPLATE_MODE = TemplateMode.HTML;
    public static final Long DEFAULT_CACHE_TTL_MS = null;

    protected abstract ITemplateResource computeTemplateResource(IEngineConfiguration iEngineConfiguration, String str, String str2, String str3, String str4, Map<String, Object> map);

    public final String getPrefix() {
        return this.prefix;
    }

    public final void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public final String getSuffix() {
        return this.suffix;
    }

    public final void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public final boolean getForceSuffix() {
        return this.forceSuffix;
    }

    public final void setForceSuffix(boolean forceSuffix) {
        this.forceSuffix = forceSuffix;
    }

    public final String getCharacterEncoding() {
        return this.characterEncoding;
    }

    public final void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

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

    public final boolean getForceTemplateMode() {
        return this.forceTemplateMode;
    }

    public final void setForceTemplateMode(boolean forceTemplateMode) {
        this.forceTemplateMode = forceTemplateMode;
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

    public final Map<String, String> getTemplateAliases() {
        return Collections.unmodifiableMap(this.templateAliases);
    }

    public final void setTemplateAliases(Map<String, String> templateAliases) {
        if (templateAliases != null) {
            this.templateAliases.putAll(templateAliases);
        }
    }

    public final void addTemplateAlias(String alias, String templateName) {
        Validate.notNull(alias, "Alias cannot be null");
        Validate.notNull(templateName, "Template name cannot be null");
        this.templateAliases.put(alias, templateName);
    }

    public final void clearTemplateAliases() {
        this.templateAliases.clear();
    }

    public final PatternSpec getXmlTemplateModePatternSpec() {
        return this.xmlTemplateModePatternSpec;
    }

    public final Set<String> getXmlTemplateModePatterns() {
        return this.xmlTemplateModePatternSpec.getPatterns();
    }

    public final void setXmlTemplateModePatterns(Set<String> newXmlTemplateModePatterns) {
        this.xmlTemplateModePatternSpec.setPatterns(newXmlTemplateModePatterns);
    }

    public final PatternSpec getHtmlTemplateModePatternSpec() {
        return this.htmlTemplateModePatternSpec;
    }

    public final Set<String> getHtmlTemplateModePatterns() {
        return this.htmlTemplateModePatternSpec.getPatterns();
    }

    public final void setHtmlTemplateModePatterns(Set<String> newHtmlTemplateModePatterns) {
        this.htmlTemplateModePatternSpec.setPatterns(newHtmlTemplateModePatterns);
    }

    public final PatternSpec getJavaScriptTemplateModePatternSpec() {
        return this.javaScriptTemplateModePatternSpec;
    }

    public final Set<String> getJavaScriptTemplateModePatterns() {
        return this.javaScriptTemplateModePatternSpec.getPatterns();
    }

    public final void setJavaScriptTemplateModePatterns(Set<String> newJavaScriptTemplateModePatterns) {
        this.javaScriptTemplateModePatternSpec.setPatterns(newJavaScriptTemplateModePatterns);
    }

    public final PatternSpec getCSSTemplateModePatternSpec() {
        return this.cssTemplateModePatternSpec;
    }

    public final Set<String> getCSSTemplateModePatterns() {
        return this.cssTemplateModePatternSpec.getPatterns();
    }

    public final void setCSSTemplateModePatterns(Set<String> newCSSTemplateModePatterns) {
        this.cssTemplateModePatternSpec.setPatterns(newCSSTemplateModePatterns);
    }

    public final PatternSpec getRawTemplateModePatternSpec() {
        return this.rawTemplateModePatternSpec;
    }

    public final Set<String> getRawTemplateModePatterns() {
        return this.rawTemplateModePatternSpec.getPatterns();
    }

    public final void setRawTemplateModePatterns(Set<String> newRawTemplateModePatterns) {
        this.rawTemplateModePatternSpec.setPatterns(newRawTemplateModePatterns);
    }

    public final PatternSpec getTextTemplateModePatternSpec() {
        return this.textTemplateModePatternSpec;
    }

    public final Set<String> getTextTemplateModePatterns() {
        return this.textTemplateModePatternSpec.getPatterns();
    }

    public final void setTextTemplateModePatterns(Set<String> newTextTemplateModePatterns) {
        this.textTemplateModePatternSpec.setPatterns(newTextTemplateModePatterns);
    }

    @Deprecated
    public final PatternSpec getValidXmlTemplateModePatternSpec() {
        return this.xmlTemplateModePatternSpec;
    }

    @Deprecated
    public final Set<String> getValidXmlTemplateModePatterns() {
        return this.xmlTemplateModePatternSpec.getPatterns();
    }

    @Deprecated
    public final void setValidXmlTemplateModePatterns(Set<String> newValidXmlTemplateModePatterns) {
        this.xmlTemplateModePatternSpec.setPatterns(newValidXmlTemplateModePatterns);
    }

    @Deprecated
    public final PatternSpec getXhtmlTemplateModePatternSpec() {
        return this.htmlTemplateModePatternSpec;
    }

    @Deprecated
    public final Set<String> getXhtmlTemplateModePatterns() {
        return this.htmlTemplateModePatternSpec.getPatterns();
    }

    @Deprecated
    public final void setXhtmlTemplateModePatterns(Set<String> newXhtmlTemplateModePatterns) {
        this.htmlTemplateModePatternSpec.setPatterns(newXhtmlTemplateModePatterns);
    }

    @Deprecated
    public final PatternSpec getValidXhtmlTemplateModePatternSpec() {
        return this.htmlTemplateModePatternSpec;
    }

    @Deprecated
    public final Set<String> getValidXhtmlTemplateModePatterns() {
        return this.htmlTemplateModePatternSpec.getPatterns();
    }

    @Deprecated
    public final void setValidXhtmlTemplateModePatterns(Set<String> newValidXhtmlTemplateModePatterns) {
        this.htmlTemplateModePatternSpec.setPatterns(newValidXhtmlTemplateModePatterns);
    }

    @Deprecated
    public final PatternSpec getLegacyHtml5TemplateModePatternSpec() {
        return this.htmlTemplateModePatternSpec;
    }

    @Deprecated
    public final Set<String> getLegacyHtml5TemplateModePatterns() {
        return this.htmlTemplateModePatternSpec.getPatterns();
    }

    @Deprecated
    public final void setLegacyHtml5TemplateModePatterns(Set<String> newLegacyHtml5TemplateModePatterns) {
        this.htmlTemplateModePatternSpec.setPatterns(newLegacyHtml5TemplateModePatterns);
    }

    @Deprecated
    public final PatternSpec getHtml5TemplateModePatternSpec() {
        return this.htmlTemplateModePatternSpec;
    }

    @Deprecated
    public final Set<String> getHtml5TemplateModePatterns() {
        return this.htmlTemplateModePatternSpec.getPatterns();
    }

    @Deprecated
    public final void setHtml5TemplateModePatterns(Set<String> newHtml5TemplateModePatterns) {
        this.htmlTemplateModePatternSpec.setPatterns(newHtml5TemplateModePatterns);
    }

    public final PatternSpec getCacheablePatternSpec() {
        return this.cacheablePatternSpec;
    }

    public final Set<String> getCacheablePatterns() {
        return this.cacheablePatternSpec.getPatterns();
    }

    public final void setCacheablePatterns(Set<String> cacheablePatterns) {
        this.cacheablePatternSpec.setPatterns(cacheablePatterns);
    }

    public final PatternSpec getNonCacheablePatternSpec() {
        return this.nonCacheablePatternSpec;
    }

    public final Set<String> getNonCacheablePatterns() {
        return this.nonCacheablePatternSpec.getPatterns();
    }

    public final void setNonCacheablePatterns(Set<String> nonCacheablePatterns) {
        this.nonCacheablePatternSpec.setPatterns(nonCacheablePatterns);
    }

    @Deprecated
    protected String computeResourceName(IEngineConfiguration configuration, String ownerTemplate, String template, String prefix, String suffix, Map<String, String> templateAliases, Map<String, Object> templateResolutionAttributes) {
        return computeResourceName(configuration, ownerTemplate, template, prefix, suffix, false, templateAliases, templateResolutionAttributes);
    }

    protected String computeResourceName(IEngineConfiguration configuration, String ownerTemplate, String template, String prefix, String suffix, boolean forceSuffix, Map<String, String> templateAliases, Map<String, Object> templateResolutionAttributes) {
        Validate.notNull(template, "Template name cannot be null");
        String unaliasedName = templateAliases.get(template);
        if (unaliasedName == null) {
            unaliasedName = template;
        }
        boolean hasPrefix = !StringUtils.isEmptyOrWhitespace(prefix);
        boolean hasSuffix = !StringUtils.isEmptyOrWhitespace(suffix);
        boolean shouldApplySuffix = hasSuffix && (forceSuffix || !ContentTypeUtils.hasRecognizedFileExtension(unaliasedName));
        if (!hasPrefix && !shouldApplySuffix) {
            return unaliasedName;
        }
        if (!hasPrefix) {
            return unaliasedName + suffix;
        }
        if (!shouldApplySuffix) {
            return prefix + unaliasedName;
        }
        return prefix + unaliasedName + suffix;
    }

    @Override // org.thymeleaf.templateresolver.AbstractTemplateResolver
    protected TemplateMode computeTemplateMode(IEngineConfiguration configuration, String ownerTemplate, String template, Map<String, Object> templateResolutionAttributes) {
        if (this.xmlTemplateModePatternSpec.matches(template)) {
            return TemplateMode.XML;
        }
        if (this.htmlTemplateModePatternSpec.matches(template)) {
            return TemplateMode.HTML;
        }
        if (this.textTemplateModePatternSpec.matches(template)) {
            return TemplateMode.TEXT;
        }
        if (this.javaScriptTemplateModePatternSpec.matches(template)) {
            return TemplateMode.JAVASCRIPT;
        }
        if (this.cssTemplateModePatternSpec.matches(template)) {
            return TemplateMode.CSS;
        }
        if (this.rawTemplateModePatternSpec.matches(template)) {
            return TemplateMode.RAW;
        }
        if (!this.forceTemplateMode) {
            String templateResourceName = computeResourceName(configuration, ownerTemplate, template, this.prefix, this.suffix, this.forceSuffix, this.templateAliases, templateResolutionAttributes);
            TemplateMode autoResolvedTemplateMode = ContentTypeUtils.computeTemplateModeForTemplateName(templateResourceName);
            if (autoResolvedTemplateMode != null) {
                return autoResolvedTemplateMode;
            }
        }
        return getTemplateMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.thymeleaf.templateresolver.AbstractTemplateResolver
    public ICacheEntryValidity computeValidity(IEngineConfiguration configuration, String ownerTemplate, String template, Map<String, Object> templateResolutionAttributes) {
        if (this.cacheablePatternSpec.matches(template)) {
            if (this.cacheTTLMs != null) {
                return new TTLCacheEntryValidity(this.cacheTTLMs.longValue());
            }
            return AlwaysValidCacheEntryValidity.INSTANCE;
        } else if (this.nonCacheablePatternSpec.matches(template)) {
            return NonCacheableCacheEntryValidity.INSTANCE;
        } else {
            if (isCacheable()) {
                if (this.cacheTTLMs != null) {
                    return new TTLCacheEntryValidity(this.cacheTTLMs.longValue());
                }
                return AlwaysValidCacheEntryValidity.INSTANCE;
            }
            return NonCacheableCacheEntryValidity.INSTANCE;
        }
    }

    @Override // org.thymeleaf.templateresolver.AbstractTemplateResolver
    protected final ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, Map<String, Object> templateResolutionAttributes) {
        String resourceName = computeResourceName(configuration, ownerTemplate, template, this.prefix, this.suffix, this.forceSuffix, this.templateAliases, templateResolutionAttributes);
        return computeTemplateResource(configuration, ownerTemplate, template, resourceName, this.characterEncoding, templateResolutionAttributes);
    }
}