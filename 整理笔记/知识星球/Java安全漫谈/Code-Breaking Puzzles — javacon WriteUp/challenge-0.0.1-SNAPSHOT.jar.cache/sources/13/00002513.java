package org.springframework.web.method.annotation;

import java.beans.ConstructorProperties;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/method/annotation/ModelAttributeMethodProcessor.class */
public class ModelAttributeMethodProcessor implements HandlerMethodArgumentResolver, HandlerMethodReturnValueHandler {
    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    protected final Log logger = LogFactory.getLog(getClass());
    private final boolean annotationNotRequired;

    public ModelAttributeMethodProcessor(boolean annotationNotRequired) {
        this.annotationNotRequired = annotationNotRequired;
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ModelAttribute.class) || (this.annotationNotRequired && !BeanUtils.isSimpleProperty(parameter.getParameterType()));
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    @Nullable
    public final Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        Assert.state(mavContainer != null, "ModelAttributeMethodProcessor requires ModelAndViewContainer");
        Assert.state(binderFactory != null, "ModelAttributeMethodProcessor requires WebDataBinderFactory");
        String name = ModelFactory.getNameForParameter(parameter);
        ModelAttribute ann = (ModelAttribute) parameter.getParameterAnnotation(ModelAttribute.class);
        if (ann != null) {
            mavContainer.setBinding(name, ann.binding());
        }
        Object attribute = null;
        BindingResult bindingResult = null;
        if (mavContainer.containsAttribute(name)) {
            attribute = mavContainer.getModel().get(name);
        } else {
            try {
                attribute = createAttribute(name, parameter, binderFactory, webRequest);
            } catch (BindException ex) {
                if (isBindExceptionRequired(parameter)) {
                    throw ex;
                }
                if (parameter.getParameterType() == Optional.class) {
                    attribute = Optional.empty();
                }
                bindingResult = ex.getBindingResult();
            }
        }
        if (bindingResult == null) {
            WebDataBinder binder = binderFactory.createBinder(webRequest, attribute, name);
            if (binder.getTarget() != null) {
                if (!mavContainer.isBindingDisabled(name)) {
                    bindRequestParameters(binder, webRequest);
                }
                validateIfApplicable(binder, parameter);
                if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
                    throw new BindException(binder.getBindingResult());
                }
            }
            if (!parameter.getParameterType().isInstance(attribute)) {
                attribute = binder.convertIfNecessary(binder.getTarget(), parameter.getParameterType(), parameter);
            }
            bindingResult = binder.getBindingResult();
        }
        Map<String, Object> bindingResultModel = bindingResult.getModel();
        mavContainer.removeAttributes(bindingResultModel);
        mavContainer.addAllAttributes(bindingResultModel);
        return attribute;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object createAttribute(String attributeName, MethodParameter parameter, WebDataBinderFactory binderFactory, NativeWebRequest webRequest) throws Exception {
        MethodParameter nestedParameter = parameter.nestedIfOptional();
        Class<?> clazz = nestedParameter.getNestedParameterType();
        Constructor<?> ctor = BeanUtils.findPrimaryConstructor(clazz);
        if (ctor == null) {
            Constructor<?>[] ctors = clazz.getConstructors();
            if (ctors.length == 1) {
                ctor = ctors[0];
            } else {
                try {
                    ctor = clazz.getDeclaredConstructor(new Class[0]);
                } catch (NoSuchMethodException ex) {
                    throw new IllegalStateException("No primary or default constructor found for " + clazz, ex);
                }
            }
        }
        Object attribute = constructAttribute(ctor, attributeName, parameter, binderFactory, webRequest);
        if (parameter != nestedParameter) {
            attribute = Optional.of(attribute);
        }
        return attribute;
    }

    protected Object constructAttribute(Constructor<?> ctor, String attributeName, MethodParameter parameter, WebDataBinderFactory binderFactory, NativeWebRequest webRequest) throws Exception {
        Object constructed = constructAttribute(ctor, attributeName, binderFactory, webRequest);
        if (constructed != null) {
            return constructed;
        }
        if (ctor.getParameterCount() == 0) {
            return BeanUtils.instantiateClass(ctor, new Object[0]);
        }
        ConstructorProperties cp = ctor.getAnnotation(ConstructorProperties.class);
        String[] paramNames = cp != null ? cp.value() : parameterNameDiscoverer.getParameterNames(ctor);
        Assert.state(paramNames != null, () -> {
            return "Cannot resolve parameter names for constructor " + ctor;
        });
        Class<?>[] paramTypes = ctor.getParameterTypes();
        Assert.state(paramNames.length == paramTypes.length, () -> {
            return "Invalid number of parameter names: " + paramNames.length + " for constructor " + ctor;
        });
        Object[] args = new Object[paramTypes.length];
        WebDataBinder binder = binderFactory.createBinder(webRequest, null, attributeName);
        String fieldDefaultPrefix = binder.getFieldDefaultPrefix();
        String fieldMarkerPrefix = binder.getFieldMarkerPrefix();
        boolean bindingFailure = false;
        Set<String> failedParams = new HashSet<>(4);
        for (int i = 0; i < paramNames.length; i++) {
            String paramName = paramNames[i];
            Class<?> paramType = paramTypes[i];
            Object value = webRequest.getParameterValues(paramName);
            if (value == null) {
                if (fieldDefaultPrefix != null) {
                    value = webRequest.getParameter(fieldDefaultPrefix + paramName);
                }
                if (value == null && fieldMarkerPrefix != null && webRequest.getParameter(fieldMarkerPrefix + paramName) != null) {
                    value = binder.getEmptyValue(paramType);
                }
            }
            try {
                MethodParameter methodParam = new FieldAwareConstructorParameter(ctor, i, paramName);
                if (value == null && methodParam.isOptional()) {
                    args[i] = methodParam.getParameterType() == Optional.class ? Optional.empty() : null;
                } else {
                    args[i] = binder.convertIfNecessary(value, paramType, methodParam);
                }
            } catch (TypeMismatchException ex) {
                ex.initPropertyName(paramName);
                args[i] = value;
                failedParams.add(paramName);
                binder.getBindingResult().recordFieldValue(paramName, paramType, value);
                binder.getBindingErrorProcessor().processPropertyAccessException(ex, binder.getBindingResult());
                bindingFailure = true;
            }
        }
        if (bindingFailure) {
            BindingResult result = binder.getBindingResult();
            for (int i2 = 0; i2 < paramNames.length; i2++) {
                String paramName2 = paramNames[i2];
                if (!failedParams.contains(paramName2)) {
                    Object value2 = args[i2];
                    result.recordFieldValue(paramName2, paramTypes[i2], value2);
                    validateValueIfApplicable(binder, parameter, ctor.getDeclaringClass(), paramName2, value2);
                }
            }
            throw new BindException(result);
        }
        return BeanUtils.instantiateClass(ctor, args);
    }

    @Nullable
    @Deprecated
    protected Object constructAttribute(Constructor<?> ctor, String attributeName, WebDataBinderFactory binderFactory, NativeWebRequest webRequest) throws Exception {
        return null;
    }

    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
        ((WebRequestDataBinder) binder).bind(request);
    }

    protected void validateIfApplicable(WebDataBinder binder, MethodParameter parameter) {
        Annotation[] parameterAnnotations;
        for (Annotation ann : parameter.getParameterAnnotations()) {
            Object[] validationHints = determineValidationHints(ann);
            if (validationHints != null) {
                binder.validate(validationHints);
                return;
            }
        }
    }

    protected void validateValueIfApplicable(WebDataBinder binder, MethodParameter parameter, Class<?> targetType, String fieldName, @Nullable Object value) {
        Annotation[] parameterAnnotations;
        for (Annotation ann : parameter.getParameterAnnotations()) {
            Object[] validationHints = determineValidationHints(ann);
            if (validationHints != null) {
                for (Validator validator : binder.getValidators()) {
                    if (validator instanceof SmartValidator) {
                        try {
                            ((SmartValidator) validator).validateValue(targetType, fieldName, value, binder.getBindingResult(), validationHints);
                        } catch (IllegalArgumentException e) {
                        }
                    }
                }
                return;
            }
        }
    }

    @Nullable
    private Object[] determineValidationHints(Annotation ann) {
        Validated validatedAnn = (Validated) AnnotationUtils.getAnnotation(ann, Validated.class);
        if (validatedAnn != null || ann.annotationType().getSimpleName().startsWith("Valid")) {
            Object hints = validatedAnn != null ? validatedAnn.value() : AnnotationUtils.getValue(ann);
            if (hints == null) {
                return new Object[0];
            }
            return hints instanceof Object[] ? (Object[]) hints : new Object[]{hints};
        }
        return null;
    }

    protected boolean isBindExceptionRequired(WebDataBinder binder, MethodParameter parameter) {
        return isBindExceptionRequired(parameter);
    }

    protected boolean isBindExceptionRequired(MethodParameter parameter) {
        int i = parameter.getParameterIndex();
        Class<?>[] paramTypes = parameter.getExecutable().getParameterTypes();
        boolean hasBindingResult = paramTypes.length > i + 1 && Errors.class.isAssignableFrom(paramTypes[i + 1]);
        return !hasBindingResult;
    }

    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(ModelAttribute.class) || (this.annotationNotRequired && !BeanUtils.isSimpleProperty(returnType.getParameterType()));
    }

    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (returnValue != null) {
            String name = ModelFactory.getNameForReturnValue(returnValue, returnType);
            mavContainer.addAttribute(name, returnValue);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/method/annotation/ModelAttributeMethodProcessor$FieldAwareConstructorParameter.class */
    public static class FieldAwareConstructorParameter extends MethodParameter {
        private final String parameterName;
        @Nullable
        private volatile Annotation[] combinedAnnotations;

        public FieldAwareConstructorParameter(Constructor<?> constructor, int parameterIndex, String parameterName) {
            super(constructor, parameterIndex);
            this.parameterName = parameterName;
        }

        @Override // org.springframework.core.MethodParameter
        public Annotation[] getParameterAnnotations() {
            Annotation[] anns = this.combinedAnnotations;
            if (anns == null) {
                anns = super.getParameterAnnotations();
                try {
                    Field field = getDeclaringClass().getDeclaredField(this.parameterName);
                    Annotation[] fieldAnns = field.getAnnotations();
                    if (fieldAnns.length > 0) {
                        List<Annotation> merged = new ArrayList<>(anns.length + fieldAnns.length);
                        merged.addAll(Arrays.asList(anns));
                        for (Annotation fieldAnn : fieldAnns) {
                            boolean existingType = false;
                            int length = anns.length;
                            int i = 0;
                            while (true) {
                                if (i >= length) {
                                    break;
                                }
                                Annotation ann = anns[i];
                                if (ann.annotationType() != fieldAnn.annotationType()) {
                                    i++;
                                } else {
                                    existingType = true;
                                    break;
                                }
                            }
                            if (!existingType) {
                                merged.add(fieldAnn);
                            }
                        }
                        anns = (Annotation[]) merged.toArray(new Annotation[0]);
                    }
                } catch (NoSuchFieldException | SecurityException e) {
                }
                this.combinedAnnotations = anns;
            }
            return anns;
        }

        @Override // org.springframework.core.MethodParameter
        public String getParameterName() {
            return this.parameterName;
        }
    }
}