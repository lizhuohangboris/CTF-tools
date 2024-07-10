package org.springframework.web.server.adapter;

import ch.qos.logback.classic.spi.CallerData;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.handler.WebHandlerDecorator;
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver;
import org.springframework.web.server.i18n.LocaleContextResolver;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/server/adapter/HttpWebHandlerAdapter.class */
public class HttpWebHandlerAdapter extends WebHandlerDecorator implements HttpHandler {
    private WebSessionManager sessionManager;
    private ServerCodecConfigurer codecConfigurer;
    private LocaleContextResolver localeContextResolver;
    @Nullable
    private ForwardedHeaderTransformer forwardedHeaderTransformer;
    @Nullable
    private ApplicationContext applicationContext;
    private boolean enableLoggingRequestDetails;
    private static final Set<String> DISCONNECTED_CLIENT_EXCEPTIONS = new HashSet(Arrays.asList("ClientAbortException", "EOFException", "EofException"));
    private static final Log logger = LogFactory.getLog(HttpWebHandlerAdapter.class);
    private static final String DISCONNECTED_CLIENT_LOG_CATEGORY = "org.springframework.web.server.DisconnectedClient";
    private static final Log lostClientLogger = LogFactory.getLog(DISCONNECTED_CLIENT_LOG_CATEGORY);

    public HttpWebHandlerAdapter(WebHandler delegate) {
        super(delegate);
        this.sessionManager = new DefaultWebSessionManager();
        this.codecConfigurer = ServerCodecConfigurer.create();
        this.localeContextResolver = new AcceptHeaderLocaleContextResolver();
        this.enableLoggingRequestDetails = false;
    }

    public void setSessionManager(WebSessionManager sessionManager) {
        Assert.notNull(sessionManager, "WebSessionManager must not be null");
        this.sessionManager = sessionManager;
    }

    public WebSessionManager getSessionManager() {
        return this.sessionManager;
    }

    public void setCodecConfigurer(ServerCodecConfigurer codecConfigurer) {
        Assert.notNull(codecConfigurer, "ServerCodecConfigurer is required");
        this.codecConfigurer = codecConfigurer;
        this.enableLoggingRequestDetails = false;
        Stream<HttpMessageReader<?>> stream = this.codecConfigurer.getReaders().stream();
        LoggingCodecSupport.class.getClass();
        stream.filter((v1) -> {
            return r1.isInstance(v1);
        }).forEach(reader -> {
            if (((LoggingCodecSupport) reader).isEnableLoggingRequestDetails()) {
                this.enableLoggingRequestDetails = true;
            }
        });
    }

    public ServerCodecConfigurer getCodecConfigurer() {
        return this.codecConfigurer;
    }

    public void setLocaleContextResolver(LocaleContextResolver resolver) {
        Assert.notNull(resolver, "LocaleContextResolver is required");
        this.localeContextResolver = resolver;
    }

    public LocaleContextResolver getLocaleContextResolver() {
        return this.localeContextResolver;
    }

    public void setForwardedHeaderTransformer(ForwardedHeaderTransformer transformer) {
        Assert.notNull(transformer, "ForwardedHeaderTransformer is required");
        this.forwardedHeaderTransformer = transformer;
    }

    @Nullable
    public ForwardedHeaderTransformer getForwardedHeaderTransformer() {
        return this.forwardedHeaderTransformer;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Nullable
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    public void afterPropertiesSet() {
        if (logger.isDebugEnabled()) {
            String value = this.enableLoggingRequestDetails ? "shown which may lead to unsafe logging of potentially sensitive data" : "masked to prevent unsafe logging of potentially sensitive data";
            logger.debug("enableLoggingRequestDetails='" + this.enableLoggingRequestDetails + "': form data and headers will be " + value);
        }
    }

    @Override // org.springframework.http.server.reactive.HttpHandler
    public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
        if (this.forwardedHeaderTransformer != null) {
            request = this.forwardedHeaderTransformer.apply(request);
        }
        ServerWebExchange exchange = createExchange(request, response);
        LogFormatUtils.traceDebug(logger, traceOn -> {
            return exchange.getLogPrefix() + formatRequest(exchange.getRequest()) + (traceOn.booleanValue() ? ", headers=" + formatHeaders(exchange.getRequest().getHeaders()) : "");
        });
        Mono onErrorResume = getDelegate().handle(exchange).doOnSuccess(aVoid -> {
            logResponse(exchange);
        }).onErrorResume(ex -> {
            return handleUnresolvedError(exchange, ex);
        });
        response.getClass();
        return onErrorResume.then(Mono.defer(this::setComplete));
    }

    protected ServerWebExchange createExchange(ServerHttpRequest request, ServerHttpResponse response) {
        return new DefaultServerWebExchange(request, response, this.sessionManager, getCodecConfigurer(), getLocaleContextResolver(), this.applicationContext);
    }

    private String formatRequest(ServerHttpRequest request) {
        String rawQuery = request.getURI().getRawQuery();
        String query = StringUtils.hasText(rawQuery) ? CallerData.NA + rawQuery : "";
        return "HTTP " + request.getMethod() + " \"" + request.getPath() + query + "\"";
    }

    private void logResponse(ServerWebExchange exchange) {
        LogFormatUtils.traceDebug(logger, traceOn -> {
            HttpStatus status = exchange.getResponse().getStatusCode();
            return exchange.getLogPrefix() + "Completed " + (status != null ? status : "200 OK") + (traceOn.booleanValue() ? ", headers=" + formatHeaders(exchange.getResponse().getHeaders()) : "");
        });
    }

    private String formatHeaders(HttpHeaders responseHeaders) {
        if (this.enableLoggingRequestDetails) {
            return responseHeaders.toString();
        }
        return responseHeaders.isEmpty() ? "{}" : "{masked}";
    }

    private Mono<Void> handleUnresolvedError(ServerWebExchange exchange, Throwable ex) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String logPrefix = exchange.getLogPrefix();
        if (isDisconnectedClientError(ex)) {
            if (lostClientLogger.isTraceEnabled()) {
                lostClientLogger.trace(logPrefix + "Client went away", ex);
            } else if (lostClientLogger.isDebugEnabled()) {
                lostClientLogger.debug(logPrefix + "Client went away: " + ex + " (stacktrace at TRACE level for '" + DISCONNECTED_CLIENT_LOG_CATEGORY + "')");
            }
            return Mono.empty();
        } else if (response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)) {
            logger.error(logPrefix + "500 Server Error for " + formatRequest(request), ex);
            return Mono.empty();
        } else {
            logger.error(logPrefix + "Error [" + ex + "] for " + formatRequest(request) + ", but ServerHttpResponse already committed (" + response.getStatusCode() + ")");
            return Mono.error(ex);
        }
    }

    private boolean isDisconnectedClientError(Throwable ex) {
        String message = NestedExceptionUtils.getMostSpecificCause(ex).getMessage();
        String message2 = message != null ? message.toLowerCase() : "";
        String className = ex.getClass().getSimpleName();
        return message2.contains("broken pipe") || DISCONNECTED_CLIENT_EXCEPTIONS.contains(className);
    }
}