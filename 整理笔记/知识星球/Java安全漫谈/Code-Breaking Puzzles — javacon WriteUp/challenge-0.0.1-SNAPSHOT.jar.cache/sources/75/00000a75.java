package org.apache.coyote.http2;

import org.apache.tomcat.util.net.SocketEvent;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http2/StreamRunnable.class */
public class StreamRunnable implements Runnable {
    private final StreamProcessor processor;
    private final SocketEvent event;

    public StreamRunnable(StreamProcessor processor, SocketEvent event) {
        this.processor = processor;
        this.event = event;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.processor.process(this.event);
    }
}