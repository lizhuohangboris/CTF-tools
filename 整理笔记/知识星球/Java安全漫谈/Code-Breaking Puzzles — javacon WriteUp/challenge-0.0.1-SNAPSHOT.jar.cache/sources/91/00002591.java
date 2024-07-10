package org.springframework.web.servlet;

import java.util.Locale;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/ViewResolver.class */
public interface ViewResolver {
    @Nullable
    View resolveViewName(String str, Locale locale) throws Exception;
}