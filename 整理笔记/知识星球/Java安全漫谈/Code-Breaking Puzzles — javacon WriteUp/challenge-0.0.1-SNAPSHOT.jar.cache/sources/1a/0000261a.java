package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/AbstractMessageConverterMethodArgumentResolver.class */
public abstract class AbstractMessageConverterMethodArgumentResolver implements HandlerMethodArgumentResolver {
    private static final Set<HttpMethod> SUPPORTED_METHODS = EnumSet.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH);
    private static final Object NO_VALUE = new Object();
    protected final Log logger;
    protected final List<HttpMessageConverter<?>> messageConverters;
    protected final List<MediaType> allSupportedMediaTypes;
    private final RequestResponseBodyAdviceChain advice;

    public AbstractMessageConverterMethodArgumentResolver(List<HttpMessageConverter<?>> converters) {
        this(converters, null);
    }

    public AbstractMessageConverterMethodArgumentResolver(List<HttpMessageConverter<?>> converters, @Nullable List<Object> requestResponseBodyAdvice) {
        this.logger = LogFactory.getLog(getClass());
        Assert.notEmpty(converters, "'messageConverters' must not be empty");
        this.messageConverters = converters;
        this.allSupportedMediaTypes = getAllSupportedMediaTypes(converters);
        this.advice = new RequestResponseBodyAdviceChain(requestResponseBodyAdvice);
    }

    private static List<MediaType> getAllSupportedMediaTypes(List<HttpMessageConverter<?>> messageConverters) {
        Set<MediaType> allSupportedMediaTypes = new LinkedHashSet<>();
        for (HttpMessageConverter<?> messageConverter : messageConverters) {
            allSupportedMediaTypes.addAll(messageConverter.getSupportedMediaTypes());
        }
        List<MediaType> result = new ArrayList<>(allSupportedMediaTypes);
        MediaType.sortBySpecificity(result);
        return Collections.unmodifiableList(result);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RequestResponseBodyAdviceChain getAdvice() {
        return this.advice;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public <T> Object readWithMessageConverters(NativeWebRequest webRequest, MethodParameter parameter, Type paramType) throws IOException, HttpMediaTypeNotSupportedException, HttpMessageNotReadableException {
        HttpInputMessage inputMessage = createInputMessage(webRequest);
        return readWithMessageConverters(inputMessage, parameter, paramType);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00e8 A[Catch: IOException -> 0x0145, TryCatch #0 {IOException -> 0x0145, blocks: (B:22:0x0071, B:23:0x0086, B:25:0x0090, B:27:0x00ab, B:31:0x00bb, B:38:0x00e0, B:40:0x00e8, B:42:0x00fc, B:44:0x0116, B:43:0x010b, B:45:0x012c, B:36:0x00d2), top: B:67:0x0071 }] */
    /* JADX WARN: Removed duplicated region for block: B:45:0x012c A[Catch: IOException -> 0x0145, TryCatch #0 {IOException -> 0x0145, blocks: (B:22:0x0071, B:23:0x0086, B:25:0x0090, B:27:0x00ab, B:31:0x00bb, B:38:0x00e0, B:40:0x00e8, B:42:0x00fc, B:44:0x0116, B:43:0x010b, B:45:0x012c, B:36:0x00d2), top: B:67:0x0071 }] */
    /* JADX WARN: Removed duplicated region for block: B:53:0x015c  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x018b  */
    @org.springframework.lang.Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public <T> java.lang.Object readWithMessageConverters(org.springframework.http.HttpInputMessage r8, org.springframework.core.MethodParameter r9, java.lang.reflect.Type r10) throws java.io.IOException, org.springframework.web.HttpMediaTypeNotSupportedException, org.springframework.http.converter.HttpMessageNotReadableException {
        /*
            Method dump skipped, instructions count: 422
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver.readWithMessageConverters(org.springframework.http.HttpInputMessage, org.springframework.core.MethodParameter, java.lang.reflect.Type):java.lang.Object");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ServletServerHttpRequest createInputMessage(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);
        Assert.state(servletRequest != null, "No HttpServletRequest");
        return new ServletServerHttpRequest(servletRequest);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void validateIfApplicable(WebDataBinder binder, MethodParameter parameter) {
        Annotation[] annotations = parameter.getParameterAnnotations();
        for (Annotation ann : annotations) {
            Validated validatedAnn = (Validated) AnnotationUtils.getAnnotation(ann, Validated.class);
            if (validatedAnn != null || ann.annotationType().getSimpleName().startsWith("Valid")) {
                Object hints = validatedAnn != null ? validatedAnn.value() : AnnotationUtils.getValue(ann);
                Object[] validationHints = hints instanceof Object[] ? (Object[]) hints : new Object[]{hints};
                binder.validate(validationHints);
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isBindExceptionRequired(WebDataBinder binder, MethodParameter parameter) {
        int i = parameter.getParameterIndex();
        Class<?>[] paramTypes = parameter.getExecutable().getParameterTypes();
        boolean hasBindingResult = paramTypes.length > i + 1 && Errors.class.isAssignableFrom(paramTypes[i + 1]);
        return !hasBindingResult;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Object adaptArgumentIfNecessary(@Nullable Object arg, MethodParameter parameter) {
        if (parameter.getParameterType() == Optional.class) {
            if (arg == null || (((arg instanceof Collection) && ((Collection) arg).isEmpty()) || ((arg instanceof Object[]) && ((Object[]) arg).length == 0))) {
                return Optional.empty();
            }
            return Optional.of(arg);
        }
        return arg;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/AbstractMessageConverterMethodArgumentResolver$EmptyBodyCheckingHttpInputMessage.class */
    public static class EmptyBodyCheckingHttpInputMessage implements HttpInputMessage {
        private final HttpHeaders headers;
        @Nullable
        private final InputStream body;

        public EmptyBodyCheckingHttpInputMessage(HttpInputMessage inputMessage) throws IOException {
            this.headers = inputMessage.getHeaders();
            InputStream inputStream = inputMessage.getBody();
            if (inputStream.markSupported()) {
                inputStream.mark(1);
                this.body = inputStream.read() != -1 ? inputStream : null;
                inputStream.reset();
                return;
            }
            PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream);
            int b = pushbackInputStream.read();
            if (b == -1) {
                this.body = null;
                return;
            }
            this.body = pushbackInputStream;
            pushbackInputStream.unread(b);
        }

        @Override // org.springframework.http.HttpMessage
        public HttpHeaders getHeaders() {
            return this.headers;
        }

        @Override // org.springframework.http.HttpInputMessage
        public InputStream getBody() {
            return this.body != null ? this.body : StreamUtils.emptyInput();
        }

        public boolean hasBody() {
            return this.body != null;
        }
    }
}