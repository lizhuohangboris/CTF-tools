package javax.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/ServletRequest.class */
public interface ServletRequest {
    Object getAttribute(String str);

    Enumeration<String> getAttributeNames();

    String getCharacterEncoding();

    void setCharacterEncoding(String str) throws UnsupportedEncodingException;

    int getContentLength();

    long getContentLengthLong();

    String getContentType();

    ServletInputStream getInputStream() throws IOException;

    String getParameter(String str);

    Enumeration<String> getParameterNames();

    String[] getParameterValues(String str);

    Map<String, String[]> getParameterMap();

    String getProtocol();

    String getScheme();

    String getServerName();

    int getServerPort();

    BufferedReader getReader() throws IOException;

    String getRemoteAddr();

    String getRemoteHost();

    void setAttribute(String str, Object obj);

    void removeAttribute(String str);

    Locale getLocale();

    Enumeration<Locale> getLocales();

    boolean isSecure();

    RequestDispatcher getRequestDispatcher(String str);

    @Deprecated
    String getRealPath(String str);

    int getRemotePort();

    String getLocalName();

    String getLocalAddr();

    int getLocalPort();

    ServletContext getServletContext();

    AsyncContext startAsync() throws IllegalStateException;

    AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException;

    boolean isAsyncStarted();

    boolean isAsyncSupported();

    AsyncContext getAsyncContext();

    DispatcherType getDispatcherType();
}