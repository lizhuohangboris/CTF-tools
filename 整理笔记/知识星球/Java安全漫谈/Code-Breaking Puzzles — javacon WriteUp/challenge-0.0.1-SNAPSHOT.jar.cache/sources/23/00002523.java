package org.springframework.web.method.support;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/method/support/AsyncHandlerMethodReturnValueHandler.class */
public interface AsyncHandlerMethodReturnValueHandler extends HandlerMethodReturnValueHandler {
    boolean isAsyncReturnValue(@Nullable Object obj, MethodParameter methodParameter);
}