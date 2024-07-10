package org.apache.tomcat.websocket.pojo;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/pojo/PojoPathParam.class */
public class PojoPathParam {
    private final Class<?> type;
    private final String name;

    public PojoPathParam(Class<?> type, String name) {
        this.type = type;
        this.name = name;
    }

    public Class<?> getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }
}