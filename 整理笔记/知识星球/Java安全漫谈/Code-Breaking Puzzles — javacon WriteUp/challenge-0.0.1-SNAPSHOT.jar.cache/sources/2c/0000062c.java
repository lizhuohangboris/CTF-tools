package javax.servlet;

import java.io.IOException;
import java.util.EventListener;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/AsyncListener.class */
public interface AsyncListener extends EventListener {
    void onComplete(AsyncEvent asyncEvent) throws IOException;

    void onTimeout(AsyncEvent asyncEvent) throws IOException;

    void onError(AsyncEvent asyncEvent) throws IOException;

    void onStartAsync(AsyncEvent asyncEvent) throws IOException;
}