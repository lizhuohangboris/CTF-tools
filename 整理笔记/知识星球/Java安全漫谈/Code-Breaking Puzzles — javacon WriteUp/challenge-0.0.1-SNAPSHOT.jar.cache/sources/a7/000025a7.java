package org.springframework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.MappedInterceptor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/config/annotation/InterceptorRegistration.class */
public class InterceptorRegistration {
    private final HandlerInterceptor interceptor;
    @Nullable
    private PathMatcher pathMatcher;
    private final List<String> includePatterns = new ArrayList();
    private final List<String> excludePatterns = new ArrayList();
    private int order = 0;

    public InterceptorRegistration(HandlerInterceptor interceptor) {
        Assert.notNull(interceptor, "Interceptor is required");
        this.interceptor = interceptor;
    }

    public InterceptorRegistration addPathPatterns(String... patterns) {
        return addPathPatterns(Arrays.asList(patterns));
    }

    public InterceptorRegistration addPathPatterns(List<String> patterns) {
        this.includePatterns.addAll(patterns);
        return this;
    }

    public InterceptorRegistration excludePathPatterns(String... patterns) {
        return excludePathPatterns(Arrays.asList(patterns));
    }

    public InterceptorRegistration excludePathPatterns(List<String> patterns) {
        this.excludePatterns.addAll(patterns);
        return this;
    }

    public InterceptorRegistration pathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
        return this;
    }

    public InterceptorRegistration order(int order) {
        this.order = order;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getOrder() {
        return this.order;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object getInterceptor() {
        if (this.includePatterns.isEmpty() && this.excludePatterns.isEmpty()) {
            return this.interceptor;
        }
        String[] include = StringUtils.toStringArray(this.includePatterns);
        String[] exclude = StringUtils.toStringArray(this.excludePatterns);
        MappedInterceptor mappedInterceptor = new MappedInterceptor(include, exclude, this.interceptor);
        if (this.pathMatcher != null) {
            mappedInterceptor.setPathMatcher(this.pathMatcher);
        }
        return mappedInterceptor;
    }
}