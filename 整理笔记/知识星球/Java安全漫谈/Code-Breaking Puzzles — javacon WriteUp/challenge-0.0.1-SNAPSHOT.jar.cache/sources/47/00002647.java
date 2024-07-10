package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ResponseBodyEmitter.class */
public class ResponseBodyEmitter {
    @Nullable
    private final Long timeout;
    @Nullable
    private Handler handler;
    private final Set<DataWithMediaType> earlySendAttempts;
    private boolean complete;
    @Nullable
    private Throwable failure;
    private boolean sendFailed;
    private final DefaultCallback timeoutCallback;
    private final ErrorCallback errorCallback;
    private final DefaultCallback completionCallback;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ResponseBodyEmitter$Handler.class */
    public interface Handler {
        void send(Object obj, @Nullable MediaType mediaType) throws IOException;

        void complete();

        void completeWithError(Throwable th);

        void onTimeout(Runnable runnable);

        void onError(Consumer<Throwable> consumer);

        void onCompletion(Runnable runnable);
    }

    public ResponseBodyEmitter() {
        this.earlySendAttempts = new LinkedHashSet(8);
        this.timeoutCallback = new DefaultCallback();
        this.errorCallback = new ErrorCallback();
        this.completionCallback = new DefaultCallback();
        this.timeout = null;
    }

    public ResponseBodyEmitter(Long timeout) {
        this.earlySendAttempts = new LinkedHashSet(8);
        this.timeoutCallback = new DefaultCallback();
        this.errorCallback = new ErrorCallback();
        this.completionCallback = new DefaultCallback();
        this.timeout = timeout;
    }

    @Nullable
    public Long getTimeout() {
        return this.timeout;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void initialize(Handler handler) throws IOException {
        this.handler = handler;
        for (DataWithMediaType sendAttempt : this.earlySendAttempts) {
            sendInternal(sendAttempt.getData(), sendAttempt.getMediaType());
        }
        this.earlySendAttempts.clear();
        if (this.complete) {
            if (this.failure != null) {
                this.handler.completeWithError(this.failure);
                return;
            } else {
                this.handler.complete();
                return;
            }
        }
        this.handler.onTimeout(this.timeoutCallback);
        this.handler.onError(this.errorCallback);
        this.handler.onCompletion(this.completionCallback);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void extendResponse(ServerHttpResponse outputMessage) {
    }

    public void send(Object object) throws IOException {
        send(object, null);
    }

    public synchronized void send(Object object, @Nullable MediaType mediaType) throws IOException {
        Assert.state(!this.complete, "ResponseBodyEmitter is already set complete");
        sendInternal(object, mediaType);
    }

    private void sendInternal(Object object, @Nullable MediaType mediaType) throws IOException {
        if (this.handler != null) {
            try {
                this.handler.send(object, mediaType);
                return;
            } catch (IOException ex) {
                this.sendFailed = true;
                throw ex;
            } catch (Throwable ex2) {
                this.sendFailed = true;
                throw new IllegalStateException("Failed to send " + object, ex2);
            }
        }
        this.earlySendAttempts.add(new DataWithMediaType(object, mediaType));
    }

    public synchronized void complete() {
        if (this.sendFailed) {
            return;
        }
        this.complete = true;
        if (this.handler != null) {
            this.handler.complete();
        }
    }

    public synchronized void completeWithError(Throwable ex) {
        if (this.sendFailed) {
            return;
        }
        this.complete = true;
        this.failure = ex;
        if (this.handler != null) {
            this.handler.completeWithError(ex);
        }
    }

    public synchronized void onTimeout(Runnable callback) {
        this.timeoutCallback.setDelegate(callback);
    }

    public synchronized void onError(Consumer<Throwable> callback) {
        this.errorCallback.setDelegate(callback);
    }

    public synchronized void onCompletion(Runnable callback) {
        this.completionCallback.setDelegate(callback);
    }

    public String toString() {
        return "ResponseBodyEmitter@" + ObjectUtils.getIdentityHexString(this);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ResponseBodyEmitter$DataWithMediaType.class */
    public static class DataWithMediaType {
        private final Object data;
        @Nullable
        private final MediaType mediaType;

        public DataWithMediaType(Object data, @Nullable MediaType mediaType) {
            this.data = data;
            this.mediaType = mediaType;
        }

        public Object getData() {
            return this.data;
        }

        @Nullable
        public MediaType getMediaType() {
            return this.mediaType;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ResponseBodyEmitter$DefaultCallback.class */
    private class DefaultCallback implements Runnable {
        @Nullable
        private Runnable delegate;

        private DefaultCallback() {
        }

        public void setDelegate(Runnable delegate) {
            this.delegate = delegate;
        }

        @Override // java.lang.Runnable
        public void run() {
            ResponseBodyEmitter.this.complete = true;
            if (this.delegate != null) {
                this.delegate.run();
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ResponseBodyEmitter$ErrorCallback.class */
    private class ErrorCallback implements Consumer<Throwable> {
        @Nullable
        private Consumer<Throwable> delegate;

        private ErrorCallback() {
        }

        public void setDelegate(Consumer<Throwable> callback) {
            this.delegate = callback;
        }

        @Override // java.util.function.Consumer
        public void accept(Throwable t) {
            ResponseBodyEmitter.this.complete = true;
            if (this.delegate != null) {
                this.delegate.accept(t);
            }
        }
    }
}