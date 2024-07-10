package org.springframework.web.servlet.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsProcessor;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.DefaultCorsProcessor;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UrlPathHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/handler/AbstractHandlerMapping.class */
public abstract class AbstractHandlerMapping extends WebApplicationObjectSupport implements HandlerMapping, Ordered, BeanNameAware {
    @Nullable
    private Object defaultHandler;
    private UrlPathHelper urlPathHelper = new UrlPathHelper();
    private PathMatcher pathMatcher = new AntPathMatcher();
    private final List<Object> interceptors = new ArrayList();
    private final List<HandlerInterceptor> adaptedInterceptors = new ArrayList();
    private CorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
    private CorsProcessor corsProcessor = new DefaultCorsProcessor();
    private int order = Integer.MAX_VALUE;
    @Nullable
    private String beanName;

    @Nullable
    protected abstract Object getHandlerInternal(HttpServletRequest httpServletRequest) throws Exception;

    public void setDefaultHandler(@Nullable Object defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

    @Nullable
    public Object getDefaultHandler() {
        return this.defaultHandler;
    }

    public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
        this.urlPathHelper.setAlwaysUseFullPath(alwaysUseFullPath);
        if (this.corsConfigurationSource instanceof UrlBasedCorsConfigurationSource) {
            ((UrlBasedCorsConfigurationSource) this.corsConfigurationSource).setAlwaysUseFullPath(alwaysUseFullPath);
        }
    }

    public void setUrlDecode(boolean urlDecode) {
        this.urlPathHelper.setUrlDecode(urlDecode);
        if (this.corsConfigurationSource instanceof UrlBasedCorsConfigurationSource) {
            ((UrlBasedCorsConfigurationSource) this.corsConfigurationSource).setUrlDecode(urlDecode);
        }
    }

    public void setRemoveSemicolonContent(boolean removeSemicolonContent) {
        this.urlPathHelper.setRemoveSemicolonContent(removeSemicolonContent);
        if (this.corsConfigurationSource instanceof UrlBasedCorsConfigurationSource) {
            ((UrlBasedCorsConfigurationSource) this.corsConfigurationSource).setRemoveSemicolonContent(removeSemicolonContent);
        }
    }

    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        Assert.notNull(urlPathHelper, "UrlPathHelper must not be null");
        this.urlPathHelper = urlPathHelper;
        if (this.corsConfigurationSource instanceof UrlBasedCorsConfigurationSource) {
            ((UrlBasedCorsConfigurationSource) this.corsConfigurationSource).setUrlPathHelper(urlPathHelper);
        }
    }

    public UrlPathHelper getUrlPathHelper() {
        return this.urlPathHelper;
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        Assert.notNull(pathMatcher, "PathMatcher must not be null");
        this.pathMatcher = pathMatcher;
        if (this.corsConfigurationSource instanceof UrlBasedCorsConfigurationSource) {
            ((UrlBasedCorsConfigurationSource) this.corsConfigurationSource).setPathMatcher(pathMatcher);
        }
    }

    public PathMatcher getPathMatcher() {
        return this.pathMatcher;
    }

    public void setInterceptors(Object... interceptors) {
        this.interceptors.addAll(Arrays.asList(interceptors));
    }

    public void setCorsConfigurations(Map<String, CorsConfiguration> corsConfigurations) {
        Assert.notNull(corsConfigurations, "corsConfigurations must not be null");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.setCorsConfigurations(corsConfigurations);
        source.setPathMatcher(this.pathMatcher);
        source.setUrlPathHelper(this.urlPathHelper);
        this.corsConfigurationSource = source;
    }

    public void setCorsConfigurationSource(CorsConfigurationSource corsConfigurationSource) {
        Assert.notNull(corsConfigurationSource, "corsConfigurationSource must not be null");
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Deprecated
    public Map<String, CorsConfiguration> getCorsConfigurations() {
        if (this.corsConfigurationSource instanceof UrlBasedCorsConfigurationSource) {
            return ((UrlBasedCorsConfigurationSource) this.corsConfigurationSource).getCorsConfigurations();
        }
        throw new IllegalStateException("No CORS configurations available when the source is not an UrlBasedCorsConfigurationSource");
    }

    public void setCorsProcessor(CorsProcessor corsProcessor) {
        Assert.notNull(corsProcessor, "CorsProcessor must not be null");
        this.corsProcessor = corsProcessor;
    }

    public CorsProcessor getCorsProcessor() {
        return this.corsProcessor;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(String name) {
        this.beanName = name;
    }

    public String formatMappingName() {
        return this.beanName != null ? "'" + this.beanName + "'" : "<unknown>";
    }

    @Override // org.springframework.context.support.ApplicationObjectSupport
    public void initApplicationContext() throws BeansException {
        extendInterceptors(this.interceptors);
        detectMappedInterceptors(this.adaptedInterceptors);
        initInterceptors();
    }

    protected void extendInterceptors(List<Object> interceptors) {
    }

    protected void detectMappedInterceptors(List<HandlerInterceptor> mappedInterceptors) {
        mappedInterceptors.addAll(BeanFactoryUtils.beansOfTypeIncludingAncestors(obtainApplicationContext(), MappedInterceptor.class, true, false).values());
    }

    protected void initInterceptors() {
        if (!this.interceptors.isEmpty()) {
            for (int i = 0; i < this.interceptors.size(); i++) {
                Object interceptor = this.interceptors.get(i);
                if (interceptor == null) {
                    throw new IllegalArgumentException("Entry number " + i + " in interceptors array is null");
                }
                this.adaptedInterceptors.add(adaptInterceptor(interceptor));
            }
        }
    }

    protected HandlerInterceptor adaptInterceptor(Object interceptor) {
        if (interceptor instanceof HandlerInterceptor) {
            return (HandlerInterceptor) interceptor;
        }
        if (interceptor instanceof WebRequestInterceptor) {
            return new WebRequestHandlerInterceptorAdapter((WebRequestInterceptor) interceptor);
        }
        throw new IllegalArgumentException("Interceptor type not supported: " + interceptor.getClass().getName());
    }

    @Nullable
    protected final HandlerInterceptor[] getAdaptedInterceptors() {
        if (this.adaptedInterceptors.isEmpty()) {
            return null;
        }
        return (HandlerInterceptor[]) this.adaptedInterceptors.toArray(new HandlerInterceptor[0]);
    }

    @Nullable
    protected final MappedInterceptor[] getMappedInterceptors() {
        List<MappedInterceptor> mappedInterceptors = new ArrayList<>(this.adaptedInterceptors.size());
        for (HandlerInterceptor interceptor : this.adaptedInterceptors) {
            if (interceptor instanceof MappedInterceptor) {
                mappedInterceptors.add((MappedInterceptor) interceptor);
            }
        }
        if (mappedInterceptors.isEmpty()) {
            return null;
        }
        return (MappedInterceptor[]) mappedInterceptors.toArray(new MappedInterceptor[0]);
    }

    @Override // org.springframework.web.servlet.HandlerMapping
    @Nullable
    public final HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        Object handler = getHandlerInternal(request);
        if (handler == null) {
            handler = getDefaultHandler();
        }
        if (handler == null) {
            return null;
        }
        if (handler instanceof String) {
            String handlerName = (String) handler;
            handler = obtainApplicationContext().getBean(handlerName);
        }
        HandlerExecutionChain executionChain = getHandlerExecutionChain(handler, request);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Mapped to " + handler);
        } else if (this.logger.isDebugEnabled() && !request.getDispatcherType().equals(DispatcherType.ASYNC)) {
            this.logger.debug("Mapped to " + executionChain.getHandler());
        }
        if (CorsUtils.isCorsRequest(request)) {
            CorsConfiguration globalConfig = this.corsConfigurationSource.getCorsConfiguration(request);
            CorsConfiguration handlerConfig = getCorsConfiguration(handler, request);
            CorsConfiguration config = globalConfig != null ? globalConfig.combine(handlerConfig) : handlerConfig;
            executionChain = getCorsHandlerExecutionChain(request, executionChain, config);
        }
        return executionChain;
    }

    protected HandlerExecutionChain getHandlerExecutionChain(Object handler, HttpServletRequest request) {
        HandlerExecutionChain chain = handler instanceof HandlerExecutionChain ? (HandlerExecutionChain) handler : new HandlerExecutionChain(handler);
        String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);
        for (HandlerInterceptor interceptor : this.adaptedInterceptors) {
            if (interceptor instanceof MappedInterceptor) {
                MappedInterceptor mappedInterceptor = (MappedInterceptor) interceptor;
                if (mappedInterceptor.matches(lookupPath, this.pathMatcher)) {
                    chain.addInterceptor(mappedInterceptor.getInterceptor());
                }
            } else {
                chain.addInterceptor(interceptor);
            }
        }
        return chain;
    }

    @Nullable
    public CorsConfiguration getCorsConfiguration(Object handler, HttpServletRequest request) {
        Object resolvedHandler = handler;
        if (handler instanceof HandlerExecutionChain) {
            resolvedHandler = ((HandlerExecutionChain) handler).getHandler();
        }
        if (resolvedHandler instanceof CorsConfigurationSource) {
            return ((CorsConfigurationSource) resolvedHandler).getCorsConfiguration(request);
        }
        return null;
    }

    protected HandlerExecutionChain getCorsHandlerExecutionChain(HttpServletRequest request, HandlerExecutionChain chain, @Nullable CorsConfiguration config) {
        if (CorsUtils.isPreFlightRequest(request)) {
            HandlerInterceptor[] interceptors = chain.getInterceptors();
            chain = new HandlerExecutionChain(new PreFlightHandler(config), interceptors);
        } else {
            chain.addInterceptor(new CorsInterceptor(config));
        }
        return chain;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/handler/AbstractHandlerMapping$PreFlightHandler.class */
    public class PreFlightHandler implements HttpRequestHandler, CorsConfigurationSource {
        @Nullable
        private final CorsConfiguration config;

        public PreFlightHandler(@Nullable CorsConfiguration config) {
            AbstractHandlerMapping.this = r4;
            this.config = config;
        }

        @Override // org.springframework.web.HttpRequestHandler
        public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
            AbstractHandlerMapping.this.corsProcessor.processRequest(this.config, request, response);
        }

        @Override // org.springframework.web.cors.CorsConfigurationSource
        @Nullable
        public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
            return this.config;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/handler/AbstractHandlerMapping$CorsInterceptor.class */
    public class CorsInterceptor extends HandlerInterceptorAdapter implements CorsConfigurationSource {
        @Nullable
        private final CorsConfiguration config;

        public CorsInterceptor(@Nullable CorsConfiguration config) {
            AbstractHandlerMapping.this = r4;
            this.config = config;
        }

        @Override // org.springframework.web.servlet.handler.HandlerInterceptorAdapter, org.springframework.web.servlet.HandlerInterceptor
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            return AbstractHandlerMapping.this.corsProcessor.processRequest(this.config, request, response);
        }

        @Override // org.springframework.web.cors.CorsConfigurationSource
        @Nullable
        public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
            return this.config;
        }
    }
}