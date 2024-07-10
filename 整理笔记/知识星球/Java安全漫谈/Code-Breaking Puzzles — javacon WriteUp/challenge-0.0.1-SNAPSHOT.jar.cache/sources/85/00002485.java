package org.springframework.web.context.request;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/request/NativeWebRequest.class */
public interface NativeWebRequest extends WebRequest {
    Object getNativeRequest();

    @Nullable
    Object getNativeResponse();

    @Nullable
    <T> T getNativeRequest(@Nullable Class<T> cls);

    @Nullable
    <T> T getNativeResponse(@Nullable Class<T> cls);
}