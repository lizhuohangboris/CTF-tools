package org.springframework.http.server.reactive;

import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.log.LogDelegateFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Operators;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/WriteResultPublisher.class */
public class WriteResultPublisher implements Publisher<Void> {
    private static final Log rsWriteResultLogger = LogDelegateFactory.getHiddenLog(WriteResultPublisher.class);
    private final AtomicReference<State> state = new AtomicReference<>(State.UNSUBSCRIBED);
    @Nullable
    private volatile Subscriber<? super Void> subscriber;
    private volatile boolean completedBeforeSubscribed;
    @Nullable
    private volatile Throwable errorBeforeSubscribed;
    private final String logPrefix;

    public WriteResultPublisher(String logPrefix) {
        this.logPrefix = logPrefix;
    }

    public final void subscribe(Subscriber<? super Void> subscriber) {
        if (rsWriteResultLogger.isTraceEnabled()) {
            rsWriteResultLogger.trace(this.logPrefix + this.state + " subscribe: " + subscriber);
        }
        this.state.get().subscribe(this, subscriber);
    }

    public void publishComplete() {
        if (rsWriteResultLogger.isTraceEnabled()) {
            rsWriteResultLogger.trace(this.logPrefix + this.state + " publishComplete");
        }
        this.state.get().publishComplete(this);
    }

    public void publishError(Throwable t) {
        if (rsWriteResultLogger.isTraceEnabled()) {
            rsWriteResultLogger.trace(this.logPrefix + this.state + " publishError: " + t);
        }
        this.state.get().publishError(this, t);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean changeState(State oldState, State newState) {
        return this.state.compareAndSet(oldState, newState);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/WriteResultPublisher$WriteResultSubscription.class */
    private static final class WriteResultSubscription implements Subscription {
        private final WriteResultPublisher publisher;

        public WriteResultSubscription(WriteResultPublisher publisher) {
            this.publisher = publisher;
        }

        public final void request(long n) {
            if (WriteResultPublisher.rsWriteResultLogger.isTraceEnabled()) {
                WriteResultPublisher.rsWriteResultLogger.trace(this.publisher.logPrefix + state() + " request: " + n);
            }
            state().request(this.publisher, n);
        }

        public final void cancel() {
            if (WriteResultPublisher.rsWriteResultLogger.isTraceEnabled()) {
                WriteResultPublisher.rsWriteResultLogger.trace(this.publisher.logPrefix + state() + " cancel");
            }
            state().cancel(this.publisher);
        }

        private State state() {
            return (State) this.publisher.state.get();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/WriteResultPublisher$State.class */
    public enum State {
        UNSUBSCRIBED { // from class: org.springframework.http.server.reactive.WriteResultPublisher.State.1
            @Override // org.springframework.http.server.reactive.WriteResultPublisher.State
            void subscribe(WriteResultPublisher publisher, Subscriber<? super Void> subscriber) {
                Assert.notNull(subscriber, "Subscriber must not be null");
                if (publisher.changeState(this, SUBSCRIBING)) {
                    Subscription subscription = new WriteResultSubscription(publisher);
                    publisher.subscriber = subscriber;
                    subscriber.onSubscribe(subscription);
                    publisher.changeState(SUBSCRIBING, SUBSCRIBED);
                    if (publisher.completedBeforeSubscribed) {
                        publisher.publishComplete();
                    }
                    Throwable publisherError = publisher.errorBeforeSubscribed;
                    if (publisherError != null) {
                        publisher.publishError(publisherError);
                        return;
                    }
                    return;
                }
                throw new IllegalStateException(toString());
            }

            @Override // org.springframework.http.server.reactive.WriteResultPublisher.State
            void publishComplete(WriteResultPublisher publisher) {
                publisher.completedBeforeSubscribed = true;
            }

            @Override // org.springframework.http.server.reactive.WriteResultPublisher.State
            void publishError(WriteResultPublisher publisher, Throwable ex) {
                publisher.errorBeforeSubscribed = ex;
            }
        },
        SUBSCRIBING { // from class: org.springframework.http.server.reactive.WriteResultPublisher.State.2
            @Override // org.springframework.http.server.reactive.WriteResultPublisher.State
            void request(WriteResultPublisher publisher, long n) {
                Operators.validate(n);
            }

            @Override // org.springframework.http.server.reactive.WriteResultPublisher.State
            void publishComplete(WriteResultPublisher publisher) {
                publisher.completedBeforeSubscribed = true;
            }

            @Override // org.springframework.http.server.reactive.WriteResultPublisher.State
            void publishError(WriteResultPublisher publisher, Throwable ex) {
                publisher.errorBeforeSubscribed = ex;
            }
        },
        SUBSCRIBED { // from class: org.springframework.http.server.reactive.WriteResultPublisher.State.3
            @Override // org.springframework.http.server.reactive.WriteResultPublisher.State
            void request(WriteResultPublisher publisher, long n) {
                Operators.validate(n);
            }
        },
        COMPLETED { // from class: org.springframework.http.server.reactive.WriteResultPublisher.State.4
            @Override // org.springframework.http.server.reactive.WriteResultPublisher.State
            void request(WriteResultPublisher publisher, long n) {
            }

            @Override // org.springframework.http.server.reactive.WriteResultPublisher.State
            void cancel(WriteResultPublisher publisher) {
            }

            @Override // org.springframework.http.server.reactive.WriteResultPublisher.State
            void publishComplete(WriteResultPublisher publisher) {
            }

            @Override // org.springframework.http.server.reactive.WriteResultPublisher.State
            void publishError(WriteResultPublisher publisher, Throwable t) {
            }
        };

        void subscribe(WriteResultPublisher publisher, Subscriber<? super Void> subscriber) {
            throw new IllegalStateException(toString());
        }

        void request(WriteResultPublisher publisher, long n) {
            throw new IllegalStateException(toString());
        }

        void cancel(WriteResultPublisher publisher) {
            if (!publisher.changeState(this, COMPLETED)) {
                ((State) publisher.state.get()).cancel(publisher);
            }
        }

        void publishComplete(WriteResultPublisher publisher) {
            if (publisher.changeState(this, COMPLETED)) {
                Subscriber<? super Void> s = publisher.subscriber;
                Assert.state(s != null, "No subscriber");
                s.onComplete();
                return;
            }
            ((State) publisher.state.get()).publishComplete(publisher);
        }

        void publishError(WriteResultPublisher publisher, Throwable t) {
            if (publisher.changeState(this, COMPLETED)) {
                Subscriber<? super Void> s = publisher.subscriber;
                Assert.state(s != null, "No subscriber");
                s.onError(t);
                return;
            }
            ((State) publisher.state.get()).publishError(publisher, t);
        }
    }
}