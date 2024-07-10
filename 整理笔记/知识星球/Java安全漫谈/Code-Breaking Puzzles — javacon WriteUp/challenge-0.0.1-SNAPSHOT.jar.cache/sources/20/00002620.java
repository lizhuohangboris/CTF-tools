package org.springframework.web.servlet.mvc.method.annotation;

import java.util.concurrent.CompletionStage;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/DeferredResultMethodReturnValueHandler.class */
public class DeferredResultMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public boolean supportsReturnType(MethodParameter returnType) {
        Class<?> type = returnType.getParameterType();
        return DeferredResult.class.isAssignableFrom(type) || ListenableFuture.class.isAssignableFrom(type) || CompletionStage.class.isAssignableFrom(type);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        DeferredResult<?> result;
        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }
        if (returnValue instanceof DeferredResult) {
            result = (DeferredResult) returnValue;
        } else if (returnValue instanceof ListenableFuture) {
            result = adaptListenableFuture((ListenableFuture) returnValue);
        } else if (returnValue instanceof CompletionStage) {
            result = adaptCompletionStage((CompletionStage) returnValue);
        } else {
            throw new IllegalStateException("Unexpected return value type: " + returnValue);
        }
        WebAsyncUtils.getAsyncManager(webRequest).startDeferredResultProcessing(result, mavContainer);
    }

    private DeferredResult<Object> adaptListenableFuture(ListenableFuture<?> future) {
        final DeferredResult<Object> result = new DeferredResult<>();
        future.addCallback(new ListenableFutureCallback<Object>() { // from class: org.springframework.web.servlet.mvc.method.annotation.DeferredResultMethodReturnValueHandler.1
            @Override // org.springframework.util.concurrent.SuccessCallback
            public void onSuccess(@Nullable Object value) {
                result.setResult(value);
            }

            @Override // org.springframework.util.concurrent.FailureCallback
            public void onFailure(Throwable ex) {
                result.setErrorResult(ex);
            }
        });
        return result;
    }

    private DeferredResult<Object> adaptCompletionStage(CompletionStage<?> future) {
        DeferredResult<Object> result = new DeferredResult<>();
        future.handle(value, ex -> {
            if (ex != null) {
                result.setErrorResult(ex);
                return null;
            }
            result.setResult(value);
            return null;
        });
        return result;
    }
}