package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.ResolvableType;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ReactiveTypeHandler.class */
class ReactiveTypeHandler {
    private static final long STREAMING_TIMEOUT_VALUE = -1;
    private static Log logger = LogFactory.getLog(ReactiveTypeHandler.class);
    private final ReactiveAdapterRegistry reactiveRegistry;
    private final TaskExecutor taskExecutor;
    private Boolean taskExecutorWarning;
    private final ContentNegotiationManager contentNegotiationManager;

    public ReactiveTypeHandler() {
        this(ReactiveAdapterRegistry.getSharedInstance(), new SyncTaskExecutor(), new ContentNegotiationManager());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReactiveTypeHandler(ReactiveAdapterRegistry registry, TaskExecutor executor, ContentNegotiationManager manager) {
        Assert.notNull(registry, "ReactiveAdapterRegistry is required");
        Assert.notNull(executor, "TaskExecutor is required");
        Assert.notNull(manager, "ContentNegotiationManager is required");
        this.reactiveRegistry = registry;
        this.taskExecutor = executor;
        this.taskExecutorWarning = Boolean.valueOf((executor instanceof SimpleAsyncTaskExecutor) || (executor instanceof SyncTaskExecutor));
        this.contentNegotiationManager = manager;
    }

    public boolean isReactiveType(Class<?> type) {
        return this.reactiveRegistry.hasAdapters() && this.reactiveRegistry.getAdapter(type) != null;
    }

    @Nullable
    public ResponseBodyEmitter handleValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mav, NativeWebRequest request) throws Exception {
        Assert.notNull(returnValue, "Expected return value");
        ReactiveAdapter adapter = this.reactiveRegistry.getAdapter(returnValue.getClass());
        Assert.state(adapter != null, () -> {
            return "Unexpected return value: " + returnValue;
        });
        ResolvableType elementType = ResolvableType.forMethodParameter(returnType).getGeneric(new int[0]);
        Class<?> elementClass = elementType.toClass();
        Collection<MediaType> mediaTypes = getMediaTypes(request);
        Optional<MediaType> mediaType = mediaTypes.stream().filter((v0) -> {
            return v0.isConcrete();
        }).findFirst();
        if (adapter.isMultiValue()) {
            Stream<MediaType> stream = mediaTypes.stream();
            MediaType mediaType2 = MediaType.TEXT_EVENT_STREAM;
            mediaType2.getClass();
            if (stream.anyMatch(this::includes) || ServerSentEvent.class.isAssignableFrom(elementClass)) {
                logExecutorWarning(returnType);
                SseEmitter emitter = new SseEmitter(-1L);
                new SseEmitterSubscriber(emitter, this.taskExecutor).connect(adapter, returnValue);
                return emitter;
            } else if (CharSequence.class.isAssignableFrom(elementClass)) {
                logExecutorWarning(returnType);
                ResponseBodyEmitter emitter2 = getEmitter(mediaType.orElse(MediaType.TEXT_PLAIN));
                new TextEmitterSubscriber(emitter2, this.taskExecutor).connect(adapter, returnValue);
                return emitter2;
            } else {
                Stream<MediaType> stream2 = mediaTypes.stream();
                MediaType mediaType3 = MediaType.APPLICATION_STREAM_JSON;
                mediaType3.getClass();
                if (stream2.anyMatch(this::includes)) {
                    logExecutorWarning(returnType);
                    ResponseBodyEmitter emitter3 = getEmitter(MediaType.APPLICATION_STREAM_JSON);
                    new JsonEmitterSubscriber(emitter3, this.taskExecutor).connect(adapter, returnValue);
                    return emitter3;
                }
            }
        }
        DeferredResult<Object> result = new DeferredResult<>();
        new DeferredResultSubscriber(result, adapter, elementType).connect(adapter, returnValue);
        WebAsyncUtils.getAsyncManager(request).startDeferredResultProcessing(result, mav);
        return null;
    }

    private Collection<MediaType> getMediaTypes(NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
        Collection<MediaType> mediaTypes = (Collection) request.getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, 0);
        return CollectionUtils.isEmpty(mediaTypes) ? this.contentNegotiationManager.resolveMediaTypes(request) : mediaTypes;
    }

    private ResponseBodyEmitter getEmitter(final MediaType mediaType) {
        return new ResponseBodyEmitter(-1L) { // from class: org.springframework.web.servlet.mvc.method.annotation.ReactiveTypeHandler.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter
            public void extendResponse(ServerHttpResponse outputMessage) {
                outputMessage.getHeaders().setContentType(mediaType);
            }
        };
    }

    private void logExecutorWarning(MethodParameter returnType) {
        if (this.taskExecutorWarning.booleanValue() && logger.isWarnEnabled()) {
            synchronized (this) {
                if (this.taskExecutorWarning.booleanValue()) {
                    String executorTypeName = this.taskExecutor.getClass().getSimpleName();
                    logger.warn("\n!!!\nStreaming through a reactive type requires an Executor to write to the response.\nPlease, configure a TaskExecutor in the MVC config under \"async support\".\nThe " + executorTypeName + " currently in use is not suitable under load.\n-------------------------------\nController:\t" + returnType.getContainingClass().getName() + "\nMethod:\t\t" + returnType.getMethod().getName() + "\nReturning:\t" + ResolvableType.forMethodParameter(returnType).toString() + "\n!!!");
                    this.taskExecutorWarning = false;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ReactiveTypeHandler$AbstractEmitterSubscriber.class */
    public static abstract class AbstractEmitterSubscriber implements Subscriber<Object>, Runnable {
        private final ResponseBodyEmitter emitter;
        private final TaskExecutor taskExecutor;
        @Nullable
        private Subscription subscription;
        @Nullable
        private Throwable error;
        private volatile boolean terminated;
        private volatile boolean done;
        private final AtomicReference<Object> elementRef = new AtomicReference<>();
        private final AtomicLong executing = new AtomicLong();

        protected abstract void send(Object obj) throws IOException;

        protected AbstractEmitterSubscriber(ResponseBodyEmitter emitter, TaskExecutor executor) {
            this.emitter = emitter;
            this.taskExecutor = executor;
        }

        public void connect(ReactiveAdapter adapter, Object returnValue) {
            Publisher<Object> publisher = adapter.toPublisher(returnValue);
            publisher.subscribe(this);
        }

        protected ResponseBodyEmitter getEmitter() {
            return this.emitter;
        }

        public final void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            this.emitter.onTimeout(() -> {
                if (ReactiveTypeHandler.logger.isTraceEnabled()) {
                    ReactiveTypeHandler.logger.trace("Connection timeout for " + this.emitter);
                }
                terminate();
                this.emitter.complete();
            });
            ResponseBodyEmitter responseBodyEmitter = this.emitter;
            ResponseBodyEmitter responseBodyEmitter2 = this.emitter;
            responseBodyEmitter2.getClass();
            responseBodyEmitter.onError(this::completeWithError);
            subscription.request(1L);
        }

        public final void onNext(Object element) {
            this.elementRef.lazySet(element);
            trySchedule();
        }

        public final void onError(Throwable ex) {
            this.error = ex;
            this.terminated = true;
            trySchedule();
        }

        public final void onComplete() {
            this.terminated = true;
            trySchedule();
        }

        private void trySchedule() {
            if (this.executing.getAndIncrement() == 0) {
                schedule();
            }
        }

        private void schedule() {
            try {
                this.taskExecutor.execute(this);
            } catch (Throwable th) {
                try {
                    terminate();
                } finally {
                    this.executing.decrementAndGet();
                    this.elementRef.lazySet(null);
                }
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.done) {
                this.elementRef.lazySet(null);
                return;
            }
            boolean isTerminated = this.terminated;
            Object element = this.elementRef.get();
            if (element != null) {
                this.elementRef.lazySet(null);
                Assert.state(this.subscription != null, "No subscription");
                try {
                    send(element);
                    this.subscription.request(1L);
                } catch (Throwable ex) {
                    if (ReactiveTypeHandler.logger.isTraceEnabled()) {
                        ReactiveTypeHandler.logger.trace("Send for " + this.emitter + " failed: " + ex);
                    }
                    terminate();
                    return;
                }
            }
            if (isTerminated) {
                this.done = true;
                Throwable ex2 = this.error;
                this.error = null;
                if (ex2 != null) {
                    if (ReactiveTypeHandler.logger.isTraceEnabled()) {
                        ReactiveTypeHandler.logger.trace("Publisher for " + this.emitter + " failed: " + ex2);
                    }
                    this.emitter.completeWithError(ex2);
                    return;
                }
                if (ReactiveTypeHandler.logger.isTraceEnabled()) {
                    ReactiveTypeHandler.logger.trace("Publisher for " + this.emitter + " completed");
                }
                this.emitter.complete();
            } else if (this.executing.decrementAndGet() != 0) {
                schedule();
            }
        }

        private void terminate() {
            this.done = true;
            if (this.subscription != null) {
                this.subscription.cancel();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ReactiveTypeHandler$SseEmitterSubscriber.class */
    public static class SseEmitterSubscriber extends AbstractEmitterSubscriber {
        SseEmitterSubscriber(SseEmitter sseEmitter, TaskExecutor executor) {
            super(sseEmitter, executor);
        }

        @Override // org.springframework.web.servlet.mvc.method.annotation.ReactiveTypeHandler.AbstractEmitterSubscriber
        protected void send(Object element) throws IOException {
            if (element instanceof ServerSentEvent) {
                ServerSentEvent<?> event = (ServerSentEvent) element;
                ((SseEmitter) getEmitter()).send(adapt(event));
                return;
            }
            getEmitter().send(element, MediaType.APPLICATION_JSON);
        }

        private SseEmitter.SseEventBuilder adapt(ServerSentEvent<?> sse) {
            SseEmitter.SseEventBuilder builder = SseEmitter.event();
            String id = sse.id();
            String event = sse.event();
            Duration retry = sse.retry();
            String comment = sse.comment();
            Object data = sse.data();
            if (id != null) {
                builder.id(id);
            }
            if (event != null) {
                builder.name(event);
            }
            if (data != null) {
                builder.data(data);
            }
            if (retry != null) {
                builder.reconnectTime(retry.toMillis());
            }
            if (comment != null) {
                builder.comment(comment);
            }
            return builder;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ReactiveTypeHandler$JsonEmitterSubscriber.class */
    public static class JsonEmitterSubscriber extends AbstractEmitterSubscriber {
        JsonEmitterSubscriber(ResponseBodyEmitter emitter, TaskExecutor executor) {
            super(emitter, executor);
        }

        @Override // org.springframework.web.servlet.mvc.method.annotation.ReactiveTypeHandler.AbstractEmitterSubscriber
        protected void send(Object element) throws IOException {
            getEmitter().send(element, MediaType.APPLICATION_JSON);
            getEmitter().send("\n", MediaType.TEXT_PLAIN);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ReactiveTypeHandler$TextEmitterSubscriber.class */
    public static class TextEmitterSubscriber extends AbstractEmitterSubscriber {
        TextEmitterSubscriber(ResponseBodyEmitter emitter, TaskExecutor executor) {
            super(emitter, executor);
        }

        @Override // org.springframework.web.servlet.mvc.method.annotation.ReactiveTypeHandler.AbstractEmitterSubscriber
        protected void send(Object element) throws IOException {
            getEmitter().send(element, MediaType.TEXT_PLAIN);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ReactiveTypeHandler$DeferredResultSubscriber.class */
    public static class DeferredResultSubscriber implements Subscriber<Object> {
        private final DeferredResult<Object> result;
        private final boolean multiValueSource;
        private final CollectedValuesList values;

        DeferredResultSubscriber(DeferredResult<Object> result, ReactiveAdapter adapter, ResolvableType elementType) {
            this.result = result;
            this.multiValueSource = adapter.isMultiValue();
            this.values = new CollectedValuesList(elementType);
        }

        public void connect(ReactiveAdapter adapter, Object returnValue) {
            Publisher<Object> publisher = adapter.toPublisher(returnValue);
            publisher.subscribe(this);
        }

        public void onSubscribe(Subscription subscription) {
            DeferredResult<Object> deferredResult = this.result;
            subscription.getClass();
            deferredResult.onTimeout(this::cancel);
            subscription.request(Long.MAX_VALUE);
        }

        public void onNext(Object element) {
            this.values.add(element);
        }

        public void onError(Throwable ex) {
            this.result.setErrorResult(ex);
        }

        public void onComplete() {
            if (this.values.size() > 1 || this.multiValueSource) {
                this.result.setResult(this.values);
            } else if (this.values.size() == 1) {
                this.result.setResult(this.values.get(0));
            } else {
                this.result.setResult(null);
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ReactiveTypeHandler$CollectedValuesList.class */
    static class CollectedValuesList extends ArrayList<Object> {
        private final ResolvableType elementType;

        CollectedValuesList(ResolvableType elementType) {
            this.elementType = elementType;
        }

        public ResolvableType getReturnType() {
            return ResolvableType.forClassWithGenerics(List.class, this.elementType);
        }
    }
}