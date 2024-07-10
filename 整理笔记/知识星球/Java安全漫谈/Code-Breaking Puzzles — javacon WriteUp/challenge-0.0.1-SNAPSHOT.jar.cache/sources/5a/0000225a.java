package org.springframework.remoting.caucho;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.UsesSunHttpServer;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.support.WebContentGenerator;

@Deprecated
@UsesSunHttpServer
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/remoting/caucho/SimpleHessianServiceExporter.class */
public class SimpleHessianServiceExporter extends HessianExporter implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        if (!WebContentGenerator.METHOD_POST.equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set(HttpHeaders.ALLOW, WebContentGenerator.METHOD_POST);
            exchange.sendResponseHeaders(405, -1L);
            return;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
        try {
            invoke(exchange.getRequestBody(), output);
            exchange.getResponseHeaders().set(HttpHeaders.CONTENT_TYPE, HessianExporter.CONTENT_TYPE_HESSIAN);
            exchange.sendResponseHeaders(200, output.size());
            FileCopyUtils.copy(output.toByteArray(), exchange.getResponseBody());
        } catch (Throwable ex) {
            exchange.sendResponseHeaders(500, -1L);
            this.logger.error("Hessian skeleton invocation failed", ex);
        }
    }
}