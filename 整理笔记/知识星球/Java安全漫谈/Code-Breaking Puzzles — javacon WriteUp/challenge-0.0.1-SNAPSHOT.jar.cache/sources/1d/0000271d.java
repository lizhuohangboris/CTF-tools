package org.springframework.web.servlet.view.script;

import org.springframework.web.servlet.view.UrlBasedViewResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/script/ScriptTemplateViewResolver.class */
public class ScriptTemplateViewResolver extends UrlBasedViewResolver {
    public ScriptTemplateViewResolver() {
        setViewClass(requiredViewClass());
    }

    public ScriptTemplateViewResolver(String prefix, String suffix) {
        this();
        setPrefix(prefix);
        setSuffix(suffix);
    }

    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver
    protected Class<?> requiredViewClass() {
        return ScriptTemplateView.class;
    }
}