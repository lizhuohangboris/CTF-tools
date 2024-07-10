package org.springframework.web.bind.support;

import org.springframework.lang.Nullable;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/bind/support/WebDataBinderFactory.class */
public interface WebDataBinderFactory {
    WebDataBinder createBinder(NativeWebRequest nativeWebRequest, @Nullable Object obj, String str) throws Exception;
}