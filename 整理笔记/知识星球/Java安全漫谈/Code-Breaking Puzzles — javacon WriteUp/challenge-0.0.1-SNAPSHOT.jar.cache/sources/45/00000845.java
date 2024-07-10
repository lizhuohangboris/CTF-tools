package org.apache.catalina.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/filters/RestCsrfPreventionFilter.class */
public class RestCsrfPreventionFilter extends CsrfPreventionFilterBase {
    private static final Pattern NON_MODIFYING_METHODS_PATTERN = Pattern.compile("GET|HEAD|OPTIONS");
    private static final Predicate<String> nonModifyingMethods = m -> {
        return Objects.nonNull(m) && NON_MODIFYING_METHODS_PATTERN.matcher(m).matches();
    };
    private Set<String> pathsAcceptingParams = new HashSet();
    private String pathsDelimiter = ",";

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/filters/RestCsrfPreventionFilter$MethodType.class */
    private enum MethodType {
        NON_MODIFYING_METHOD,
        MODIFYING_METHOD
    }

    @FunctionalInterface
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/filters/RestCsrfPreventionFilter$NonceConsumer.class */
    private interface NonceConsumer<T> {
        void setNonce(T t, String str, String str2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @FunctionalInterface
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/filters/RestCsrfPreventionFilter$NonceSupplier.class */
    public interface NonceSupplier<T, R> {
        R getNonce(T t, String str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/filters/RestCsrfPreventionFilter$RestCsrfPreventionStrategy.class */
    public interface RestCsrfPreventionStrategy {
        public static final NonceSupplier<HttpServletRequest, String> nonceFromRequestHeader = r, k -> {
            return r.getHeader(k);
        };
        public static final NonceSupplier<HttpServletRequest, String[]> nonceFromRequestParams = r, k -> {
            return r.getParameterValues(k);
        };
        public static final NonceSupplier<HttpSession, String> nonceFromSession = s, k -> {
            if (Objects.isNull(s)) {
                return null;
            }
            return (String) s.getAttribute(k);
        };
        public static final NonceConsumer<HttpServletResponse> nonceToResponse = r, k, v -> {
            r.setHeader(k, v);
        };
        public static final NonceConsumer<HttpSession> nonceToSession = s, k, v -> {
            s.setAttribute(k, v);
        };

        boolean apply(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException;
    }

    @Override // javax.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        RestCsrfPreventionStrategy strategy;
        if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
            MethodType mType = MethodType.MODIFYING_METHOD;
            if (nonModifyingMethods.test(((HttpServletRequest) request).getMethod())) {
                mType = MethodType.NON_MODIFYING_METHOD;
            }
            switch (mType) {
                case NON_MODIFYING_METHOD:
                    strategy = new FetchRequest();
                    break;
                default:
                    strategy = new StateChangingRequest();
                    break;
            }
            if (!strategy.apply((HttpServletRequest) request, (HttpServletResponse) response)) {
                return;
            }
        }
        chain.doFilter(request, response);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/filters/RestCsrfPreventionFilter$StateChangingRequest.class */
    private class StateChangingRequest implements RestCsrfPreventionStrategy {
        private StateChangingRequest() {
        }

        @Override // org.apache.catalina.filters.RestCsrfPreventionFilter.RestCsrfPreventionStrategy
        public boolean apply(HttpServletRequest request, HttpServletResponse response) throws IOException {
            if (isValidStateChangingRequest(extractNonceFromRequest(request), nonceFromSession.getNonce(request.getSession(false), Constants.CSRF_REST_NONCE_SESSION_ATTR_NAME))) {
                return true;
            }
            nonceToResponse.setNonce(response, Constants.CSRF_REST_NONCE_HEADER_NAME, Constants.CSRF_REST_NONCE_HEADER_REQUIRED_VALUE);
            response.sendError(RestCsrfPreventionFilter.this.getDenyStatus(), FilterBase.sm.getString("restCsrfPreventionFilter.invalidNonce"));
            return false;
        }

        private boolean isValidStateChangingRequest(String reqNonce, String sessionNonce) {
            return Objects.nonNull(reqNonce) && Objects.nonNull(sessionNonce) && Objects.equals(reqNonce, sessionNonce);
        }

        private String extractNonceFromRequest(HttpServletRequest request) {
            String nonceFromRequest = nonceFromRequestHeader.getNonce(request, Constants.CSRF_REST_NONCE_HEADER_NAME);
            if ((Objects.isNull(nonceFromRequest) || Objects.equals("", nonceFromRequest)) && !RestCsrfPreventionFilter.this.getPathsAcceptingParams().isEmpty() && RestCsrfPreventionFilter.this.getPathsAcceptingParams().contains(RestCsrfPreventionFilter.this.getRequestedPath(request))) {
                nonceFromRequest = extractNonceFromRequestParams(request);
            }
            return nonceFromRequest;
        }

        private String extractNonceFromRequestParams(HttpServletRequest request) {
            String[] params = nonceFromRequestParams.getNonce(request, Constants.CSRF_REST_NONCE_HEADER_NAME);
            if (Objects.nonNull(params) && params.length > 0) {
                String nonce = params[0];
                for (String param : params) {
                    if (!Objects.equals(param, nonce)) {
                        return null;
                    }
                }
                return nonce;
            }
            return null;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/filters/RestCsrfPreventionFilter$FetchRequest.class */
    private class FetchRequest implements RestCsrfPreventionStrategy {
        private final Predicate<String> fetchRequest;

        private FetchRequest() {
            this.fetchRequest = s -> {
                return Constants.CSRF_REST_NONCE_HEADER_FETCH_VALUE.equalsIgnoreCase(s);
            };
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.apache.catalina.filters.RestCsrfPreventionFilter.RestCsrfPreventionStrategy
        public boolean apply(HttpServletRequest request, HttpServletResponse response) {
            if (this.fetchRequest.test(nonceFromRequestHeader.getNonce(request, Constants.CSRF_REST_NONCE_HEADER_NAME))) {
                String nonceFromSessionStr = nonceFromSession.getNonce(request.getSession(false), Constants.CSRF_REST_NONCE_SESSION_ATTR_NAME);
                if (nonceFromSessionStr == null) {
                    nonceFromSessionStr = RestCsrfPreventionFilter.this.generateNonce();
                    nonceToSession.setNonce(Objects.requireNonNull(request.getSession(true)), Constants.CSRF_REST_NONCE_SESSION_ATTR_NAME, nonceFromSessionStr);
                }
                nonceToResponse.setNonce(response, Constants.CSRF_REST_NONCE_HEADER_NAME, nonceFromSessionStr);
                return true;
            }
            return true;
        }
    }

    public void setPathsAcceptingParams(String pathsList) {
        if (Objects.nonNull(pathsList)) {
            Arrays.asList(pathsList.split(this.pathsDelimiter)).forEach(e -> {
                this.pathsAcceptingParams.add(e.trim());
            });
        }
    }

    public Set<String> getPathsAcceptingParams() {
        return this.pathsAcceptingParams;
    }
}