package org.springframework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.OrderComparator;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/config/annotation/InterceptorRegistry.class */
public class InterceptorRegistry {
    private final List<InterceptorRegistration> registrations = new ArrayList();
    private static final Comparator<Object> INTERCEPTOR_ORDER_COMPARATOR = OrderComparator.INSTANCE.withSourceProvider(object -> {
        if (object instanceof InterceptorRegistration) {
            InterceptorRegistration interceptorRegistration = (InterceptorRegistration) object;
            interceptorRegistration.getClass();
            return this::getOrder;
        }
        return null;
    });

    public InterceptorRegistration addInterceptor(HandlerInterceptor interceptor) {
        InterceptorRegistration registration = new InterceptorRegistration(interceptor);
        this.registrations.add(registration);
        return registration;
    }

    public InterceptorRegistration addWebRequestInterceptor(WebRequestInterceptor interceptor) {
        WebRequestHandlerInterceptorAdapter adapted = new WebRequestHandlerInterceptorAdapter(interceptor);
        InterceptorRegistration registration = new InterceptorRegistration(adapted);
        this.registrations.add(registration);
        return registration;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<Object> getInterceptors() {
        return (List) this.registrations.stream().sorted(INTERCEPTOR_ORDER_COMPARATOR).map((v0) -> {
            return v0.getInterceptor();
        }).collect(Collectors.toList());
    }
}