package org.springframework.boot.web.embedded.jetty;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ErrorHandler;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/jetty/JettyEmbeddedErrorHandler.class */
class JettyEmbeddedErrorHandler extends ErrorHandler {
    private final ErrorHandler delegate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JettyEmbeddedErrorHandler(ErrorHandler delegate) {
        this.delegate = delegate;
    }

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String method = request.getMethod();
        if (!HttpMethod.GET.is(method) && !HttpMethod.POST.is(method) && !HttpMethod.HEAD.is(method)) {
            request = new ErrorHttpServletRequest(request);
        }
        this.delegate.handle(target, baseRequest, request, response);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/jetty/JettyEmbeddedErrorHandler$ErrorHttpServletRequest.class */
    private static class ErrorHttpServletRequest extends HttpServletRequestWrapper {
        private boolean simulateGetMethod;

        ErrorHttpServletRequest(HttpServletRequest request) {
            super(request);
            this.simulateGetMethod = true;
        }

        @Override // javax.servlet.http.HttpServletRequestWrapper, javax.servlet.http.HttpServletRequest
        public String getMethod() {
            return this.simulateGetMethod ? HttpMethod.GET.toString() : super.getMethod();
        }

        @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
        public ServletContext getServletContext() {
            this.simulateGetMethod = false;
            return super.getServletContext();
        }
    }
}