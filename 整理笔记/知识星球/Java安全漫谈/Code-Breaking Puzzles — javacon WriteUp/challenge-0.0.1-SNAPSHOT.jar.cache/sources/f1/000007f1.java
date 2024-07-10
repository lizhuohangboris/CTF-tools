package org.apache.catalina.core;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.SessionTrackingMode;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.PushBuilder;
import org.apache.catalina.Context;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Request;
import org.apache.catalina.util.SessionConfig;
import org.apache.coyote.ActionCode;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.collections.CaseInsensitiveKeyMap;
import org.apache.tomcat.util.http.CookieProcessor;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.servlet.support.WebContentGenerator;
import org.thymeleaf.spring5.processor.SpringInputGeneralFieldTagProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/ApplicationPushBuilder.class */
public class ApplicationPushBuilder implements PushBuilder {
    private static final StringManager sm = StringManager.getManager(ApplicationPushBuilder.class);
    private static final Set<String> DISALLOWED_METHODS = new HashSet();
    private final HttpServletRequest baseRequest;
    private final Request catalinaRequest;
    private final org.apache.coyote.Request coyoteRequest;
    private final String sessionCookieName;
    private final String sessionPathParameterName;
    private final boolean addSessionCookie;
    private final boolean addSessionPathParameter;
    private final Map<String, List<String>> headers = new CaseInsensitiveKeyMap();
    private final List<Cookie> cookies = new ArrayList();
    private String method = "GET";
    private String path;
    private String queryString;
    private String sessionId;
    private String userName;

    static {
        DISALLOWED_METHODS.add(WebContentGenerator.METHOD_POST);
        DISALLOWED_METHODS.add("PUT");
        DISALLOWED_METHODS.add("DELETE");
        DISALLOWED_METHODS.add("CONNECT");
        DISALLOWED_METHODS.add("OPTIONS");
        DISALLOWED_METHODS.add("TRACE");
    }

    public ApplicationPushBuilder(Request catalinaRequest, HttpServletRequest request) {
        Cookie[] cookies;
        this.baseRequest = request;
        this.catalinaRequest = catalinaRequest;
        this.coyoteRequest = catalinaRequest.getCoyoteRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            List<String> values = new ArrayList<>();
            this.headers.put(headerName, values);
            Enumeration<String> headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                values.add(headerValues.nextElement());
            }
        }
        this.headers.remove("if-match");
        this.headers.remove("if-none-match");
        this.headers.remove("if-modified-since");
        this.headers.remove("if-unmodified-since");
        this.headers.remove("if-range");
        this.headers.remove(SpringInputGeneralFieldTagProcessor.RANGE_INPUT_TYPE_ATTR_VALUE);
        this.headers.remove("expect");
        this.headers.remove("authorization");
        this.headers.remove("referer");
        this.headers.remove("cookie");
        StringBuffer referer = request.getRequestURL();
        if (request.getQueryString() != null) {
            referer.append('?');
            referer.append(request.getQueryString());
        }
        addHeader("referer", referer.toString());
        Context context = catalinaRequest.getContext();
        this.sessionCookieName = SessionConfig.getSessionCookieName(context);
        this.sessionPathParameterName = SessionConfig.getSessionUriParamName(context);
        HttpSession session = request.getSession(false);
        if (session != null) {
            this.sessionId = session.getId();
        }
        if (this.sessionId == null) {
            this.sessionId = request.getRequestedSessionId();
        }
        if (!request.isRequestedSessionIdFromCookie() && !request.isRequestedSessionIdFromURL() && this.sessionId != null) {
            Set<SessionTrackingMode> sessionTrackingModes = request.getServletContext().getEffectiveSessionTrackingModes();
            this.addSessionCookie = sessionTrackingModes.contains(SessionTrackingMode.COOKIE);
            this.addSessionPathParameter = sessionTrackingModes.contains(SessionTrackingMode.URL);
        } else {
            this.addSessionCookie = request.isRequestedSessionIdFromCookie();
            this.addSessionPathParameter = request.isRequestedSessionIdFromURL();
        }
        if (request.getCookies() != null) {
            for (Cookie requestCookie : request.getCookies()) {
                this.cookies.add(requestCookie);
            }
        }
        for (Cookie responseCookie : catalinaRequest.getResponse().getCookies()) {
            if (responseCookie.getMaxAge() < 0) {
                Iterator<Cookie> cookieIterator = this.cookies.iterator();
                while (cookieIterator.hasNext()) {
                    Cookie cookie = cookieIterator.next();
                    if (cookie.getName().equals(responseCookie.getName())) {
                        cookieIterator.remove();
                    }
                }
            } else {
                this.cookies.add(new Cookie(responseCookie.getName(), responseCookie.getValue()));
            }
        }
        if (catalinaRequest.getPrincipal() != null) {
            if (session == null || catalinaRequest.getSessionInternal(false).getPrincipal() == null || !(context.getAuthenticator() instanceof AuthenticatorBase) || !((AuthenticatorBase) context.getAuthenticator()).getCache()) {
                this.userName = catalinaRequest.getPrincipal().getName();
            }
            setHeader("authorization", "x-push");
        }
    }

    @Override // javax.servlet.http.PushBuilder
    public PushBuilder path(String path) {
        if (path.startsWith("/")) {
            this.path = path;
        } else {
            String contextPath = this.baseRequest.getContextPath();
            int len = contextPath.length() + path.length() + 1;
            StringBuilder sb = new StringBuilder(len);
            sb.append(contextPath);
            sb.append('/');
            sb.append(path);
            this.path = sb.toString();
        }
        return this;
    }

    @Override // javax.servlet.http.PushBuilder
    public String getPath() {
        return this.path;
    }

    @Override // javax.servlet.http.PushBuilder
    public PushBuilder method(String method) {
        char[] charArray;
        String upperMethod = method.trim().toUpperCase(Locale.ENGLISH);
        if (DISALLOWED_METHODS.contains(upperMethod) || upperMethod.length() == 0) {
            throw new IllegalArgumentException(sm.getString("applicationPushBuilder.methodInvalid", upperMethod));
        }
        for (char c : upperMethod.toCharArray()) {
            if (!HttpParser.isToken(c)) {
                throw new IllegalArgumentException(sm.getString("applicationPushBuilder.methodNotToken", upperMethod));
            }
        }
        this.method = method;
        return this;
    }

    @Override // javax.servlet.http.PushBuilder
    public String getMethod() {
        return this.method;
    }

    @Override // javax.servlet.http.PushBuilder
    public PushBuilder queryString(String queryString) {
        this.queryString = queryString;
        return this;
    }

    @Override // javax.servlet.http.PushBuilder
    public String getQueryString() {
        return this.queryString;
    }

    @Override // javax.servlet.http.PushBuilder
    public PushBuilder sessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    @Override // javax.servlet.http.PushBuilder
    public String getSessionId() {
        return this.sessionId;
    }

    @Override // javax.servlet.http.PushBuilder
    public PushBuilder addHeader(String name, String value) {
        List<String> values = this.headers.get(name);
        if (values == null) {
            values = new ArrayList<>();
            this.headers.put(name, values);
        }
        values.add(value);
        return this;
    }

    @Override // javax.servlet.http.PushBuilder
    public PushBuilder setHeader(String name, String value) {
        List<String> values = this.headers.get(name);
        if (values == null) {
            values = new ArrayList<>();
            this.headers.put(name, values);
        } else {
            values.clear();
        }
        values.add(value);
        return this;
    }

    @Override // javax.servlet.http.PushBuilder
    public PushBuilder removeHeader(String name) {
        this.headers.remove(name);
        return this;
    }

    @Override // javax.servlet.http.PushBuilder
    public Set<String> getHeaderNames() {
        return Collections.unmodifiableSet(this.headers.keySet());
    }

    @Override // javax.servlet.http.PushBuilder
    public String getHeader(String name) {
        List<String> values = this.headers.get(name);
        if (values == null) {
            return null;
        }
        return values.get(0);
    }

    @Override // javax.servlet.http.PushBuilder
    public void push() {
        String pushPath;
        if (this.path == null) {
            throw new IllegalStateException(sm.getString("pushBuilder.noPath"));
        }
        org.apache.coyote.Request pushTarget = new org.apache.coyote.Request();
        pushTarget.method().setString(this.method);
        pushTarget.serverName().setString(this.baseRequest.getServerName());
        pushTarget.setServerPort(this.baseRequest.getServerPort());
        pushTarget.scheme().setString(this.baseRequest.getScheme());
        for (Map.Entry<String, List<String>> header : this.headers.entrySet()) {
            for (String value : header.getValue()) {
                pushTarget.getMimeHeaders().addValue(header.getKey()).setString(value);
            }
        }
        int queryIndex = this.path.indexOf(63);
        String pushQueryString = null;
        if (queryIndex > -1) {
            pushPath = this.path.substring(0, queryIndex);
            if (queryIndex + 1 < this.path.length()) {
                pushQueryString = this.path.substring(queryIndex + 1);
            }
        } else {
            pushPath = this.path;
        }
        if (this.sessionId != null) {
            if (this.addSessionPathParameter) {
                pushPath = pushPath + ";" + this.sessionPathParameterName + "=" + this.sessionId;
                pushTarget.addPathParameter(this.sessionPathParameterName, this.sessionId);
            }
            if (this.addSessionCookie) {
                this.cookies.add(new Cookie(this.sessionCookieName, this.sessionId));
            }
        }
        pushTarget.requestURI().setString(pushPath);
        pushTarget.decodedURI().setString(decode(pushPath, this.catalinaRequest.getConnector().getURICharset()));
        if (pushQueryString == null && this.queryString != null) {
            pushTarget.queryString().setString(this.queryString);
        } else if (pushQueryString != null && this.queryString == null) {
            pushTarget.queryString().setString(pushQueryString);
        } else if (pushQueryString != null && this.queryString != null) {
            pushTarget.queryString().setString(pushQueryString + BeanFactory.FACTORY_BEAN_PREFIX + this.queryString);
        }
        pushTarget.getMimeHeaders().addValue("cookie").setString(generateCookieHeader(this.cookies, this.catalinaRequest.getContext().getCookieProcessor()));
        if (this.userName != null) {
            pushTarget.getRemoteUser().setString(this.userName);
            pushTarget.setRemoteUserNeedsAuthorization(true);
        }
        this.coyoteRequest.action(ActionCode.PUSH_REQUEST, pushTarget);
        this.path = null;
        this.headers.remove("if-none-match");
        this.headers.remove("if-modified-since");
    }

    static String decode(String input, Charset charset) {
        int start = input.indexOf(37);
        int end = 0;
        if (start == -1) {
            return input;
        }
        StringBuilder result = new StringBuilder(input.length());
        while (start != -1) {
            result.append(input.substring(end, start));
            end = start + 3;
            while (end < input.length() && input.charAt(end) == '%') {
                end += 3;
            }
            result.append(decodePercentSequence(input.substring(start, end), charset));
            start = input.indexOf(37, end);
        }
        result.append(input.substring(end));
        return result.toString();
    }

    private static String decodePercentSequence(String sequence, Charset charset) {
        byte[] bytes = new byte[sequence.length() / 3];
        for (int i = 0; i < bytes.length; i += 3) {
            bytes[i] = (byte) ((HexUtils.getDec(sequence.charAt(1 + (3 * i))) << 4) + HexUtils.getDec(sequence.charAt(2 + (3 * i))));
        }
        return new String(bytes, charset);
    }

    private static String generateCookieHeader(List<Cookie> cookies, CookieProcessor cookieProcessor) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Cookie cookie : cookies) {
            if (first) {
                first = false;
            } else {
                result.append(';');
            }
            result.append(cookieProcessor.generateHeader(cookie));
        }
        return result.toString();
    }
}