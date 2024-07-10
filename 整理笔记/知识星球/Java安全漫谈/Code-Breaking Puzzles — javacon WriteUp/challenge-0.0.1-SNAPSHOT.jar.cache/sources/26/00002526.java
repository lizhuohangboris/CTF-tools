package org.springframework.web.method.support;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/method/support/HandlerMethodArgumentResolverComposite.class */
public class HandlerMethodArgumentResolverComposite implements HandlerMethodArgumentResolver {
    protected final Log logger = LogFactory.getLog(getClass());
    private final List<HandlerMethodArgumentResolver> argumentResolvers = new LinkedList();
    private final Map<MethodParameter, HandlerMethodArgumentResolver> argumentResolverCache = new ConcurrentHashMap(256);

    public HandlerMethodArgumentResolverComposite addResolver(HandlerMethodArgumentResolver resolver) {
        this.argumentResolvers.add(resolver);
        return this;
    }

    public HandlerMethodArgumentResolverComposite addResolvers(@Nullable HandlerMethodArgumentResolver... resolvers) {
        if (resolvers != null) {
            for (HandlerMethodArgumentResolver resolver : resolvers) {
                this.argumentResolvers.add(resolver);
            }
        }
        return this;
    }

    public HandlerMethodArgumentResolverComposite addResolvers(@Nullable List<? extends HandlerMethodArgumentResolver> resolvers) {
        if (resolvers != null) {
            for (HandlerMethodArgumentResolver resolver : resolvers) {
                this.argumentResolvers.add(resolver);
            }
        }
        return this;
    }

    public List<HandlerMethodArgumentResolver> getResolvers() {
        return Collections.unmodifiableList(this.argumentResolvers);
    }

    public void clear() {
        this.argumentResolvers.clear();
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        return getArgumentResolver(parameter) != null;
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    @Nullable
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        HandlerMethodArgumentResolver resolver = getArgumentResolver(parameter);
        if (resolver == null) {
            throw new IllegalArgumentException("Unknown parameter type [" + parameter.getParameterType().getName() + "]");
        }
        return resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
    }

    @Nullable
    private HandlerMethodArgumentResolver getArgumentResolver(MethodParameter parameter) {
        HandlerMethodArgumentResolver result = this.argumentResolverCache.get(parameter);
        if (result == null) {
            Iterator<HandlerMethodArgumentResolver> it = this.argumentResolvers.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                HandlerMethodArgumentResolver methodArgumentResolver = it.next();
                if (methodArgumentResolver.supportsParameter(parameter)) {
                    result = methodArgumentResolver;
                    this.argumentResolverCache.put(parameter, result);
                    break;
                }
            }
        }
        return result;
    }
}