package org.apache.coyote.http2;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http2/Http2Exception.class */
abstract class Http2Exception extends Exception {
    private static final long serialVersionUID = 1;
    private final Http2Error error;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Http2Exception(String msg, Http2Error error) {
        super(msg);
        this.error = error;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Http2Exception(String msg, Http2Error error, Throwable cause) {
        super(msg, cause);
        this.error = error;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Http2Error getError() {
        return this.error;
    }
}