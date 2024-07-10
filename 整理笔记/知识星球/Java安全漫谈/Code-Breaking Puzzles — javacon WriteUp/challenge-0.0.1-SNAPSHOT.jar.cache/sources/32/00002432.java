package org.springframework.web.bind.support;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/bind/support/WebArgumentResolver.class */
public interface WebArgumentResolver {
    public static final Object UNRESOLVED = new Object();

    @Nullable
    Object resolveArgument(MethodParameter methodParameter, NativeWebRequest nativeWebRequest) throws Exception;
}