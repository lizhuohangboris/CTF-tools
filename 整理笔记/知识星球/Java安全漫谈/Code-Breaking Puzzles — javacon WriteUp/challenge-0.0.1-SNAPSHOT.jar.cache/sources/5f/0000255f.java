package org.springframework.web.server.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.handler.ExceptionHandlingWebHandler;
import org.springframework.web.server.handler.FilteringWebHandler;
import org.springframework.web.server.i18n.LocaleContextResolver;
import org.springframework.web.server.session.WebSessionManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/server/adapter/WebHttpHandlerBuilder.class */
public final class WebHttpHandlerBuilder {
    public static final String WEB_HANDLER_BEAN_NAME = "webHandler";
    public static final String WEB_SESSION_MANAGER_BEAN_NAME = "webSessionManager";
    public static final String SERVER_CODEC_CONFIGURER_BEAN_NAME = "serverCodecConfigurer";
    public static final String LOCALE_CONTEXT_RESOLVER_BEAN_NAME = "localeContextResolver";
    public static final String FORWARDED_HEADER_TRANSFORMER_BEAN_NAME = "forwardedHeaderTransformer";
    private final WebHandler webHandler;
    @Nullable
    private final ApplicationContext applicationContext;
    private final List<WebFilter> filters = new ArrayList();
    private final List<WebExceptionHandler> exceptionHandlers = new ArrayList();
    @Nullable
    private WebSessionManager sessionManager;
    @Nullable
    private ServerCodecConfigurer codecConfigurer;
    @Nullable
    private LocaleContextResolver localeContextResolver;
    @Nullable
    private ForwardedHeaderTransformer forwardedHeaderTransformer;

    private WebHttpHandlerBuilder(WebHandler webHandler, @Nullable ApplicationContext applicationContext) {
        Assert.notNull(webHandler, "WebHandler must not be null");
        this.webHandler = webHandler;
        this.applicationContext = applicationContext;
    }

    private WebHttpHandlerBuilder(WebHttpHandlerBuilder other) {
        this.webHandler = other.webHandler;
        this.applicationContext = other.applicationContext;
        this.filters.addAll(other.filters);
        this.exceptionHandlers.addAll(other.exceptionHandlers);
        this.sessionManager = other.sessionManager;
        this.codecConfigurer = other.codecConfigurer;
        this.localeContextResolver = other.localeContextResolver;
        this.forwardedHeaderTransformer = other.forwardedHeaderTransformer;
    }

    public static WebHttpHandlerBuilder webHandler(WebHandler webHandler) {
        return new WebHttpHandlerBuilder(webHandler, null);
    }

    public static WebHttpHandlerBuilder applicationContext(ApplicationContext context) {
        WebHttpHandlerBuilder builder = new WebHttpHandlerBuilder((WebHandler) context.getBean(WEB_HANDLER_BEAN_NAME, WebHandler.class), context);
        List<WebFilter> webFilters = (List) context.getBeanProvider(WebFilter.class).orderedStream().collect(Collectors.toList());
        builder.filters(filters -> {
            filters.addAll(webFilters);
        });
        List<WebExceptionHandler> exceptionHandlers = (List) context.getBeanProvider(WebExceptionHandler.class).orderedStream().collect(Collectors.toList());
        builder.exceptionHandlers(handlers -> {
            handlers.addAll(exceptionHandlers);
        });
        try {
            builder.sessionManager((WebSessionManager) context.getBean(WEB_SESSION_MANAGER_BEAN_NAME, WebSessionManager.class));
        } catch (NoSuchBeanDefinitionException e) {
        }
        try {
            builder.codecConfigurer((ServerCodecConfigurer) context.getBean(SERVER_CODEC_CONFIGURER_BEAN_NAME, ServerCodecConfigurer.class));
        } catch (NoSuchBeanDefinitionException e2) {
        }
        try {
            builder.localeContextResolver((LocaleContextResolver) context.getBean(LOCALE_CONTEXT_RESOLVER_BEAN_NAME, LocaleContextResolver.class));
        } catch (NoSuchBeanDefinitionException e3) {
        }
        try {
            builder.localeContextResolver((LocaleContextResolver) context.getBean(LOCALE_CONTEXT_RESOLVER_BEAN_NAME, LocaleContextResolver.class));
        } catch (NoSuchBeanDefinitionException e4) {
        }
        try {
            builder.forwardedHeaderTransformer((ForwardedHeaderTransformer) context.getBean(FORWARDED_HEADER_TRANSFORMER_BEAN_NAME, ForwardedHeaderTransformer.class));
        } catch (NoSuchBeanDefinitionException e5) {
        }
        return builder;
    }

    public WebHttpHandlerBuilder filter(WebFilter... filters) {
        if (!ObjectUtils.isEmpty((Object[]) filters)) {
            this.filters.addAll(Arrays.asList(filters));
            updateFilters();
        }
        return this;
    }

    public WebHttpHandlerBuilder filters(Consumer<List<WebFilter>> consumer) {
        consumer.accept(this.filters);
        updateFilters();
        return this;
    }

    private void updateFilters() {
        if (this.filters.isEmpty()) {
            return;
        }
        List<WebFilter> filtersToUse = (List) this.filters.stream().peek(filter -> {
            if ((filter instanceof ForwardedHeaderTransformer) && this.forwardedHeaderTransformer == null) {
                this.forwardedHeaderTransformer = (ForwardedHeaderTransformer) filter;
            }
        }).filter(filter2 -> {
            return !(filter2 instanceof ForwardedHeaderTransformer);
        }).collect(Collectors.toList());
        this.filters.clear();
        this.filters.addAll(filtersToUse);
    }

    public WebHttpHandlerBuilder exceptionHandler(WebExceptionHandler... handlers) {
        if (!ObjectUtils.isEmpty((Object[]) handlers)) {
            this.exceptionHandlers.addAll(Arrays.asList(handlers));
        }
        return this;
    }

    public WebHttpHandlerBuilder exceptionHandlers(Consumer<List<WebExceptionHandler>> consumer) {
        consumer.accept(this.exceptionHandlers);
        return this;
    }

    public WebHttpHandlerBuilder sessionManager(WebSessionManager manager) {
        this.sessionManager = manager;
        return this;
    }

    public boolean hasSessionManager() {
        return this.sessionManager != null;
    }

    public WebHttpHandlerBuilder codecConfigurer(ServerCodecConfigurer codecConfigurer) {
        this.codecConfigurer = codecConfigurer;
        return this;
    }

    public boolean hasCodecConfigurer() {
        return this.codecConfigurer != null;
    }

    public WebHttpHandlerBuilder localeContextResolver(LocaleContextResolver localeContextResolver) {
        this.localeContextResolver = localeContextResolver;
        return this;
    }

    public boolean hasLocaleContextResolver() {
        return this.localeContextResolver != null;
    }

    public WebHttpHandlerBuilder forwardedHeaderTransformer(ForwardedHeaderTransformer transformer) {
        this.forwardedHeaderTransformer = transformer;
        return this;
    }

    public boolean hasForwardedHeaderTransformer() {
        return this.forwardedHeaderTransformer != null;
    }

    public HttpHandler build() {
        WebHandler decorated = new FilteringWebHandler(this.webHandler, this.filters);
        HttpWebHandlerAdapter adapted = new HttpWebHandlerAdapter(new ExceptionHandlingWebHandler(decorated, this.exceptionHandlers));
        if (this.sessionManager != null) {
            adapted.setSessionManager(this.sessionManager);
        }
        if (this.codecConfigurer != null) {
            adapted.setCodecConfigurer(this.codecConfigurer);
        }
        if (this.localeContextResolver != null) {
            adapted.setLocaleContextResolver(this.localeContextResolver);
        }
        if (this.forwardedHeaderTransformer != null) {
            adapted.setForwardedHeaderTransformer(this.forwardedHeaderTransformer);
        }
        if (this.applicationContext != null) {
            adapted.setApplicationContext(this.applicationContext);
        }
        adapted.afterPropertiesSet();
        return adapted;
    }

    /* renamed from: clone */
    public WebHttpHandlerBuilder m1826clone() {
        return new WebHttpHandlerBuilder(this);
    }
}