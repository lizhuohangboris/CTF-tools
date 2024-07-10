package org.apache.coyote.http2;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http2/ConnectionSettingsRemote.class */
class ConnectionSettingsRemote extends ConnectionSettingsBase<ConnectionException> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public ConnectionSettingsRemote(String connectionId) {
        super(connectionId);
    }

    @Override // org.apache.coyote.http2.ConnectionSettingsBase
    final void throwException(String msg, Http2Error error) throws ConnectionException {
        throw new ConnectionException(msg, error);
    }
}