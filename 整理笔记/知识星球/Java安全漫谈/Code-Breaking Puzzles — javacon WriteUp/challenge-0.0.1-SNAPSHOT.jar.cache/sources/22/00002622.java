package org.springframework.web.servlet.mvc.method.annotation;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.ControllerAdviceBean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.method.annotation.MapMethodProcessor;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.method.annotation.ModelMethodProcessor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.AbstractHandlerMethodExceptionResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ExceptionHandlerExceptionResolver.class */
public class ExceptionHandlerExceptionResolver extends AbstractHandlerMethodExceptionResolver implements ApplicationContextAware, InitializingBean {
    @Nullable
    private List<HandlerMethodArgumentResolver> customArgumentResolvers;
    @Nullable
    private HandlerMethodArgumentResolverComposite argumentResolvers;
    @Nullable
    private List<HandlerMethodReturnValueHandler> customReturnValueHandlers;
    @Nullable
    private HandlerMethodReturnValueHandlerComposite returnValueHandlers;
    private List<HttpMessageConverter<?>> messageConverters;
    @Nullable
    private ApplicationContext applicationContext;
    private ContentNegotiationManager contentNegotiationManager = new ContentNegotiationManager();
    private final List<Object> responseBodyAdvice = new ArrayList();
    private final Map<Class<?>, ExceptionHandlerMethodResolver> exceptionHandlerCache = new ConcurrentHashMap(64);
    private final Map<ControllerAdviceBean, ExceptionHandlerMethodResolver> exceptionHandlerAdviceCache = new LinkedHashMap();

    public ExceptionHandlerExceptionResolver() {
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setWriteAcceptCharset(false);
        this.messageConverters = new ArrayList();
        this.messageConverters.add(new ByteArrayHttpMessageConverter());
        this.messageConverters.add(stringHttpMessageConverter);
        try {
            this.messageConverters.add(new SourceHttpMessageConverter());
        } catch (Error e) {
        }
        this.messageConverters.add(new AllEncompassingFormHttpMessageConverter());
    }

    public void setCustomArgumentResolvers(@Nullable List<HandlerMethodArgumentResolver> argumentResolvers) {
        this.customArgumentResolvers = argumentResolvers;
    }

    @Nullable
    public List<HandlerMethodArgumentResolver> getCustomArgumentResolvers() {
        return this.customArgumentResolvers;
    }

    public void setArgumentResolvers(@Nullable List<HandlerMethodArgumentResolver> argumentResolvers) {
        if (argumentResolvers == null) {
            this.argumentResolvers = null;
            return;
        }
        this.argumentResolvers = new HandlerMethodArgumentResolverComposite();
        this.argumentResolvers.addResolvers(argumentResolvers);
    }

    @Nullable
    public HandlerMethodArgumentResolverComposite getArgumentResolvers() {
        return this.argumentResolvers;
    }

    public void setCustomReturnValueHandlers(@Nullable List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        this.customReturnValueHandlers = returnValueHandlers;
    }

    @Nullable
    public List<HandlerMethodReturnValueHandler> getCustomReturnValueHandlers() {
        return this.customReturnValueHandlers;
    }

    public void setReturnValueHandlers(@Nullable List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        if (returnValueHandlers == null) {
            this.returnValueHandlers = null;
            return;
        }
        this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite();
        this.returnValueHandlers.addHandlers(returnValueHandlers);
    }

    @Nullable
    public HandlerMethodReturnValueHandlerComposite getReturnValueHandlers() {
        return this.returnValueHandlers;
    }

    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = messageConverters;
    }

    public List<HttpMessageConverter<?>> getMessageConverters() {
        return this.messageConverters;
    }

    public void setContentNegotiationManager(ContentNegotiationManager contentNegotiationManager) {
        this.contentNegotiationManager = contentNegotiationManager;
    }

    public ContentNegotiationManager getContentNegotiationManager() {
        return this.contentNegotiationManager;
    }

    public void setResponseBodyAdvice(@Nullable List<ResponseBodyAdvice<?>> responseBodyAdvice) {
        this.responseBodyAdvice.clear();
        if (responseBodyAdvice != null) {
            this.responseBodyAdvice.addAll(responseBodyAdvice);
        }
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Nullable
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        initExceptionHandlerAdviceCache();
        if (this.argumentResolvers == null) {
            List<HandlerMethodArgumentResolver> resolvers = getDefaultArgumentResolvers();
            this.argumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
        }
        if (this.returnValueHandlers == null) {
            List<HandlerMethodReturnValueHandler> handlers = getDefaultReturnValueHandlers();
            this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite().addHandlers(handlers);
        }
    }

    private void initExceptionHandlerAdviceCache() {
        if (getApplicationContext() == null) {
            return;
        }
        List<ControllerAdviceBean> adviceBeans = ControllerAdviceBean.findAnnotatedBeans(getApplicationContext());
        AnnotationAwareOrderComparator.sort(adviceBeans);
        for (ControllerAdviceBean adviceBean : adviceBeans) {
            Class<?> beanType = adviceBean.getBeanType();
            if (beanType == null) {
                throw new IllegalStateException("Unresolvable type for ControllerAdviceBean: " + adviceBean);
            }
            ExceptionHandlerMethodResolver resolver = new ExceptionHandlerMethodResolver(beanType);
            if (resolver.hasExceptionMappings()) {
                this.exceptionHandlerAdviceCache.put(adviceBean, resolver);
            }
            if (ResponseBodyAdvice.class.isAssignableFrom(beanType)) {
                this.responseBodyAdvice.add(adviceBean);
            }
        }
        if (this.logger.isDebugEnabled()) {
            int handlerSize = this.exceptionHandlerAdviceCache.size();
            int adviceSize = this.responseBodyAdvice.size();
            if (handlerSize == 0 && adviceSize == 0) {
                this.logger.debug("ControllerAdvice beans: none");
            } else {
                this.logger.debug("ControllerAdvice beans: " + handlerSize + " @ExceptionHandler, " + adviceSize + " ResponseBodyAdvice");
            }
        }
    }

    public Map<ControllerAdviceBean, ExceptionHandlerMethodResolver> getExceptionHandlerAdviceCache() {
        return Collections.unmodifiableMap(this.exceptionHandlerAdviceCache);
    }

    protected List<HandlerMethodArgumentResolver> getDefaultArgumentResolvers() {
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
        resolvers.add(new SessionAttributeMethodArgumentResolver());
        resolvers.add(new RequestAttributeMethodArgumentResolver());
        resolvers.add(new ServletRequestMethodArgumentResolver());
        resolvers.add(new ServletResponseMethodArgumentResolver());
        resolvers.add(new RedirectAttributesMethodArgumentResolver());
        resolvers.add(new ModelMethodProcessor());
        if (getCustomArgumentResolvers() != null) {
            resolvers.addAll(getCustomArgumentResolvers());
        }
        return resolvers;
    }

    protected List<HandlerMethodReturnValueHandler> getDefaultReturnValueHandlers() {
        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<>();
        handlers.add(new ModelAndViewMethodReturnValueHandler());
        handlers.add(new ModelMethodProcessor());
        handlers.add(new ViewMethodReturnValueHandler());
        handlers.add(new HttpEntityMethodProcessor(getMessageConverters(), this.contentNegotiationManager, this.responseBodyAdvice));
        handlers.add(new ModelAttributeMethodProcessor(false));
        handlers.add(new RequestResponseBodyMethodProcessor(getMessageConverters(), this.contentNegotiationManager, this.responseBodyAdvice));
        handlers.add(new ViewNameMethodReturnValueHandler());
        handlers.add(new MapMethodProcessor());
        if (getCustomReturnValueHandlers() != null) {
            handlers.addAll(getCustomReturnValueHandlers());
        }
        handlers.add(new ModelAttributeMethodProcessor(true));
        return handlers;
    }

    @Override // org.springframework.web.servlet.handler.AbstractHandlerMethodExceptionResolver
    @Nullable
    protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request, HttpServletResponse response, @Nullable HandlerMethod handlerMethod, Exception exception) {
        ServletInvocableHandlerMethod exceptionHandlerMethod = getExceptionHandlerMethod(handlerMethod, exception);
        if (exceptionHandlerMethod == null) {
            return null;
        }
        if (this.argumentResolvers != null) {
            exceptionHandlerMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
        }
        if (this.returnValueHandlers != null) {
            exceptionHandlerMethod.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);
        }
        ServletWebRequest webRequest = new ServletWebRequest(request, response);
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        try {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using @ExceptionHandler " + exceptionHandlerMethod);
            }
            Throwable cause = exception.getCause();
            if (cause != null) {
                exceptionHandlerMethod.invokeAndHandle(webRequest, mavContainer, exception, cause, handlerMethod);
            } else {
                exceptionHandlerMethod.invokeAndHandle(webRequest, mavContainer, exception, handlerMethod);
            }
            if (mavContainer.isRequestHandled()) {
                return new ModelAndView();
            }
            ModelMap model = mavContainer.getModel();
            HttpStatus status = mavContainer.getStatus();
            ModelAndView mav = new ModelAndView(mavContainer.getViewName(), model, status);
            mav.setViewName(mavContainer.getViewName());
            if (!mavContainer.isViewReference()) {
                mav.setView((View) mavContainer.getView());
            }
            if (model instanceof RedirectAttributes) {
                Map<String, ?> flashAttributes = ((RedirectAttributes) model).getFlashAttributes();
                RequestContextUtils.getOutputFlashMap(request).putAll(flashAttributes);
            }
            return mav;
        } catch (Throwable invocationEx) {
            if (invocationEx != exception && this.logger.isWarnEnabled()) {
                this.logger.warn("Failure in @ExceptionHandler " + exceptionHandlerMethod, invocationEx);
                return null;
            }
            return null;
        }
    }

    @Nullable
    protected ServletInvocableHandlerMethod getExceptionHandlerMethod(@Nullable HandlerMethod handlerMethod, Exception exception) {
        Method method;
        Class<?> handlerType = null;
        if (handlerMethod != null) {
            handlerType = handlerMethod.getBeanType();
            ExceptionHandlerMethodResolver resolver = this.exceptionHandlerCache.get(handlerType);
            if (resolver == null) {
                resolver = new ExceptionHandlerMethodResolver(handlerType);
                this.exceptionHandlerCache.put(handlerType, resolver);
            }
            Method method2 = resolver.resolveMethod(exception);
            if (method2 != null) {
                return new ServletInvocableHandlerMethod(handlerMethod.getBean(), method2);
            }
            if (Proxy.isProxyClass(handlerType)) {
                handlerType = AopUtils.getTargetClass(handlerMethod.getBean());
            }
        }
        for (Map.Entry<ControllerAdviceBean, ExceptionHandlerMethodResolver> entry : this.exceptionHandlerAdviceCache.entrySet()) {
            ControllerAdviceBean advice = entry.getKey();
            if (advice.isApplicableToBeanType(handlerType) && (method = entry.getValue().resolveMethod(exception)) != null) {
                return new ServletInvocableHandlerMethod(advice.resolveBean(), method);
            }
        }
        return null;
    }
}