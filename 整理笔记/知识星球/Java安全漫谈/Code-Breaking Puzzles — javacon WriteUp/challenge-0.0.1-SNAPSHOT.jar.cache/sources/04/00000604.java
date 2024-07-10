package javax.el;

import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/ValueReference.class */
public class ValueReference implements Serializable {
    private static final long serialVersionUID = 1;
    private final Object base;
    private final Object property;

    public ValueReference(Object base, Object property) {
        this.base = base;
        this.property = property;
    }

    public Object getBase() {
        return this.base;
    }

    public Object getProperty() {
        return this.property;
    }
}