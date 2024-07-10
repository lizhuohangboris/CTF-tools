package org.apache.catalina.core;

import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.res.StringManager;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/StandardContextValve.class */
public final class StandardContextValve extends ValveBase {
    private static final StringManager sm = StringManager.getManager(StandardContextValve.class);

    public StandardContextValve() {
        super(true);
    }

    @Override // org.apache.catalina.Valve
    public final void invoke(Request request, Response response) throws IOException, ServletException {
        MessageBytes requestPathMB = request.getRequestPathMB();
        if (requestPathMB.startsWithIgnoreCase("/META-INF/", 0) || requestPathMB.equalsIgnoreCase("/META-INF") || requestPathMB.startsWithIgnoreCase("/WEB-INF/", 0) || requestPathMB.equalsIgnoreCase("/WEB-INF")) {
            response.sendError(404);
            return;
        }
        Wrapper wrapper = request.getWrapper();
        if (wrapper == null || wrapper.isUnavailable()) {
            response.sendError(404);
            return;
        }
        try {
            response.sendAcknowledgement();
            if (request.isAsyncSupported()) {
                request.setAsyncSupported(wrapper.getPipeline().isAsyncSupported());
            }
            wrapper.getPipeline().getFirst().invoke(request, response);
        } catch (IOException ioe) {
            this.container.getLogger().error(sm.getString("standardContextValve.acknowledgeException"), ioe);
            request.setAttribute("javax.servlet.error.exception", ioe);
            response.sendError(500);
        }
    }
}