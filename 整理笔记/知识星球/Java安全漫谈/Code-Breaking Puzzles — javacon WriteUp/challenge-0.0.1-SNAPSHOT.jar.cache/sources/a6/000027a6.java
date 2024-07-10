package org.thymeleaf.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.engine.TemplateModel;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/cache/StandardCacheManager.class */
public class StandardCacheManager extends AbstractCacheManager {
    public static final String DEFAULT_TEMPLATE_CACHE_NAME = "TEMPLATE_CACHE";
    public static final int DEFAULT_TEMPLATE_CACHE_INITIAL_SIZE = 20;
    public static final int DEFAULT_TEMPLATE_CACHE_MAX_SIZE = 200;
    public static final boolean DEFAULT_TEMPLATE_CACHE_ENABLE_COUNTERS = false;
    public static final boolean DEFAULT_TEMPLATE_CACHE_USE_SOFT_REFERENCES = true;
    public static final String DEFAULT_EXPRESSION_CACHE_NAME = "EXPRESSION_CACHE";
    public static final int DEFAULT_EXPRESSION_CACHE_INITIAL_SIZE = 100;
    public static final int DEFAULT_EXPRESSION_CACHE_MAX_SIZE = 500;
    public static final boolean DEFAULT_EXPRESSION_CACHE_ENABLE_COUNTERS = false;
    public static final boolean DEFAULT_EXPRESSION_CACHE_USE_SOFT_REFERENCES = true;
    private String templateCacheName = DEFAULT_TEMPLATE_CACHE_NAME;
    private int templateCacheInitialSize = 20;
    private int templateCacheMaxSize = 200;
    private boolean templateCacheEnableCounters = false;
    private boolean templateCacheUseSoftReferences = true;
    private String templateCacheLoggerName = DEFAULT_TEMPLATE_CACHE_LOGGER_NAME;
    private ICacheEntryValidityChecker<TemplateCacheKey, TemplateModel> templateCacheValidityChecker = DEFAULT_TEMPLATE_CACHE_VALIDITY_CHECKER;
    private String expressionCacheName = DEFAULT_EXPRESSION_CACHE_NAME;
    private int expressionCacheInitialSize = 100;
    private int expressionCacheMaxSize = 500;
    private boolean expressionCacheEnableCounters = false;
    private boolean expressionCacheUseSoftReferences = true;
    private String expressionCacheLoggerName = DEFAULT_EXPRESSION_CACHE_LOGGER_NAME;
    private ICacheEntryValidityChecker<ExpressionCacheKey, Object> expressionCacheValidityChecker = DEFAULT_EXPRESSION_CACHE_VALIDITY_CHECKER;
    public static final String DEFAULT_TEMPLATE_CACHE_LOGGER_NAME = null;
    public static final ICacheEntryValidityChecker<TemplateCacheKey, TemplateModel> DEFAULT_TEMPLATE_CACHE_VALIDITY_CHECKER = new StandardParsedTemplateEntryValidator();
    public static final String DEFAULT_EXPRESSION_CACHE_LOGGER_NAME = null;
    public static final ICacheEntryValidityChecker<ExpressionCacheKey, Object> DEFAULT_EXPRESSION_CACHE_VALIDITY_CHECKER = null;

    @Override // org.thymeleaf.cache.AbstractCacheManager
    protected final ICache<TemplateCacheKey, TemplateModel> initializeTemplateCache() {
        int maxSize = getTemplateCacheMaxSize();
        if (maxSize == 0) {
            return null;
        }
        return new StandardCache(getTemplateCacheName(), getTemplateCacheUseSoftReferences(), getTemplateCacheInitialSize(), maxSize, getTemplateCacheValidityChecker(), getTemplateCacheLogger(), getTemplateCacheEnableCounters());
    }

    @Override // org.thymeleaf.cache.AbstractCacheManager
    protected final ICache<ExpressionCacheKey, Object> initializeExpressionCache() {
        int maxSize = getExpressionCacheMaxSize();
        if (maxSize == 0) {
            return null;
        }
        return new StandardCache(getExpressionCacheName(), getExpressionCacheUseSoftReferences(), getExpressionCacheInitialSize(), maxSize, getExpressionCacheValidityChecker(), getExpressionCacheLogger(), getExpressionCacheEnableCounters());
    }

    public String getTemplateCacheName() {
        return this.templateCacheName;
    }

    public boolean getTemplateCacheUseSoftReferences() {
        return this.templateCacheUseSoftReferences;
    }

    private boolean getTemplateCacheEnableCounters() {
        return this.templateCacheEnableCounters;
    }

    public int getTemplateCacheInitialSize() {
        return this.templateCacheInitialSize;
    }

    public int getTemplateCacheMaxSize() {
        return this.templateCacheMaxSize;
    }

    public String getTemplateCacheLoggerName() {
        return this.templateCacheLoggerName;
    }

    public ICacheEntryValidityChecker<TemplateCacheKey, TemplateModel> getTemplateCacheValidityChecker() {
        return this.templateCacheValidityChecker;
    }

    public final Logger getTemplateCacheLogger() {
        String loggerName = getTemplateCacheLoggerName();
        if (loggerName != null) {
            return LoggerFactory.getLogger(loggerName);
        }
        return LoggerFactory.getLogger(TemplateEngine.class.getName() + ".cache." + getTemplateCacheName());
    }

    public String getExpressionCacheName() {
        return this.expressionCacheName;
    }

    public boolean getExpressionCacheUseSoftReferences() {
        return this.expressionCacheUseSoftReferences;
    }

    private boolean getExpressionCacheEnableCounters() {
        return this.expressionCacheEnableCounters;
    }

    public int getExpressionCacheInitialSize() {
        return this.expressionCacheInitialSize;
    }

    public int getExpressionCacheMaxSize() {
        return this.expressionCacheMaxSize;
    }

    public String getExpressionCacheLoggerName() {
        return this.expressionCacheLoggerName;
    }

    public ICacheEntryValidityChecker<ExpressionCacheKey, Object> getExpressionCacheValidityChecker() {
        return this.expressionCacheValidityChecker;
    }

    public final Logger getExpressionCacheLogger() {
        String loggerName = getExpressionCacheLoggerName();
        if (loggerName != null) {
            return LoggerFactory.getLogger(loggerName);
        }
        return LoggerFactory.getLogger(TemplateEngine.class.getName() + ".cache." + getExpressionCacheName());
    }

    public void setTemplateCacheName(String templateCacheName) {
        this.templateCacheName = templateCacheName;
    }

    public void setTemplateCacheInitialSize(int templateCacheInitialSize) {
        this.templateCacheInitialSize = templateCacheInitialSize;
    }

    public void setTemplateCacheMaxSize(int templateCacheMaxSize) {
        this.templateCacheMaxSize = templateCacheMaxSize;
    }

    public void setTemplateCacheUseSoftReferences(boolean templateCacheUseSoftReferences) {
        this.templateCacheUseSoftReferences = templateCacheUseSoftReferences;
    }

    public void setTemplateCacheLoggerName(String templateCacheLoggerName) {
        this.templateCacheLoggerName = templateCacheLoggerName;
    }

    public void setTemplateCacheValidityChecker(ICacheEntryValidityChecker<TemplateCacheKey, TemplateModel> templateCacheValidityChecker) {
        this.templateCacheValidityChecker = templateCacheValidityChecker;
    }

    public void setTemplateCacheEnableCounters(boolean templateCacheEnableCounters) {
        this.templateCacheEnableCounters = templateCacheEnableCounters;
    }

    public void setExpressionCacheName(String expressionCacheName) {
        this.expressionCacheName = expressionCacheName;
    }

    public void setExpressionCacheInitialSize(int expressionCacheInitialSize) {
        this.expressionCacheInitialSize = expressionCacheInitialSize;
    }

    public void setExpressionCacheMaxSize(int expressionCacheMaxSize) {
        this.expressionCacheMaxSize = expressionCacheMaxSize;
    }

    public void setExpressionCacheUseSoftReferences(boolean expressionCacheUseSoftReferences) {
        this.expressionCacheUseSoftReferences = expressionCacheUseSoftReferences;
    }

    public void setExpressionCacheLoggerName(String expressionCacheLoggerName) {
        this.expressionCacheLoggerName = expressionCacheLoggerName;
    }

    public void setExpressionCacheValidityChecker(ICacheEntryValidityChecker<ExpressionCacheKey, Object> expressionCacheValidityChecker) {
        this.expressionCacheValidityChecker = expressionCacheValidityChecker;
    }

    public void setExpressionCacheEnableCounters(boolean expressionCacheEnableCounters) {
        this.expressionCacheEnableCounters = expressionCacheEnableCounters;
    }
}