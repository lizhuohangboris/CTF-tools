package org.springframework.boot.autoconfigure.web.reactive.error;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.servlet.tags.BindTag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/reactive/error/DefaultErrorWebExceptionHandler.class */
public class DefaultErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {
    private static final Map<HttpStatus.Series, String> SERIES_VIEWS;
    private static final Log logger = HttpLogging.forLogName(DefaultErrorWebExceptionHandler.class);
    private final ErrorProperties errorProperties;

    static {
        Map<HttpStatus.Series, String> views = new EnumMap<>(HttpStatus.Series.class);
        views.put(HttpStatus.Series.CLIENT_ERROR, "4xx");
        views.put(HttpStatus.Series.SERVER_ERROR, "5xx");
        SERIES_VIEWS = Collections.unmodifiableMap(views);
    }

    public DefaultErrorWebExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, applicationContext);
        this.errorProperties = errorProperties;
    }

    @Override // org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(acceptsTextHtml(), this::renderErrorView).andRoute(RequestPredicates.all(), this::renderErrorResponse);
    }

    protected Mono<ServerResponse> renderErrorView(ServerRequest request) {
        Mono<ServerResponse> error;
        boolean includeStackTrace = isIncludeStackTrace(request, MediaType.TEXT_HTML);
        Map<String, Object> error2 = getErrorAttributes(request, includeStackTrace);
        HttpStatus errorStatus = getHttpStatus(error2);
        ServerResponse.BodyBuilder responseBody = ServerResponse.status(errorStatus).contentType(MediaType.TEXT_HTML);
        Flux flatMap = Flux.just(new String[]{"error/" + errorStatus.toString(), "error/" + SERIES_VIEWS.get(errorStatus.series()), "error/error"}).flatMap(viewName -> {
            return renderErrorView(viewName, responseBody, error2);
        });
        if (this.errorProperties.getWhitelabel().isEnabled()) {
            error = renderDefaultErrorView(responseBody, error2);
        } else {
            error = Mono.error(getError(request));
        }
        return flatMap.switchIfEmpty(error).next().doOnNext(response -> {
            logError(request, errorStatus);
        });
    }

    protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        boolean includeStackTrace = isIncludeStackTrace(request, MediaType.ALL);
        Map<String, Object> error = getErrorAttributes(request, includeStackTrace);
        HttpStatus errorStatus = getHttpStatus(error);
        return ServerResponse.status(getHttpStatus(error)).contentType(MediaType.APPLICATION_JSON_UTF8).body(BodyInserters.fromObject(error)).doOnNext(resp -> {
            logError(request, errorStatus);
        });
    }

    protected boolean isIncludeStackTrace(ServerRequest request, MediaType produces) {
        ErrorProperties.IncludeStacktrace include = this.errorProperties.getIncludeStacktrace();
        if (include == ErrorProperties.IncludeStacktrace.ALWAYS) {
            return true;
        }
        if (include == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM) {
            return isTraceEnabled(request);
        }
        return false;
    }

    protected HttpStatus getHttpStatus(Map<String, Object> errorAttributes) {
        int statusCode = ((Integer) errorAttributes.get(BindTag.STATUS_VARIABLE_NAME)).intValue();
        return HttpStatus.valueOf(statusCode);
    }

    protected RequestPredicate acceptsTextHtml() {
        return serverRequest -> {
            try {
                List<MediaType> acceptedMediaTypes = serverRequest.headers().accept();
                acceptedMediaTypes.remove(MediaType.ALL);
                MediaType.sortBySpecificityAndQuality(acceptedMediaTypes);
                Stream<MediaType> stream = acceptedMediaTypes.stream();
                MediaType mediaType = MediaType.TEXT_HTML;
                mediaType.getClass();
                return stream.anyMatch(this::isCompatibleWith);
            } catch (InvalidMediaTypeException e) {
                return false;
            }
        };
    }

    protected void logError(ServerRequest request, HttpStatus errorStatus) {
        Throwable ex = getError(request);
        if (logger.isDebugEnabled()) {
            logger.debug(request.exchange().getLogPrefix() + formatError(ex, request));
        }
    }

    private String formatError(Throwable ex, ServerRequest request) {
        String reason = ex.getClass().getSimpleName() + ": " + ex.getMessage();
        return "Resolved [" + reason + "] for HTTP " + request.methodName() + " " + request.path();
    }
}