package org.springframework.web.cors;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.UrlPathHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/cors/UrlBasedCorsConfigurationSource.class */
public class UrlBasedCorsConfigurationSource implements CorsConfigurationSource {
    private final Map<String, CorsConfiguration> corsConfigurations = new LinkedHashMap();
    private PathMatcher pathMatcher = new AntPathMatcher();
    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    public void setPathMatcher(PathMatcher pathMatcher) {
        Assert.notNull(pathMatcher, "PathMatcher must not be null");
        this.pathMatcher = pathMatcher;
    }

    public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
        this.urlPathHelper.setAlwaysUseFullPath(alwaysUseFullPath);
    }

    public void setUrlDecode(boolean urlDecode) {
        this.urlPathHelper.setUrlDecode(urlDecode);
    }

    public void setRemoveSemicolonContent(boolean removeSemicolonContent) {
        this.urlPathHelper.setRemoveSemicolonContent(removeSemicolonContent);
    }

    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        Assert.notNull(urlPathHelper, "UrlPathHelper must not be null");
        this.urlPathHelper = urlPathHelper;
    }

    public void setCorsConfigurations(@Nullable Map<String, CorsConfiguration> corsConfigurations) {
        this.corsConfigurations.clear();
        if (corsConfigurations != null) {
            this.corsConfigurations.putAll(corsConfigurations);
        }
    }

    public Map<String, CorsConfiguration> getCorsConfigurations() {
        return Collections.unmodifiableMap(this.corsConfigurations);
    }

    public void registerCorsConfiguration(String path, CorsConfiguration config) {
        this.corsConfigurations.put(path, config);
    }

    @Override // org.springframework.web.cors.CorsConfigurationSource
    @Nullable
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);
        for (Map.Entry<String, CorsConfiguration> entry : this.corsConfigurations.entrySet()) {
            if (this.pathMatcher.match(entry.getKey(), lookupPath)) {
                return entry.getValue();
            }
        }
        return null;
    }
}