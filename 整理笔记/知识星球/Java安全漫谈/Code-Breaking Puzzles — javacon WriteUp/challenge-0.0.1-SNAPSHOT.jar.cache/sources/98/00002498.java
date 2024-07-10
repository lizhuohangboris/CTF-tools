package org.springframework.web.context.request.async;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/request/async/DeferredResultInterceptorChain.class */
class DeferredResultInterceptorChain {
    private static final Log logger = LogFactory.getLog(DeferredResultInterceptorChain.class);
    private final List<DeferredResultProcessingInterceptor> interceptors;
    private int preProcessingIndex = -1;

    public DeferredResultInterceptorChain(List<DeferredResultProcessingInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public void applyBeforeConcurrentHandling(NativeWebRequest request, DeferredResult<?> deferredResult) throws Exception {
        for (DeferredResultProcessingInterceptor interceptor : this.interceptors) {
            interceptor.beforeConcurrentHandling(request, deferredResult);
        }
    }

    public void applyPreProcess(NativeWebRequest request, DeferredResult<?> deferredResult) throws Exception {
        for (DeferredResultProcessingInterceptor interceptor : this.interceptors) {
            interceptor.preProcess(request, deferredResult);
            this.preProcessingIndex++;
        }
    }

    public Object applyPostProcess(NativeWebRequest request, DeferredResult<?> deferredResult, Object concurrentResult) {
        try {
            for (int i = this.preProcessingIndex; i >= 0; i--) {
                this.interceptors.get(i).postProcess(request, deferredResult, concurrentResult);
            }
            return concurrentResult;
        } catch (Throwable ex) {
            return ex;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:5:0x0013  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void triggerAfterTimeout(org.springframework.web.context.request.NativeWebRequest r5, org.springframework.web.context.request.async.DeferredResult<?> r6) throws java.lang.Exception {
        /*
            r4 = this;
            r0 = r4
            java.util.List<org.springframework.web.context.request.async.DeferredResultProcessingInterceptor> r0 = r0.interceptors
            java.util.Iterator r0 = r0.iterator()
            r7 = r0
        La:
            r0 = r7
            boolean r0 = r0.hasNext()
            if (r0 == 0) goto L38
            r0 = r7
            java.lang.Object r0 = r0.next()
            org.springframework.web.context.request.async.DeferredResultProcessingInterceptor r0 = (org.springframework.web.context.request.async.DeferredResultProcessingInterceptor) r0
            r8 = r0
            r0 = r6
            boolean r0 = r0.isSetOrExpired()
            if (r0 == 0) goto L26
            return
        L26:
            r0 = r8
            r1 = r5
            r2 = r6
            boolean r0 = r0.handleTimeout(r1, r2)
            if (r0 != 0) goto L35
            goto L38
        L35:
            goto La
        L38:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.web.context.request.async.DeferredResultInterceptorChain.triggerAfterTimeout(org.springframework.web.context.request.NativeWebRequest, org.springframework.web.context.request.async.DeferredResult):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:5:0x0015  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean triggerAfterError(org.springframework.web.context.request.NativeWebRequest r6, org.springframework.web.context.request.async.DeferredResult<?> r7, java.lang.Throwable r8) throws java.lang.Exception {
        /*
            r5 = this;
            r0 = r5
            java.util.List<org.springframework.web.context.request.async.DeferredResultProcessingInterceptor> r0 = r0.interceptors
            java.util.Iterator r0 = r0.iterator()
            r9 = r0
        Lb:
            r0 = r9
            boolean r0 = r0.hasNext()
            if (r0 == 0) goto L3c
            r0 = r9
            java.lang.Object r0 = r0.next()
            org.springframework.web.context.request.async.DeferredResultProcessingInterceptor r0 = (org.springframework.web.context.request.async.DeferredResultProcessingInterceptor) r0
            r10 = r0
            r0 = r7
            boolean r0 = r0.isSetOrExpired()
            if (r0 == 0) goto L2a
            r0 = 0
            return r0
        L2a:
            r0 = r10
            r1 = r6
            r2 = r7
            r3 = r8
            boolean r0 = r0.handleError(r1, r2, r3)
            if (r0 != 0) goto L39
            r0 = 0
            return r0
        L39:
            goto Lb
        L3c:
            r0 = 1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.web.context.request.async.DeferredResultInterceptorChain.triggerAfterError(org.springframework.web.context.request.NativeWebRequest, org.springframework.web.context.request.async.DeferredResult, java.lang.Throwable):boolean");
    }

    public void triggerAfterCompletion(NativeWebRequest request, DeferredResult<?> deferredResult) {
        for (int i = this.preProcessingIndex; i >= 0; i--) {
            try {
                this.interceptors.get(i).afterCompletion(request, deferredResult);
            } catch (Throwable ex) {
                logger.trace("Ignoring failure in afterCompletion method", ex);
            }
        }
    }
}