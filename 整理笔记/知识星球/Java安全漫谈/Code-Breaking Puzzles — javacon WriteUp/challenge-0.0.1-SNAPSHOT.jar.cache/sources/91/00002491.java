package org.springframework.web.context.request.async;

import java.util.function.Consumer;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/request/async/AsyncWebRequest.class */
public interface AsyncWebRequest extends NativeWebRequest {
    void setTimeout(@Nullable Long l);

    void addTimeoutHandler(Runnable runnable);

    void addErrorHandler(Consumer<Throwable> consumer);

    void addCompletionHandler(Runnable runnable);

    void startAsync();

    boolean isAsyncStarted();

    void dispatch();

    boolean isAsyncComplete();
}