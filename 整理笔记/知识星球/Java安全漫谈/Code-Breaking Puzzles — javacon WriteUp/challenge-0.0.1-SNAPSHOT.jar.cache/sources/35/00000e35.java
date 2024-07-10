package org.apache.tomcat.websocket.server;

import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.tomcat.websocket.BackgroundProcess;
import org.apache.tomcat.websocket.BackgroundProcessManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/server/WsWriteTimeout.class */
public class WsWriteTimeout implements BackgroundProcess {
    private final Set<WsRemoteEndpointImplServer> endpoints = new ConcurrentSkipListSet(new EndpointComparator());
    private final AtomicInteger count = new AtomicInteger(0);
    private int backgroundProcessCount = 0;
    private volatile int processPeriod = 1;

    @Override // org.apache.tomcat.websocket.BackgroundProcess
    public void backgroundProcess() {
        this.backgroundProcessCount++;
        if (this.backgroundProcessCount >= this.processPeriod) {
            this.backgroundProcessCount = 0;
            long now = System.currentTimeMillis();
            for (WsRemoteEndpointImplServer endpoint : this.endpoints) {
                if (endpoint.getTimeoutExpiry() < now) {
                    endpoint.onTimeout(false);
                } else {
                    return;
                }
            }
        }
    }

    @Override // org.apache.tomcat.websocket.BackgroundProcess
    public void setProcessPeriod(int period) {
        this.processPeriod = period;
    }

    @Override // org.apache.tomcat.websocket.BackgroundProcess
    public int getProcessPeriod() {
        return this.processPeriod;
    }

    public void register(WsRemoteEndpointImplServer endpoint) {
        boolean result = this.endpoints.add(endpoint);
        if (result) {
            int newCount = this.count.incrementAndGet();
            if (newCount == 1) {
                BackgroundProcessManager.getInstance().register(this);
            }
        }
    }

    public void unregister(WsRemoteEndpointImplServer endpoint) {
        boolean result = this.endpoints.remove(endpoint);
        if (result) {
            int newCount = this.count.decrementAndGet();
            if (newCount == 0) {
                BackgroundProcessManager.getInstance().unregister(this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/server/WsWriteTimeout$EndpointComparator.class */
    public static class EndpointComparator implements Comparator<WsRemoteEndpointImplServer> {
        private EndpointComparator() {
        }

        @Override // java.util.Comparator
        public int compare(WsRemoteEndpointImplServer o1, WsRemoteEndpointImplServer o2) {
            long t1 = o1.getTimeoutExpiry();
            long t2 = o2.getTimeoutExpiry();
            if (t1 < t2) {
                return -1;
            }
            if (t1 == t2) {
                return 0;
            }
            return 1;
        }
    }
}