package org.springframework.web.servlet.view;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/AbstractCachingViewResolver.class */
public abstract class AbstractCachingViewResolver extends WebApplicationObjectSupport implements ViewResolver {
    public static final int DEFAULT_CACHE_LIMIT = 1024;
    private static final View UNRESOLVED_VIEW = new View() { // from class: org.springframework.web.servlet.view.AbstractCachingViewResolver.1
        @Override // org.springframework.web.servlet.View
        @Nullable
        public String getContentType() {
            return null;
        }

        @Override // org.springframework.web.servlet.View
        public void render(@Nullable Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
        }
    };
    private volatile int cacheLimit = 1024;
    private boolean cacheUnresolved = true;
    private final Map<Object, View> viewAccessCache = new ConcurrentHashMap(1024);
    private final Map<Object, View> viewCreationCache = new LinkedHashMap<Object, View>(1024, 0.75f, true) { // from class: org.springframework.web.servlet.view.AbstractCachingViewResolver.2
        {
            AbstractCachingViewResolver.this = this;
        }

        @Override // java.util.LinkedHashMap
        protected boolean removeEldestEntry(Map.Entry<Object, View> eldest) {
            if (size() > AbstractCachingViewResolver.this.getCacheLimit()) {
                AbstractCachingViewResolver.this.viewAccessCache.remove(eldest.getKey());
                return true;
            }
            return false;
        }
    };

    @Nullable
    protected abstract View loadView(String str, Locale locale) throws Exception;

    public void setCacheLimit(int cacheLimit) {
        this.cacheLimit = cacheLimit;
    }

    public int getCacheLimit() {
        return this.cacheLimit;
    }

    public void setCache(boolean cache) {
        this.cacheLimit = cache ? 1024 : 0;
    }

    public boolean isCache() {
        return this.cacheLimit > 0;
    }

    public void setCacheUnresolved(boolean cacheUnresolved) {
        this.cacheUnresolved = cacheUnresolved;
    }

    public boolean isCacheUnresolved() {
        return this.cacheUnresolved;
    }

    @Override // org.springframework.web.servlet.ViewResolver
    @Nullable
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        if (!isCache()) {
            return createView(viewName, locale);
        }
        Object cacheKey = getCacheKey(viewName, locale);
        View view = this.viewAccessCache.get(cacheKey);
        if (view == null) {
            synchronized (this.viewCreationCache) {
                view = this.viewCreationCache.get(cacheKey);
                if (view == null) {
                    view = createView(viewName, locale);
                    if (view == null && this.cacheUnresolved) {
                        view = UNRESOLVED_VIEW;
                    }
                    if (view != null) {
                        this.viewAccessCache.put(cacheKey, view);
                        this.viewCreationCache.put(cacheKey, view);
                    }
                }
            }
        } else if (this.logger.isTraceEnabled()) {
            this.logger.trace(formatKey(cacheKey) + "served from cache");
        }
        if (view != UNRESOLVED_VIEW) {
            return view;
        }
        return null;
    }

    private static String formatKey(Object cacheKey) {
        return "View with key [" + cacheKey + "] ";
    }

    protected Object getCacheKey(String viewName, Locale locale) {
        return viewName + '_' + locale;
    }

    public void removeFromCache(String viewName, Locale locale) {
        Object cachedView;
        if (!isCache()) {
            this.logger.warn("Caching is OFF (removal not necessary)");
            return;
        }
        Object cacheKey = getCacheKey(viewName, locale);
        synchronized (this.viewCreationCache) {
            this.viewAccessCache.remove(cacheKey);
            cachedView = this.viewCreationCache.remove(cacheKey);
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(formatKey(cacheKey) + (cachedView != null ? "cleared from cache" : "not found in the cache"));
        }
    }

    public void clearCache() {
        this.logger.debug("Clearing all views from the cache");
        synchronized (this.viewCreationCache) {
            this.viewAccessCache.clear();
            this.viewCreationCache.clear();
        }
    }

    @Nullable
    public View createView(String viewName, Locale locale) throws Exception {
        return loadView(viewName, locale);
    }
}