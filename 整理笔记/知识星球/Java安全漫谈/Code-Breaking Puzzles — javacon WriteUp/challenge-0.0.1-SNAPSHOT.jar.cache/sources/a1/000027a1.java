package org.thymeleaf.cache;

import java.util.List;
import org.thymeleaf.engine.TemplateModel;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/cache/ICacheManager.class */
public interface ICacheManager {
    ICache<TemplateCacheKey, TemplateModel> getTemplateCache();

    ICache<ExpressionCacheKey, Object> getExpressionCache();

    <K, V> ICache<K, V> getSpecificCache(String str);

    List<String> getAllSpecificCacheNames();

    void clearAllCaches();
}