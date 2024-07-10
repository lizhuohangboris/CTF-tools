package org.springframework.web.context.request.async;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/request/async/WebAsyncManager.class */
public final class WebAsyncManager {
    private static final Object RESULT_NONE = new Object();
    private static final AsyncTaskExecutor DEFAULT_TASK_EXECUTOR = new SimpleAsyncTaskExecutor(WebAsyncManager.class.getSimpleName());
    private static final Log logger = LogFactory.getLog(WebAsyncManager.class);
    private static final CallableProcessingInterceptor timeoutCallableInterceptor = new TimeoutCallableProcessingInterceptor();
    private static final DeferredResultProcessingInterceptor timeoutDeferredResultInterceptor = new TimeoutDeferredResultProcessingInterceptor();
    private static Boolean taskExecutorWarning = true;
    private AsyncWebRequest asyncWebRequest;
    private volatile Object[] concurrentResultContext;
    private AsyncTaskExecutor taskExecutor = DEFAULT_TASK_EXECUTOR;
    private volatile Object concurrentResult = RESULT_NONE;
    private final Map<Object, CallableProcessingInterceptor> callableInterceptors = new LinkedHashMap();
    private final Map<Object, DeferredResultProcessingInterceptor> deferredResultInterceptors = new LinkedHashMap();

    public void setAsyncWebRequest(AsyncWebRequest asyncWebRequest) {
        Assert.notNull(asyncWebRequest, "AsyncWebRequest must not be null");
        this.asyncWebRequest = asyncWebRequest;
        this.asyncWebRequest.addCompletionHandler(() -> {
            asyncWebRequest.removeAttribute(WebAsyncUtils.WEB_ASYNC_MANAGER_ATTRIBUTE, 0);
        });
    }

    public void setTaskExecutor(AsyncTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public boolean isConcurrentHandlingStarted() {
        return this.asyncWebRequest != null && this.asyncWebRequest.isAsyncStarted();
    }

    public boolean hasConcurrentResult() {
        return this.concurrentResult != RESULT_NONE;
    }

    public Object getConcurrentResult() {
        return this.concurrentResult;
    }

    public Object[] getConcurrentResultContext() {
        return this.concurrentResultContext;
    }

    @Nullable
    public CallableProcessingInterceptor getCallableInterceptor(Object key) {
        return this.callableInterceptors.get(key);
    }

    @Nullable
    public DeferredResultProcessingInterceptor getDeferredResultInterceptor(Object key) {
        return this.deferredResultInterceptors.get(key);
    }

    public void registerCallableInterceptor(Object key, CallableProcessingInterceptor interceptor) {
        Assert.notNull(key, "Key is required");
        Assert.notNull(interceptor, "CallableProcessingInterceptor  is required");
        this.callableInterceptors.put(key, interceptor);
    }

    public void registerCallableInterceptors(CallableProcessingInterceptor... interceptors) {
        Assert.notNull(interceptors, "A CallableProcessingInterceptor is required");
        for (CallableProcessingInterceptor interceptor : interceptors) {
            String key = interceptor.getClass().getName() + ":" + interceptor.hashCode();
            this.callableInterceptors.put(key, interceptor);
        }
    }

    public void registerDeferredResultInterceptor(Object key, DeferredResultProcessingInterceptor interceptor) {
        Assert.notNull(key, "Key is required");
        Assert.notNull(interceptor, "DeferredResultProcessingInterceptor is required");
        this.deferredResultInterceptors.put(key, interceptor);
    }

    public void registerDeferredResultInterceptors(DeferredResultProcessingInterceptor... interceptors) {
        Assert.notNull(interceptors, "A DeferredResultProcessingInterceptor is required");
        for (DeferredResultProcessingInterceptor interceptor : interceptors) {
            String key = interceptor.getClass().getName() + ":" + interceptor.hashCode();
            this.deferredResultInterceptors.put(key, interceptor);
        }
    }

    public void clearConcurrentResult() {
        synchronized (this) {
            this.concurrentResult = RESULT_NONE;
            this.concurrentResultContext = null;
        }
    }

    public void startCallableProcessing(Callable<?> callable, Object... processingContext) throws Exception {
        Assert.notNull(callable, "Callable must not be null");
        startCallableProcessing(new WebAsyncTask<>(callable), processingContext);
    }

    public void startCallableProcessing(WebAsyncTask<?> webAsyncTask, Object... processingContext) throws Exception {
        Assert.notNull(webAsyncTask, "WebAsyncTask must not be null");
        Assert.state(this.asyncWebRequest != null, "AsyncWebRequest must not be null");
        Long timeout = webAsyncTask.getTimeout();
        if (timeout != null) {
            this.asyncWebRequest.setTimeout(timeout);
        }
        AsyncTaskExecutor executor = webAsyncTask.getExecutor();
        if (executor != null) {
            this.taskExecutor = executor;
        } else {
            logExecutorWarning();
        }
        List<CallableProcessingInterceptor> interceptors = new ArrayList<>();
        interceptors.add(webAsyncTask.getInterceptor());
        interceptors.addAll(this.callableInterceptors.values());
        interceptors.add(timeoutCallableInterceptor);
        Callable<?> callable = webAsyncTask.getCallable();
        CallableInterceptorChain interceptorChain = new CallableInterceptorChain(interceptors);
        this.asyncWebRequest.addTimeoutHandler(() -> {
            logger.debug("Async request timeout for " + formatRequestUri());
            Object result = interceptorChain.triggerAfterTimeout(this.asyncWebRequest, callable);
            if (result != CallableProcessingInterceptor.RESULT_NONE) {
                setConcurrentResultAndDispatch(result);
            }
        });
        this.asyncWebRequest.addErrorHandler(ex -> {
            logger.debug("Async request error for " + formatRequestUri() + ": " + ex);
            Object result = interceptorChain.triggerAfterError(this.asyncWebRequest, callable, ex);
            setConcurrentResultAndDispatch(result != CallableProcessingInterceptor.RESULT_NONE ? result : ex);
        });
        this.asyncWebRequest.addCompletionHandler(() -> {
            interceptorChain.triggerAfterCompletion(this.asyncWebRequest, callable);
        });
        interceptorChain.applyBeforeConcurrentHandling(this.asyncWebRequest, callable);
        startAsyncProcessing(processingContext);
        try {
            Future<?> future = this.taskExecutor.submit(()
            /*  JADX ERROR: Method code generation error
                jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x00db: INVOKE  (r0v36 'future' java.util.concurrent.Future<?>) = 
                  (wrap: org.springframework.core.task.AsyncTaskExecutor : 0x00ce: IGET  (r0v35 org.springframework.core.task.AsyncTaskExecutor A[REMOVE]) = 
                  (r5v0 'this' org.springframework.web.context.request.async.WebAsyncManager A[D('this' org.springframework.web.context.request.async.WebAsyncManager), IMMUTABLE_TYPE, THIS])
                 org.springframework.web.context.request.async.WebAsyncManager.taskExecutor org.springframework.core.task.AsyncTaskExecutor)
                  (wrap: java.lang.Runnable : 0x00d6: INVOKE_CUSTOM (r1v23 java.lang.Runnable A[REMOVE]) = 
                  (r5v0 'this' org.springframework.web.context.request.async.WebAsyncManager A[D('this' org.springframework.web.context.request.async.WebAsyncManager), DONT_INLINE, IMMUTABLE_TYPE, THIS])
                  (r0v21 'interceptorChain' org.springframework.web.context.request.async.CallableInterceptorChain A[D('interceptorChain' org.springframework.web.context.request.async.CallableInterceptorChain), DONT_INLINE])
                  (r0v20 'callable' java.util.concurrent.Callable<?> A[D('callable' java.util.concurrent.Callable<?>), DONT_INLINE])
                
                 handle type: INVOKE_DIRECT
                 lambda: java.lang.Runnable.run():void
                 call insn: ?: INVOKE  
                  (r1 I:org.springframework.web.context.request.async.WebAsyncManager)
                  (r2 I:org.springframework.web.context.request.async.CallableInterceptorChain)
                  (r3 I:java.util.concurrent.Callable)
                 type: DIRECT call: org.springframework.web.context.request.async.WebAsyncManager.lambda$startCallableProcessing$4(org.springframework.web.context.request.async.CallableInterceptorChain, java.util.concurrent.Callable):void)
                 type: INTERFACE call: org.springframework.core.task.AsyncTaskExecutor.submit(java.lang.Runnable):java.util.concurrent.Future in method: org.springframework.web.context.request.async.WebAsyncManager.startCallableProcessing(org.springframework.web.context.request.async.WebAsyncTask<?>, java.lang.Object[]):void, file: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/request/async/WebAsyncManager.class
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:309)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:272)
                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:91)
                	at jadx.core.dex.nodes.IBlock.generate(IBlock.java:15)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
                	at jadx.core.dex.regions.Region.generate(Region.java:35)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:80)
                	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:302)
                	at jadx.core.dex.regions.TryCatchRegion.generate(TryCatchRegion.java:85)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
                	at jadx.core.dex.regions.Region.generate(Region.java:35)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:296)
                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:275)
                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:377)
                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:306)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:272)
                	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
                	at java.base/java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                	at java.base/java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                Caused by: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.SSAVar.setCodeVar(jadx.core.dex.instructions.args.CodeVar)" because the return value of "jadx.core.dex.instructions.args.RegisterArg.getSVar()" is null
                	at jadx.core.codegen.InsnGen.makeInlinedLambdaMethod(InsnGen.java:1021)
                	at jadx.core.codegen.InsnGen.makeInvokeLambda(InsnGen.java:924)
                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:815)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:421)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:144)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:120)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:1097)
                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:872)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:421)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:302)
                	... 21 more
                */
            /*
                Method dump skipped, instructions count: 263
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: org.springframework.web.context.request.async.WebAsyncManager.startCallableProcessing(org.springframework.web.context.request.async.WebAsyncTask, java.lang.Object[]):void");
        }

        private void logExecutorWarning() {
            if (taskExecutorWarning.booleanValue() && logger.isWarnEnabled()) {
                synchronized (DEFAULT_TASK_EXECUTOR) {
                    AsyncTaskExecutor executor = this.taskExecutor;
                    if (taskExecutorWarning.booleanValue() && ((executor instanceof SimpleAsyncTaskExecutor) || (executor instanceof SyncTaskExecutor))) {
                        String executorTypeName = executor.getClass().getSimpleName();
                        logger.warn("\n!!!\nAn Executor is required to handle java.util.concurrent.Callable return values.\nPlease, configure a TaskExecutor in the MVC config under \"async support\".\nThe " + executorTypeName + " currently in use is not suitable under load.\n-------------------------------\nRequest URI: '" + formatRequestUri() + "'\n!!!");
                        taskExecutorWarning = false;
                    }
                }
            }
        }

        private String formatRequestUri() {
            HttpServletRequest request = (HttpServletRequest) this.asyncWebRequest.getNativeRequest(HttpServletRequest.class);
            return request != null ? request.getRequestURI() : "servlet container";
        }

        private void setConcurrentResultAndDispatch(Object result) {
            synchronized (this) {
                if (this.concurrentResult != RESULT_NONE) {
                    return;
                }
                this.concurrentResult = result;
                if (this.asyncWebRequest.isAsyncComplete()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Async result set but request already complete: " + formatRequestUri());
                        return;
                    }
                    return;
                }
                if (logger.isDebugEnabled()) {
                    boolean isError = result instanceof Throwable;
                    logger.debug("Async " + (isError ? "error" : "result set") + ", dispatch to " + formatRequestUri());
                }
                this.asyncWebRequest.dispatch();
            }
        }

        public void startDeferredResultProcessing(DeferredResult<?> deferredResult, Object... processingContext) throws Exception {
            Assert.notNull(deferredResult, "DeferredResult must not be null");
            Assert.state(this.asyncWebRequest != null, "AsyncWebRequest must not be null");
            Long timeout = deferredResult.getTimeoutValue();
            if (timeout != null) {
                this.asyncWebRequest.setTimeout(timeout);
            }
            List<DeferredResultProcessingInterceptor> interceptors = new ArrayList<>();
            interceptors.add(deferredResult.getInterceptor());
            interceptors.addAll(this.deferredResultInterceptors.values());
            interceptors.add(timeoutDeferredResultInterceptor);
            DeferredResultInterceptorChain interceptorChain = new DeferredResultInterceptorChain(interceptors);
            this.asyncWebRequest.addTimeoutHandler(() -> {
                try {
                    interceptorChain.triggerAfterTimeout(this.asyncWebRequest, deferredResult);
                } catch (Throwable ex) {
                    setConcurrentResultAndDispatch(ex);
                }
            });
            this.asyncWebRequest.addErrorHandler(ex -> {
                try {
                    if (!interceptorChain.triggerAfterError(this.asyncWebRequest, deferredResult, ex)) {
                        return;
                    }
                    deferredResult.setErrorResult(ex);
                } catch (Throwable interceptorEx) {
                    setConcurrentResultAndDispatch(interceptorEx);
                }
            });
            this.asyncWebRequest.addCompletionHandler(() -> {
                interceptorChain.triggerAfterCompletion(this.asyncWebRequest, deferredResult);
            });
            interceptorChain.applyBeforeConcurrentHandling(this.asyncWebRequest, deferredResult);
            startAsyncProcessing(processingContext);
            try {
                interceptorChain.applyPreProcess(this.asyncWebRequest, deferredResult);
                deferredResult.setResultHandler(result -> {
                    setConcurrentResultAndDispatch(interceptorChain.applyPostProcess(this.asyncWebRequest, deferredResult, result));
                });
            } catch (Throwable ex2) {
                setConcurrentResultAndDispatch(ex2);
            }
        }

        private void startAsyncProcessing(Object[] processingContext) {
            synchronized (this) {
                this.concurrentResult = RESULT_NONE;
                this.concurrentResultContext = processingContext;
            }
            this.asyncWebRequest.startAsync();
            if (logger.isDebugEnabled()) {
                logger.debug("Started async request");
            }
        }
    }