package org.springframework.web.context.request.async;

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/request/async/DeferredResult.class */
public class DeferredResult<T> {
    private static final Object RESULT_NONE = new Object();
    private static final Log logger = LogFactory.getLog(DeferredResult.class);
    @Nullable
    private final Long timeout;
    private final Supplier<?> timeoutResult;
    private Runnable timeoutCallback;
    private Consumer<Throwable> errorCallback;
    private Runnable completionCallback;
    private DeferredResultHandler resultHandler;
    private volatile Object result;
    private volatile boolean expired;

    @FunctionalInterface
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/request/async/DeferredResult$DeferredResultHandler.class */
    public interface DeferredResultHandler {
        void handleResult(Object obj);
    }

    public DeferredResult() {
        this((Long) null, (Supplier<?>) () -> {
            return RESULT_NONE;
        });
    }

    public DeferredResult(Long timeout) {
        this(timeout, (Supplier<?>) () -> {
            return RESULT_NONE;
        });
    }

    public DeferredResult(@Nullable Long timeout, Object timeoutResult) {
        this.result = RESULT_NONE;
        this.expired = false;
        this.timeoutResult = () -> {
            return timeoutResult;
        };
        this.timeout = timeout;
    }

    public DeferredResult(@Nullable Long timeout, Supplier<?> timeoutResult) {
        this.result = RESULT_NONE;
        this.expired = false;
        this.timeoutResult = timeoutResult;
        this.timeout = timeout;
    }

    public final boolean isSetOrExpired() {
        return this.result != RESULT_NONE || this.expired;
    }

    public boolean hasResult() {
        return this.result != RESULT_NONE;
    }

    @Nullable
    public Object getResult() {
        Object resultToCheck = this.result;
        if (resultToCheck != RESULT_NONE) {
            return resultToCheck;
        }
        return null;
    }

    @Nullable
    public final Long getTimeoutValue() {
        return this.timeout;
    }

    public void onTimeout(Runnable callback) {
        this.timeoutCallback = callback;
    }

    public void onError(Consumer<Throwable> callback) {
        this.errorCallback = callback;
    }

    public void onCompletion(Runnable callback) {
        this.completionCallback = callback;
    }

    public final void setResultHandler(DeferredResultHandler resultHandler) {
        Assert.notNull(resultHandler, "DeferredResultHandler is required");
        if (this.expired) {
            return;
        }
        synchronized (this) {
            if (this.expired) {
                return;
            }
            Object resultToHandle = this.result;
            if (resultToHandle == RESULT_NONE) {
                this.resultHandler = resultHandler;
                return;
            }
            try {
                resultHandler.handleResult(resultToHandle);
            } catch (Throwable ex) {
                logger.debug("Failed to process async result", ex);
            }
        }
    }

    public boolean setResult(T result) {
        return setResultInternal(result);
    }

    public boolean setResultInternal(Object result) {
        if (isSetOrExpired()) {
            return false;
        }
        synchronized (this) {
            if (isSetOrExpired()) {
                return false;
            }
            this.result = result;
            DeferredResultHandler resultHandlerToUse = this.resultHandler;
            if (resultHandlerToUse == null) {
                return true;
            }
            this.resultHandler = null;
            resultHandlerToUse.handleResult(result);
            return true;
        }
    }

    public boolean setErrorResult(Object result) {
        return setResultInternal(result);
    }

    public final DeferredResultProcessingInterceptor getInterceptor() {
        return new DeferredResultProcessingInterceptor() { // from class: org.springframework.web.context.request.async.DeferredResult.1
            {
                DeferredResult.this = this;
            }

            /* JADX WARN: Finally extract failed */
            @Override // org.springframework.web.context.request.async.DeferredResultProcessingInterceptor
            public <S> boolean handleTimeout(NativeWebRequest request, DeferredResult<S> deferredResult) {
                boolean continueProcessing = true;
                try {
                    if (DeferredResult.this.timeoutCallback != null) {
                        DeferredResult.this.timeoutCallback.run();
                    }
                    Object value = DeferredResult.this.timeoutResult.get();
                    if (value != DeferredResult.RESULT_NONE) {
                        continueProcessing = false;
                        try {
                            DeferredResult.this.setResultInternal(value);
                        } catch (Throwable ex) {
                            DeferredResult.logger.debug("Failed to handle timeout result", ex);
                        }
                    }
                    return continueProcessing;
                } catch (Throwable th) {
                    Object value2 = DeferredResult.this.timeoutResult.get();
                    if (value2 != DeferredResult.RESULT_NONE) {
                        try {
                            DeferredResult.this.setResultInternal(value2);
                        } catch (Throwable ex2) {
                            DeferredResult.logger.debug("Failed to handle timeout result", ex2);
                        }
                    }
                    throw th;
                }
            }

            @Override // org.springframework.web.context.request.async.DeferredResultProcessingInterceptor
            public <S> boolean handleError(NativeWebRequest request, DeferredResult<S> deferredResult, Throwable t) {
                try {
                    if (DeferredResult.this.errorCallback != null) {
                        DeferredResult.this.errorCallback.accept(t);
                    }
                    try {
                        return false;
                    } catch (Throwable ex) {
                        return false;
                    }
                } finally {
                    try {
                        DeferredResult.this.setResultInternal(t);
                    } catch (Throwable ex2) {
                        DeferredResult.logger.debug("Failed to handle error result", ex2);
                    }
                }
            }

            @Override // org.springframework.web.context.request.async.DeferredResultProcessingInterceptor
            public <S> void afterCompletion(NativeWebRequest request, DeferredResult<S> deferredResult) {
                DeferredResult.this.expired = true;
                if (DeferredResult.this.completionCallback != null) {
                    DeferredResult.this.completionCallback.run();
                }
            }
        };
    }
}