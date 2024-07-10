package org.springframework.web.servlet.view.groovy;

import java.util.Locale;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/groovy/GroovyMarkupViewResolver.class */
public class GroovyMarkupViewResolver extends AbstractTemplateViewResolver {
    public GroovyMarkupViewResolver() {
        setViewClass(requiredViewClass());
    }

    public GroovyMarkupViewResolver(String prefix, String suffix) {
        this();
        setPrefix(prefix);
        setSuffix(suffix);
    }

    @Override // org.springframework.web.servlet.view.AbstractTemplateViewResolver, org.springframework.web.servlet.view.UrlBasedViewResolver
    protected Class<?> requiredViewClass() {
        return GroovyMarkupView.class;
    }

    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver, org.springframework.web.servlet.view.AbstractCachingViewResolver
    protected Object getCacheKey(String viewName, Locale locale) {
        return viewName + '_' + locale;
    }
}