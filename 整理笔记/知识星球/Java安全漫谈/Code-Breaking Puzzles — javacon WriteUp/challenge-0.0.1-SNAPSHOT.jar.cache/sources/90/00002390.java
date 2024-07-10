package org.springframework.util.concurrent;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/concurrent/MonoToListenableFutureAdapter.class */
public class MonoToListenableFutureAdapter<T> implements ListenableFuture<T> {
    private final MonoProcessor<T> processor;
    private final ListenableFutureCallbackRegistry<T> registry = new ListenableFutureCallbackRegistry<>();

    public MonoToListenableFutureAdapter(Mono<T> mono) {
        Assert.notNull(mono, "Mono must not be null");
        ListenableFutureCallbackRegistry<T> listenableFutureCallbackRegistry = this.registry;
        listenableFutureCallbackRegistry.getClass();
        Mono doOnSuccess = mono.doOnSuccess(this::success);
        ListenableFutureCallbackRegistry<T> listenableFutureCallbackRegistry2 = this.registry;
        listenableFutureCallbackRegistry2.getClass();
        this.processor = doOnSuccess.doOnError(this::failure).toProcessor();
    }

    @Override // java.util.concurrent.Future
    @Nullable
    public T get() {
        return (T) this.processor.block();
    }

    @Override // java.util.concurrent.Future
    @Nullable
    public T get(long timeout, TimeUnit unit) {
        Assert.notNull(unit, "TimeUnit must not be null");
        Duration duration = Duration.ofMillis(TimeUnit.MILLISECONDS.convert(timeout, unit));
        return (T) this.processor.block(duration);
    }

    @Override // java.util.concurrent.Future
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (isCancelled()) {
            return false;
        }
        this.processor.cancel();
        return this.processor.isCancelled();
    }

    @Override // java.util.concurrent.Future
    public boolean isCancelled() {
        return this.processor.isCancelled();
    }

    @Override // java.util.concurrent.Future
    public boolean isDone() {
        return this.processor.isTerminated();
    }

    @Override // org.springframework.util.concurrent.ListenableFuture
    public void addCallback(ListenableFutureCallback<? super T> callback) {
        this.registry.addCallback(callback);
    }

    @Override // org.springframework.util.concurrent.ListenableFuture
    public void addCallback(SuccessCallback<? super T> success, FailureCallback failure) {
        this.registry.addSuccessCallback(success);
        this.registry.addFailureCallback(failure);
    }
}