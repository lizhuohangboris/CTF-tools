package org.apache.coyote;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.PrivilegedGetTccl;
import org.apache.tomcat.util.security.PrivilegedSetTccl;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/AsyncStateMachine.class */
public class AsyncStateMachine {
    private static final StringManager sm = StringManager.getManager(AsyncStateMachine.class);
    private volatile AsyncState state = AsyncState.DISPATCHED;
    private volatile long lastAsyncStart = 0;
    private final AtomicLong generation = new AtomicLong(0);
    private AsyncContextCallback asyncCtxt = null;
    private final AbstractProcessor processor;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/AsyncStateMachine$AsyncState.class */
    public enum AsyncState {
        DISPATCHED(false, false, false, false),
        STARTING(true, true, false, false),
        STARTED(true, true, false, false),
        MUST_COMPLETE(true, true, true, false),
        COMPLETE_PENDING(true, true, false, false),
        COMPLETING(true, false, true, false),
        TIMING_OUT(true, true, false, false),
        MUST_DISPATCH(true, true, false, true),
        DISPATCH_PENDING(true, true, false, false),
        DISPATCHING(true, false, false, true),
        READ_WRITE_OP(true, true, false, false),
        MUST_ERROR(true, true, false, false),
        ERROR(true, true, false, false);
        
        private final boolean isAsync;
        private final boolean isStarted;
        private final boolean isCompleting;
        private final boolean isDispatching;

        AsyncState(boolean isAsync, boolean isStarted, boolean isCompleting, boolean isDispatching) {
            this.isAsync = isAsync;
            this.isStarted = isStarted;
            this.isCompleting = isCompleting;
            this.isDispatching = isDispatching;
        }

        boolean isAsync() {
            return this.isAsync;
        }

        boolean isStarted() {
            return this.isStarted;
        }

        boolean isDispatching() {
            return this.isDispatching;
        }

        boolean isCompleting() {
            return this.isCompleting;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AsyncStateMachine(AbstractProcessor processor) {
        this.processor = processor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAsync() {
        return this.state.isAsync();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAsyncDispatching() {
        return this.state.isDispatching();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAsyncStarted() {
        return this.state.isStarted();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAsyncTimingOut() {
        return this.state == AsyncState.TIMING_OUT;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAsyncError() {
        return this.state == AsyncState.ERROR;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isCompleting() {
        return this.state.isCompleting();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getLastAsyncStart() {
        return this.lastAsyncStart;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getCurrentGeneration() {
        return this.generation.get();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void asyncStart(AsyncContextCallback asyncCtxt) {
        if (this.state == AsyncState.DISPATCHED) {
            this.generation.incrementAndGet();
            this.state = AsyncState.STARTING;
            this.asyncCtxt = asyncCtxt;
            this.lastAsyncStart = System.currentTimeMillis();
            return;
        }
        throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", "asyncStart()", this.state));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void asyncOperation() {
        if (this.state == AsyncState.STARTED) {
            this.state = AsyncState.READ_WRITE_OP;
            return;
        }
        throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", "asyncOperation()", this.state));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized AbstractEndpoint.Handler.SocketState asyncPostProcess() {
        if (this.state == AsyncState.COMPLETE_PENDING) {
            doComplete();
            return AbstractEndpoint.Handler.SocketState.ASYNC_END;
        } else if (this.state == AsyncState.DISPATCH_PENDING) {
            doDispatch();
            return AbstractEndpoint.Handler.SocketState.ASYNC_END;
        } else if (this.state == AsyncState.STARTING || this.state == AsyncState.READ_WRITE_OP) {
            this.state = AsyncState.STARTED;
            return AbstractEndpoint.Handler.SocketState.LONG;
        } else if (this.state == AsyncState.MUST_COMPLETE || this.state == AsyncState.COMPLETING) {
            this.asyncCtxt.fireOnComplete();
            this.state = AsyncState.DISPATCHED;
            return AbstractEndpoint.Handler.SocketState.ASYNC_END;
        } else if (this.state == AsyncState.MUST_DISPATCH) {
            this.state = AsyncState.DISPATCHING;
            return AbstractEndpoint.Handler.SocketState.ASYNC_END;
        } else if (this.state == AsyncState.DISPATCHING) {
            this.state = AsyncState.DISPATCHED;
            return AbstractEndpoint.Handler.SocketState.ASYNC_END;
        } else if (this.state == AsyncState.STARTED) {
            return AbstractEndpoint.Handler.SocketState.LONG;
        } else {
            throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", "asyncPostProcess()", this.state));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean asyncComplete() {
        if (!ContainerThreadMarker.isContainerThread() && this.state == AsyncState.STARTING) {
            this.state = AsyncState.COMPLETE_PENDING;
            return false;
        }
        return doComplete();
    }

    private synchronized boolean doComplete() {
        clearNonBlockingListeners();
        boolean doComplete = false;
        if (this.state == AsyncState.STARTING || this.state == AsyncState.TIMING_OUT || this.state == AsyncState.ERROR || this.state == AsyncState.READ_WRITE_OP) {
            this.state = AsyncState.MUST_COMPLETE;
        } else if (this.state == AsyncState.STARTED || this.state == AsyncState.COMPLETE_PENDING) {
            this.state = AsyncState.COMPLETING;
            doComplete = true;
        } else {
            throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", "asyncComplete()", this.state));
        }
        return doComplete;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean asyncTimeout() {
        if (this.state == AsyncState.STARTED) {
            this.state = AsyncState.TIMING_OUT;
            return true;
        } else if (this.state == AsyncState.COMPLETING || this.state == AsyncState.DISPATCHING || this.state == AsyncState.DISPATCHED) {
            return false;
        } else {
            throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", "asyncTimeout()", this.state));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean asyncDispatch() {
        if (!ContainerThreadMarker.isContainerThread() && this.state == AsyncState.STARTING) {
            this.state = AsyncState.DISPATCH_PENDING;
            return false;
        }
        return doDispatch();
    }

    private synchronized boolean doDispatch() {
        clearNonBlockingListeners();
        boolean doDispatch = false;
        if (this.state == AsyncState.STARTING || this.state == AsyncState.TIMING_OUT || this.state == AsyncState.ERROR) {
            this.state = AsyncState.MUST_DISPATCH;
        } else if (this.state == AsyncState.STARTED || this.state == AsyncState.DISPATCH_PENDING) {
            this.state = AsyncState.DISPATCHING;
            doDispatch = true;
        } else if (this.state == AsyncState.READ_WRITE_OP) {
            this.state = AsyncState.DISPATCHING;
            if (!ContainerThreadMarker.isContainerThread()) {
                doDispatch = true;
            }
        } else {
            throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", "asyncDispatch()", this.state));
        }
        return doDispatch;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void asyncDispatched() {
        if (this.state == AsyncState.DISPATCHING || this.state == AsyncState.MUST_DISPATCH) {
            this.state = AsyncState.DISPATCHED;
            return;
        }
        throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", "asyncDispatched()", this.state));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void asyncMustError() {
        if (this.state == AsyncState.STARTED) {
            clearNonBlockingListeners();
            this.state = AsyncState.MUST_ERROR;
            return;
        }
        throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", "asyncMustError()", this.state));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void asyncError() {
        if (this.state == AsyncState.STARTING || this.state == AsyncState.STARTED || this.state == AsyncState.DISPATCHED || this.state == AsyncState.TIMING_OUT || this.state == AsyncState.MUST_COMPLETE || this.state == AsyncState.READ_WRITE_OP || this.state == AsyncState.COMPLETING || this.state == AsyncState.MUST_ERROR) {
            clearNonBlockingListeners();
            this.state = AsyncState.ERROR;
            return;
        }
        throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", "asyncError()", this.state));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void asyncRun(Runnable runnable) {
        ClassLoader oldCL;
        if (this.state == AsyncState.STARTING || this.state == AsyncState.STARTED || this.state == AsyncState.READ_WRITE_OP) {
            if (Constants.IS_SECURITY_ENABLED) {
                PrivilegedAction<ClassLoader> pa = new PrivilegedGetTccl();
                oldCL = (ClassLoader) AccessController.doPrivileged(pa);
            } else {
                oldCL = Thread.currentThread().getContextClassLoader();
            }
            try {
                if (Constants.IS_SECURITY_ENABLED) {
                    PrivilegedAction<Void> pa2 = new PrivilegedSetTccl(getClass().getClassLoader());
                    AccessController.doPrivileged(pa2);
                } else {
                    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                }
                this.processor.execute(runnable);
                if (Constants.IS_SECURITY_ENABLED) {
                    PrivilegedAction<Void> pa3 = new PrivilegedSetTccl(oldCL);
                    AccessController.doPrivileged(pa3);
                    return;
                }
                Thread.currentThread().setContextClassLoader(oldCL);
                return;
            } catch (Throwable th) {
                if (Constants.IS_SECURITY_ENABLED) {
                    PrivilegedAction<Void> pa4 = new PrivilegedSetTccl(oldCL);
                    AccessController.doPrivileged(pa4);
                } else {
                    Thread.currentThread().setContextClassLoader(oldCL);
                }
                throw th;
            }
        }
        throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", "asyncRun()", this.state));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void recycle() {
        if (this.lastAsyncStart == 0) {
            return;
        }
        notifyAll();
        this.asyncCtxt = null;
        this.state = AsyncState.DISPATCHED;
        this.lastAsyncStart = 0L;
    }

    private void clearNonBlockingListeners() {
        this.processor.getRequest().listener = null;
        this.processor.getRequest().getResponse().listener = null;
    }
}