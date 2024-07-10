package org.springframework.web.servlet.support;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/support/RequestDataValueProcessor.class */
public interface RequestDataValueProcessor {
    String processAction(HttpServletRequest httpServletRequest, String str, String str2);

    String processFormFieldValue(HttpServletRequest httpServletRequest, @Nullable String str, String str2, String str3);

    @Nullable
    Map<String, String> getExtraHiddenFields(HttpServletRequest httpServletRequest);

    String processUrl(HttpServletRequest httpServletRequest, String str);
}