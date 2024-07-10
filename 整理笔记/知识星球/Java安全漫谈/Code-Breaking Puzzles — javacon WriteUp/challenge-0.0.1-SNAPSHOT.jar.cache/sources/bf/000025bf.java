package org.springframework.web.servlet.handler;

import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/handler/AbstractHandlerExceptionResolver.class */
public abstract class AbstractHandlerExceptionResolver implements HandlerExceptionResolver, Ordered {
    private static final String HEADER_CACHE_CONTROL = "Cache-Control";
    @Nullable
    private Set<?> mappedHandlers;
    @Nullable
    private Class<?>[] mappedHandlerClasses;
    @Nullable
    private Log warnLogger;
    protected final Log logger = LogFactory.getLog(getClass());
    private int order = Integer.MAX_VALUE;
    private boolean preventResponseCaching = false;

    @Nullable
    protected abstract ModelAndView doResolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @Nullable Object obj, Exception exc);

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setMappedHandlers(Set<?> mappedHandlers) {
        this.mappedHandlers = mappedHandlers;
    }

    public void setMappedHandlerClasses(Class<?>... mappedHandlerClasses) {
        this.mappedHandlerClasses = mappedHandlerClasses;
    }

    public void setWarnLogCategory(String loggerName) {
        this.warnLogger = LogFactory.getLog(loggerName);
    }

    public void setPreventResponseCaching(boolean preventResponseCaching) {
        this.preventResponseCaching = preventResponseCaching;
    }

    @Override // org.springframework.web.servlet.HandlerExceptionResolver
    @Nullable
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
        if (shouldApplyTo(request, handler)) {
            prepareResponse(ex, response);
            ModelAndView result = doResolveException(request, response, handler, ex);
            if (result != null) {
                if (this.logger.isDebugEnabled() && (this.warnLogger == null || !this.warnLogger.isWarnEnabled())) {
                    this.logger.debug("Resolved [" + ex + "]" + (result.isEmpty() ? "" : " to " + result));
                }
                logException(ex, request);
            }
            return result;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean shouldApplyTo(HttpServletRequest request, @Nullable Object handler) {
        Class<?>[] clsArr;
        if (handler != null) {
            if (this.mappedHandlers != null && this.mappedHandlers.contains(handler)) {
                return true;
            }
            if (this.mappedHandlerClasses != null) {
                for (Class<?> handlerClass : this.mappedHandlerClasses) {
                    if (handlerClass.isInstance(handler)) {
                        return true;
                    }
                }
            }
        }
        return this.mappedHandlers == null && this.mappedHandlerClasses == null;
    }

    protected void logException(Exception ex, HttpServletRequest request) {
        if (this.warnLogger != null && this.warnLogger.isWarnEnabled()) {
            this.warnLogger.warn(buildLogMessage(ex, request));
        }
    }

    protected String buildLogMessage(Exception ex, HttpServletRequest request) {
        return "Resolved [" + ex + "]";
    }

    protected void prepareResponse(Exception ex, HttpServletResponse response) {
        if (this.preventResponseCaching) {
            preventCaching(response);
        }
    }

    protected void preventCaching(HttpServletResponse response) {
        response.addHeader("Cache-Control", "no-store");
    }
}