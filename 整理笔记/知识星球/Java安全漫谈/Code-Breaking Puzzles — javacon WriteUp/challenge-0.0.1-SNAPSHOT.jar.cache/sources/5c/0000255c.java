package org.springframework.web.server.adapter;

import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.springframework.beans.PropertyAccessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.i18n.LocaleContextResolver;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/server/adapter/DefaultServerWebExchange.class */
public class DefaultServerWebExchange implements ServerWebExchange {
    private static final List<HttpMethod> SAFE_METHODS = Arrays.asList(HttpMethod.GET, HttpMethod.HEAD);
    private static final ResolvableType FORM_DATA_TYPE = ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, String.class);
    private static final ResolvableType MULTIPART_DATA_TYPE = ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, Part.class);
    private static final Mono<MultiValueMap<String, String>> EMPTY_FORM_DATA = Mono.just(CollectionUtils.unmodifiableMultiValueMap(new LinkedMultiValueMap(0))).cache();
    private static final Mono<MultiValueMap<String, Part>> EMPTY_MULTIPART_DATA = Mono.just(CollectionUtils.unmodifiableMultiValueMap(new LinkedMultiValueMap(0))).cache();
    private final ServerHttpRequest request;
    private final ServerHttpResponse response;
    private final Map<String, Object> attributes;
    private final Mono<WebSession> sessionMono;
    private final LocaleContextResolver localeContextResolver;
    private final Mono<MultiValueMap<String, String>> formDataMono;
    private final Mono<MultiValueMap<String, Part>> multipartDataMono;
    @Nullable
    private final ApplicationContext applicationContext;
    private volatile boolean notModified;
    private Function<String, String> urlTransformer;
    @Nullable
    private Object logId;
    private String logPrefix;

    public DefaultServerWebExchange(ServerHttpRequest request, ServerHttpResponse response, WebSessionManager sessionManager, ServerCodecConfigurer codecConfigurer, LocaleContextResolver localeContextResolver) {
        this(request, response, sessionManager, codecConfigurer, localeContextResolver, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultServerWebExchange(ServerHttpRequest request, ServerHttpResponse response, WebSessionManager sessionManager, ServerCodecConfigurer codecConfigurer, LocaleContextResolver localeContextResolver, @Nullable ApplicationContext applicationContext) {
        this.attributes = new ConcurrentHashMap();
        this.urlTransformer = url -> {
            return url;
        };
        this.logPrefix = "";
        Assert.notNull(request, "'request' is required");
        Assert.notNull(response, "'response' is required");
        Assert.notNull(sessionManager, "'sessionManager' is required");
        Assert.notNull(codecConfigurer, "'codecConfigurer' is required");
        Assert.notNull(localeContextResolver, "'localeContextResolver' is required");
        this.attributes.put(ServerWebExchange.LOG_ID_ATTRIBUTE, request.getId());
        this.request = request;
        this.response = response;
        this.sessionMono = sessionManager.getSession(this).cache();
        this.localeContextResolver = localeContextResolver;
        this.formDataMono = initFormData(request, codecConfigurer, getLogPrefix());
        this.multipartDataMono = initMultipartData(request, codecConfigurer, getLogPrefix());
        this.applicationContext = applicationContext;
    }

    private static Mono<MultiValueMap<String, String>> initFormData(ServerHttpRequest request, ServerCodecConfigurer configurer, String logPrefix) {
        try {
            MediaType contentType = request.getHeaders().getContentType();
            if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType)) {
                return configurer.getReaders().stream().filter(reader -> {
                    return reader.canRead(FORM_DATA_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
                }).findFirst().orElseThrow(() -> {
                    return new IllegalStateException("No form data HttpMessageReader.");
                }).readMono(FORM_DATA_TYPE, request, Hints.from(Hints.LOG_PREFIX_HINT, logPrefix)).switchIfEmpty(EMPTY_FORM_DATA).cache();
            }
        } catch (InvalidMediaTypeException e) {
        }
        return EMPTY_FORM_DATA;
    }

    private static Mono<MultiValueMap<String, Part>> initMultipartData(ServerHttpRequest request, ServerCodecConfigurer configurer, String logPrefix) {
        try {
            MediaType contentType = request.getHeaders().getContentType();
            if (MediaType.MULTIPART_FORM_DATA.isCompatibleWith(contentType)) {
                return configurer.getReaders().stream().filter(reader -> {
                    return reader.canRead(MULTIPART_DATA_TYPE, MediaType.MULTIPART_FORM_DATA);
                }).findFirst().orElseThrow(() -> {
                    return new IllegalStateException("No multipart HttpMessageReader.");
                }).readMono(MULTIPART_DATA_TYPE, request, Hints.from(Hints.LOG_PREFIX_HINT, logPrefix)).switchIfEmpty(EMPTY_MULTIPART_DATA).cache();
            }
        } catch (InvalidMediaTypeException e) {
        }
        return EMPTY_MULTIPART_DATA;
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public ServerHttpRequest getRequest() {
        return this.request;
    }

    private HttpHeaders getRequestHeaders() {
        return getRequest().getHeaders();
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public ServerHttpResponse getResponse() {
        return this.response;
    }

    private HttpHeaders getResponseHeaders() {
        return getResponse().getHeaders();
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public Mono<WebSession> getSession() {
        return this.sessionMono;
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public <T extends Principal> Mono<T> getPrincipal() {
        return Mono.empty();
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public Mono<MultiValueMap<String, String>> getFormData() {
        return this.formDataMono;
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public Mono<MultiValueMap<String, Part>> getMultipartData() {
        return this.multipartDataMono;
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public LocaleContext getLocaleContext() {
        return this.localeContextResolver.resolveLocaleContext(this);
    }

    @Override // org.springframework.web.server.ServerWebExchange
    @Nullable
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public boolean isNotModified() {
        return this.notModified;
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public boolean checkNotModified(Instant lastModified) {
        return checkNotModified(null, lastModified);
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public boolean checkNotModified(String etag) {
        return checkNotModified(etag, Instant.MIN);
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public boolean checkNotModified(@Nullable String etag, Instant lastModified) {
        HttpStatus status = getResponse().getStatusCode();
        if (this.notModified || (status != null && !HttpStatus.OK.equals(status))) {
            return this.notModified;
        }
        if (validateIfUnmodifiedSince(lastModified)) {
            if (this.notModified) {
                getResponse().setStatusCode(HttpStatus.PRECONDITION_FAILED);
            }
            return this.notModified;
        }
        boolean validated = validateIfNoneMatch(etag);
        if (!validated) {
            validateIfModifiedSince(lastModified);
        }
        boolean isHttpGetOrHead = SAFE_METHODS.contains(getRequest().getMethod());
        if (this.notModified) {
            getResponse().setStatusCode(isHttpGetOrHead ? HttpStatus.NOT_MODIFIED : HttpStatus.PRECONDITION_FAILED);
        }
        if (isHttpGetOrHead) {
            if (lastModified.isAfter(Instant.EPOCH) && getResponseHeaders().getLastModified() == -1) {
                getResponseHeaders().setLastModified(lastModified.toEpochMilli());
            }
            if (StringUtils.hasLength(etag) && getResponseHeaders().getETag() == null) {
                getResponseHeaders().setETag(padEtagIfNecessary(etag));
            }
        }
        return this.notModified;
    }

    private boolean validateIfUnmodifiedSince(Instant lastModified) {
        if (lastModified.isBefore(Instant.EPOCH)) {
            return false;
        }
        long ifUnmodifiedSince = getRequestHeaders().getIfUnmodifiedSince();
        if (ifUnmodifiedSince == -1) {
            return false;
        }
        Instant sinceInstant = Instant.ofEpochMilli(ifUnmodifiedSince);
        this.notModified = sinceInstant.isBefore(lastModified.truncatedTo(ChronoUnit.SECONDS));
        return true;
    }

    private boolean validateIfNoneMatch(@Nullable String etag) {
        if (!StringUtils.hasLength(etag)) {
            return false;
        }
        try {
            List<String> ifNoneMatch = getRequestHeaders().getIfNoneMatch();
            if (ifNoneMatch.isEmpty()) {
                return false;
            }
            String etag2 = padEtagIfNecessary(etag);
            if (etag2.startsWith("W/")) {
                etag2 = etag2.substring(2);
            }
            Iterator<String> it = ifNoneMatch.iterator();
            while (it.hasNext()) {
                String clientEtag = it.next();
                if (StringUtils.hasLength(clientEtag)) {
                    if (clientEtag.startsWith("W/")) {
                        clientEtag = clientEtag.substring(2);
                    }
                    if (clientEtag.equals(etag2)) {
                        this.notModified = true;
                        return true;
                    }
                }
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private String padEtagIfNecessary(String etag) {
        if (!StringUtils.hasLength(etag)) {
            return etag;
        }
        if ((etag.startsWith("\"") || etag.startsWith("W/\"")) && etag.endsWith("\"")) {
            return etag;
        }
        return "\"" + etag + "\"";
    }

    private boolean validateIfModifiedSince(Instant lastModified) {
        if (lastModified.isBefore(Instant.EPOCH)) {
            return false;
        }
        long ifModifiedSince = getRequestHeaders().getIfModifiedSince();
        if (ifModifiedSince == -1) {
            return false;
        }
        this.notModified = ChronoUnit.SECONDS.between(lastModified, Instant.ofEpochMilli(ifModifiedSince)) >= 0;
        return true;
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public String transformUrl(String url) {
        return this.urlTransformer.apply(url);
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public void addUrlTransformer(Function<String, String> transformer) {
        Assert.notNull(transformer, "'encoder' must not be null");
        this.urlTransformer = this.urlTransformer.andThen(transformer);
    }

    @Override // org.springframework.web.server.ServerWebExchange
    public String getLogPrefix() {
        Object value = getAttribute(LOG_ID_ATTRIBUTE);
        if (this.logId != value) {
            this.logId = value;
            this.logPrefix = value != null ? PropertyAccessor.PROPERTY_KEY_PREFIX + value + "] " : "";
        }
        return this.logPrefix;
    }
}