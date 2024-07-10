package org.thymeleaf.engine;

import java.util.Set;
import org.thymeleaf.cache.ICacheEntryValidity;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/TemplateData.class */
public final class TemplateData {
    private final String template;
    private final Set<String> templateSelectors;
    private final ITemplateResource templateResource;
    private final TemplateMode templateMode;
    private final ICacheEntryValidity cacheValidity;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TemplateData(String template, Set<String> templateSelectors, ITemplateResource templateResource, TemplateMode templateMode, ICacheEntryValidity cacheValidity) {
        this.template = template;
        this.templateSelectors = templateSelectors;
        this.templateResource = templateResource;
        this.templateMode = templateMode;
        this.cacheValidity = cacheValidity;
    }

    public String getTemplate() {
        return this.template;
    }

    public boolean hasTemplateSelectors() {
        return this.templateSelectors != null;
    }

    public Set<String> getTemplateSelectors() {
        return this.templateSelectors;
    }

    public ITemplateResource getTemplateResource() {
        return this.templateResource;
    }

    public TemplateMode getTemplateMode() {
        return this.templateMode;
    }

    public ICacheEntryValidity getValidity() {
        return this.cacheValidity;
    }
}