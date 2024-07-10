package org.springframework.web.cors;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.WebUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/cors/DefaultCorsProcessor.class */
public class DefaultCorsProcessor implements CorsProcessor {
    private static final Log logger = LogFactory.getLog(DefaultCorsProcessor.class);

    @Override // org.springframework.web.cors.CorsProcessor
    public boolean processRequest(@Nullable CorsConfiguration config, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!CorsUtils.isCorsRequest(request)) {
            return true;
        }
        ServletServerHttpResponse serverResponse = new ServletServerHttpResponse(response);
        if (responseHasCors(serverResponse)) {
            logger.trace("Skip: response already contains \"Access-Control-Allow-Origin\"");
            return true;
        }
        ServletServerHttpRequest serverRequest = new ServletServerHttpRequest(request);
        if (WebUtils.isSameOrigin(serverRequest)) {
            logger.trace("Skip: request is from same origin");
            return true;
        }
        boolean preFlightRequest = CorsUtils.isPreFlightRequest(request);
        if (config == null) {
            if (preFlightRequest) {
                rejectRequest(serverResponse);
                return false;
            }
            return true;
        }
        return handleInternal(serverRequest, serverResponse, config, preFlightRequest);
    }

    private boolean responseHasCors(ServerHttpResponse response) {
        try {
            return response.getHeaders().getAccessControlAllowOrigin() != null;
        } catch (NullPointerException e) {
            return false;
        }
    }

    protected void rejectRequest(ServerHttpResponse response) throws IOException {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getBody().write("Invalid CORS request".getBytes(StandardCharsets.UTF_8));
    }

    protected boolean handleInternal(ServerHttpRequest request, ServerHttpResponse response, CorsConfiguration config, boolean preFlightRequest) throws IOException {
        String requestOrigin = request.getHeaders().getOrigin();
        String allowOrigin = checkOrigin(config, requestOrigin);
        HttpHeaders responseHeaders = response.getHeaders();
        responseHeaders.addAll("Vary", Arrays.asList("Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        if (allowOrigin == null) {
            logger.debug("Reject: '" + requestOrigin + "' origin is not allowed");
            rejectRequest(response);
            return false;
        }
        HttpMethod requestMethod = getMethodToUse(request, preFlightRequest);
        List<HttpMethod> allowMethods = checkMethods(config, requestMethod);
        if (allowMethods == null) {
            logger.debug("Reject: HTTP '" + requestMethod + "' is not allowed");
            rejectRequest(response);
            return false;
        }
        List<String> requestHeaders = getHeadersToUse(request, preFlightRequest);
        List<String> allowHeaders = checkHeaders(config, requestHeaders);
        if (preFlightRequest && allowHeaders == null) {
            logger.debug("Reject: headers '" + requestHeaders + "' are not allowed");
            rejectRequest(response);
            return false;
        }
        responseHeaders.setAccessControlAllowOrigin(allowOrigin);
        if (preFlightRequest) {
            responseHeaders.setAccessControlAllowMethods(allowMethods);
        }
        if (preFlightRequest && !allowHeaders.isEmpty()) {
            responseHeaders.setAccessControlAllowHeaders(allowHeaders);
        }
        if (!CollectionUtils.isEmpty(config.getExposedHeaders())) {
            responseHeaders.setAccessControlExposeHeaders(config.getExposedHeaders());
        }
        if (Boolean.TRUE.equals(config.getAllowCredentials())) {
            responseHeaders.setAccessControlAllowCredentials(true);
        }
        if (preFlightRequest && config.getMaxAge() != null) {
            responseHeaders.setAccessControlMaxAge(config.getMaxAge().longValue());
        }
        response.flush();
        return true;
    }

    @Nullable
    protected String checkOrigin(CorsConfiguration config, @Nullable String requestOrigin) {
        return config.checkOrigin(requestOrigin);
    }

    @Nullable
    protected List<HttpMethod> checkMethods(CorsConfiguration config, @Nullable HttpMethod requestMethod) {
        return config.checkHttpMethod(requestMethod);
    }

    @Nullable
    private HttpMethod getMethodToUse(ServerHttpRequest request, boolean isPreFlight) {
        return isPreFlight ? request.getHeaders().getAccessControlRequestMethod() : request.getMethod();
    }

    @Nullable
    protected List<String> checkHeaders(CorsConfiguration config, List<String> requestHeaders) {
        return config.checkHeaders(requestHeaders);
    }

    private List<String> getHeadersToUse(ServerHttpRequest request, boolean isPreFlight) {
        HttpHeaders headers = request.getHeaders();
        return isPreFlight ? headers.getAccessControlRequestHeaders() : new ArrayList(headers.keySet());
    }
}