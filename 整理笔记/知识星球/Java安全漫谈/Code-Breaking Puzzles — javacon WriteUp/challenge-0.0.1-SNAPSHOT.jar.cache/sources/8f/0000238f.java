package org.springframework.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/concurrent/ListenableFutureTask.class */
public class ListenableFutureTask<T> extends FutureTask<T> implements ListenableFuture<T> {
    private final ListenableFutureCallbackRegistry<T> callbacks;

    public ListenableFutureTask(Callable<T> callable) {
        super(callable);
        this.callbacks = new ListenableFutureCallbackRegistry<>();
    }

    public ListenableFutureTask(Runnable runnable, @Nullable T result) {
        super(runnable, result);
        this.callbacks = new ListenableFutureCallbackRegistry<>();
    }

    @Override // org.springframework.util.concurrent.ListenableFuture
    public void addCallback(ListenableFutureCallback<? super T> callback) {
        this.callbacks.addCallback(callback);
    }

    @Override // org.springframework.util.concurrent.ListenableFuture
    public void addCallback(SuccessCallback<? super T> successCallback, FailureCallback failureCallback) {
        this.callbacks.addSuccessCallback(successCallback);
        this.callbacks.addFailureCallback(failureCallback);
    }

    @Override // org.springframework.util.concurrent.ListenableFuture
    public CompletableFuture<T> completable() {
        CompletableFuture<T> completable = new DelegatingCompletableFuture<>(this);
        ListenableFutureCallbackRegistry<T> listenableFutureCallbackRegistry = this.callbacks;
        completable.getClass();
        listenableFutureCallbackRegistry.addSuccessCallback(this::complete);
        ListenableFutureCallbackRegistry<T> listenableFutureCallbackRegistry2 = this.callbacks;
        completable.getClass();
        listenableFutureCallbackRegistry2.addFailureCallback(this::completeExceptionally);
        return completable;
    }

    @Override // java.util.concurrent.FutureTask
    public void done() {
        Throwable cause;
        try {
            T result = get();
            this.callbacks.success(result);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException ex) {
            cause = ex.getCause();
            if (cause == null) {
                cause = ex;
            }
            this.callbacks.failure(cause);
        } catch (Throwable ex2) {
            cause = ex2;
            this.callbacks.failure(cause);
        }
    }
}