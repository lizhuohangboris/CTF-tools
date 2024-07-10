package org.apache.catalina;

import java.util.EventObject;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ContainerEvent.class */
public final class ContainerEvent extends EventObject {
    private static final long serialVersionUID = 1;
    private final Object data;
    private final String type;

    public ContainerEvent(Container container, String type, Object data) {
        super(container);
        this.type = type;
        this.data = data;
    }

    public Object getData() {
        return this.data;
    }

    public Container getContainer() {
        return (Container) getSource();
    }

    public String getType() {
        return this.type;
    }

    @Override // java.util.EventObject
    public String toString() {
        return "ContainerEvent['" + getContainer() + "','" + getType() + "','" + getData() + "']";
    }
}