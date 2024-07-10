package org.apache.catalina.authenticator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.Cookie;
import org.apache.tomcat.util.buf.ByteChunk;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/authenticator/SavedRequest.class */
public final class SavedRequest {
    private final List<Cookie> cookies = new ArrayList();
    private final Map<String, List<String>> headers = new HashMap();
    private final List<Locale> locales = new ArrayList();
    private String method = null;
    private String queryString = null;
    private String requestURI = null;
    private String decodedRequestURI = null;
    private ByteChunk body = null;
    private String contentType = null;

    public void addCookie(Cookie cookie) {
        this.cookies.add(cookie);
    }

    public Iterator<Cookie> getCookies() {
        return this.cookies.iterator();
    }

    public void addHeader(String name, String value) {
        List<String> values = this.headers.get(name);
        if (values == null) {
            values = new ArrayList<>();
            this.headers.put(name, values);
        }
        values.add(value);
    }

    public Iterator<String> getHeaderNames() {
        return this.headers.keySet().iterator();
    }

    public Iterator<String> getHeaderValues(String name) {
        List<String> values = this.headers.get(name);
        if (values == null) {
            return Collections.emptyIterator();
        }
        return values.iterator();
    }

    public void addLocale(Locale locale) {
        this.locales.add(locale);
    }

    public Iterator<Locale> getLocales() {
        return this.locales.iterator();
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getQueryString() {
        return this.queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getRequestURI() {
        return this.requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public String getDecodedRequestURI() {
        return this.decodedRequestURI;
    }

    public void setDecodedRequestURI(String decodedRequestURI) {
        this.decodedRequestURI = decodedRequestURI;
    }

    public ByteChunk getBody() {
        return this.body;
    }

    public void setBody(ByteChunk body) {
        this.body = body;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}