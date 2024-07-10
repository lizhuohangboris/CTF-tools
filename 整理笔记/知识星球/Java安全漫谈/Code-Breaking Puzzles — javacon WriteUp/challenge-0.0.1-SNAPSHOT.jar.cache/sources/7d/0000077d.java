package org.apache.catalina;

import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/Valve.class */
public interface Valve {
    Valve getNext();

    void setNext(Valve valve);

    void backgroundProcess();

    void invoke(Request request, Response response) throws IOException, ServletException;

    boolean isAsyncSupported();
}