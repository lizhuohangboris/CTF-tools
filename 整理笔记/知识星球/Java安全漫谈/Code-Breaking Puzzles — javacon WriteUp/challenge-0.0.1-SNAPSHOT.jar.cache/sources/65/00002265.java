package org.springframework.remoting.httpinvoker;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.UsesSunHttpServer;
import org.springframework.remoting.rmi.RemoteInvocationSerializingExporter;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;

@Deprecated
@UsesSunHttpServer
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/remoting/httpinvoker/SimpleHttpInvokerServiceExporter.class */
public class SimpleHttpInvokerServiceExporter extends RemoteInvocationSerializingExporter implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        try {
            RemoteInvocation invocation = readRemoteInvocation(exchange);
            RemoteInvocationResult result = invokeAndCreateResult(invocation, getProxy());
            writeRemoteInvocationResult(exchange, result);
            exchange.close();
        } catch (ClassNotFoundException ex) {
            exchange.sendResponseHeaders(500, -1L);
            this.logger.error("Class not found during deserialization", ex);
        }
    }

    protected RemoteInvocation readRemoteInvocation(HttpExchange exchange) throws IOException, ClassNotFoundException {
        return readRemoteInvocation(exchange, exchange.getRequestBody());
    }

    protected RemoteInvocation readRemoteInvocation(HttpExchange exchange, InputStream is) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = createObjectInputStream(decorateInputStream(exchange, is));
        return doReadRemoteInvocation(ois);
    }

    protected InputStream decorateInputStream(HttpExchange exchange, InputStream is) throws IOException {
        return is;
    }

    protected void writeRemoteInvocationResult(HttpExchange exchange, RemoteInvocationResult result) throws IOException {
        exchange.getResponseHeaders().set(HttpHeaders.CONTENT_TYPE, getContentType());
        exchange.sendResponseHeaders(200, 0L);
        writeRemoteInvocationResult(exchange, result, exchange.getResponseBody());
    }

    protected void writeRemoteInvocationResult(HttpExchange exchange, RemoteInvocationResult result, OutputStream os) throws IOException {
        ObjectOutputStream oos = createObjectOutputStream(decorateOutputStream(exchange, os));
        doWriteRemoteInvocationResult(result, oos);
        oos.flush();
    }

    protected OutputStream decorateOutputStream(HttpExchange exchange, OutputStream os) throws IOException {
        return os;
    }
}