package org.thymeleaf.spring5.view.reactive;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.reactive.result.view.RedirectView;
import org.springframework.web.reactive.result.view.View;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.reactive.result.view.ViewResolverSupport;
import org.thymeleaf.spring5.ISpringWebFluxTemplateEngine;
import org.thymeleaf.util.Validate;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/view/reactive/ThymeleafReactiveViewResolver.class */
public class ThymeleafReactiveViewResolver extends ViewResolverSupport implements ViewResolver, ApplicationContextAware {
    public static final String REDIRECT_URL_PREFIX = "redirect:";
    public static final String FORWARD_URL_PREFIX = "forward:";
    private ApplicationContext applicationContext;
    private Function<String, RedirectView> redirectViewProvider = url -> {
        return new RedirectView(url);
    };
    private boolean alwaysProcessRedirectAndForward = true;
    private Class<? extends ThymeleafReactiveView> viewClass = ThymeleafReactiveView.class;
    private String[] viewNames = null;
    private String[] excludedViewNames = null;
    private int order = Integer.MAX_VALUE;
    private final Map<String, Object> staticVariables = new LinkedHashMap(10);
    private int responseMaxChunkSizeBytes = Integer.MAX_VALUE;
    private String[] fullModeViewNames = null;
    private String[] chunkedModeViewNames = null;
    private ISpringWebFluxTemplateEngine templateEngine;
    private static final Logger vrlogger = LoggerFactory.getLogger(ThymeleafReactiveViewResolver.class);
    private static final List<MediaType> SUPPORTED_MEDIA_TYPES = Arrays.asList(MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML_XML, MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_RSS_XML, MediaType.APPLICATION_ATOM_XML, new MediaType("application", "javascript"), new MediaType("application", "ecmascript"), new MediaType("text", "javascript"), new MediaType("text", "ecmascript"), MediaType.APPLICATION_JSON, new MediaType("text", "css"), MediaType.TEXT_PLAIN, MediaType.TEXT_EVENT_STREAM);

    public ThymeleafReactiveViewResolver() {
        setSupportedMediaTypes(SUPPORTED_MEDIA_TYPES);
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    public void setViewClass(Class<? extends ThymeleafReactiveView> viewClass) {
        if (viewClass == null || !ThymeleafReactiveView.class.isAssignableFrom(viewClass)) {
            throw new IllegalArgumentException("Given view class [" + (viewClass != null ? viewClass.getName() : null) + "] is not of type [" + ThymeleafReactiveView.class.getName() + "]");
        }
        this.viewClass = viewClass;
    }

    protected Class<? extends ThymeleafReactiveView> getViewClass() {
        return this.viewClass;
    }

    public ISpringWebFluxTemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }

    public void setTemplateEngine(ISpringWebFluxTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public Map<String, Object> getStaticVariables() {
        return Collections.unmodifiableMap(this.staticVariables);
    }

    public void addStaticVariable(String name, Object value) {
        this.staticVariables.put(name, value);
    }

    public void setStaticVariables(Map<String, ?> variables) {
        if (variables != null) {
            for (Map.Entry<String, ?> entry : variables.entrySet()) {
                addStaticVariable(entry.getKey(), entry.getValue());
            }
        }
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    public void setRedirectViewProvider(Function<String, RedirectView> redirectViewProvider) {
        Validate.notNull(redirectViewProvider, "RedirectView provider cannot be null");
        this.redirectViewProvider = redirectViewProvider;
    }

    public Function<String, RedirectView> getRedirectViewProvider() {
        return this.redirectViewProvider;
    }

    public void setAlwaysProcessRedirectAndForward(boolean alwaysProcessRedirectAndForward) {
        this.alwaysProcessRedirectAndForward = alwaysProcessRedirectAndForward;
    }

    public boolean getAlwaysProcessRedirectAndForward() {
        return this.alwaysProcessRedirectAndForward;
    }

    public void setResponseMaxChunkSizeBytes(int responseMaxChunkSizeBytes) {
        this.responseMaxChunkSizeBytes = responseMaxChunkSizeBytes;
    }

    public int getResponseMaxChunkSizeBytes() {
        return this.responseMaxChunkSizeBytes;
    }

    public void setViewNames(String[] viewNames) {
        this.viewNames = viewNames;
    }

    public String[] getViewNames() {
        return this.viewNames;
    }

    public void setExcludedViewNames(String[] excludedViewNames) {
        this.excludedViewNames = excludedViewNames;
    }

    public String[] getExcludedViewNames() {
        return this.excludedViewNames;
    }

    public void setFullModeViewNames(String[] fullModeViewNames) {
        this.fullModeViewNames = fullModeViewNames;
    }

    public String[] getFullModeViewNames() {
        return this.fullModeViewNames;
    }

    public void setChunkedModeViewNames(String[] chunkedModeViewNames) {
        this.chunkedModeViewNames = chunkedModeViewNames;
    }

    public String[] getChunkedModeViewNames() {
        return this.chunkedModeViewNames;
    }

    protected boolean canHandle(String viewName, Locale locale) {
        String[] viewNamesToBeProcessed = getViewNames();
        String[] viewNamesNotToBeProcessed = getExcludedViewNames();
        return (viewNamesToBeProcessed == null || PatternMatchUtils.simpleMatch(viewNamesToBeProcessed, viewName)) && (viewNamesNotToBeProcessed == null || !PatternMatchUtils.simpleMatch(viewNamesNotToBeProcessed, viewName));
    }

    protected boolean shouldUseChunkedExecution(String viewName) {
        int viewResponseMaxChunkSizeBytes = getResponseMaxChunkSizeBytes();
        String[] viewChunkedModeViewNames = getChunkedModeViewNames();
        String[] viewFullModeViewNames = getFullModeViewNames();
        if (viewResponseMaxChunkSizeBytes == Integer.MAX_VALUE) {
            if (viewChunkedModeViewNames != null) {
                vrlogger.warn("[THYMELEAF] A set of view names to be executed in CHUNKED mode has been specified, but no response max chunk size has been specified, so this configuration parameter has no practical effect (no way to configure CHUNKED mode from the ViewResolver). Please fix your configuration.");
            }
            if (viewFullModeViewNames != null) {
                vrlogger.warn("[THYMELEAF] A set of view names to be executed in FULL mode has been specified, but no response max chunk size has been specified, so the former configuration parameter has no practical effect (all templates will actually be executed as FULL). Please fix your configuration.");
                return false;
            }
            return false;
        } else if (viewChunkedModeViewNames != null) {
            return PatternMatchUtils.simpleMatch(viewChunkedModeViewNames, viewName);
        } else {
            return viewFullModeViewNames == null || !PatternMatchUtils.simpleMatch(viewFullModeViewNames, viewName);
        }
    }

    public Mono<View> resolveViewName(String viewName, Locale locale) {
        if (!this.alwaysProcessRedirectAndForward && !canHandle(viewName, locale)) {
            vrlogger.trace("[THYMELEAF] View \"{}\" cannot be handled by ThymeleafReactiveViewResolver. Passing on to the next resolver in the chain.", viewName);
            return Mono.empty();
        } else if (viewName.startsWith("redirect:")) {
            vrlogger.trace("[THYMELEAF] View \"{}\" is a redirect, and will not be handled directly by ThymeleafReactiveViewResolver.", viewName);
            String redirectUrl = viewName.substring("redirect:".length());
            RedirectView view = this.redirectViewProvider.apply(redirectUrl);
            RedirectView initializedView = (RedirectView) getApplicationContext().getAutowireCapableBeanFactory().initializeBean(view, viewName);
            return Mono.just(initializedView);
        } else if (viewName.startsWith("forward:")) {
            vrlogger.trace("[THYMELEAF] View \"{}\" is a forward, and will not be handled directly by ThymeleafReactiveViewResolver.", viewName);
            return Mono.error(new UnsupportedOperationException("Forwards are not currently supported by ThymeleafReactiveViewResolver"));
        } else if (this.alwaysProcessRedirectAndForward && !canHandle(viewName, locale)) {
            vrlogger.trace("[THYMELEAF] View \"{}\" cannot be handled by ThymeleafReactiveViewResolver. Passing on to the next resolver in the chain.", viewName);
            return Mono.empty();
        } else {
            vrlogger.trace("[THYMELEAF] View {} will be handled by ThymeleafReactiveViewResolver and a {} instance will be created for it", viewName, getViewClass().getSimpleName());
            return loadView(viewName, locale);
        }
    }

    protected Mono<View> loadView(String viewName, Locale locale) {
        ThymeleafReactiveView view;
        BeanDefinition beanDefinition;
        AutowireCapableBeanFactory beanFactory = getApplicationContext().getAutowireCapableBeanFactory();
        boolean viewBeanExists = beanFactory.containsBean(viewName);
        Class<?> viewBeanType = viewBeanExists ? beanFactory.getType(viewName) : null;
        if (viewBeanExists && viewBeanType != null && ThymeleafReactiveView.class.isAssignableFrom(viewBeanType)) {
            if (beanFactory instanceof ConfigurableListableBeanFactory) {
                beanDefinition = ((ConfigurableListableBeanFactory) beanFactory).getBeanDefinition(viewName);
            } else {
                beanDefinition = null;
            }
            BeanDefinition viewBeanDefinition = beanDefinition;
            if (viewBeanDefinition == null || !viewBeanDefinition.isPrototype()) {
                view = (ThymeleafReactiveView) beanFactory.configureBean((ThymeleafReactiveView) BeanUtils.instantiateClass(getViewClass()), viewName);
            } else {
                view = (ThymeleafReactiveView) beanFactory.getBean(viewName);
            }
        } else {
            ThymeleafReactiveView viewInstance = (ThymeleafReactiveView) BeanUtils.instantiateClass(getViewClass());
            if (viewBeanExists && viewBeanType == null) {
                beanFactory.autowireBeanProperties(viewInstance, 0, false);
                beanFactory.applyBeanPropertyValues(viewInstance, viewName);
                view = (ThymeleafReactiveView) beanFactory.initializeBean(viewInstance, viewName);
            } else {
                beanFactory.autowireBeanProperties(viewInstance, 0, false);
                view = (ThymeleafReactiveView) beanFactory.initializeBean(viewInstance, viewName);
            }
        }
        view.setTemplateEngine(getTemplateEngine());
        view.setStaticVariables(getStaticVariables());
        if (view.getTemplateName() == null) {
            view.setTemplateName(viewName);
        }
        if (!view.isSupportedMediaTypesSet()) {
            view.setSupportedMediaTypes(getSupportedMediaTypes());
        }
        if (!view.isDefaultCharsetSet()) {
            view.setDefaultCharset(getDefaultCharset());
        }
        if (locale != null && view.getLocale() == null) {
            view.setLocale(locale);
        }
        boolean shouldUseChunkedExecution = shouldUseChunkedExecution(viewName);
        if (shouldUseChunkedExecution && view.getNullableResponseMaxChunkSize() == null) {
            view.setResponseMaxChunkSizeBytes(getResponseMaxChunkSizeBytes());
        }
        return Mono.just(view);
    }
}