package org.springframework.http.server.reactive;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.log.LogDelegateFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/AbstractListenerWriteFlushProcessor.class */
public abstract class AbstractListenerWriteFlushProcessor<T> implements Processor<Publisher<? extends T>, Void> {
    protected static final Log rsWriteFlushLogger = LogDelegateFactory.getHiddenLog(AbstractListenerWriteFlushProcessor.class);
    private final AtomicReference<State> state;
    @Nullable
    private Subscription subscription;
    private volatile boolean subscriberCompleted;
    private final WriteResultPublisher resultPublisher;
    private final String logPrefix;

    protected abstract Processor<? super T, Void> createWriteProcessor();

    protected abstract boolean isWritePossible();

    protected abstract void flush() throws IOException;

    protected abstract boolean isFlushPending();

    public /* bridge */ /* synthetic */ void onNext(Object obj) {
        onNext((Publisher) ((Publisher) obj));
    }

    public AbstractListenerWriteFlushProcessor() {
        this("");
    }

    public AbstractListenerWriteFlushProcessor(String logPrefix) {
        this.state = new AtomicReference<>(State.UNSUBSCRIBED);
        this.logPrefix = logPrefix;
        this.resultPublisher = new WriteResultPublisher(logPrefix);
    }

    public String getLogPrefix() {
        return this.logPrefix;
    }

    public final void onSubscribe(Subscription subscription) {
        this.state.get().onSubscribe(this, subscription);
    }

    public final void onNext(Publisher<? extends T> publisher) {
        if (rsWriteFlushLogger.isTraceEnabled()) {
            rsWriteFlushLogger.trace(getLogPrefix() + "Received onNext publisher");
        }
        this.state.get().onNext(this, publisher);
    }

    public final void onError(Throwable ex) {
        if (rsWriteFlushLogger.isTraceEnabled()) {
            rsWriteFlushLogger.trace(getLogPrefix() + "Received onError: " + ex);
        }
        this.state.get().onError(this, ex);
    }

    public final void onComplete() {
        if (rsWriteFlushLogger.isTraceEnabled()) {
            rsWriteFlushLogger.trace(getLogPrefix() + "Received onComplete");
        }
        this.state.get().onComplete(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void onFlushPossible() {
        this.state.get().onFlushPossible(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void cancel() {
        if (rsWriteFlushLogger.isTraceEnabled()) {
            rsWriteFlushLogger.trace(getLogPrefix() + "Received request to cancel");
        }
        if (this.subscription != null) {
            this.subscription.cancel();
        }
    }

    public final void subscribe(Subscriber<? super Void> subscriber) {
        this.resultPublisher.subscribe(subscriber);
    }

    protected void flushingFailed(Throwable t) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean changeState(State oldState, State newState) {
        boolean result = this.state.compareAndSet(oldState, newState);
        if (result && rsWriteFlushLogger.isTraceEnabled()) {
            rsWriteFlushLogger.trace(getLogPrefix() + oldState + " -> " + newState);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void flushIfPossible() {
        boolean result = isWritePossible();
        if (rsWriteFlushLogger.isTraceEnabled()) {
            rsWriteFlushLogger.trace(getLogPrefix() + "isWritePossible[" + result + "]");
        }
        if (result) {
            onFlushPossible();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/AbstractListenerWriteFlushProcessor$State.class */
    public enum State {
        UNSUBSCRIBED { // from class: org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State.1
            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void onSubscribe(AbstractListenerWriteFlushProcessor<T> processor, Subscription subscription) {
                Assert.notNull(subscription, "Subscription must not be null");
                if (processor.changeState(this, REQUESTED)) {
                    ((AbstractListenerWriteFlushProcessor) processor).subscription = subscription;
                    subscription.request(1L);
                    return;
                }
                super.onSubscribe(processor, subscription);
            }
        },
        REQUESTED { // from class: org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State.2
            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void onNext(AbstractListenerWriteFlushProcessor<T> processor, Publisher<? extends T> currentPublisher) {
                if (processor.changeState(this, RECEIVED)) {
                    Processor<? super T, Void> currentProcessor = processor.createWriteProcessor();
                    currentPublisher.subscribe(currentProcessor);
                    currentProcessor.subscribe(new WriteResultSubscriber(processor));
                }
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {
                if (processor.changeState(this, COMPLETED)) {
                    ((AbstractListenerWriteFlushProcessor) processor).resultPublisher.publishComplete();
                } else {
                    ((State) ((AbstractListenerWriteFlushProcessor) processor).state.get()).onComplete(processor);
                }
            }
        },
        RECEIVED { // from class: org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State.3
            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void writeComplete(AbstractListenerWriteFlushProcessor<T> processor) {
                try {
                    processor.flush();
                    if (processor.changeState(this, REQUESTED)) {
                        if (!((AbstractListenerWriteFlushProcessor) processor).subscriberCompleted) {
                            Assert.state(((AbstractListenerWriteFlushProcessor) processor).subscription != null, "No subscription");
                            ((AbstractListenerWriteFlushProcessor) processor).subscription.request(1L);
                        } else if (processor.isFlushPending()) {
                            processor.changeState(REQUESTED, FLUSHING);
                            processor.flushIfPossible();
                        } else if (processor.changeState(REQUESTED, COMPLETED)) {
                            ((AbstractListenerWriteFlushProcessor) processor).resultPublisher.publishComplete();
                        } else {
                            ((State) ((AbstractListenerWriteFlushProcessor) processor).state.get()).onComplete(processor);
                        }
                    }
                } catch (Throwable ex) {
                    processor.flushingFailed(ex);
                }
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {
                ((AbstractListenerWriteFlushProcessor) processor).subscriberCompleted = true;
            }
        },
        FLUSHING { // from class: org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State.4
            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void onFlushPossible(AbstractListenerWriteFlushProcessor<T> processor) {
                try {
                    processor.flush();
                    if (processor.changeState(this, COMPLETED)) {
                        ((AbstractListenerWriteFlushProcessor) processor).resultPublisher.publishComplete();
                    } else {
                        ((State) ((AbstractListenerWriteFlushProcessor) processor).state.get()).onComplete(processor);
                    }
                } catch (Throwable ex) {
                    processor.flushingFailed(ex);
                }
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void onNext(AbstractListenerWriteFlushProcessor<T> proc, Publisher<? extends T> pub) {
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {
            }
        },
        COMPLETED { // from class: org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State.5
            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void onNext(AbstractListenerWriteFlushProcessor<T> proc, Publisher<? extends T> pub) {
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void onError(AbstractListenerWriteFlushProcessor<T> processor, Throwable t) {
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {
            }
        };

        public <T> void onSubscribe(AbstractListenerWriteFlushProcessor<T> proc, Subscription subscription) {
            subscription.cancel();
        }

        public <T> void onNext(AbstractListenerWriteFlushProcessor<T> proc, Publisher<? extends T> pub) {
            throw new IllegalStateException(toString());
        }

        public <T> void onError(AbstractListenerWriteFlushProcessor<T> processor, Throwable ex) {
            if (processor.changeState(this, COMPLETED)) {
                ((AbstractListenerWriteFlushProcessor) processor).resultPublisher.publishError(ex);
            } else {
                ((State) ((AbstractListenerWriteFlushProcessor) processor).state.get()).onError(processor, ex);
            }
        }

        public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {
            throw new IllegalStateException(toString());
        }

        public <T> void writeComplete(AbstractListenerWriteFlushProcessor<T> processor) {
            throw new IllegalStateException(toString());
        }

        public <T> void onFlushPossible(AbstractListenerWriteFlushProcessor<T> processor) {
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/AbstractListenerWriteFlushProcessor$State$WriteResultSubscriber.class */
        private static class WriteResultSubscriber implements Subscriber<Void> {
            private final AbstractListenerWriteFlushProcessor<?> processor;

            public WriteResultSubscriber(AbstractListenerWriteFlushProcessor<?> processor) {
                this.processor = processor;
            }

            public void onSubscribe(Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            public void onNext(Void aVoid) {
            }

            public void onError(Throwable ex) {
                this.processor.cancel();
                this.processor.onError(ex);
            }

            public void onComplete() {
                if (AbstractListenerWriteFlushProcessor.rsWriteFlushLogger.isTraceEnabled()) {
                    AbstractListenerWriteFlushProcessor.rsWriteFlushLogger.trace(this.processor.getLogPrefix() + ((AbstractListenerWriteFlushProcessor) this.processor).state + " writeComplete");
                }
                ((State) ((AbstractListenerWriteFlushProcessor) this.processor).state.get()).writeComplete(this.processor);
            }
        }
    }
}