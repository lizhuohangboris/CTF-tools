package org.thymeleaf.cache;

import java.util.Collections;
import java.util.List;
import org.thymeleaf.engine.TemplateModel;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/cache/AbstractCacheManager.class */
public abstract class AbstractCacheManager implements ICacheManager {
    private volatile ICache<TemplateCacheKey, TemplateModel> templateCache;
    private volatile ICache<ExpressionCacheKey, Object> expressionCache;
    private volatile boolean templateCacheInitialized = false;
    private volatile boolean expressionCacheInitialized = false;

    protected abstract ICache<TemplateCacheKey, TemplateModel> initializeTemplateCache();

    protected abstract ICache<ExpressionCacheKey, Object> initializeExpressionCache();

    @Override // org.thymeleaf.cache.ICacheManager
    public final ICache<TemplateCacheKey, TemplateModel> getTemplateCache() {
        if (!this.templateCacheInitialized) {
            synchronized (this) {
                if (!this.templateCacheInitialized) {
                    this.templateCache = initializeTemplateCache();
                    this.templateCacheInitialized = true;
                }
            }
        }
        return this.templateCache;
    }

    @Override // org.thymeleaf.cache.ICacheManager
    public final ICache<ExpressionCacheKey, Object> getExpressionCache() {
        if (!this.expressionCacheInitialized) {
            synchronized (this) {
                if (!this.expressionCacheInitialized) {
                    this.expressionCache = initializeExpressionCache();
                    this.expressionCacheInitialized = true;
                }
            }
        }
        return this.expressionCache;
    }

    @Override // org.thymeleaf.cache.ICacheManager
    public <K, V> ICache<K, V> getSpecificCache(String name) {
        return null;
    }

    @Override // org.thymeleaf.cache.ICacheManager
    public List<String> getAllSpecificCacheNames() {
        return Collections.emptyList();
    }

    @Override // org.thymeleaf.cache.ICacheManager
    public void clearAllCaches() {
        ICache<TemplateCacheKey, TemplateModel> templateCacheObj = getTemplateCache();
        if (templateCacheObj != null) {
            templateCacheObj.clear();
        }
        ICache<ExpressionCacheKey, Object> expressionCacheObj = getExpressionCache();
        if (expressionCacheObj != null) {
            expressionCacheObj.clear();
        }
        List<String> allSpecificCacheNamesObj = getAllSpecificCacheNames();
        if (allSpecificCacheNamesObj != null) {
            for (String specificCacheName : allSpecificCacheNamesObj) {
                ICache<?, ?> specificCache = getSpecificCache(specificCacheName);
                if (specificCache != null) {
                    specificCache.clear();
                }
            }
        }
    }
}