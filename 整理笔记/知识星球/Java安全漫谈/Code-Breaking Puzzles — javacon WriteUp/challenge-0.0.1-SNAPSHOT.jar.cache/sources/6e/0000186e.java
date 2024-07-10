package org.springframework.boot.autoconfigure.web.reactive.error;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProviders;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.servlet.tags.BindTag;
import org.springframework.web.util.HtmlUtils;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/reactive/error/AbstractErrorWebExceptionHandler.class */
public abstract class AbstractErrorWebExceptionHandler implements ErrorWebExceptionHandler, InitializingBean {
    private final ApplicationContext applicationContext;
    private final ErrorAttributes errorAttributes;
    private final ResourceProperties resourceProperties;
    private final TemplateAvailabilityProviders templateAvailabilityProviders;
    private List<HttpMessageReader<?>> messageReaders = Collections.emptyList();
    private List<HttpMessageWriter<?>> messageWriters = Collections.emptyList();
    private List<ViewResolver> viewResolvers = Collections.emptyList();

    protected abstract RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes);

    public AbstractErrorWebExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ApplicationContext applicationContext) {
        Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
        Assert.notNull(resourceProperties, "ResourceProperties must not be null");
        Assert.notNull(applicationContext, "ApplicationContext must not be null");
        this.errorAttributes = errorAttributes;
        this.resourceProperties = resourceProperties;
        this.applicationContext = applicationContext;
        this.templateAvailabilityProviders = new TemplateAvailabilityProviders(applicationContext);
    }

    public void setMessageWriters(List<HttpMessageWriter<?>> messageWriters) {
        Assert.notNull(messageWriters, "'messageWriters' must not be null");
        this.messageWriters = messageWriters;
    }

    public void setMessageReaders(List<HttpMessageReader<?>> messageReaders) {
        Assert.notNull(messageReaders, "'messageReaders' must not be null");
        this.messageReaders = messageReaders;
    }

    public void setViewResolvers(List<ViewResolver> viewResolvers) {
        this.viewResolvers = viewResolvers;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        return this.errorAttributes.getErrorAttributes(request, includeStackTrace);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Throwable getError(ServerRequest request) {
        return this.errorAttributes.getError(request);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isTraceEnabled(ServerRequest request) {
        String parameter = (String) request.queryParam("trace").orElse("false");
        return !"false".equalsIgnoreCase(parameter);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Mono<ServerResponse> renderErrorView(String viewName, ServerResponse.BodyBuilder responseBody, Map<String, Object> error) {
        if (isTemplateAvailable(viewName)) {
            return responseBody.render(viewName, error);
        }
        Resource resource = resolveResource(viewName);
        if (resource != null) {
            return responseBody.body(BodyInserters.fromResource(resource));
        }
        return Mono.empty();
    }

    private boolean isTemplateAvailable(String viewName) {
        return this.templateAvailabilityProviders.getProvider(viewName, this.applicationContext) != null;
    }

    private Resource resolveResource(String viewName) {
        String[] staticLocations;
        Resource resource;
        for (String location : this.resourceProperties.getStaticLocations()) {
            try {
                resource = this.applicationContext.getResource(location).createRelative(viewName + ThymeleafProperties.DEFAULT_SUFFIX);
            } catch (Exception e) {
            }
            if (resource.exists()) {
                return resource;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Mono<ServerResponse> renderDefaultErrorView(ServerResponse.BodyBuilder responseBody, Map<String, Object> error) {
        StringBuilder builder = new StringBuilder();
        Date timestamp = (Date) error.get("timestamp");
        Object message = error.get(ConstraintHelper.MESSAGE);
        Object trace = error.get("trace");
        builder.append("<html><body><h1>Whitelabel Error Page</h1>").append("<p>This application has no configured error view, so you are seeing this as a fallback.</p>").append("<div id='created'>").append(timestamp).append("</div>").append("<div>There was an unexpected error (type=").append(htmlEscape(error.get("error"))).append(", status=").append(htmlEscape(error.get(BindTag.STATUS_VARIABLE_NAME))).append(").</div>");
        if (message != null) {
            builder.append("<div>").append(htmlEscape(message)).append("</div>");
        }
        if (trace != null) {
            builder.append("<div>").append(htmlEscape(trace)).append("</div>");
        }
        builder.append("</body></html>");
        return responseBody.syncBody(builder.toString());
    }

    private String htmlEscape(Object input) {
        if (input != null) {
            return HtmlUtils.htmlEscape(input.toString());
        }
        return null;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        if (CollectionUtils.isEmpty(this.messageWriters)) {
            throw new IllegalArgumentException("Property 'messageWriters' is required");
        }
    }

    @Override // org.springframework.web.server.WebExceptionHandler
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(throwable);
        }
        this.errorAttributes.storeErrorInformation(throwable, exchange);
        ServerRequest request = ServerRequest.create(exchange, this.messageReaders);
        return getRoutingFunction(this.errorAttributes).route(request).switchIfEmpty(Mono.error(throwable)).flatMap(handler -> {
            return handler.handle(request);
        }).flatMap(response -> {
            return write(exchange, response);
        });
    }

    private Mono<? extends Void> write(ServerWebExchange exchange, ServerResponse response) {
        exchange.getResponse().getHeaders().setContentType(response.headers().getContentType());
        return response.writeTo(exchange, new ResponseContext());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/reactive/error/AbstractErrorWebExceptionHandler$ResponseContext.class */
    public class ResponseContext implements ServerResponse.Context {
        private ResponseContext() {
        }

        public List<HttpMessageWriter<?>> messageWriters() {
            return AbstractErrorWebExceptionHandler.this.messageWriters;
        }

        public List<ViewResolver> viewResolvers() {
            return AbstractErrorWebExceptionHandler.this.viewResolvers;
        }
    }
}