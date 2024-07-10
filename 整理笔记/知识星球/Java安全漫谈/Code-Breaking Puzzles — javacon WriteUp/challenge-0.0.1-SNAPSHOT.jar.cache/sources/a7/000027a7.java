package org.thymeleaf.cache;

import org.thymeleaf.engine.TemplateModel;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/cache/StandardParsedTemplateEntryValidator.class */
public final class StandardParsedTemplateEntryValidator implements ICacheEntryValidityChecker<TemplateCacheKey, TemplateModel> {
    private static final long serialVersionUID = -185355204140990247L;

    @Override // org.thymeleaf.cache.ICacheEntryValidityChecker
    public boolean checkIsValueStillValid(TemplateCacheKey key, TemplateModel value, long entryCreationTimestamp) {
        return value.getTemplateData().getValidity().isCacheStillValid();
    }
}