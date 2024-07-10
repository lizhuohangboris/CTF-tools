package org.springframework.web.context.request.async;

import java.util.concurrent.Callable;
import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/request/async/TimeoutCallableProcessingInterceptor.class */
public class TimeoutCallableProcessingInterceptor implements CallableProcessingInterceptor {
    @Override // org.springframework.web.context.request.async.CallableProcessingInterceptor
    public <T> Object handleTimeout(NativeWebRequest request, Callable<T> task) throws Exception {
        return new AsyncRequestTimeoutException();
    }
}