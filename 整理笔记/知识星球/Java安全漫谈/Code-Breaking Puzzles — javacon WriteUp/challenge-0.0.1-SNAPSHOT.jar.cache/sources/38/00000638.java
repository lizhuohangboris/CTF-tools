package javax.servlet;

import java.io.IOException;
import java.util.EventListener;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/ReadListener.class */
public interface ReadListener extends EventListener {
    void onDataAvailable() throws IOException;

    void onAllDataRead() throws IOException;

    void onError(Throwable th);
}