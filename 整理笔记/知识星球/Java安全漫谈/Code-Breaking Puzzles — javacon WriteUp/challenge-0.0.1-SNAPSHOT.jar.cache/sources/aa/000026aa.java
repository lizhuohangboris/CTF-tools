package org.springframework.web.servlet.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.context.support.WebApplicationObjectSupport;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/support/WebContentGenerator.class */
public abstract class WebContentGenerator extends WebApplicationObjectSupport {
    public static final String METHOD_GET = "GET";
    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_POST = "POST";
    private static final String HEADER_PRAGMA = "Pragma";
    private static final String HEADER_EXPIRES = "Expires";
    protected static final String HEADER_CACHE_CONTROL = "Cache-Control";
    @Nullable
    private Set<String> supportedMethods;
    @Nullable
    private String allowHeader;
    private boolean requireSession;
    @Nullable
    private CacheControl cacheControl;
    private int cacheSeconds;
    @Nullable
    private String[] varyByRequestHeaders;
    private boolean useExpiresHeader;
    private boolean useCacheControlHeader;
    private boolean useCacheControlNoStore;
    private boolean alwaysMustRevalidate;

    public WebContentGenerator() {
        this(true);
    }

    public WebContentGenerator(boolean restrictDefaultSupportedMethods) {
        this.requireSession = false;
        this.cacheSeconds = -1;
        this.useExpiresHeader = false;
        this.useCacheControlHeader = true;
        this.useCacheControlNoStore = true;
        this.alwaysMustRevalidate = false;
        if (restrictDefaultSupportedMethods) {
            this.supportedMethods = new LinkedHashSet(4);
            this.supportedMethods.add("GET");
            this.supportedMethods.add(METHOD_HEAD);
            this.supportedMethods.add(METHOD_POST);
        }
        initAllowHeader();
    }

    public WebContentGenerator(String... supportedMethods) {
        this.requireSession = false;
        this.cacheSeconds = -1;
        this.useExpiresHeader = false;
        this.useCacheControlHeader = true;
        this.useCacheControlNoStore = true;
        this.alwaysMustRevalidate = false;
        setSupportedMethods(supportedMethods);
    }

    public final void setSupportedMethods(@Nullable String... methods) {
        if (!ObjectUtils.isEmpty((Object[]) methods)) {
            this.supportedMethods = new LinkedHashSet(Arrays.asList(methods));
        } else {
            this.supportedMethods = null;
        }
        initAllowHeader();
    }

    @Nullable
    public final String[] getSupportedMethods() {
        if (this.supportedMethods != null) {
            return StringUtils.toStringArray(this.supportedMethods);
        }
        return null;
    }

    private void initAllowHeader() {
        Collection<String> allowedMethods;
        HttpMethod[] values;
        if (this.supportedMethods == null) {
            allowedMethods = new ArrayList<>(HttpMethod.values().length - 1);
            for (HttpMethod method : HttpMethod.values()) {
                if (method != HttpMethod.TRACE) {
                    allowedMethods.add(method.name());
                }
            }
        } else if (this.supportedMethods.contains(HttpMethod.OPTIONS.name())) {
            allowedMethods = this.supportedMethods;
        } else {
            allowedMethods = new ArrayList<>(this.supportedMethods);
            allowedMethods.add(HttpMethod.OPTIONS.name());
        }
        this.allowHeader = StringUtils.collectionToCommaDelimitedString(allowedMethods);
    }

    @Nullable
    public String getAllowHeader() {
        return this.allowHeader;
    }

    public final void setRequireSession(boolean requireSession) {
        this.requireSession = requireSession;
    }

    public final boolean isRequireSession() {
        return this.requireSession;
    }

    public final void setCacheControl(@Nullable CacheControl cacheControl) {
        this.cacheControl = cacheControl;
    }

    @Nullable
    public final CacheControl getCacheControl() {
        return this.cacheControl;
    }

    public final void setCacheSeconds(int seconds) {
        this.cacheSeconds = seconds;
    }

    public final int getCacheSeconds() {
        return this.cacheSeconds;
    }

    public final void setVaryByRequestHeaders(@Nullable String... varyByRequestHeaders) {
        this.varyByRequestHeaders = varyByRequestHeaders;
    }

    @Nullable
    public final String[] getVaryByRequestHeaders() {
        return this.varyByRequestHeaders;
    }

    @Deprecated
    public final void setUseExpiresHeader(boolean useExpiresHeader) {
        this.useExpiresHeader = useExpiresHeader;
    }

    @Deprecated
    public final boolean isUseExpiresHeader() {
        return this.useExpiresHeader;
    }

    @Deprecated
    public final void setUseCacheControlHeader(boolean useCacheControlHeader) {
        this.useCacheControlHeader = useCacheControlHeader;
    }

    @Deprecated
    public final boolean isUseCacheControlHeader() {
        return this.useCacheControlHeader;
    }

    @Deprecated
    public final void setUseCacheControlNoStore(boolean useCacheControlNoStore) {
        this.useCacheControlNoStore = useCacheControlNoStore;
    }

    @Deprecated
    public final boolean isUseCacheControlNoStore() {
        return this.useCacheControlNoStore;
    }

    @Deprecated
    public final void setAlwaysMustRevalidate(boolean mustRevalidate) {
        this.alwaysMustRevalidate = mustRevalidate;
    }

    @Deprecated
    public final boolean isAlwaysMustRevalidate() {
        return this.alwaysMustRevalidate;
    }

    public final void checkRequest(HttpServletRequest request) throws ServletException {
        String method = request.getMethod();
        if (this.supportedMethods != null && !this.supportedMethods.contains(method)) {
            throw new HttpRequestMethodNotSupportedException(method, this.supportedMethods);
        }
        if (this.requireSession && request.getSession(false) == null) {
            throw new HttpSessionRequiredException("Pre-existing session required but none found");
        }
    }

    public final void prepareResponse(HttpServletResponse response) {
        if (this.cacheControl != null) {
            applyCacheControl(response, this.cacheControl);
        } else {
            applyCacheSeconds(response, this.cacheSeconds);
        }
        if (this.varyByRequestHeaders != null) {
            for (String value : getVaryRequestHeadersToAdd(response, this.varyByRequestHeaders)) {
                response.addHeader("Vary", value);
            }
        }
    }

    public final void applyCacheControl(HttpServletResponse response, CacheControl cacheControl) {
        String ccValue = cacheControl.getHeaderValue();
        if (ccValue != null) {
            response.setHeader("Cache-Control", ccValue);
            if (response.containsHeader("Pragma")) {
                response.setHeader("Pragma", "");
            }
            if (response.containsHeader("Expires")) {
                response.setHeader("Expires", "");
            }
        }
    }

    public final void applyCacheSeconds(HttpServletResponse response, int cacheSeconds) {
        CacheControl cControl;
        if (this.useExpiresHeader || !this.useCacheControlHeader) {
            if (cacheSeconds > 0) {
                cacheForSeconds(response, cacheSeconds);
                return;
            } else if (cacheSeconds == 0) {
                preventCaching(response);
                return;
            } else {
                return;
            }
        }
        if (cacheSeconds > 0) {
            cControl = CacheControl.maxAge(cacheSeconds, TimeUnit.SECONDS);
            if (this.alwaysMustRevalidate) {
                cControl = cControl.mustRevalidate();
            }
        } else if (cacheSeconds == 0) {
            cControl = this.useCacheControlNoStore ? CacheControl.noStore() : CacheControl.noCache();
        } else {
            cControl = CacheControl.empty();
        }
        applyCacheControl(response, cControl);
    }

    @Deprecated
    protected final void checkAndPrepare(HttpServletRequest request, HttpServletResponse response, boolean lastModified) throws ServletException {
        checkRequest(request);
        prepareResponse(response);
    }

    @Deprecated
    protected final void checkAndPrepare(HttpServletRequest request, HttpServletResponse response, int cacheSeconds, boolean lastModified) throws ServletException {
        checkRequest(request);
        applyCacheSeconds(response, cacheSeconds);
    }

    @Deprecated
    protected final void applyCacheSeconds(HttpServletResponse response, int cacheSeconds, boolean mustRevalidate) {
        if (cacheSeconds > 0) {
            cacheForSeconds(response, cacheSeconds, mustRevalidate);
        } else if (cacheSeconds == 0) {
            preventCaching(response);
        }
    }

    @Deprecated
    protected final void cacheForSeconds(HttpServletResponse response, int seconds) {
        cacheForSeconds(response, seconds, false);
    }

    @Deprecated
    protected final void cacheForSeconds(HttpServletResponse response, int seconds, boolean mustRevalidate) {
        if (this.useExpiresHeader) {
            response.setDateHeader("Expires", System.currentTimeMillis() + (seconds * 1000));
        } else if (response.containsHeader("Expires")) {
            response.setHeader("Expires", "");
        }
        if (this.useCacheControlHeader) {
            String headerValue = "max-age=" + seconds;
            if (mustRevalidate || this.alwaysMustRevalidate) {
                headerValue = headerValue + ", must-revalidate";
            }
            response.setHeader("Cache-Control", headerValue);
        }
        if (response.containsHeader("Pragma")) {
            response.setHeader("Pragma", "");
        }
    }

    @Deprecated
    protected final void preventCaching(HttpServletResponse response) {
        response.setHeader("Pragma", "no-cache");
        if (this.useExpiresHeader) {
            response.setDateHeader("Expires", 1L);
        }
        if (this.useCacheControlHeader) {
            response.setHeader("Cache-Control", "no-cache");
            if (this.useCacheControlNoStore) {
                response.addHeader("Cache-Control", "no-store");
            }
        }
    }

    private Collection<String> getVaryRequestHeadersToAdd(HttpServletResponse response, String[] varyByRequestHeaders) {
        String[] strArr;
        if (!response.containsHeader("Vary")) {
            return Arrays.asList(varyByRequestHeaders);
        }
        Collection<String> result = new ArrayList<>(varyByRequestHeaders.length);
        Collections.addAll(result, varyByRequestHeaders);
        for (String header : response.getHeaders("Vary")) {
            for (String existing : StringUtils.tokenizeToStringArray(header, ",")) {
                if ("*".equals(existing)) {
                    return Collections.emptyList();
                }
                for (String value : varyByRequestHeaders) {
                    if (value.equalsIgnoreCase(existing)) {
                        result.remove(value);
                    }
                }
            }
        }
        return result;
    }
}