package org.springframework.web.context.request;

import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/request/WebRequest.class */
public interface WebRequest extends RequestAttributes {
    @Nullable
    String getHeader(String str);

    @Nullable
    String[] getHeaderValues(String str);

    Iterator<String> getHeaderNames();

    @Nullable
    String getParameter(String str);

    @Nullable
    String[] getParameterValues(String str);

    Iterator<String> getParameterNames();

    Map<String, String[]> getParameterMap();

    Locale getLocale();

    String getContextPath();

    @Nullable
    String getRemoteUser();

    @Nullable
    Principal getUserPrincipal();

    boolean isUserInRole(String str);

    boolean isSecure();

    boolean checkNotModified(long j);

    boolean checkNotModified(String str);

    boolean checkNotModified(@Nullable String str, long j);

    String getDescription(boolean z);
}