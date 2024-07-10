package org.springframework.boot.web.reactive.error;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.tags.BindTag;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/reactive/error/DefaultErrorAttributes.class */
public class DefaultErrorAttributes implements ErrorAttributes {
    private static final String ERROR_ATTRIBUTE = DefaultErrorAttributes.class.getName() + ".ERROR";
    private final boolean includeException;

    public DefaultErrorAttributes() {
        this(false);
    }

    public DefaultErrorAttributes(boolean includeException) {
        this.includeException = includeException;
    }

    @Override // org.springframework.boot.web.reactive.error.ErrorAttributes
    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        errorAttributes.put("timestamp", new Date());
        errorAttributes.put("path", request.path());
        Throwable error = getError(request);
        HttpStatus errorStatus = determineHttpStatus(error);
        errorAttributes.put(BindTag.STATUS_VARIABLE_NAME, Integer.valueOf(errorStatus.value()));
        errorAttributes.put("error", errorStatus.getReasonPhrase());
        errorAttributes.put(ConstraintHelper.MESSAGE, determineMessage(error));
        handleException(errorAttributes, determineException(error), includeStackTrace);
        return errorAttributes;
    }

    private HttpStatus determineHttpStatus(Throwable error) {
        if (error instanceof ResponseStatusException) {
            return ((ResponseStatusException) error).getStatus();
        }
        ResponseStatus responseStatus = (ResponseStatus) AnnotatedElementUtils.findMergedAnnotation(error.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            return responseStatus.code();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String determineMessage(Throwable error) {
        if (error instanceof WebExchangeBindException) {
            return error.getMessage();
        }
        if (error instanceof ResponseStatusException) {
            return ((ResponseStatusException) error).getReason();
        }
        ResponseStatus responseStatus = (ResponseStatus) AnnotatedElementUtils.findMergedAnnotation(error.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            return responseStatus.reason();
        }
        return error.getMessage();
    }

    private Throwable determineException(Throwable error) {
        if (error instanceof ResponseStatusException) {
            return error.getCause() != null ? error.getCause() : error;
        }
        return error;
    }

    private void addStackTrace(Map<String, Object> errorAttributes, Throwable error) {
        StringWriter stackTrace = new StringWriter();
        error.printStackTrace(new PrintWriter(stackTrace));
        stackTrace.flush();
        errorAttributes.put("trace", stackTrace.toString());
    }

    private void handleException(Map<String, Object> errorAttributes, Throwable error, boolean includeStackTrace) {
        if (this.includeException) {
            errorAttributes.put(SimpleMappingExceptionResolver.DEFAULT_EXCEPTION_ATTRIBUTE, error.getClass().getName());
        }
        if (includeStackTrace) {
            addStackTrace(errorAttributes, error);
        }
        if (error instanceof BindingResult) {
            BindingResult result = (BindingResult) error;
            if (result.hasErrors()) {
                errorAttributes.put("errors", result.getAllErrors());
            }
        }
    }

    @Override // org.springframework.boot.web.reactive.error.ErrorAttributes
    public Throwable getError(ServerRequest request) {
        return (Throwable) request.attribute(ERROR_ATTRIBUTE).orElseThrow(() -> {
            return new IllegalStateException("Missing exception attribute in ServerWebExchange");
        });
    }

    @Override // org.springframework.boot.web.reactive.error.ErrorAttributes
    public void storeErrorInformation(Throwable error, ServerWebExchange exchange) {
        exchange.getAttributes().putIfAbsent(ERROR_ATTRIBUTE, error);
    }
}