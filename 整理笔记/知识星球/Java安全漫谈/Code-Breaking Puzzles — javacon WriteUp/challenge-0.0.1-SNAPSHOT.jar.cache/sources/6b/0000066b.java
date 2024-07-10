package javax.servlet.http;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.ResourceBundle;
import javax.servlet.DispatcherType;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.springframework.http.HttpHeaders;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/http/HttpServlet.class */
public abstract class HttpServlet extends GenericServlet {
    private static final long serialVersionUID = 1;
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_HEAD = "HEAD";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_TRACE = "TRACE";
    private static final String HEADER_IFMODSINCE = "If-Modified-Since";
    private static final String HEADER_LASTMOD = "Last-Modified";
    private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
    private static final ResourceBundle lStrings = ResourceBundle.getBundle(LSTRING_FILE);

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String protocol = req.getProtocol();
        String msg = lStrings.getString("http.method_get_not_supported");
        if (protocol.endsWith("1.1")) {
            resp.sendError(405, msg);
        } else {
            resp.sendError(400, msg);
        }
    }

    protected long getLastModified(HttpServletRequest req) {
        return -1L;
    }

    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (DispatcherType.INCLUDE.equals(req.getDispatcherType())) {
            doGet(req, resp);
            return;
        }
        NoBodyResponse response = new NoBodyResponse(resp);
        doGet(req, response);
        response.setContentLength();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String protocol = req.getProtocol();
        String msg = lStrings.getString("http.method_post_not_supported");
        if (protocol.endsWith("1.1")) {
            resp.sendError(405, msg);
        } else {
            resp.sendError(400, msg);
        }
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String protocol = req.getProtocol();
        String msg = lStrings.getString("http.method_put_not_supported");
        if (protocol.endsWith("1.1")) {
            resp.sendError(405, msg);
        } else {
            resp.sendError(400, msg);
        }
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String protocol = req.getProtocol();
        String msg = lStrings.getString("http.method_delete_not_supported");
        if (protocol.endsWith("1.1")) {
            resp.sendError(405, msg);
        } else {
            resp.sendError(400, msg);
        }
    }

    private static Method[] getAllDeclaredMethods(Class<?> c) {
        if (c.equals(HttpServlet.class)) {
            return null;
        }
        Method[] parentMethods = getAllDeclaredMethods(c.getSuperclass());
        Method[] thisMethods = c.getDeclaredMethods();
        if (parentMethods != null && parentMethods.length > 0) {
            Method[] allMethods = new Method[parentMethods.length + thisMethods.length];
            System.arraycopy(parentMethods, 0, allMethods, 0, parentMethods.length);
            System.arraycopy(thisMethods, 0, allMethods, parentMethods.length, thisMethods.length);
            thisMethods = allMethods;
        }
        return thisMethods;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Method[] methods = getAllDeclaredMethods(getClass());
        boolean ALLOW_GET = false;
        boolean ALLOW_HEAD = false;
        boolean ALLOW_POST = false;
        boolean ALLOW_PUT = false;
        boolean ALLOW_DELETE = false;
        boolean ALLOW_TRACE = true;
        try {
            Class<?> clazz = Class.forName("org.apache.catalina.connector.RequestFacade");
            Method getAllowTrace = clazz.getMethod("getAllowTrace", null);
            ALLOW_TRACE = ((Boolean) getAllowTrace.invoke(req, null)).booleanValue();
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
        }
        for (Method m : methods) {
            if (m.getName().equals("doGet")) {
                ALLOW_GET = true;
                ALLOW_HEAD = true;
            }
            if (m.getName().equals("doPost")) {
                ALLOW_POST = true;
            }
            if (m.getName().equals("doPut")) {
                ALLOW_PUT = true;
            }
            if (m.getName().equals("doDelete")) {
                ALLOW_DELETE = true;
            }
        }
        String allow = null;
        if (ALLOW_GET) {
            allow = "GET";
        }
        if (ALLOW_HEAD) {
            allow = allow == null ? "HEAD" : allow + ", HEAD";
        }
        if (ALLOW_POST) {
            allow = allow == null ? "POST" : allow + ", POST";
        }
        if (ALLOW_PUT) {
            allow = allow == null ? METHOD_PUT : allow + ", PUT";
        }
        if (ALLOW_DELETE) {
            allow = allow == null ? METHOD_DELETE : allow + ", DELETE";
        }
        if (ALLOW_TRACE) {
            allow = allow == null ? METHOD_TRACE : allow + ", TRACE";
        }
        if (1 != 0) {
            allow = allow == null ? METHOD_OPTIONS : allow + ", OPTIONS";
        }
        resp.setHeader(HttpHeaders.ALLOW, allow);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StringBuilder buffer = new StringBuilder("TRACE ").append(req.getRequestURI()).append(" ").append(req.getProtocol());
        Enumeration<String> reqHeaderEnum = req.getHeaderNames();
        while (reqHeaderEnum.hasMoreElements()) {
            String headerName = reqHeaderEnum.nextElement();
            buffer.append("\r\n").append(headerName).append(": ").append(req.getHeader(headerName));
        }
        buffer.append("\r\n");
        int responseLength = buffer.length();
        resp.setContentType("message/http");
        resp.setContentLength(responseLength);
        ServletOutputStream out = resp.getOutputStream();
        out.print(buffer.toString());
        out.close();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long ifModifiedSince;
        String method = req.getMethod();
        if (method.equals("GET")) {
            long lastModified = getLastModified(req);
            if (lastModified == -1) {
                doGet(req, resp);
                return;
            }
            try {
                ifModifiedSince = req.getDateHeader("If-Modified-Since");
            } catch (IllegalArgumentException e) {
                ifModifiedSince = -1;
            }
            if (ifModifiedSince < (lastModified / 1000) * 1000) {
                maybeSetLastModified(resp, lastModified);
                doGet(req, resp);
                return;
            }
            resp.setStatus(304);
        } else if (method.equals("HEAD")) {
            maybeSetLastModified(resp, getLastModified(req));
            doHead(req, resp);
        } else if (method.equals("POST")) {
            doPost(req, resp);
        } else if (method.equals(METHOD_PUT)) {
            doPut(req, resp);
        } else if (method.equals(METHOD_DELETE)) {
            doDelete(req, resp);
        } else if (method.equals(METHOD_OPTIONS)) {
            doOptions(req, resp);
        } else if (method.equals(METHOD_TRACE)) {
            doTrace(req, resp);
        } else {
            String errMsg = lStrings.getString("http.method_not_implemented");
            Object[] errArgs = {method};
            resp.sendError(501, MessageFormat.format(errMsg, errArgs));
        }
    }

    private void maybeSetLastModified(HttpServletResponse resp, long lastModified) {
        if (!resp.containsHeader("Last-Modified") && lastModified >= 0) {
            resp.setDateHeader("Last-Modified", lastModified);
        }
    }

    @Override // javax.servlet.GenericServlet, javax.servlet.Servlet
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        try {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;
            service(request, response);
        } catch (ClassCastException e) {
            throw new ServletException("non-HTTP request or response");
        }
    }
}