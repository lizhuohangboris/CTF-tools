package org.apache.catalina.core;

import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.catalina.Host;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/StandardEngineValve.class */
public final class StandardEngineValve extends ValveBase {
    public StandardEngineValve() {
        super(true);
    }

    @Override // org.apache.catalina.Valve
    public final void invoke(Request request, Response response) throws IOException, ServletException {
        Host host = request.getHost();
        if (host == null) {
            return;
        }
        if (request.isAsyncSupported()) {
            request.setAsyncSupported(host.getPipeline().isAsyncSupported());
        }
        host.getPipeline().getFirst().invoke(request, response);
    }
}