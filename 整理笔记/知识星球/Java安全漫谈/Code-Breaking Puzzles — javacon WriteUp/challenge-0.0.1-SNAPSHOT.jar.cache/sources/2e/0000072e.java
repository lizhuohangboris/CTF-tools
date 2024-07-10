package javax.websocket;

import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/Extension.class */
public interface Extension {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/Extension$Parameter.class */
    public interface Parameter {
        String getName();

        String getValue();
    }

    String getName();

    List<Parameter> getParameters();
}