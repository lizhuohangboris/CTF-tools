package org.springframework.web.method.annotation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;
import org.springframework.web.method.support.UriComponentsContributor;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.multipart.support.MultipartResolutionDelegate;
import org.springframework.web.util.UriComponentsBuilder;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/method/annotation/RequestParamMethodArgumentResolver.class */
public class RequestParamMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver implements UriComponentsContributor {
    private static final TypeDescriptor STRING_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(String.class);
    private final boolean useDefaultResolution;

    public RequestParamMethodArgumentResolver(boolean useDefaultResolution) {
        this.useDefaultResolution = useDefaultResolution;
    }

    public RequestParamMethodArgumentResolver(@Nullable ConfigurableBeanFactory beanFactory, boolean useDefaultResolution) {
        super(beanFactory);
        this.useDefaultResolution = useDefaultResolution;
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(RequestParam.class)) {
            if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
                RequestParam requestParam = (RequestParam) parameter.getParameterAnnotation(RequestParam.class);
                return requestParam != null && StringUtils.hasText(requestParam.name());
            }
            return true;
        } else if (parameter.hasParameterAnnotation(RequestPart.class)) {
            return false;
        } else {
            MethodParameter parameter2 = parameter.nestedIfOptional();
            if (MultipartResolutionDelegate.isMultipartArgument(parameter2)) {
                return true;
            }
            if (this.useDefaultResolution) {
                return BeanUtils.isSimpleProperty(parameter2.getNestedParameterType());
            }
            return false;
        }
    }

    @Override // org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
    protected AbstractNamedValueMethodArgumentResolver.NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        RequestParam ann = (RequestParam) parameter.getParameterAnnotation(RequestParam.class);
        return ann != null ? new RequestParamNamedValueInfo(ann) : new RequestParamNamedValueInfo();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
    @Nullable
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        Object mpArg;
        HttpServletRequest servletRequest = (HttpServletRequest) request.getNativeRequest(HttpServletRequest.class);
        if (servletRequest != null && (mpArg = MultipartResolutionDelegate.resolveMultipartArgument(name, parameter, servletRequest)) != MultipartResolutionDelegate.UNRESOLVABLE) {
            return mpArg;
        }
        Object obj = null;
        MultipartRequest multipartRequest = (MultipartRequest) request.getNativeRequest(MultipartRequest.class);
        Object arg = obj;
        if (multipartRequest != null) {
            List<MultipartFile> files = multipartRequest.getFiles(name);
            arg = obj;
            if (!files.isEmpty()) {
                arg = files.size() == 1 ? files.get(0) : files;
            }
        }
        Object obj2 = arg;
        Object arg2 = arg;
        if (obj2 == null) {
            Object[] paramValues = request.getParameterValues(name);
            arg2 = arg;
            if (paramValues != null) {
                arg2 = paramValues.length == 1 ? paramValues[0] : paramValues;
            }
        }
        return arg2;
    }

    @Override // org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
    protected void handleMissingValue(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        HttpServletRequest servletRequest = (HttpServletRequest) request.getNativeRequest(HttpServletRequest.class);
        if (MultipartResolutionDelegate.isMultipartArgument(parameter)) {
            if (servletRequest == null || !MultipartResolutionDelegate.isMultipartRequest(servletRequest)) {
                throw new MultipartException("Current request is not a multipart request");
            }
            throw new MissingServletRequestPartException(name);
        }
        throw new MissingServletRequestParameterException(name, parameter.getNestedParameterType().getSimpleName());
    }

    @Override // org.springframework.web.method.support.UriComponentsContributor
    public void contributeMethodArgument(MethodParameter parameter, @Nullable Object value, UriComponentsBuilder builder, Map<String, Object> uriVariables, ConversionService conversionService) {
        Class<?> paramType = parameter.getNestedParameterType();
        if (Map.class.isAssignableFrom(paramType) || MultipartFile.class == paramType || Part.class == paramType) {
            return;
        }
        RequestParam requestParam = (RequestParam) parameter.getParameterAnnotation(RequestParam.class);
        String name = (requestParam == null || StringUtils.isEmpty(requestParam.name())) ? parameter.getParameterName() : requestParam.name();
        Assert.state(name != null, "Unresolvable parameter name");
        if (value == null) {
            if (requestParam != null && (!requestParam.required() || !requestParam.defaultValue().equals(ValueConstants.DEFAULT_NONE))) {
                return;
            }
            builder.queryParam(name, new Object[0]);
        } else if (value instanceof Collection) {
            for (Object element : (Collection) value) {
                builder.queryParam(name, formatUriValue(conversionService, TypeDescriptor.nested(parameter, 1), element));
            }
        } else {
            builder.queryParam(name, formatUriValue(conversionService, new TypeDescriptor(parameter), value));
        }
    }

    @Nullable
    protected String formatUriValue(@Nullable ConversionService cs, @Nullable TypeDescriptor sourceType, @Nullable Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        if (cs != null) {
            return (String) cs.convert(value, sourceType, STRING_TYPE_DESCRIPTOR);
        }
        return value.toString();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/method/annotation/RequestParamMethodArgumentResolver$RequestParamNamedValueInfo.class */
    private static class RequestParamNamedValueInfo extends AbstractNamedValueMethodArgumentResolver.NamedValueInfo {
        public RequestParamNamedValueInfo() {
            super("", false, ValueConstants.DEFAULT_NONE);
        }

        public RequestParamNamedValueInfo(RequestParam annotation) {
            super(annotation.name(), annotation.required(), annotation.defaultValue());
        }
    }
}