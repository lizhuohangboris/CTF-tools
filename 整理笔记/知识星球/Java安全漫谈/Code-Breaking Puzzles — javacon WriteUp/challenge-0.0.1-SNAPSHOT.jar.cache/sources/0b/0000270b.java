package org.springframework.web.servlet.view.freemarker;

import org.springframework.web.servlet.view.AbstractTemplateViewResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/freemarker/FreeMarkerViewResolver.class */
public class FreeMarkerViewResolver extends AbstractTemplateViewResolver {
    public FreeMarkerViewResolver() {
        setViewClass(requiredViewClass());
    }

    public FreeMarkerViewResolver(String prefix, String suffix) {
        this();
        setPrefix(prefix);
        setSuffix(suffix);
    }

    @Override // org.springframework.web.servlet.view.AbstractTemplateViewResolver, org.springframework.web.servlet.view.UrlBasedViewResolver
    protected Class<?> requiredViewClass() {
        return FreeMarkerView.class;
    }
}