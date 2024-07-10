package org.springframework.http.server.reactive;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.log.LogDelegateFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/AbstractListenerWriteProcessor.class */
public abstract class AbstractListenerWriteProcessor<T> implements Processor<T, Void> {
    protected static final Log rsWriteLogger = LogDelegateFactory.getHiddenLog(AbstractListenerWriteProcessor.class);
    private final AtomicReference<State> state;
    @Nullable
    private Subscription subscription;
    @Nullable
    private volatile T currentData;
    private volatile boolean subscriberCompleted;
    private final WriteResultPublisher resultPublisher;
    private final String logPrefix;

    protected abstract boolean isDataEmpty(T t);

    protected abstract boolean isWritePossible();

    protected abstract boolean write(T t) throws IOException;

    protected abstract void discardData(T t);

    public AbstractListenerWriteProcessor() {
        this("");
    }

    public AbstractListenerWriteProcessor(String logPrefix) {
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

    public final void onNext(T data) {
        if (rsWriteLogger.isTraceEnabled()) {
            rsWriteLogger.trace(getLogPrefix() + "Item to write");
        }
        this.state.get().onNext(this, data);
    }

    public final void onError(Throwable ex) {
        if (rsWriteLogger.isTraceEnabled()) {
            rsWriteLogger.trace(getLogPrefix() + "Write source error: " + ex);
        }
        this.state.get().onError(this, ex);
    }

    public final void onComplete() {
        if (rsWriteLogger.isTraceEnabled()) {
            rsWriteLogger.trace(getLogPrefix() + "No more items to write");
        }
        this.state.get().onComplete(this);
    }

    public final void onWritePossible() {
        if (rsWriteLogger.isTraceEnabled()) {
            rsWriteLogger.trace(getLogPrefix() + "onWritePossible");
        }
        this.state.get().onWritePossible(this);
    }

    public void cancel() {
        rsWriteLogger.trace(getLogPrefix() + "Cancellation");
        if (this.subscription != null) {
            this.subscription.cancel();
        }
    }

    public final void subscribe(Subscriber<? super Void> subscriber) {
        this.resultPublisher.subscribe(subscriber);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dataReceived(T data) {
        T prev = this.currentData;
        if (prev != null) {
            discardData(data);
            cancel();
            onError(new IllegalStateException("Received new data while current not processed yet."));
        }
        this.currentData = data;
    }

    @Deprecated
    protected void writingPaused() {
    }

    protected void writingComplete() {
    }

    protected void writingFailed(Throwable ex) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean changeState(State oldState, State newState) {
        boolean result = this.state.compareAndSet(oldState, newState);
        if (result && rsWriteLogger.isTraceEnabled()) {
            rsWriteLogger.trace(getLogPrefix() + oldState + " -> " + newState);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeStateToReceived(State oldState) {
        if (changeState(oldState, State.RECEIVED)) {
            writeIfPossible();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeStateToComplete(State oldState) {
        if (changeState(oldState, State.COMPLETED)) {
            discardCurrentData();
            writingComplete();
            this.resultPublisher.publishComplete();
            return;
        }
        this.state.get().onComplete(this);
    }

    private void writeIfPossible() {
        boolean result = isWritePossible();
        if (!result && rsWriteLogger.isTraceEnabled()) {
            rsWriteLogger.trace(getLogPrefix() + "isWritePossible: false");
        }
        if (result) {
            onWritePossible();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void discardCurrentData() {
        T data = this.currentData;
        this.currentData = null;
        if (data != null) {
            discardData(data);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/AbstractListenerWriteProcessor$State.class */
    public enum State {
        UNSUBSCRIBED { // from class: org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State.1
            @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State
            public <T> void onSubscribe(AbstractListenerWriteProcessor<T> processor, Subscription subscription) {
                Assert.notNull(subscription, "Subscription must not be null");
                if (processor.changeState(this, REQUESTED)) {
                    ((AbstractListenerWriteProcessor) processor).subscription = subscription;
                    subscription.request(1L);
                    return;
                }
                super.onSubscribe(processor, subscription);
            }
        },
        REQUESTED { // from class: org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State.2
            @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State
            public <T> void onNext(AbstractListenerWriteProcessor<T> processor, T data) {
                if (processor.isDataEmpty(data)) {
                    Assert.state(((AbstractListenerWriteProcessor) processor).subscription != null, "No subscription");
                    ((AbstractListenerWriteProcessor) processor).subscription.request(1L);
                    return;
                }
                processor.dataReceived(data);
                processor.changeStateToReceived(this);
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State
            public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
                processor.changeStateToComplete(this);
            }
        },
        RECEIVED { // from class: org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State.3
            /* JADX WARN: Multi-variable type inference failed */
            @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State
            public <T> void onWritePossible(AbstractListenerWriteProcessor<T> processor) {
                if (processor.changeState(this, WRITING)) {
                    Object obj = ((AbstractListenerWriteProcessor) processor).currentData;
                    Assert.state(obj != null, "No data");
                    try {
                        if (processor.write(obj)) {
                            if (processor.changeState(WRITING, REQUESTED)) {
                                ((AbstractListenerWriteProcessor) processor).currentData = null;
                                if (((AbstractListenerWriteProcessor) processor).subscriberCompleted) {
                                    processor.changeStateToComplete(REQUESTED);
                                } else {
                                    processor.writingPaused();
                                    Assert.state(((AbstractListenerWriteProcessor) processor).subscription != null, "No subscription");
                                    ((AbstractListenerWriteProcessor) processor).subscription.request(1L);
                                }
                            }
                        } else {
                            processor.changeStateToReceived(WRITING);
                        }
                    } catch (IOException ex) {
                        processor.writingFailed(ex);
                    }
                }
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State
            public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
                ((AbstractListenerWriteProcessor) processor).subscriberCompleted = true;
            }
        },
        WRITING { // from class: org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State.4
            @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State
            public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
                ((AbstractListenerWriteProcessor) processor).subscriberCompleted = true;
            }
        },
        COMPLETED { // from class: org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State.5
            @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State
            public <T> void onNext(AbstractListenerWriteProcessor<T> processor, T data) {
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State
            public <T> void onError(AbstractListenerWriteProcessor<T> processor, Throwable ex) {
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State
            public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
            }
        };

        public <T> void onSubscribe(AbstractListenerWriteProcessor<T> processor, Subscription subscription) {
            subscription.cancel();
        }

        public <T> void onNext(AbstractListenerWriteProcessor<T> processor, T data) {
            processor.discardData(data);
            processor.cancel();
            processor.onError(new IllegalStateException("Illegal onNext without demand"));
        }

        public <T> void onError(AbstractListenerWriteProcessor<T> processor, Throwable ex) {
            if (processor.changeState(this, COMPLETED)) {
                processor.discardCurrentData();
                processor.writingComplete();
                ((AbstractListenerWriteProcessor) processor).resultPublisher.publishError(ex);
                return;
            }
            ((State) ((AbstractListenerWriteProcessor) processor).state.get()).onError(processor, ex);
        }

        public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
            throw new IllegalStateException(toString());
        }

        public <T> void onWritePossible(AbstractListenerWriteProcessor<T> processor) {
        }
    }
}