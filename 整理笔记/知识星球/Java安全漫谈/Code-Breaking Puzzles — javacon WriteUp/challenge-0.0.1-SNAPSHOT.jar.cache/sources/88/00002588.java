package org.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/LocaleContextResolver.class */
public interface LocaleContextResolver extends LocaleResolver {
    LocaleContext resolveLocaleContext(HttpServletRequest httpServletRequest);

    void setLocaleContext(HttpServletRequest httpServletRequest, @Nullable HttpServletResponse httpServletResponse, @Nullable LocaleContext localeContext);
}