package org.springframework.web.method.annotation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestScope;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/method/annotation/AbstractNamedValueMethodArgumentResolver.class */
public abstract class AbstractNamedValueMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Nullable
    private final ConfigurableBeanFactory configurableBeanFactory;
    @Nullable
    private final BeanExpressionContext expressionContext;
    private final Map<MethodParameter, NamedValueInfo> namedValueInfoCache;

    protected abstract NamedValueInfo createNamedValueInfo(MethodParameter methodParameter);

    @Nullable
    protected abstract Object resolveName(String str, MethodParameter methodParameter, NativeWebRequest nativeWebRequest) throws Exception;

    public AbstractNamedValueMethodArgumentResolver() {
        this.namedValueInfoCache = new ConcurrentHashMap(256);
        this.configurableBeanFactory = null;
        this.expressionContext = null;
    }

    public AbstractNamedValueMethodArgumentResolver(@Nullable ConfigurableBeanFactory beanFactory) {
        this.namedValueInfoCache = new ConcurrentHashMap(256);
        this.configurableBeanFactory = beanFactory;
        this.expressionContext = beanFactory != null ? new BeanExpressionContext(beanFactory, new RequestScope()) : null;
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    @Nullable
    public final Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        NamedValueInfo namedValueInfo = getNamedValueInfo(parameter);
        MethodParameter nestedParameter = parameter.nestedIfOptional();
        Object resolvedName = resolveStringValue(namedValueInfo.name);
        if (resolvedName == null) {
            throw new IllegalArgumentException("Specified name must not resolve to null: [" + namedValueInfo.name + "]");
        }
        Object arg = resolveName(resolvedName.toString(), nestedParameter, webRequest);
        if (arg != null) {
            if ("".equals(arg) && namedValueInfo.defaultValue != null) {
                arg = resolveStringValue(namedValueInfo.defaultValue);
            }
        } else {
            if (namedValueInfo.defaultValue != null) {
                arg = resolveStringValue(namedValueInfo.defaultValue);
            } else if (namedValueInfo.required && !nestedParameter.isOptional()) {
                handleMissingValue(namedValueInfo.name, nestedParameter, webRequest);
            }
            arg = handleNullValue(namedValueInfo.name, arg, nestedParameter.getNestedParameterType());
        }
        if (binderFactory != null) {
            WebDataBinder binder = binderFactory.createBinder(webRequest, null, namedValueInfo.name);
            try {
                arg = binder.convertIfNecessary(arg, parameter.getParameterType(), parameter);
            } catch (ConversionNotSupportedException ex) {
                throw new MethodArgumentConversionNotSupportedException(arg, ex.getRequiredType(), namedValueInfo.name, parameter, ex.getCause());
            } catch (TypeMismatchException ex2) {
                throw new MethodArgumentTypeMismatchException(arg, ex2.getRequiredType(), namedValueInfo.name, parameter, ex2.getCause());
            }
        }
        handleResolvedValue(arg, namedValueInfo.name, parameter, mavContainer, webRequest);
        return arg;
    }

    private NamedValueInfo getNamedValueInfo(MethodParameter parameter) {
        NamedValueInfo namedValueInfo = this.namedValueInfoCache.get(parameter);
        if (namedValueInfo == null) {
            namedValueInfo = updateNamedValueInfo(parameter, createNamedValueInfo(parameter));
            this.namedValueInfoCache.put(parameter, namedValueInfo);
        }
        return namedValueInfo;
    }

    private NamedValueInfo updateNamedValueInfo(MethodParameter parameter, NamedValueInfo info) {
        String name = info.name;
        if (info.name.isEmpty()) {
            name = parameter.getParameterName();
            if (name == null) {
                throw new IllegalArgumentException("Name for argument type [" + parameter.getNestedParameterType().getName() + "] not available, and parameter name information not found in class file either.");
            }
        }
        String defaultValue = ValueConstants.DEFAULT_NONE.equals(info.defaultValue) ? null : info.defaultValue;
        return new NamedValueInfo(name, info.required, defaultValue);
    }

    @Nullable
    private Object resolveStringValue(String value) {
        if (this.configurableBeanFactory == null) {
            return value;
        }
        String placeholdersResolved = this.configurableBeanFactory.resolveEmbeddedValue(value);
        BeanExpressionResolver exprResolver = this.configurableBeanFactory.getBeanExpressionResolver();
        if (exprResolver == null || this.expressionContext == null) {
            return value;
        }
        return exprResolver.evaluate(placeholdersResolved, this.expressionContext);
    }

    protected void handleMissingValue(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        handleMissingValue(name, parameter);
    }

    protected void handleMissingValue(String name, MethodParameter parameter) throws ServletException {
        throw new ServletRequestBindingException("Missing argument '" + name + "' for method parameter of type " + parameter.getNestedParameterType().getSimpleName());
    }

    @Nullable
    private Object handleNullValue(String name, @Nullable Object value, Class<?> paramType) {
        if (value == null) {
            if (Boolean.TYPE.equals(paramType)) {
                return Boolean.FALSE;
            }
            if (paramType.isPrimitive()) {
                throw new IllegalStateException("Optional " + paramType.getSimpleName() + " parameter '" + name + "' is present but cannot be translated into a null value due to being declared as a primitive type. Consider declaring it as object wrapper for the corresponding primitive type.");
            }
        }
        return value;
    }

    protected void handleResolvedValue(@Nullable Object arg, String name, MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/method/annotation/AbstractNamedValueMethodArgumentResolver$NamedValueInfo.class */
    public static class NamedValueInfo {
        private final String name;
        private final boolean required;
        @Nullable
        private final String defaultValue;

        public NamedValueInfo(String name, boolean required, @Nullable String defaultValue) {
            this.name = name;
            this.required = required;
            this.defaultValue = defaultValue;
        }
    }
}