package org.apache.catalina.filters;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.http.ResponseUtil;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.support.WebContentGenerator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/filters/CorsFilter.class */
public class CorsFilter extends GenericFilter {
    private static final long serialVersionUID = 1;
    private boolean anyOriginAllowed;
    private boolean supportsCredentials;
    private long preflightMaxAge;
    private boolean decorateRequest;
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    @Deprecated
    public static final String REQUEST_HEADER_VARY = "Vary";
    public static final String REQUEST_HEADER_ORIGIN = "Origin";
    public static final String REQUEST_HEADER_ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
    public static final String REQUEST_HEADER_ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
    public static final String HTTP_REQUEST_ATTRIBUTE_PREFIX = "cors.";
    public static final String HTTP_REQUEST_ATTRIBUTE_ORIGIN = "cors.request.origin";
    public static final String HTTP_REQUEST_ATTRIBUTE_IS_CORS_REQUEST = "cors.isCorsRequest";
    public static final String HTTP_REQUEST_ATTRIBUTE_REQUEST_TYPE = "cors.request.type";
    public static final String HTTP_REQUEST_ATTRIBUTE_REQUEST_HEADERS = "cors.request.headers";
    public static final String DEFAULT_ALLOWED_ORIGINS = "";
    public static final String DEFAULT_ALLOWED_HTTP_METHODS = "GET,POST,HEAD,OPTIONS";
    public static final String DEFAULT_PREFLIGHT_MAXAGE = "1800";
    public static final String DEFAULT_SUPPORTS_CREDENTIALS = "false";
    public static final String DEFAULT_ALLOWED_HTTP_HEADERS = "Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers";
    public static final String DEFAULT_EXPOSED_HEADERS = "";
    public static final String DEFAULT_DECORATE_REQUEST = "true";
    public static final String PARAM_CORS_ALLOWED_ORIGINS = "cors.allowed.origins";
    public static final String PARAM_CORS_SUPPORT_CREDENTIALS = "cors.support.credentials";
    public static final String PARAM_CORS_EXPOSED_HEADERS = "cors.exposed.headers";
    public static final String PARAM_CORS_ALLOWED_HEADERS = "cors.allowed.headers";
    public static final String PARAM_CORS_ALLOWED_METHODS = "cors.allowed.methods";
    public static final String PARAM_CORS_PREFLIGHT_MAXAGE = "cors.preflight.maxage";
    public static final String PARAM_CORS_REQUEST_DECORATE = "cors.request.decorate";
    private static final StringManager sm = StringManager.getManager(CorsFilter.class);
    public static final Collection<String> SIMPLE_HTTP_REQUEST_CONTENT_TYPE_VALUES = Collections.unmodifiableSet(new HashSet(Arrays.asList(MediaType.APPLICATION_FORM_URLENCODED_VALUE, "multipart/form-data", "text/plain")));
    private transient Log log = LogFactory.getLog(CorsFilter.class);
    private final Collection<String> allowedOrigins = new HashSet();
    private final Collection<String> allowedHttpMethods = new HashSet();
    private final Collection<String> allowedHttpHeaders = new HashSet();
    private final Collection<String> exposedHeaders = new HashSet();

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/filters/CorsFilter$CORSRequestType.class */
    public enum CORSRequestType {
        SIMPLE,
        ACTUAL,
        PRE_FLIGHT,
        NOT_CORS,
        INVALID_CORS
    }

    @Override // javax.servlet.Filter
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!(servletRequest instanceof HttpServletRequest) || !(servletResponse instanceof HttpServletResponse)) {
            throw new ServletException(sm.getString("corsFilter.onlyHttp"));
        }
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        CORSRequestType requestType = checkRequestType(request);
        if (this.decorateRequest) {
            decorateCORSProperties(request, requestType);
        }
        switch (requestType) {
            case SIMPLE:
            case ACTUAL:
                handleSimpleCORS(request, response, filterChain);
                return;
            case PRE_FLIGHT:
                handlePreflightCORS(request, response, filterChain);
                return;
            case NOT_CORS:
                handleNonCORS(request, response, filterChain);
                return;
            default:
                handleInvalidCORS(request, response, filterChain);
                return;
        }
    }

    @Override // javax.servlet.GenericFilter
    public void init() throws ServletException {
        parseAndStore(getInitParameter(PARAM_CORS_ALLOWED_ORIGINS, ""), getInitParameter(PARAM_CORS_ALLOWED_METHODS, DEFAULT_ALLOWED_HTTP_METHODS), getInitParameter(PARAM_CORS_ALLOWED_HEADERS, DEFAULT_ALLOWED_HTTP_HEADERS), getInitParameter(PARAM_CORS_EXPOSED_HEADERS, ""), getInitParameter(PARAM_CORS_SUPPORT_CREDENTIALS, "false"), getInitParameter(PARAM_CORS_PREFLIGHT_MAXAGE, DEFAULT_PREFLIGHT_MAXAGE), getInitParameter(PARAM_CORS_REQUEST_DECORATE, "true"));
    }

    private String getInitParameter(String name, String defaultValue) {
        String value = getInitParameter(name);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    protected void handleSimpleCORS(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        CORSRequestType requestType = checkRequestType(request);
        if (requestType != CORSRequestType.SIMPLE && requestType != CORSRequestType.ACTUAL) {
            throw new IllegalArgumentException(sm.getString("corsFilter.wrongType2", CORSRequestType.SIMPLE, CORSRequestType.ACTUAL));
        }
        String origin = request.getHeader("Origin");
        String method = request.getMethod();
        if (!isOriginAllowed(origin)) {
            handleInvalidCORS(request, response, filterChain);
        } else if (!this.allowedHttpMethods.contains(method)) {
            handleInvalidCORS(request, response, filterChain);
        } else {
            addStandardHeaders(request, response);
            filterChain.doFilter(request, response);
        }
    }

    protected void handlePreflightCORS(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        CORSRequestType requestType = checkRequestType(request);
        if (requestType != CORSRequestType.PRE_FLIGHT) {
            throw new IllegalArgumentException(sm.getString("corsFilter.wrongType1", CORSRequestType.PRE_FLIGHT.name().toLowerCase(Locale.ENGLISH)));
        }
        String origin = request.getHeader("Origin");
        if (!isOriginAllowed(origin)) {
            handleInvalidCORS(request, response, filterChain);
            return;
        }
        String accessControlRequestMethod = request.getHeader("Access-Control-Request-Method");
        if (accessControlRequestMethod == null) {
            handleInvalidCORS(request, response, filterChain);
            return;
        }
        String accessControlRequestMethod2 = accessControlRequestMethod.trim();
        String accessControlRequestHeadersHeader = request.getHeader("Access-Control-Request-Headers");
        List<String> accessControlRequestHeaders = new LinkedList<>();
        if (accessControlRequestHeadersHeader != null && !accessControlRequestHeadersHeader.trim().isEmpty()) {
            String[] headers = accessControlRequestHeadersHeader.trim().split(",");
            for (String header : headers) {
                accessControlRequestHeaders.add(header.trim().toLowerCase(Locale.ENGLISH));
            }
        }
        if (!this.allowedHttpMethods.contains(accessControlRequestMethod2)) {
            handleInvalidCORS(request, response, filterChain);
            return;
        }
        if (!accessControlRequestHeaders.isEmpty()) {
            for (String header2 : accessControlRequestHeaders) {
                if (!this.allowedHttpHeaders.contains(header2)) {
                    handleInvalidCORS(request, response, filterChain);
                    return;
                }
            }
        }
        addStandardHeaders(request, response);
    }

    private void handleNonCORS(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        addStandardHeaders(request, response);
        filterChain.doFilter(request, response);
    }

    private void handleInvalidCORS(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String origin = request.getHeader("Origin");
        String method = request.getMethod();
        String accessControlRequestHeaders = request.getHeader("Access-Control-Request-Headers");
        response.setContentType("text/plain");
        response.setStatus(403);
        response.resetBuffer();
        if (this.log.isDebugEnabled()) {
            StringBuilder message = new StringBuilder("Invalid CORS request; Origin=");
            message.append(origin);
            message.append(";Method=");
            message.append(method);
            if (accessControlRequestHeaders != null) {
                message.append(";Access-Control-Request-Headers=");
                message.append(accessControlRequestHeaders);
            }
            this.log.debug(message.toString());
        }
    }

    private void addStandardHeaders(HttpServletRequest request, HttpServletResponse response) {
        String method = request.getMethod();
        String origin = request.getHeader("Origin");
        if (!this.anyOriginAllowed) {
            ResponseUtil.addVaryFieldName(response, "Origin");
        }
        if (this.anyOriginAllowed) {
            response.addHeader("Access-Control-Allow-Origin", "*");
        } else {
            response.addHeader("Access-Control-Allow-Origin", origin);
        }
        if (this.supportsCredentials) {
            response.addHeader("Access-Control-Allow-Credentials", "true");
        }
        if (this.exposedHeaders != null && this.exposedHeaders.size() > 0) {
            String exposedHeadersString = join(this.exposedHeaders, ",");
            response.addHeader("Access-Control-Expose-Headers", exposedHeadersString);
        }
        if ("OPTIONS".equals(method)) {
            ResponseUtil.addVaryFieldName(response, "Access-Control-Request-Method");
            ResponseUtil.addVaryFieldName(response, "Access-Control-Request-Headers");
            if (this.preflightMaxAge > 0) {
                response.addHeader("Access-Control-Max-Age", String.valueOf(this.preflightMaxAge));
            }
            if (this.allowedHttpMethods != null && !this.allowedHttpMethods.isEmpty()) {
                response.addHeader("Access-Control-Allow-Methods", join(this.allowedHttpMethods, ","));
            }
            if (this.allowedHttpHeaders != null && !this.allowedHttpHeaders.isEmpty()) {
                response.addHeader("Access-Control-Allow-Headers", join(this.allowedHttpHeaders, ","));
            }
        }
    }

    protected static void decorateCORSProperties(HttpServletRequest request, CORSRequestType corsRequestType) {
        if (request == null) {
            throw new IllegalArgumentException(sm.getString("corsFilter.nullRequest"));
        }
        if (corsRequestType == null) {
            throw new IllegalArgumentException(sm.getString("corsFilter.nullRequestType"));
        }
        switch (corsRequestType) {
            case SIMPLE:
            case ACTUAL:
                request.setAttribute(HTTP_REQUEST_ATTRIBUTE_IS_CORS_REQUEST, Boolean.TRUE);
                request.setAttribute(HTTP_REQUEST_ATTRIBUTE_ORIGIN, request.getHeader("Origin"));
                request.setAttribute(HTTP_REQUEST_ATTRIBUTE_REQUEST_TYPE, corsRequestType.name().toLowerCase(Locale.ENGLISH));
                return;
            case PRE_FLIGHT:
                request.setAttribute(HTTP_REQUEST_ATTRIBUTE_IS_CORS_REQUEST, Boolean.TRUE);
                request.setAttribute(HTTP_REQUEST_ATTRIBUTE_ORIGIN, request.getHeader("Origin"));
                request.setAttribute(HTTP_REQUEST_ATTRIBUTE_REQUEST_TYPE, corsRequestType.name().toLowerCase(Locale.ENGLISH));
                String headers = request.getHeader("Access-Control-Request-Headers");
                if (headers == null) {
                    headers = "";
                }
                request.setAttribute(HTTP_REQUEST_ATTRIBUTE_REQUEST_HEADERS, headers);
                return;
            case NOT_CORS:
                request.setAttribute(HTTP_REQUEST_ATTRIBUTE_IS_CORS_REQUEST, Boolean.FALSE);
                return;
            default:
                return;
        }
    }

    protected static String join(Collection<String> elements, String joinSeparator) {
        String separator = ",";
        if (elements == null) {
            return null;
        }
        if (joinSeparator != null) {
            separator = joinSeparator;
        }
        StringBuilder buffer = new StringBuilder();
        boolean isFirst = true;
        for (String element : elements) {
            if (!isFirst) {
                buffer.append(separator);
            } else {
                isFirst = false;
            }
            if (element != null) {
                buffer.append(element);
            }
        }
        return buffer.toString();
    }

    protected CORSRequestType checkRequestType(HttpServletRequest request) {
        CORSRequestType requestType = CORSRequestType.INVALID_CORS;
        if (request == null) {
            throw new IllegalArgumentException(sm.getString("corsFilter.nullRequest"));
        }
        String originHeader = request.getHeader("Origin");
        if (originHeader != null) {
            if (originHeader.isEmpty()) {
                requestType = CORSRequestType.INVALID_CORS;
            } else if (!isValidOrigin(originHeader)) {
                requestType = CORSRequestType.INVALID_CORS;
            } else if (isLocalOrigin(request, originHeader)) {
                return CORSRequestType.NOT_CORS;
            } else {
                String method = request.getMethod();
                if (method != null) {
                    if ("OPTIONS".equals(method)) {
                        String accessControlRequestMethodHeader = request.getHeader("Access-Control-Request-Method");
                        requestType = (accessControlRequestMethodHeader == null || accessControlRequestMethodHeader.isEmpty()) ? (accessControlRequestMethodHeader == null || !accessControlRequestMethodHeader.isEmpty()) ? CORSRequestType.ACTUAL : CORSRequestType.INVALID_CORS : CORSRequestType.PRE_FLIGHT;
                    } else if ("GET".equals(method) || WebContentGenerator.METHOD_HEAD.equals(method)) {
                        requestType = CORSRequestType.SIMPLE;
                    } else if (WebContentGenerator.METHOD_POST.equals(method)) {
                        String mediaType = getMediaType(request.getContentType());
                        if (mediaType != null) {
                            requestType = SIMPLE_HTTP_REQUEST_CONTENT_TYPE_VALUES.contains(mediaType) ? CORSRequestType.SIMPLE : CORSRequestType.ACTUAL;
                        }
                    } else {
                        requestType = CORSRequestType.ACTUAL;
                    }
                }
            }
        } else {
            requestType = CORSRequestType.NOT_CORS;
        }
        return requestType;
    }

    private boolean isLocalOrigin(HttpServletRequest request, String origin) {
        StringBuilder target = new StringBuilder();
        String scheme = request.getScheme();
        if (scheme == null) {
            return false;
        }
        String scheme2 = scheme.toLowerCase(Locale.ENGLISH);
        target.append(scheme2);
        target.append("://");
        String host = request.getServerName();
        if (host == null) {
            return false;
        }
        target.append(host);
        int port = request.getServerPort();
        if (("http".equals(scheme2) && port != 80) || ("https".equals(scheme2) && port != 443)) {
            target.append(':');
            target.append(port);
        }
        return origin.equalsIgnoreCase(target.toString());
    }

    private String getMediaType(String contentType) {
        if (contentType == null) {
            return null;
        }
        String result = contentType.toLowerCase(Locale.ENGLISH);
        int firstSemiColonIndex = result.indexOf(59);
        if (firstSemiColonIndex > -1) {
            result = result.substring(0, firstSemiColonIndex);
        }
        return result.trim();
    }

    private boolean isOriginAllowed(String origin) {
        if (this.anyOriginAllowed) {
            return true;
        }
        return this.allowedOrigins.contains(origin);
    }

    private void parseAndStore(String allowedOrigins, String allowedHttpMethods, String allowedHttpHeaders, String exposedHeaders, String supportsCredentials, String preflightMaxAge, String decorateRequest) throws ServletException {
        if (allowedOrigins.trim().equals("*")) {
            this.anyOriginAllowed = true;
        } else {
            this.anyOriginAllowed = false;
            Set<String> setAllowedOrigins = parseStringToSet(allowedOrigins);
            this.allowedOrigins.clear();
            this.allowedOrigins.addAll(setAllowedOrigins);
        }
        Set<String> setAllowedHttpMethods = parseStringToSet(allowedHttpMethods);
        this.allowedHttpMethods.clear();
        this.allowedHttpMethods.addAll(setAllowedHttpMethods);
        Set<String> setAllowedHttpHeaders = parseStringToSet(allowedHttpHeaders);
        Set<String> lowerCaseHeaders = new HashSet<>();
        for (String header : setAllowedHttpHeaders) {
            String lowerCase = header.toLowerCase(Locale.ENGLISH);
            lowerCaseHeaders.add(lowerCase);
        }
        this.allowedHttpHeaders.clear();
        this.allowedHttpHeaders.addAll(lowerCaseHeaders);
        Set<String> setExposedHeaders = parseStringToSet(exposedHeaders);
        this.exposedHeaders.clear();
        this.exposedHeaders.addAll(setExposedHeaders);
        this.supportsCredentials = Boolean.parseBoolean(supportsCredentials);
        if (this.supportsCredentials && this.anyOriginAllowed) {
            throw new ServletException(sm.getString("corsFilter.invalidSupportsCredentials"));
        }
        try {
            if (!preflightMaxAge.isEmpty()) {
                this.preflightMaxAge = Long.parseLong(preflightMaxAge);
            } else {
                this.preflightMaxAge = 0L;
            }
            this.decorateRequest = Boolean.parseBoolean(decorateRequest);
        } catch (NumberFormatException e) {
            throw new ServletException(sm.getString("corsFilter.invalidPreflightMaxAge"), e);
        }
    }

    private Set<String> parseStringToSet(String data) {
        String[] splits;
        String[] strArr;
        if (data != null && data.length() > 0) {
            splits = data.split(",");
        } else {
            splits = new String[0];
        }
        Set<String> set = new HashSet<>();
        if (splits.length > 0) {
            for (String split : splits) {
                set.add(split.trim());
            }
        }
        return set;
    }

    protected static boolean isValidOrigin(String origin) {
        if (origin.contains(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL)) {
            return false;
        }
        if (BeanDefinitionParserDelegate.NULL_ELEMENT.equals(origin) || origin.startsWith("file://")) {
            return true;
        }
        try {
            URI originURI = new URI(origin);
            return originURI.getScheme() != null;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public boolean isAnyOriginAllowed() {
        return this.anyOriginAllowed;
    }

    public Collection<String> getExposedHeaders() {
        return this.exposedHeaders;
    }

    public boolean isSupportsCredentials() {
        return this.supportsCredentials;
    }

    public long getPreflightMaxAge() {
        return this.preflightMaxAge;
    }

    public Collection<String> getAllowedOrigins() {
        return this.allowedOrigins;
    }

    public Collection<String> getAllowedHttpMethods() {
        return this.allowedHttpMethods;
    }

    public Collection<String> getAllowedHttpHeaders() {
        return this.allowedHttpHeaders;
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.log = LogFactory.getLog(CorsFilter.class);
    }
}