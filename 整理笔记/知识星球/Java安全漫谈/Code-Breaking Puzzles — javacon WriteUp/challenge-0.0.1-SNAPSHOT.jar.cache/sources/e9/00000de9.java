package org.apache.tomcat.websocket;

import javax.websocket.Extension;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/WsExtensionParameter.class */
public class WsExtensionParameter implements Extension.Parameter {
    private final String name;
    private final String value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WsExtensionParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override // javax.websocket.Extension.Parameter
    public String getName() {
        return this.name;
    }

    @Override // javax.websocket.Extension.Parameter
    public String getValue() {
        return this.value;
    }
}