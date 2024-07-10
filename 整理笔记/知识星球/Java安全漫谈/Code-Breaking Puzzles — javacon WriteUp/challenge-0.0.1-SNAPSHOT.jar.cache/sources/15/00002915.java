package org.thymeleaf.standard.expression;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.cache.ExpressionCacheKey;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/ExpressionCache.class */
public final class ExpressionCache {
    private static final String EXPRESSION_CACHE_TYPE_STANDARD_EXPRESSION = "expr";
    private static final String EXPRESSION_CACHE_TYPE_ASSIGNATION_SEQUENCE = "aseq";
    private static final String EXPRESSION_CACHE_TYPE_EXPRESSION_SEQUENCE = "eseq";
    private static final String EXPRESSION_CACHE_TYPE_EACH = "each";
    private static final String EXPRESSION_CACHE_TYPE_FRAGMENT_SIGNATURE = "fsig";

    private ExpressionCache() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object getFromCache(IEngineConfiguration configuration, String input, String type) {
        ICache<ExpressionCacheKey, Object> cache;
        ICacheManager cacheManager = configuration.getCacheManager();
        if (cacheManager != null && (cache = cacheManager.getExpressionCache()) != null) {
            return cache.get(new ExpressionCacheKey(type, input));
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <V> void putIntoCache(IEngineConfiguration configuration, String input, V value, String type) {
        ICache<ExpressionCacheKey, Object> cache;
        ICacheManager cacheManager = configuration.getCacheManager();
        if (cacheManager != null && (cache = cacheManager.getExpressionCache()) != null) {
            cache.put(new ExpressionCacheKey(type, input), value);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <V> void removeFromCache(IEngineConfiguration configuration, String input, String type) {
        ICache<ExpressionCacheKey, Object> cache;
        ICacheManager cacheManager = configuration.getCacheManager();
        if (cacheManager != null && (cache = cacheManager.getExpressionCache()) != null) {
            cache.clearKey(new ExpressionCacheKey(type, input));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static IStandardExpression getExpressionFromCache(IEngineConfiguration configuration, String input) {
        return (IStandardExpression) getFromCache(configuration, input, EXPRESSION_CACHE_TYPE_STANDARD_EXPRESSION);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void putExpressionIntoCache(IEngineConfiguration configuration, String input, IStandardExpression value) {
        putIntoCache(configuration, input, value, EXPRESSION_CACHE_TYPE_STANDARD_EXPRESSION);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AssignationSequence getAssignationSequenceFromCache(IEngineConfiguration configuration, String input) {
        return (AssignationSequence) getFromCache(configuration, input, EXPRESSION_CACHE_TYPE_ASSIGNATION_SEQUENCE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void putAssignationSequenceIntoCache(IEngineConfiguration configuration, String input, AssignationSequence value) {
        putIntoCache(configuration, input, value, EXPRESSION_CACHE_TYPE_ASSIGNATION_SEQUENCE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ExpressionSequence getExpressionSequenceFromCache(IEngineConfiguration configuration, String input) {
        return (ExpressionSequence) getFromCache(configuration, input, EXPRESSION_CACHE_TYPE_EXPRESSION_SEQUENCE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void putExpressionSequenceIntoCache(IEngineConfiguration configuration, String input, ExpressionSequence value) {
        putIntoCache(configuration, input, value, EXPRESSION_CACHE_TYPE_EXPRESSION_SEQUENCE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Each getEachFromCache(IEngineConfiguration configuration, String input) {
        return (Each) getFromCache(configuration, input, "each");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void putEachIntoCache(IEngineConfiguration configuration, String input, Each value) {
        putIntoCache(configuration, input, value, "each");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static FragmentSignature getFragmentSignatureFromCache(IEngineConfiguration configuration, String input) {
        return (FragmentSignature) getFromCache(configuration, input, EXPRESSION_CACHE_TYPE_FRAGMENT_SIGNATURE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void putFragmentSignatureIntoCache(IEngineConfiguration configuration, String input, FragmentSignature value) {
        putIntoCache(configuration, input, value, EXPRESSION_CACHE_TYPE_FRAGMENT_SIGNATURE);
    }
}