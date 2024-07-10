package org.thymeleaf.spring5.view.reactive;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.result.view.AbstractView;
import org.springframework.web.reactive.result.view.RequestContext;
import org.springframework.web.server.ServerWebExchange;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring5.ISpringWebFluxTemplateEngine;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxExpressionContext;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxThymeleafRequestContext;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/view/reactive/ThymeleafReactiveView.class */
public class ThymeleafReactiveView extends AbstractView implements BeanNameAware {
    protected static final Logger logger = LoggerFactory.getLogger(ThymeleafReactiveView.class);
    public static final int DEFAULT_RESPONSE_CHUNK_SIZE_BYTES = Integer.MAX_VALUE;
    public static final String REACTIVE_MODEL_ADDITIONS_EXECUTION_ATTRIBUTE_PREFIX = "ThymeleafReactiveModelAdditions:";
    private static final String WEBFLUX_CONVERSION_SERVICE_NAME = "webFluxConversionService";
    private String beanName = null;
    private ISpringWebFluxTemplateEngine templateEngine = null;
    private String templateName = null;
    private Locale locale = null;
    private Map<String, Object> staticVariables = null;
    private boolean defaultCharsetSet = false;
    private boolean supportedMediaTypesSet = false;
    private Set<String> markupSelectors = null;
    private Integer responseMaxChunkSizeBytes = null;

    public String getMarkupSelector() {
        if (this.markupSelectors == null || this.markupSelectors.size() == 0) {
            return null;
        }
        return this.markupSelectors.iterator().next();
    }

    public void setMarkupSelector(String markupSelector) {
        this.markupSelectors = (markupSelector == null || markupSelector.trim().length() == 0) ? null : Collections.singleton(markupSelector.trim());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isDefaultCharsetSet() {
        return this.defaultCharsetSet;
    }

    public void setDefaultCharset(Charset defaultCharset) {
        super.setDefaultCharset(defaultCharset);
        this.defaultCharsetSet = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isSupportedMediaTypesSet() {
        return this.supportedMediaTypesSet;
    }

    public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
        super.setSupportedMediaTypes(supportedMediaTypes);
        this.supportedMediaTypesSet = true;
    }

    public String getBeanName() {
        return this.beanName;
    }

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Locale getLocale() {
        return this.locale;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public int getResponseMaxChunkSizeBytes() {
        if (this.responseMaxChunkSizeBytes == null) {
            return Integer.MAX_VALUE;
        }
        return this.responseMaxChunkSizeBytes.intValue();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Integer getNullableResponseMaxChunkSize() {
        return this.responseMaxChunkSizeBytes;
    }

    public void setResponseMaxChunkSizeBytes(int responseMaxBufferSizeBytes) {
        this.responseMaxChunkSizeBytes = Integer.valueOf(responseMaxBufferSizeBytes);
    }

    protected ISpringWebFluxTemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setTemplateEngine(ISpringWebFluxTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public Map<String, Object> getStaticVariables() {
        if (this.staticVariables == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(this.staticVariables);
    }

    public void addStaticVariable(String name, Object value) {
        if (this.staticVariables == null) {
            this.staticVariables = new HashMap(3, 1.0f);
        }
        this.staticVariables.put(name, value);
    }

    public void setStaticVariables(Map<String, ?> variables) {
        if (variables != null) {
            if (this.staticVariables == null) {
                this.staticVariables = new HashMap(3, 1.0f);
            }
            this.staticVariables.putAll(variables);
        }
    }

    public Mono<Void> render(Map<String, ?> model, MediaType contentType, ServerWebExchange exchange) {
        ISpringWebFluxTemplateEngine viewTemplateEngine = getTemplateEngine();
        if (viewTemplateEngine == null) {
            return Mono.error(new IllegalArgumentException("Property 'thymeleafTemplateEngine' is required"));
        }
        IEngineConfiguration configuration = viewTemplateEngine.getConfiguration();
        Map<String, Object> executionAttributes = configuration.getExecutionAttributes();
        Map<String, Object> enrichedModel = null;
        for (String executionAttributeName : executionAttributes.keySet()) {
            if (executionAttributeName != null && executionAttributeName.startsWith(REACTIVE_MODEL_ADDITIONS_EXECUTION_ATTRIBUTE_PREFIX)) {
                Object executionAttributeValue = executionAttributes.get(executionAttributeName);
                String modelAttributeName = executionAttributeName.substring(REACTIVE_MODEL_ADDITIONS_EXECUTION_ATTRIBUTE_PREFIX.length());
                Publisher<?> modelAttributeValue = null;
                if (executionAttributeValue != null) {
                    if (executionAttributeValue instanceof Publisher) {
                        modelAttributeValue = (Publisher) executionAttributeValue;
                    } else if (executionAttributeValue instanceof Supplier) {
                        Supplier<Publisher<?>> supplier = (Supplier) executionAttributeValue;
                        modelAttributeValue = supplier.get();
                    } else if (executionAttributeValue instanceof Function) {
                        Function<ServerWebExchange, Publisher<?>> function = (Function) executionAttributeValue;
                        modelAttributeValue = function.apply(exchange);
                    }
                }
                if (enrichedModel == null) {
                    enrichedModel = new LinkedHashMap<>(model);
                }
                enrichedModel.put(modelAttributeName, modelAttributeValue);
            }
        }
        return super.render(enrichedModel != null ? enrichedModel : model, contentType, exchange);
    }

    protected Mono<Void> renderInternal(Map<String, Object> renderAttributes, MediaType contentType, ServerWebExchange exchange) {
        return renderFragmentInternal(this.markupSelectors, renderAttributes, contentType, exchange);
    }

    protected Mono<Void> renderFragmentInternal(Set<String> markupSelectorsToRender, Map<String, Object> renderAttributes, MediaType contentType, ServerWebExchange exchange) {
        String templateName;
        Set<String> markupSelectors;
        Set<String> processMarkupSelectors;
        String viewTemplateName = getTemplateName();
        ISpringWebFluxTemplateEngine viewTemplateEngine = getTemplateEngine();
        if (viewTemplateName == null) {
            return Mono.error(new IllegalArgumentException("Property 'templateName' is required"));
        }
        if (getLocale() == null) {
            return Mono.error(new IllegalArgumentException("Property 'locale' is required"));
        }
        if (viewTemplateEngine == null) {
            return Mono.error(new IllegalArgumentException("Property 'thymeleafTemplateEngine' is required"));
        }
        ServerHttpResponse response = exchange.getResponse();
        Map<String, Object> mergedModel = new HashMap<>(30);
        Map<String, Object> templateStaticVariables = getStaticVariables();
        if (templateStaticVariables != null) {
            mergedModel.putAll(templateStaticVariables);
        }
        Map<String, Object> pathVars = (Map) exchange.getAttributes().get(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVars != null) {
            mergedModel.putAll(pathVars);
        }
        if (renderAttributes != null) {
            mergedModel.putAll(renderAttributes);
        }
        ApplicationContext applicationContext = getApplicationContext();
        RequestContext requestContext = createRequestContext(exchange, mergedModel);
        SpringWebFluxThymeleafRequestContext thymeleafRequestContext = new SpringWebFluxThymeleafRequestContext(requestContext, exchange);
        mergedModel.put(SpringContextVariableNames.SPRING_REQUEST_CONTEXT, requestContext);
        mergedModel.put(SpringContextVariableNames.THYMELEAF_REQUEST_CONTEXT, thymeleafRequestContext);
        ConversionService conversionService = applicationContext.containsBean(WEBFLUX_CONVERSION_SERVICE_NAME) ? (ConversionService) applicationContext.getBean(WEBFLUX_CONVERSION_SERVICE_NAME) : null;
        ThymeleafEvaluationContext evaluationContext = new ThymeleafEvaluationContext(applicationContext, conversionService);
        mergedModel.put(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);
        boolean dataDriven = isDataDriven(mergedModel);
        IEngineConfiguration configuration = viewTemplateEngine.getConfiguration();
        SpringWebFluxExpressionContext context = new SpringWebFluxExpressionContext(configuration, exchange, getReactiveAdapterRegistry(), getLocale(), mergedModel);
        if (!viewTemplateName.contains("::")) {
            templateName = viewTemplateName;
            markupSelectors = null;
        } else {
            IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);
            try {
                FragmentExpression fragmentExpression = (FragmentExpression) parser.parseExpression(context, "~{" + viewTemplateName + "}");
                FragmentExpression.ExecutedFragmentExpression fragment = FragmentExpression.createExecutedFragmentExpression(context, fragmentExpression);
                templateName = FragmentExpression.resolveTemplateName(fragment);
                markupSelectors = FragmentExpression.resolveFragments(fragment);
                Map<String, Object> nameFragmentParameters = fragment.getFragmentParameters();
                if (nameFragmentParameters != null) {
                    if (fragment.hasSyntheticParameters()) {
                        return Mono.error(new IllegalArgumentException("Parameters in a view specification must be named (non-synthetic): '" + viewTemplateName + "'"));
                    }
                    context.setVariables(nameFragmentParameters);
                }
            } catch (TemplateProcessingException e) {
                return Mono.error(new IllegalArgumentException("Invalid template name specification: '" + viewTemplateName + "'"));
            }
        }
        if (markupSelectors != null && markupSelectors.size() > 0) {
            if (markupSelectorsToRender != null && markupSelectorsToRender.size() > 0) {
                return Mono.error(new IllegalArgumentException("A markup selector has been specified (" + Arrays.asList(markupSelectors) + ") for a view that was already being executed as a fragment (" + Arrays.asList(markupSelectorsToRender) + "). Only one fragment selection is allowed."));
            }
            processMarkupSelectors = markupSelectors;
        } else if (markupSelectorsToRender != null && markupSelectorsToRender.size() > 0) {
            processMarkupSelectors = markupSelectorsToRender;
        } else {
            processMarkupSelectors = null;
        }
        int templateResponseMaxChunkSizeBytes = getResponseMaxChunkSizeBytes();
        HttpHeaders responseHeaders = exchange.getResponse().getHeaders();
        Locale templateLocale = getLocale();
        if (templateLocale != null) {
            responseHeaders.setContentLanguage(templateLocale);
        }
        Charset charset = getCharset(contentType).orElse(getDefaultCharset());
        Publisher<DataBuffer> stream = viewTemplateEngine.processStream(templateName, processMarkupSelectors, context, response.bufferFactory(), contentType, charset, templateResponseMaxChunkSizeBytes);
        if (templateResponseMaxChunkSizeBytes == Integer.MAX_VALUE && !dataDriven) {
            return response.writeWith(stream);
        }
        return response.writeAndFlushWith(Flux.from(stream).window(1));
    }

    private static Optional<Charset> getCharset(MediaType mediaType) {
        return mediaType != null ? Optional.ofNullable(mediaType.getCharset()) : Optional.empty();
    }

    private static boolean isDataDriven(Map<String, Object> mergedModel) {
        if (mergedModel == null || mergedModel.size() == 0) {
            return false;
        }
        for (Object value : mergedModel.values()) {
            if (value instanceof IReactiveDataDriverContextVariable) {
                return true;
            }
        }
        return false;
    }

    private ReactiveAdapterRegistry getReactiveAdapterRegistry() {
        ApplicationContext applicationContext = getApplicationContext();
        if (applicationContext != null && applicationContext != null) {
            try {
                return (ReactiveAdapterRegistry) applicationContext.getBean(ReactiveAdapterRegistry.class);
            } catch (NoSuchBeanDefinitionException e) {
                return null;
            }
        }
        return null;
    }
}