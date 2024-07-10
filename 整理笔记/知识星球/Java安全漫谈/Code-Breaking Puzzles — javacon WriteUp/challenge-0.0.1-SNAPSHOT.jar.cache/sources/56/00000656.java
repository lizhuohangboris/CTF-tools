package javax.servlet;

import java.io.IOException;
import java.util.EventListener;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/WriteListener.class */
public interface WriteListener extends EventListener {
    void onWritePossible() throws IOException;

    void onError(Throwable th);
}