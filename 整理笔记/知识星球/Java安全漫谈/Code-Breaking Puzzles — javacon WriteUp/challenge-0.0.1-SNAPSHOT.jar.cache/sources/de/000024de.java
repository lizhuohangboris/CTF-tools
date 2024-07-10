package org.springframework.web.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UrlPathHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/filter/ForwardedHeaderFilter.class */
public class ForwardedHeaderFilter extends OncePerRequestFilter {
    private static final Set<String> FORWARDED_HEADER_NAMES = Collections.newSetFromMap(new LinkedCaseInsensitiveMap(6, Locale.ENGLISH));
    private final UrlPathHelper pathHelper = new UrlPathHelper();
    private boolean removeOnly;
    private boolean relativeRedirects;

    static {
        FORWARDED_HEADER_NAMES.add("Forwarded");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Host");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Port");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Proto");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Prefix");
        FORWARDED_HEADER_NAMES.add("X-Forwarded-Ssl");
    }

    public ForwardedHeaderFilter() {
        this.pathHelper.setUrlDecode(false);
        this.pathHelper.setRemoveSemicolonContent(false);
    }

    public void setRemoveOnly(boolean removeOnly) {
        this.removeOnly = removeOnly;
    }

    public void setRelativeRedirects(boolean relativeRedirects) {
        this.relativeRedirects = relativeRedirects;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected boolean shouldNotFilter(HttpServletRequest request) {
        for (String headerName : FORWARDED_HEADER_NAMES) {
            if (request.getHeader(headerName) != null) {
                return false;
            }
        }
        return true;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (this.removeOnly) {
            filterChain.doFilter(new ForwardedHeaderRemovingRequest(request), response);
            return;
        }
        HttpServletRequest wrappedRequest = new ForwardedHeaderExtractingRequest(request, this.pathHelper);
        HttpServletResponse wrappedResponse = this.relativeRedirects ? RelativeRedirectResponseWrapper.wrapIfNecessary(response, HttpStatus.SEE_OTHER) : new ForwardedHeaderExtractingResponse(response, wrappedRequest);
        filterChain.doFilter(wrappedRequest, wrappedResponse);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/filter/ForwardedHeaderFilter$ForwardedHeaderRemovingRequest.class */
    private static class ForwardedHeaderRemovingRequest extends HttpServletRequestWrapper {
        private final Map<String, List<String>> headers;

        public ForwardedHeaderRemovingRequest(HttpServletRequest request) {
            super(request);
            this.headers = initHeaders(request);
        }

        private static Map<String, List<String>> initHeaders(HttpServletRequest request) {
            Map<String, List<String>> headers = new LinkedCaseInsensitiveMap<>(Locale.ENGLISH);
            Enumeration<String> names = request.getHeaderNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                if (!ForwardedHeaderFilter.FORWARDED_HEADER_NAMES.contains(name)) {
                    headers.put(name, Collections.list(request.getHeaders(name)));
                }
            }
            return headers;
        }

        @Override // javax.servlet.http.HttpServletRequestWrapper, javax.servlet.http.HttpServletRequest
        @Nullable
        public String getHeader(String name) {
            List<String> value = this.headers.get(name);
            if (CollectionUtils.isEmpty(value)) {
                return null;
            }
            return value.get(0);
        }

        @Override // javax.servlet.http.HttpServletRequestWrapper, javax.servlet.http.HttpServletRequest
        public Enumeration<String> getHeaders(String name) {
            List<String> value = this.headers.get(name);
            return Collections.enumeration(value != null ? value : Collections.emptySet());
        }

        @Override // javax.servlet.http.HttpServletRequestWrapper, javax.servlet.http.HttpServletRequest
        public Enumeration<String> getHeaderNames() {
            return Collections.enumeration(this.headers.keySet());
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/filter/ForwardedHeaderFilter$ForwardedHeaderExtractingRequest.class */
    private static class ForwardedHeaderExtractingRequest extends ForwardedHeaderRemovingRequest {
        @Nullable
        private final String scheme;
        private final boolean secure;
        @Nullable
        private final String host;
        private final int port;
        private final ForwardedPrefixExtractor forwardedPrefixExtractor;

        ForwardedHeaderExtractingRequest(HttpServletRequest request, UrlPathHelper pathHelper) {
            super(request);
            HttpRequest httpRequest = new ServletServerHttpRequest(request);
            UriComponents uriComponents = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
            int port = uriComponents.getPort();
            this.scheme = uriComponents.getScheme();
            this.secure = "https".equals(this.scheme);
            this.host = uriComponents.getHost();
            this.port = port == -1 ? this.secure ? 443 : 80 : port;
            String baseUrl = this.scheme + "://" + this.host + (port == -1 ? "" : ":" + port);
            Supplier<HttpServletRequest> delegateRequest = () -> {
                return (HttpServletRequest) getRequest();
            };
            this.forwardedPrefixExtractor = new ForwardedPrefixExtractor(delegateRequest, pathHelper, baseUrl);
        }

        @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
        @Nullable
        public String getScheme() {
            return this.scheme;
        }

        @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
        @Nullable
        public String getServerName() {
            return this.host;
        }

        @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
        public int getServerPort() {
            return this.port;
        }

        @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
        public boolean isSecure() {
            return this.secure;
        }

        @Override // javax.servlet.http.HttpServletRequestWrapper, javax.servlet.http.HttpServletRequest
        public String getContextPath() {
            return this.forwardedPrefixExtractor.getContextPath();
        }

        @Override // javax.servlet.http.HttpServletRequestWrapper, javax.servlet.http.HttpServletRequest
        public String getRequestURI() {
            return this.forwardedPrefixExtractor.getRequestUri();
        }

        @Override // javax.servlet.http.HttpServletRequestWrapper, javax.servlet.http.HttpServletRequest
        public StringBuffer getRequestURL() {
            return this.forwardedPrefixExtractor.getRequestUrl();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/filter/ForwardedHeaderFilter$ForwardedPrefixExtractor.class */
    private static class ForwardedPrefixExtractor {
        private final Supplier<HttpServletRequest> delegate;
        private final UrlPathHelper pathHelper;
        private final String baseUrl;
        private String actualRequestUri;
        @Nullable
        private final String forwardedPrefix;
        @Nullable
        private String requestUri = initRequestUri();
        private String requestUrl = initRequestUrl();

        public ForwardedPrefixExtractor(Supplier<HttpServletRequest> delegateRequest, UrlPathHelper pathHelper, String baseUrl) {
            this.delegate = delegateRequest;
            this.pathHelper = pathHelper;
            this.baseUrl = baseUrl;
            this.actualRequestUri = delegateRequest.get().getRequestURI();
            this.forwardedPrefix = initForwardedPrefix(delegateRequest.get());
        }

        @Nullable
        private static String initForwardedPrefix(HttpServletRequest request) {
            String result = null;
            Enumeration<String> names = request.getHeaderNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                if ("X-Forwarded-Prefix".equalsIgnoreCase(name)) {
                    result = request.getHeader(name);
                }
            }
            if (result != null) {
                while (result.endsWith("/")) {
                    result = result.substring(0, result.length() - 1);
                }
            }
            return result;
        }

        @Nullable
        private String initRequestUri() {
            if (this.forwardedPrefix != null) {
                return this.forwardedPrefix + this.pathHelper.getPathWithinApplication(this.delegate.get());
            }
            return null;
        }

        private String initRequestUrl() {
            return this.baseUrl + (this.requestUri != null ? this.requestUri : this.delegate.get().getRequestURI());
        }

        public String getContextPath() {
            return this.forwardedPrefix == null ? this.delegate.get().getContextPath() : this.forwardedPrefix;
        }

        public String getRequestUri() {
            if (this.requestUri == null) {
                return this.delegate.get().getRequestURI();
            }
            recalculatePathsIfNecesary();
            return this.requestUri;
        }

        public StringBuffer getRequestUrl() {
            recalculatePathsIfNecesary();
            return new StringBuffer(this.requestUrl);
        }

        private void recalculatePathsIfNecesary() {
            if (!this.actualRequestUri.equals(this.delegate.get().getRequestURI())) {
                this.actualRequestUri = this.delegate.get().getRequestURI();
                this.requestUri = initRequestUri();
                this.requestUrl = initRequestUrl();
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/filter/ForwardedHeaderFilter$ForwardedHeaderExtractingResponse.class */
    private static class ForwardedHeaderExtractingResponse extends HttpServletResponseWrapper {
        private static final String FOLDER_SEPARATOR = "/";
        private final HttpServletRequest request;

        ForwardedHeaderExtractingResponse(HttpServletResponse response, HttpServletRequest request) {
            super(response);
            this.request = request;
        }

        @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
        public void sendRedirect(String location) throws IOException {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(location);
            UriComponents uriComponents = builder.build();
            if (uriComponents.getScheme() != null) {
                super.sendRedirect(location);
            } else if (location.startsWith("//")) {
                String scheme = this.request.getScheme();
                super.sendRedirect(builder.scheme(scheme).toUriString());
            } else {
                String path = uriComponents.getPath();
                if (path != null) {
                    path = path.startsWith("/") ? path : StringUtils.applyRelativePath(this.request.getRequestURI(), path);
                }
                String result = UriComponentsBuilder.fromHttpRequest(new ServletServerHttpRequest(this.request)).replacePath(path).replaceQuery(uriComponents.getQuery()).fragment(uriComponents.getFragment()).build().normalize().toUriString();
                super.sendRedirect(result);
            }
        }
    }
}