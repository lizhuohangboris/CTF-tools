package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/HttpEntityMethodProcessor.class */
public class HttpEntityMethodProcessor extends AbstractMessageConverterMethodProcessor {
    private static final Set<HttpMethod> SAFE_METHODS = EnumSet.of(HttpMethod.GET, HttpMethod.HEAD);

    public HttpEntityMethodProcessor(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    public HttpEntityMethodProcessor(List<HttpMessageConverter<?>> converters, ContentNegotiationManager manager) {
        super(converters, manager);
    }

    public HttpEntityMethodProcessor(List<HttpMessageConverter<?>> converters, List<Object> requestResponseBodyAdvice) {
        super(converters, null, requestResponseBodyAdvice);
    }

    public HttpEntityMethodProcessor(List<HttpMessageConverter<?>> converters, @Nullable ContentNegotiationManager manager, List<Object> requestResponseBodyAdvice) {
        super(converters, manager, requestResponseBodyAdvice);
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        return HttpEntity.class == parameter.getParameterType() || RequestEntity.class == parameter.getParameterType();
    }

    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public boolean supportsReturnType(MethodParameter returnType) {
        return HttpEntity.class.isAssignableFrom(returnType.getParameterType()) && !RequestEntity.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    @Nullable
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws IOException, HttpMediaTypeNotSupportedException {
        ServletServerHttpRequest inputMessage = createInputMessage(webRequest);
        Type paramType = getHttpEntityType(parameter);
        if (paramType == null) {
            throw new IllegalArgumentException("HttpEntity parameter '" + parameter.getParameterName() + "' in method " + parameter.getMethod() + " is not parameterized");
        }
        Object body = readWithMessageConverters(webRequest, parameter, paramType);
        if (RequestEntity.class == parameter.getParameterType()) {
            return new RequestEntity(body, inputMessage.getHeaders(), inputMessage.getMethod(), inputMessage.getURI());
        }
        return new HttpEntity(body, inputMessage.getHeaders());
    }

    @Nullable
    private Type getHttpEntityType(MethodParameter parameter) {
        Assert.isAssignable(HttpEntity.class, parameter.getParameterType());
        Type parameterType = parameter.getGenericParameterType();
        if (parameterType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) parameterType;
            if (type.getActualTypeArguments().length != 1) {
                throw new IllegalArgumentException("Expected single generic parameter on '" + parameter.getParameterName() + "' in method " + parameter.getMethod());
            }
            return type.getActualTypeArguments()[0];
        } else if (parameterType instanceof Class) {
            return Object.class;
        } else {
            return null;
        }
    }

    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        String location;
        mavContainer.setRequestHandled(true);
        if (returnValue == null) {
            return;
        }
        ServletServerHttpRequest inputMessage = createInputMessage(webRequest);
        ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);
        Assert.isInstanceOf(HttpEntity.class, returnValue);
        HttpEntity<?> responseEntity = (HttpEntity) returnValue;
        HttpHeaders outputHeaders = outputMessage.getHeaders();
        HttpHeaders entityHeaders = responseEntity.getHeaders();
        if (!entityHeaders.isEmpty()) {
            entityHeaders.forEach(key, value -> {
                if ("Vary".equals(key) && outputHeaders.containsKey("Vary")) {
                    List<String> values = getVaryRequestHeadersToAdd(outputHeaders, entityHeaders);
                    if (!values.isEmpty()) {
                        outputHeaders.setVary(values);
                        return;
                    }
                    return;
                }
                outputHeaders.put(key, (List<String>) value);
            });
        }
        if (responseEntity instanceof ResponseEntity) {
            int returnStatus = ((ResponseEntity) responseEntity).getStatusCodeValue();
            outputMessage.getServletResponse().setStatus(returnStatus);
            if (returnStatus == 200) {
                if (SAFE_METHODS.contains(inputMessage.getMethod()) && isResourceNotModified(inputMessage, outputMessage)) {
                    outputMessage.flush();
                    return;
                }
            } else if (returnStatus / 100 == 3 && (location = outputHeaders.getFirst("location")) != null) {
                saveFlashAttributes(mavContainer, webRequest, location);
            }
        }
        writeWithMessageConverters(responseEntity.getBody(), returnType, inputMessage, outputMessage);
        outputMessage.flush();
    }

    private List<String> getVaryRequestHeadersToAdd(HttpHeaders responseHeaders, HttpHeaders entityHeaders) {
        String[] strArr;
        List<String> entityHeadersVary = entityHeaders.getVary();
        List<String> vary = responseHeaders.get("Vary");
        if (vary != null) {
            List<String> result = new ArrayList<>(entityHeadersVary);
            for (String header : vary) {
                for (String existing : StringUtils.tokenizeToStringArray(header, ",")) {
                    if ("*".equals(existing)) {
                        return Collections.emptyList();
                    }
                    for (String value : entityHeadersVary) {
                        if (value.equalsIgnoreCase(existing)) {
                            result.remove(value);
                        }
                    }
                }
            }
            return result;
        }
        return entityHeadersVary;
    }

    private boolean isResourceNotModified(ServletServerHttpRequest request, ServletServerHttpResponse response) {
        ServletWebRequest servletWebRequest = new ServletWebRequest(request.getServletRequest(), response.getServletResponse());
        HttpHeaders responseHeaders = response.getHeaders();
        String etag = responseHeaders.getETag();
        long lastModifiedTimestamp = responseHeaders.getLastModified();
        if (request.getMethod() == HttpMethod.GET || request.getMethod() == HttpMethod.HEAD) {
            responseHeaders.remove(HttpHeaders.ETAG);
            responseHeaders.remove(HttpHeaders.LAST_MODIFIED);
        }
        return servletWebRequest.checkNotModified(etag, lastModifiedTimestamp);
    }

    private void saveFlashAttributes(ModelAndViewContainer mav, NativeWebRequest request, String location) {
        mav.setRedirectModelScenario(true);
        ModelMap model = mav.getModel();
        if (model instanceof RedirectAttributes) {
            Map<String, ?> flashAttributes = ((RedirectAttributes) model).getFlashAttributes();
            if (!CollectionUtils.isEmpty(flashAttributes)) {
                HttpServletRequest req = (HttpServletRequest) request.getNativeRequest(HttpServletRequest.class);
                HttpServletResponse res = (HttpServletResponse) request.getNativeResponse(HttpServletResponse.class);
                if (req != null) {
                    RequestContextUtils.getOutputFlashMap(req).putAll(flashAttributes);
                    if (res != null) {
                        RequestContextUtils.saveOutputFlashMap(location, req, res);
                    }
                }
            }
        }
    }

    @Override // org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor
    protected Class<?> getReturnValueType(@Nullable Object returnValue, MethodParameter returnType) {
        if (returnValue != null) {
            return returnValue.getClass();
        }
        Type type = getHttpEntityType(returnType);
        return ResolvableType.forMethodParameter(returnType, type != null ? type : Object.class).toClass();
    }
}