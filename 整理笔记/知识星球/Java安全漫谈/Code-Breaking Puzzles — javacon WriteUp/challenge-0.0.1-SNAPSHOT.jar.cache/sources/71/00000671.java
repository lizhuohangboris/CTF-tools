package javax.servlet.http;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import javax.servlet.ServletResponseWrapper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/http/HttpServletResponseWrapper.class */
public class HttpServletResponseWrapper extends ServletResponseWrapper implements HttpServletResponse {
    public HttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    private HttpServletResponse _getHttpServletResponse() {
        return (HttpServletResponse) super.getResponse();
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void addCookie(Cookie cookie) {
        _getHttpServletResponse().addCookie(cookie);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public boolean containsHeader(String name) {
        return _getHttpServletResponse().containsHeader(name);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public String encodeURL(String url) {
        return _getHttpServletResponse().encodeURL(url);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public String encodeRedirectURL(String url) {
        return _getHttpServletResponse().encodeRedirectURL(url);
    }

    @Override // javax.servlet.http.HttpServletResponse
    @Deprecated
    public String encodeUrl(String url) {
        return _getHttpServletResponse().encodeUrl(url);
    }

    @Override // javax.servlet.http.HttpServletResponse
    @Deprecated
    public String encodeRedirectUrl(String url) {
        return _getHttpServletResponse().encodeRedirectUrl(url);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void sendError(int sc, String msg) throws IOException {
        _getHttpServletResponse().sendError(sc, msg);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void sendError(int sc) throws IOException {
        _getHttpServletResponse().sendError(sc);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void sendRedirect(String location) throws IOException {
        _getHttpServletResponse().sendRedirect(location);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void setDateHeader(String name, long date) {
        _getHttpServletResponse().setDateHeader(name, date);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void addDateHeader(String name, long date) {
        _getHttpServletResponse().addDateHeader(name, date);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void setHeader(String name, String value) {
        _getHttpServletResponse().setHeader(name, value);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void addHeader(String name, String value) {
        _getHttpServletResponse().addHeader(name, value);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void setIntHeader(String name, int value) {
        _getHttpServletResponse().setIntHeader(name, value);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void addIntHeader(String name, int value) {
        _getHttpServletResponse().addIntHeader(name, value);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void setStatus(int sc) {
        _getHttpServletResponse().setStatus(sc);
    }

    @Override // javax.servlet.http.HttpServletResponse
    @Deprecated
    public void setStatus(int sc, String sm) {
        _getHttpServletResponse().setStatus(sc, sm);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public int getStatus() {
        return _getHttpServletResponse().getStatus();
    }

    @Override // javax.servlet.http.HttpServletResponse
    public String getHeader(String name) {
        return _getHttpServletResponse().getHeader(name);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public Collection<String> getHeaders(String name) {
        return _getHttpServletResponse().getHeaders(name);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public Collection<String> getHeaderNames() {
        return _getHttpServletResponse().getHeaderNames();
    }

    @Override // javax.servlet.http.HttpServletResponse
    public void setTrailerFields(Supplier<Map<String, String>> supplier) {
        _getHttpServletResponse().setTrailerFields(supplier);
    }

    @Override // javax.servlet.http.HttpServletResponse
    public Supplier<Map<String, String>> getTrailerFields() {
        return _getHttpServletResponse().getTrailerFields();
    }
}