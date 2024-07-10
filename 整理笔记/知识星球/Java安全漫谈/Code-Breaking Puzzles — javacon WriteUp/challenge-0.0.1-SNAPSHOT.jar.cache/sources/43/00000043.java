package ch.qos.logback.classic.net;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.net.AbstractSSLSocketAppender;
import ch.qos.logback.core.spi.PreSerializationTransformer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/net/SSLSocketAppender.class */
public class SSLSocketAppender extends AbstractSSLSocketAppender<ILoggingEvent> {
    private final PreSerializationTransformer<ILoggingEvent> pst = new LoggingEventPreSerializationTransformer();
    private boolean includeCallerData;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.net.AbstractSocketAppender
    public void postProcessEvent(ILoggingEvent event) {
        if (this.includeCallerData) {
            event.getCallerData();
        }
    }

    public void setIncludeCallerData(boolean includeCallerData) {
        this.includeCallerData = includeCallerData;
    }

    @Override // ch.qos.logback.core.net.AbstractSocketAppender
    public PreSerializationTransformer<ILoggingEvent> getPST() {
        return this.pst;
    }
}