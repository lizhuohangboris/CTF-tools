package javax.el;

import java.util.EventListener;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/ELContextListener.class */
public interface ELContextListener extends EventListener {
    void contextCreated(ELContextEvent eLContextEvent);
}