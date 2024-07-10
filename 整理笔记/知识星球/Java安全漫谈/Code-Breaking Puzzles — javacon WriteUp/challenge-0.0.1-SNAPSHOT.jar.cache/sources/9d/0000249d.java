package org.springframework.web.context.request.async;

import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/request/async/TimeoutDeferredResultProcessingInterceptor.class */
public class TimeoutDeferredResultProcessingInterceptor implements DeferredResultProcessingInterceptor {
    @Override // org.springframework.web.context.request.async.DeferredResultProcessingInterceptor
    public <T> boolean handleTimeout(NativeWebRequest request, DeferredResult<T> result) throws Exception {
        result.setErrorResult(new AsyncRequestTimeoutException());
        return false;
    }
}