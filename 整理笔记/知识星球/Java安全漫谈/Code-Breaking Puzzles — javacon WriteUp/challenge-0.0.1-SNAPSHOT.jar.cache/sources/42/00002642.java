package org.springframework.web.servlet.mvc.method.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringValueResolver;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.MatchableHandlerMapping;
import org.springframework.web.servlet.handler.RequestMatchResult;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerMapping.class */
public class RequestMappingHandlerMapping extends RequestMappingInfoHandlerMapping implements MatchableHandlerMapping, EmbeddedValueResolverAware {
    @Nullable
    private StringValueResolver embeddedValueResolver;
    private boolean useSuffixPatternMatch = true;
    private boolean useRegisteredSuffixPatternMatch = false;
    private boolean useTrailingSlashMatch = true;
    private Map<String, Predicate<Class<?>>> pathPrefixes = new LinkedHashMap();
    private ContentNegotiationManager contentNegotiationManager = new ContentNegotiationManager();
    private RequestMappingInfo.BuilderConfiguration config = new RequestMappingInfo.BuilderConfiguration();

    @Override // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping
    @Nullable
    protected /* bridge */ /* synthetic */ RequestMappingInfo getMappingForMethod(Method method, Class cls) {
        return getMappingForMethod(method, (Class<?>) cls);
    }

    public void setUseSuffixPatternMatch(boolean useSuffixPatternMatch) {
        this.useSuffixPatternMatch = useSuffixPatternMatch;
    }

    public void setUseRegisteredSuffixPatternMatch(boolean useRegisteredSuffixPatternMatch) {
        this.useRegisteredSuffixPatternMatch = useRegisteredSuffixPatternMatch;
        this.useSuffixPatternMatch = useRegisteredSuffixPatternMatch || this.useSuffixPatternMatch;
    }

    public void setUseTrailingSlashMatch(boolean useTrailingSlashMatch) {
        this.useTrailingSlashMatch = useTrailingSlashMatch;
    }

    public void setPathPrefixes(Map<String, Predicate<Class<?>>> prefixes) {
        this.pathPrefixes = Collections.unmodifiableMap(new LinkedHashMap(prefixes));
    }

    public Map<String, Predicate<Class<?>>> getPathPrefixes() {
        return this.pathPrefixes;
    }

    public void setContentNegotiationManager(ContentNegotiationManager contentNegotiationManager) {
        Assert.notNull(contentNegotiationManager, "ContentNegotiationManager must not be null");
        this.contentNegotiationManager = contentNegotiationManager;
    }

    public ContentNegotiationManager getContentNegotiationManager() {
        return this.contentNegotiationManager;
    }

    @Override // org.springframework.context.EmbeddedValueResolverAware
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    @Override // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        this.config = new RequestMappingInfo.BuilderConfiguration();
        this.config.setUrlPathHelper(getUrlPathHelper());
        this.config.setPathMatcher(getPathMatcher());
        this.config.setSuffixPatternMatch(this.useSuffixPatternMatch);
        this.config.setTrailingSlashMatch(this.useTrailingSlashMatch);
        this.config.setRegisteredSuffixPatternMatch(this.useRegisteredSuffixPatternMatch);
        this.config.setContentNegotiationManager(getContentNegotiationManager());
        super.afterPropertiesSet();
    }

    public boolean useSuffixPatternMatch() {
        return this.useSuffixPatternMatch;
    }

    public boolean useRegisteredSuffixPatternMatch() {
        return this.useRegisteredSuffixPatternMatch;
    }

    public boolean useTrailingSlashMatch() {
        return this.useTrailingSlashMatch;
    }

    @Nullable
    public List<String> getFileExtensions() {
        return this.config.getFileExtensions();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping
    public boolean isHandler(Class<?> beanType) {
        return AnnotatedElementUtils.hasAnnotation(beanType, Controller.class) || AnnotatedElementUtils.hasAnnotation(beanType, RequestMapping.class);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping
    @Nullable
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = createRequestMappingInfo(method);
        if (info != null) {
            RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType);
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }
            String prefix = getPathPrefix(handlerType);
            if (prefix != null) {
                info = RequestMappingInfo.paths(prefix).build().combine(info);
            }
        }
        return info;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public String getPathPrefix(Class<?> handlerType) {
        for (Map.Entry<String, Predicate<Class<?>>> entry : this.pathPrefixes.entrySet()) {
            if (entry.getValue().test(handlerType)) {
                String prefix = entry.getKey();
                if (this.embeddedValueResolver != null) {
                    prefix = this.embeddedValueResolver.resolveStringValue(prefix);
                }
                return prefix;
            }
        }
        return null;
    }

    @Nullable
    private RequestMappingInfo createRequestMappingInfo(AnnotatedElement element) {
        RequestMapping requestMapping = (RequestMapping) AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
        RequestCondition<?> condition = element instanceof Class ? getCustomTypeCondition((Class) element) : getCustomMethodCondition((Method) element);
        if (requestMapping != null) {
            return createRequestMappingInfo(requestMapping, condition);
        }
        return null;
    }

    @Nullable
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        return null;
    }

    @Nullable
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        return null;
    }

    protected RequestMappingInfo createRequestMappingInfo(RequestMapping requestMapping, @Nullable RequestCondition<?> customCondition) {
        RequestMappingInfo.Builder builder = RequestMappingInfo.paths(resolveEmbeddedValuesInPatterns(requestMapping.path())).methods(requestMapping.method()).params(requestMapping.params()).headers(requestMapping.headers()).consumes(requestMapping.consumes()).produces(requestMapping.produces()).mappingName(requestMapping.name());
        if (customCondition != null) {
            builder.customCondition(customCondition);
        }
        return builder.options(this.config).build();
    }

    protected String[] resolveEmbeddedValuesInPatterns(String[] patterns) {
        if (this.embeddedValueResolver == null) {
            return patterns;
        }
        String[] resolvedPatterns = new String[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            resolvedPatterns[i] = this.embeddedValueResolver.resolveStringValue(patterns[i]);
        }
        return resolvedPatterns;
    }

    @Override // org.springframework.web.servlet.handler.MatchableHandlerMapping
    public RequestMatchResult match(HttpServletRequest request, String pattern) {
        RequestMappingInfo info = RequestMappingInfo.paths(pattern).options(this.config).build();
        RequestMappingInfo matchingInfo = info.getMatchingCondition(request);
        if (matchingInfo == null) {
            return null;
        }
        Set<String> patterns = matchingInfo.getPatternsCondition().getPatterns();
        String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
        return new RequestMatchResult(patterns.iterator().next(), lookupPath, getPathMatcher());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping
    public CorsConfiguration initCorsConfiguration(Object handler, Method method, RequestMappingInfo mappingInfo) {
        HandlerMethod handlerMethod = createHandlerMethod(handler, method);
        Class<?> beanType = handlerMethod.getBeanType();
        CrossOrigin typeAnnotation = (CrossOrigin) AnnotatedElementUtils.findMergedAnnotation(beanType, CrossOrigin.class);
        CrossOrigin methodAnnotation = (CrossOrigin) AnnotatedElementUtils.findMergedAnnotation(method, CrossOrigin.class);
        if (typeAnnotation == null && methodAnnotation == null) {
            return null;
        }
        CorsConfiguration config = new CorsConfiguration();
        updateCorsConfig(config, typeAnnotation);
        updateCorsConfig(config, methodAnnotation);
        if (CollectionUtils.isEmpty(config.getAllowedMethods())) {
            for (RequestMethod allowedMethod : mappingInfo.getMethodsCondition().getMethods()) {
                config.addAllowedMethod(allowedMethod.name());
            }
        }
        return config.applyPermitDefaultValues();
    }

    private void updateCorsConfig(CorsConfiguration config, @Nullable CrossOrigin annotation) {
        String[] origins;
        RequestMethod[] methods;
        String[] allowedHeaders;
        String[] exposedHeaders;
        if (annotation == null) {
            return;
        }
        for (String origin : annotation.origins()) {
            config.addAllowedOrigin(resolveCorsAnnotationValue(origin));
        }
        for (RequestMethod method : annotation.methods()) {
            config.addAllowedMethod(method.name());
        }
        for (String header : annotation.allowedHeaders()) {
            config.addAllowedHeader(resolveCorsAnnotationValue(header));
        }
        for (String header2 : annotation.exposedHeaders()) {
            config.addExposedHeader(resolveCorsAnnotationValue(header2));
        }
        String allowCredentials = resolveCorsAnnotationValue(annotation.allowCredentials());
        if ("true".equalsIgnoreCase(allowCredentials)) {
            config.setAllowCredentials(true);
        } else if ("false".equalsIgnoreCase(allowCredentials)) {
            config.setAllowCredentials(false);
        } else if (!allowCredentials.isEmpty()) {
            throw new IllegalStateException("@CrossOrigin's allowCredentials value must be \"true\", \"false\", or an empty string (\"\"): current value is [" + allowCredentials + "]");
        }
        if (annotation.maxAge() >= 0 && config.getMaxAge() == null) {
            config.setMaxAge(Long.valueOf(annotation.maxAge()));
        }
    }

    private String resolveCorsAnnotationValue(String value) {
        if (this.embeddedValueResolver != null) {
            String resolved = this.embeddedValueResolver.resolveStringValue(value);
            return resolved != null ? resolved : "";
        }
        return value;
    }
}