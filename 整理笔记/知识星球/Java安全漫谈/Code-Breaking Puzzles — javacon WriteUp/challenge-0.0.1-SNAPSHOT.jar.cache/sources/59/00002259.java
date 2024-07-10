package org.springframework.remoting.caucho;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.support.WebContentGenerator;
import org.springframework.web.util.NestedServletException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/remoting/caucho/HessianServiceExporter.class */
public class HessianServiceExporter extends HessianExporter implements HttpRequestHandler {
    @Override // org.springframework.web.HttpRequestHandler
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!WebContentGenerator.METHOD_POST.equals(request.getMethod())) {
            throw new HttpRequestMethodNotSupportedException(request.getMethod(), new String[]{WebContentGenerator.METHOD_POST}, "HessianServiceExporter only supports POST requests");
        }
        response.setContentType(HessianExporter.CONTENT_TYPE_HESSIAN);
        try {
            invoke(request.getInputStream(), response.getOutputStream());
        } catch (Throwable ex) {
            throw new NestedServletException("Hessian skeleton invocation failed", ex);
        }
    }
}