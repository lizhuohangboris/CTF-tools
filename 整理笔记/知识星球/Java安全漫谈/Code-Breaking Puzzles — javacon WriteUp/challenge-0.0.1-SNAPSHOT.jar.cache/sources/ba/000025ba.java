package org.springframework.web.servlet.config.annotation;

import java.util.List;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/config/annotation/WebMvcConfigurerAdapter.class */
public abstract class WebMvcConfigurerAdapter implements WebMvcConfigurer {
    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void configurePathMatch(PathMatchConfigurer configurer) {
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void addFormatters(FormatterRegistry registry) {
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void addInterceptors(InterceptorRegistry registry) {
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void addCorsMappings(CorsRegistry registry) {
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void addViewControllers(ViewControllerRegistry registry) {
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void configureViewResolvers(ViewResolverRegistry registry) {
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    @Nullable
    public Validator getValidator() {
        return null;
    }

    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    @Nullable
    public MessageCodesResolver getMessageCodesResolver() {
        return null;
    }
}