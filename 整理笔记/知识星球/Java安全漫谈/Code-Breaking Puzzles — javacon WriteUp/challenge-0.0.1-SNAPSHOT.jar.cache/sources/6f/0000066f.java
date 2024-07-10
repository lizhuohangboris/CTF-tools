package javax.servlet.http;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletRequestWrapper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/http/HttpServletRequestWrapper.class */
public class HttpServletRequestWrapper extends ServletRequestWrapper implements HttpServletRequest {
    public HttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    private HttpServletRequest _getHttpServletRequest() {
        return (HttpServletRequest) super.getRequest();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getAuthType() {
        return _getHttpServletRequest().getAuthType();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Cookie[] getCookies() {
        return _getHttpServletRequest().getCookies();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public long getDateHeader(String name) {
        return _getHttpServletRequest().getDateHeader(name);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getHeader(String name) {
        return _getHttpServletRequest().getHeader(name);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Enumeration<String> getHeaders(String name) {
        return _getHttpServletRequest().getHeaders(name);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Enumeration<String> getHeaderNames() {
        return _getHttpServletRequest().getHeaderNames();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public int getIntHeader(String name) {
        return _getHttpServletRequest().getIntHeader(name);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public HttpServletMapping getHttpServletMapping() {
        return _getHttpServletRequest().getHttpServletMapping();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getMethod() {
        return _getHttpServletRequest().getMethod();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getPathInfo() {
        return _getHttpServletRequest().getPathInfo();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getPathTranslated() {
        return _getHttpServletRequest().getPathTranslated();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getContextPath() {
        return _getHttpServletRequest().getContextPath();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getQueryString() {
        return _getHttpServletRequest().getQueryString();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getRemoteUser() {
        return _getHttpServletRequest().getRemoteUser();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean isUserInRole(String role) {
        return _getHttpServletRequest().isUserInRole(role);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Principal getUserPrincipal() {
        return _getHttpServletRequest().getUserPrincipal();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getRequestedSessionId() {
        return _getHttpServletRequest().getRequestedSessionId();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getRequestURI() {
        return _getHttpServletRequest().getRequestURI();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public StringBuffer getRequestURL() {
        return _getHttpServletRequest().getRequestURL();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getServletPath() {
        return _getHttpServletRequest().getServletPath();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public HttpSession getSession(boolean create) {
        return _getHttpServletRequest().getSession(create);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public HttpSession getSession() {
        return _getHttpServletRequest().getSession();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String changeSessionId() {
        return _getHttpServletRequest().changeSessionId();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean isRequestedSessionIdValid() {
        return _getHttpServletRequest().isRequestedSessionIdValid();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean isRequestedSessionIdFromCookie() {
        return _getHttpServletRequest().isRequestedSessionIdFromCookie();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean isRequestedSessionIdFromURL() {
        return _getHttpServletRequest().isRequestedSessionIdFromURL();
    }

    @Override // javax.servlet.http.HttpServletRequest
    @Deprecated
    public boolean isRequestedSessionIdFromUrl() {
        return _getHttpServletRequest().isRequestedSessionIdFromUrl();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return _getHttpServletRequest().authenticate(response);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public void login(String username, String password) throws ServletException {
        _getHttpServletRequest().login(username, password);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public void logout() throws ServletException {
        _getHttpServletRequest().logout();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Collection<Part> getParts() throws IOException, ServletException {
        return _getHttpServletRequest().getParts();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Part getPart(String name) throws IOException, ServletException {
        return _getHttpServletRequest().getPart(name);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> httpUpgradeHandlerClass) throws IOException, ServletException {
        return (T) _getHttpServletRequest().upgrade(httpUpgradeHandlerClass);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public PushBuilder newPushBuilder() {
        return _getHttpServletRequest().newPushBuilder();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Map<String, String> getTrailerFields() {
        return _getHttpServletRequest().getTrailerFields();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean isTrailerFieldsReady() {
        return _getHttpServletRequest().isTrailerFieldsReady();
    }
}