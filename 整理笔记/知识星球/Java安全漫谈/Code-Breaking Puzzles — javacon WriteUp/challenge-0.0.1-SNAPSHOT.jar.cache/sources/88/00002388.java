package org.springframework.util.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/concurrent/ListenableFuture.class */
public interface ListenableFuture<T> extends Future<T> {
    void addCallback(ListenableFutureCallback<? super T> listenableFutureCallback);

    void addCallback(SuccessCallback<? super T> successCallback, FailureCallback failureCallback);

    default CompletableFuture<T> completable() {
        CompletableFuture<T> completable = new DelegatingCompletableFuture<>(this);
        completable.getClass();
        SuccessCallback<? super T> successCallback = this::complete;
        completable.getClass();
        addCallback(successCallback, this::completeExceptionally);
        return completable;
    }
}