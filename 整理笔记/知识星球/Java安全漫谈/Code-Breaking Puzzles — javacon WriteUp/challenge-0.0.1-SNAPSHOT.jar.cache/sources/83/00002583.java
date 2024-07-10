package org.springframework.web.servlet;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/HandlerExecutionChain.class */
public class HandlerExecutionChain {
    private static final Log logger = LogFactory.getLog(HandlerExecutionChain.class);
    private final Object handler;
    @Nullable
    private HandlerInterceptor[] interceptors;
    @Nullable
    private List<HandlerInterceptor> interceptorList;
    private int interceptorIndex;

    public HandlerExecutionChain(Object handler) {
        this(handler, null);
    }

    public HandlerExecutionChain(Object handler, @Nullable HandlerInterceptor... interceptors) {
        this.interceptorIndex = -1;
        if (handler instanceof HandlerExecutionChain) {
            HandlerExecutionChain originalChain = (HandlerExecutionChain) handler;
            this.handler = originalChain.getHandler();
            this.interceptorList = new ArrayList();
            CollectionUtils.mergeArrayIntoCollection(originalChain.getInterceptors(), this.interceptorList);
            CollectionUtils.mergeArrayIntoCollection(interceptors, this.interceptorList);
            return;
        }
        this.handler = handler;
        this.interceptors = interceptors;
    }

    public Object getHandler() {
        return this.handler;
    }

    public void addInterceptor(HandlerInterceptor interceptor) {
        initInterceptorList().add(interceptor);
    }

    public void addInterceptors(HandlerInterceptor... interceptors) {
        if (!ObjectUtils.isEmpty((Object[]) interceptors)) {
            CollectionUtils.mergeArrayIntoCollection(interceptors, initInterceptorList());
        }
    }

    private List<HandlerInterceptor> initInterceptorList() {
        if (this.interceptorList == null) {
            this.interceptorList = new ArrayList();
            if (this.interceptors != null) {
                CollectionUtils.mergeArrayIntoCollection(this.interceptors, this.interceptorList);
            }
        }
        this.interceptors = null;
        return this.interceptorList;
    }

    @Nullable
    public HandlerInterceptor[] getInterceptors() {
        if (this.interceptors == null && this.interceptorList != null) {
            this.interceptors = (HandlerInterceptor[]) this.interceptorList.toArray(new HandlerInterceptor[0]);
        }
        return this.interceptors;
    }

    public boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HandlerInterceptor[] interceptors = getInterceptors();
        if (!ObjectUtils.isEmpty((Object[]) interceptors)) {
            for (int i = 0; i < interceptors.length; i++) {
                HandlerInterceptor interceptor = interceptors[i];
                if (!interceptor.preHandle(request, response, this.handler)) {
                    triggerAfterCompletion(request, response, null);
                    return false;
                }
                this.interceptorIndex = i;
            }
            return true;
        }
        return true;
    }

    public void applyPostHandle(HttpServletRequest request, HttpServletResponse response, @Nullable ModelAndView mv) throws Exception {
        HandlerInterceptor[] interceptors = getInterceptors();
        if (!ObjectUtils.isEmpty((Object[]) interceptors)) {
            for (int i = interceptors.length - 1; i >= 0; i--) {
                HandlerInterceptor interceptor = interceptors[i];
                interceptor.postHandle(request, response, this.handler, mv);
            }
        }
    }

    public void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, @Nullable Exception ex) throws Exception {
        HandlerInterceptor[] interceptors = getInterceptors();
        if (!ObjectUtils.isEmpty((Object[]) interceptors)) {
            for (int i = this.interceptorIndex; i >= 0; i--) {
                HandlerInterceptor interceptor = interceptors[i];
                try {
                    interceptor.afterCompletion(request, response, this.handler, ex);
                } catch (Throwable ex2) {
                    logger.error("HandlerInterceptor.afterCompletion threw exception", ex2);
                }
            }
        }
    }

    public void applyAfterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response) {
        HandlerInterceptor[] interceptors = getInterceptors();
        if (!ObjectUtils.isEmpty((Object[]) interceptors)) {
            for (int i = interceptors.length - 1; i >= 0; i--) {
                if (interceptors[i] instanceof AsyncHandlerInterceptor) {
                    try {
                        AsyncHandlerInterceptor asyncInterceptor = (AsyncHandlerInterceptor) interceptors[i];
                        asyncInterceptor.afterConcurrentHandlingStarted(request, response, this.handler);
                    } catch (Throwable ex) {
                        logger.error("Interceptor [" + interceptors[i] + "] failed in afterConcurrentHandlingStarted", ex);
                    }
                }
            }
        }
    }

    public String toString() {
        Object handler = getHandler();
        StringBuilder sb = new StringBuilder();
        sb.append("HandlerExecutionChain with [").append(handler).append("] and ");
        if (this.interceptorList != null) {
            sb.append(this.interceptorList.size());
        } else if (this.interceptors != null) {
            sb.append(this.interceptors.length);
        } else {
            sb.append(0);
        }
        return sb.append(" interceptors").toString();
    }
}