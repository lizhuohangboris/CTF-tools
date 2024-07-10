package org.apache.coyote;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.DispatchType;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/AbstractProcessorLight.class */
public abstract class AbstractProcessorLight implements Processor {
    private Set<DispatchType> dispatches = new CopyOnWriteArraySet();

    protected abstract AbstractEndpoint.Handler.SocketState service(SocketWrapperBase<?> socketWrapperBase) throws IOException;

    protected abstract AbstractEndpoint.Handler.SocketState dispatch(SocketEvent socketEvent);

    protected abstract AbstractEndpoint.Handler.SocketState asyncPostProcess();

    public abstract Log getLog();

    @Override // org.apache.coyote.Processor
    public AbstractEndpoint.Handler.SocketState process(SocketWrapperBase<?> socketWrapper, SocketEvent status) throws IOException {
        AbstractEndpoint.Handler.SocketState state = AbstractEndpoint.Handler.SocketState.CLOSED;
        Iterator<DispatchType> dispatches = null;
        while (true) {
            if (dispatches != null) {
                DispatchType nextDispatch = dispatches.next();
                state = dispatch(nextDispatch.getSocketStatus());
            } else if (status != SocketEvent.DISCONNECT) {
                if (isAsync() || isUpgrade() || state == AbstractEndpoint.Handler.SocketState.ASYNC_END) {
                    state = dispatch(status);
                    if (state == AbstractEndpoint.Handler.SocketState.OPEN) {
                        state = service(socketWrapper);
                    }
                } else if (status == SocketEvent.OPEN_WRITE) {
                    state = AbstractEndpoint.Handler.SocketState.LONG;
                } else if (status == SocketEvent.OPEN_READ) {
                    state = service(socketWrapper);
                } else {
                    state = AbstractEndpoint.Handler.SocketState.CLOSED;
                }
            }
            if (getLog().isDebugEnabled()) {
                getLog().debug("Socket: [" + socketWrapper + "], Status in: [" + status + "], State out: [" + state + "]");
            }
            if (state != AbstractEndpoint.Handler.SocketState.CLOSED && isAsync()) {
                state = asyncPostProcess();
                if (getLog().isDebugEnabled()) {
                    getLog().debug("Socket: [" + socketWrapper + "], State after async post processing: [" + state + "]");
                }
            }
            if (dispatches == null || !dispatches.hasNext()) {
                dispatches = getIteratorAndClearDispatches();
            }
            if (state == AbstractEndpoint.Handler.SocketState.ASYNC_END || (dispatches != null && state != AbstractEndpoint.Handler.SocketState.CLOSED)) {
            }
        }
        return state;
    }

    public void addDispatch(DispatchType dispatchType) {
        synchronized (this.dispatches) {
            this.dispatches.add(dispatchType);
        }
    }

    public Iterator<DispatchType> getIteratorAndClearDispatches() {
        Iterator<DispatchType> result;
        synchronized (this.dispatches) {
            result = this.dispatches.iterator();
            if (result.hasNext()) {
                this.dispatches.clear();
            } else {
                result = null;
            }
        }
        return result;
    }

    public void clearDispatches() {
        synchronized (this.dispatches) {
            this.dispatches.clear();
        }
    }
}