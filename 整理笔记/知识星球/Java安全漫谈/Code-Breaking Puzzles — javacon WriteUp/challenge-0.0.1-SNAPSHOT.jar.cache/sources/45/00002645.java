package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.Conventions;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/RequestResponseBodyMethodProcessor.class */
public class RequestResponseBodyMethodProcessor extends AbstractMessageConverterMethodProcessor {
    public RequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    public RequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters, @Nullable ContentNegotiationManager manager) {
        super(converters, manager);
    }

    public RequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters, @Nullable List<Object> requestResponseBodyAdvice) {
        super(converters, null, requestResponseBodyAdvice);
    }

    public RequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters, @Nullable ContentNegotiationManager manager, @Nullable List<Object> requestResponseBodyAdvice) {
        super(converters, manager, requestResponseBodyAdvice);
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestBody.class);
    }

    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public boolean supportsReturnType(MethodParameter returnType) {
        return AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), ResponseBody.class) || returnType.hasMethodAnnotation(ResponseBody.class);
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        MethodParameter parameter2 = parameter.nestedIfOptional();
        Object arg = readWithMessageConverters(webRequest, parameter2, parameter2.getNestedGenericParameterType());
        String name = Conventions.getVariableNameForParameter(parameter2);
        if (binderFactory != null) {
            WebDataBinder binder = binderFactory.createBinder(webRequest, arg, name);
            if (arg != null) {
                validateIfApplicable(binder, parameter2);
                if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter2)) {
                    throw new MethodArgumentNotValidException(parameter2, binder.getBindingResult());
                }
            }
            if (mavContainer != null) {
                mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + name, binder.getBindingResult());
            }
        }
        return adaptArgumentIfNecessary(arg, parameter2);
    }

    @Override // org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver
    public <T> Object readWithMessageConverters(NativeWebRequest webRequest, MethodParameter parameter, Type paramType) throws IOException, HttpMediaTypeNotSupportedException, HttpMessageNotReadableException {
        HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);
        Assert.state(servletRequest != null, "No HttpServletRequest");
        ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(servletRequest);
        Object arg = readWithMessageConverters(inputMessage, parameter, paramType);
        if (arg == null && checkRequired(parameter)) {
            throw new HttpMessageNotReadableException("Required request body is missing: " + parameter.getExecutable().toGenericString(), inputMessage);
        }
        return arg;
    }

    protected boolean checkRequired(MethodParameter parameter) {
        RequestBody requestBody = (RequestBody) parameter.getParameterAnnotation(RequestBody.class);
        return (requestBody == null || !requestBody.required() || parameter.isOptional()) ? false : true;
    }

    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {
        mavContainer.setRequestHandled(true);
        ServletServerHttpRequest inputMessage = createInputMessage(webRequest);
        ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);
        writeWithMessageConverters(returnValue, returnType, inputMessage, outputMessage);
    }
}