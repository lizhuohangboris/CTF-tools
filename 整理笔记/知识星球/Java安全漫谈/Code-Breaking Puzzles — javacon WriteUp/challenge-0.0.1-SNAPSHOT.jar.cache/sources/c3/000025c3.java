package org.springframework.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/handler/AbstractHandlerMethodExceptionResolver.class */
public abstract class AbstractHandlerMethodExceptionResolver extends AbstractHandlerExceptionResolver {
    @Nullable
    protected abstract ModelAndView doResolveHandlerMethodException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @Nullable HandlerMethod handlerMethod, Exception exc);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver
    public boolean shouldApplyTo(HttpServletRequest request, @Nullable Object handler) {
        if (handler == null) {
            return super.shouldApplyTo(request, null);
        }
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            return super.shouldApplyTo(request, handlerMethod.getBean());
        }
        return false;
    }

    @Override // org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver
    @Nullable
    protected final ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
        return doResolveHandlerMethodException(request, response, (HandlerMethod) handler, ex);
    }
}