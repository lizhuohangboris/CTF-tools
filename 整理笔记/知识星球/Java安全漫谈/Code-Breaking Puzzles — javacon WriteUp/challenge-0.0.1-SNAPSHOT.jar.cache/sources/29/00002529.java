package org.springframework.web.method.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.PropertyAccessor;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/method/support/InvocableHandlerMethod.class */
public class InvocableHandlerMethod extends HandlerMethod {
    @Nullable
    private WebDataBinderFactory dataBinderFactory;
    private HandlerMethodArgumentResolverComposite argumentResolvers;
    private ParameterNameDiscoverer parameterNameDiscoverer;

    public InvocableHandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod);
        this.argumentResolvers = new HandlerMethodArgumentResolverComposite();
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    }

    public InvocableHandlerMethod(Object bean, Method method) {
        super(bean, method);
        this.argumentResolvers = new HandlerMethodArgumentResolverComposite();
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    }

    public InvocableHandlerMethod(Object bean, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        super(bean, methodName, parameterTypes);
        this.argumentResolvers = new HandlerMethodArgumentResolverComposite();
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    }

    public void setDataBinderFactory(WebDataBinderFactory dataBinderFactory) {
        this.dataBinderFactory = dataBinderFactory;
    }

    public void setHandlerMethodArgumentResolvers(HandlerMethodArgumentResolverComposite argumentResolvers) {
        this.argumentResolvers = argumentResolvers;
    }

    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    @Nullable
    public Object invokeForRequest(NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {
        Object[] args = getMethodArgumentValues(request, mavContainer, providedArgs);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Arguments: " + Arrays.toString(args));
        }
        return doInvoke(args);
    }

    protected Object[] getMethodArgumentValues(NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {
        String message;
        MethodParameter[] parameters = getMethodParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter parameter = parameters[i];
            parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);
            args[i] = resolveProvidedArgument(parameter, providedArgs);
            if (args[i] == null) {
                if (this.argumentResolvers.supportsParameter(parameter)) {
                    try {
                        args[i] = this.argumentResolvers.resolveArgument(parameter, mavContainer, request, this.dataBinderFactory);
                    } catch (Exception ex) {
                        if (this.logger.isDebugEnabled() && (message = ex.getMessage()) != null && !message.contains(parameter.getExecutable().toGenericString())) {
                            this.logger.debug(formatArgumentError(parameter, message));
                        }
                        throw ex;
                    }
                } else if (args[i] == null) {
                    throw new IllegalStateException(formatArgumentError(parameter, "No suitable resolver"));
                }
            }
        }
        return args;
    }

    private static String formatArgumentError(MethodParameter param, String message) {
        return "Could not resolve parameter [" + param.getParameterIndex() + "] in " + param.getExecutable().toGenericString() + (StringUtils.hasText(message) ? ": " + message : "");
    }

    @Nullable
    private Object resolveProvidedArgument(MethodParameter parameter, @Nullable Object... providedArgs) {
        if (providedArgs == null) {
            return null;
        }
        for (Object providedArg : providedArgs) {
            if (parameter.getParameterType().isInstance(providedArg)) {
                return providedArg;
            }
        }
        return null;
    }

    protected Object doInvoke(Object... args) throws Exception {
        ReflectionUtils.makeAccessible(getBridgedMethod());
        try {
            return getBridgedMethod().invoke(getBean(), args);
        } catch (IllegalArgumentException ex) {
            assertTargetBean(getBridgedMethod(), getBean(), args);
            String text = ex.getMessage() != null ? ex.getMessage() : "Illegal argument";
            throw new IllegalStateException(formatInvokeError(text, args), ex);
        } catch (InvocationTargetException ex2) {
            Throwable targetException = ex2.getTargetException();
            if (targetException instanceof RuntimeException) {
                throw ((RuntimeException) targetException);
            }
            if (targetException instanceof Error) {
                throw ((Error) targetException);
            }
            if (targetException instanceof Exception) {
                throw ((Exception) targetException);
            }
            throw new IllegalStateException(formatInvokeError("Invocation failure", args), targetException);
        }
    }

    private void assertTargetBean(Method method, Object targetBean, Object[] args) {
        Class<?> methodDeclaringClass = method.getDeclaringClass();
        Class<?> targetBeanClass = targetBean.getClass();
        if (!methodDeclaringClass.isAssignableFrom(targetBeanClass)) {
            String text = "The mapped handler method class '" + methodDeclaringClass.getName() + "' is not an instance of the actual controller bean class '" + targetBeanClass.getName() + "'. If the controller requires proxying (e.g. due to @Transactional), please use class-based proxying.";
            throw new IllegalStateException(formatInvokeError(text, args));
        }
    }

    private String formatInvokeError(String text, Object[] args) {
        String formattedArgs = (String) IntStream.range(0, args.length).mapToObj(i -> {
            return args[i] != null ? PropertyAccessor.PROPERTY_KEY_PREFIX + i + "] [type=" + args[i].getClass().getName() + "] [value=" + args[i] + "]" : PropertyAccessor.PROPERTY_KEY_PREFIX + i + "] [null]";
        }).collect(Collectors.joining(",\n", " ", " "));
        return text + "\nController [" + getBeanType().getName() + "]\nMethod [" + getBridgedMethod().toGenericString() + "] with argument values:\n" + formattedArgs;
    }
}