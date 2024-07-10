package org.springframework.remoting.httpinvoker;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.remoting.rmi.RemoteInvocationSerializingExporter;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.util.NestedServletException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/remoting/httpinvoker/HttpInvokerServiceExporter.class */
public class HttpInvokerServiceExporter extends RemoteInvocationSerializingExporter implements HttpRequestHandler {
    @Override // org.springframework.web.HttpRequestHandler
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            RemoteInvocation invocation = readRemoteInvocation(request);
            RemoteInvocationResult result = invokeAndCreateResult(invocation, getProxy());
            writeRemoteInvocationResult(request, response, result);
        } catch (ClassNotFoundException ex) {
            throw new NestedServletException("Class not found during deserialization", ex);
        }
    }

    protected RemoteInvocation readRemoteInvocation(HttpServletRequest request) throws IOException, ClassNotFoundException {
        return readRemoteInvocation(request, request.getInputStream());
    }

    protected RemoteInvocation readRemoteInvocation(HttpServletRequest request, InputStream is) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = createObjectInputStream(decorateInputStream(request, is));
        try {
            RemoteInvocation doReadRemoteInvocation = doReadRemoteInvocation(ois);
            ois.close();
            return doReadRemoteInvocation;
        } catch (Throwable th) {
            ois.close();
            throw th;
        }
    }

    protected InputStream decorateInputStream(HttpServletRequest request, InputStream is) throws IOException {
        return is;
    }

    protected void writeRemoteInvocationResult(HttpServletRequest request, HttpServletResponse response, RemoteInvocationResult result) throws IOException {
        response.setContentType(getContentType());
        writeRemoteInvocationResult(request, response, result, response.getOutputStream());
    }

    protected void writeRemoteInvocationResult(HttpServletRequest request, HttpServletResponse response, RemoteInvocationResult result, OutputStream os) throws IOException {
        ObjectOutputStream oos = createObjectOutputStream(new FlushGuardedOutputStream(decorateOutputStream(request, response, os)));
        try {
            doWriteRemoteInvocationResult(result, oos);
            oos.close();
        } catch (Throwable th) {
            oos.close();
            throw th;
        }
    }

    protected OutputStream decorateOutputStream(HttpServletRequest request, HttpServletResponse response, OutputStream os) throws IOException {
        return os;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/remoting/httpinvoker/HttpInvokerServiceExporter$FlushGuardedOutputStream.class */
    public static class FlushGuardedOutputStream extends FilterOutputStream {
        public FlushGuardedOutputStream(OutputStream out) {
            super(out);
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
        public void flush() throws IOException {
        }
    }
}