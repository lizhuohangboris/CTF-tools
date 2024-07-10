package org.springframework.web.servlet.mvc.method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.WebContentGenerator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/AbstractHandlerMethodAdapter.class */
public abstract class AbstractHandlerMethodAdapter extends WebContentGenerator implements HandlerAdapter, Ordered {
    private int order;

    protected abstract boolean supportsInternal(HandlerMethod handlerMethod);

    @Nullable
    protected abstract ModelAndView handleInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HandlerMethod handlerMethod) throws Exception;

    protected abstract long getLastModifiedInternal(HttpServletRequest httpServletRequest, HandlerMethod handlerMethod);

    public AbstractHandlerMethodAdapter() {
        super(false);
        this.order = Integer.MAX_VALUE;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Override // org.springframework.web.servlet.HandlerAdapter
    public final boolean supports(Object handler) {
        return (handler instanceof HandlerMethod) && supportsInternal((HandlerMethod) handler);
    }

    @Override // org.springframework.web.servlet.HandlerAdapter
    @Nullable
    public final ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return handleInternal(request, response, (HandlerMethod) handler);
    }

    @Override // org.springframework.web.servlet.HandlerAdapter
    public final long getLastModified(HttpServletRequest request, Object handler) {
        return getLastModifiedInternal(request, (HandlerMethod) handler);
    }
}