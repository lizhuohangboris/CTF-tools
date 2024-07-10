package javax.el;

import java.util.EventObject;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/ELContextEvent.class */
public class ELContextEvent extends EventObject {
    private static final long serialVersionUID = 1255131906285426769L;

    public ELContextEvent(ELContext source) {
        super(source);
    }

    public ELContext getELContext() {
        return (ELContext) getSource();
    }
}