package org.apache.catalina.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.naming.NamingException;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.AsyncDispatcher;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Request;
import org.apache.coyote.ActionCode;
import org.apache.coyote.AsyncContextCallback;
import org.apache.coyote.RequestInfo;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/AsyncContextImpl.class */
public class AsyncContextImpl implements AsyncContext, AsyncContextCallback {
    private static final Log log = LogFactory.getLog(AsyncContextImpl.class);
    protected static final StringManager sm = StringManager.getManager(Constants.Package);
    private final Object asyncContextLock = new Object();
    private volatile ServletRequest servletRequest = null;
    private volatile ServletResponse servletResponse = null;
    private final List<AsyncListenerWrapper> listeners = new ArrayList();
    private boolean hasOriginalRequestAndResponse = true;
    private volatile Runnable dispatch = null;
    private Context context = null;
    private long timeout = -1;
    private AsyncEvent event = null;
    private volatile Request request;

    public AsyncContextImpl(Request request) {
        if (log.isDebugEnabled()) {
            logDebug("Constructor");
        }
        this.request = request;
    }

    @Override // javax.servlet.AsyncContext
    public void complete() {
        if (log.isDebugEnabled()) {
            logDebug("complete   ");
        }
        check();
        this.request.getCoyoteRequest().action(ActionCode.ASYNC_COMPLETE, null);
    }

    @Override // org.apache.coyote.AsyncContextCallback
    public void fireOnComplete() {
        List<AsyncListenerWrapper> listenersCopy = new ArrayList<>();
        listenersCopy.addAll(this.listeners);
        ClassLoader oldCL = this.context.bind(Globals.IS_SECURITY_ENABLED, null);
        try {
            for (AsyncListenerWrapper listener : listenersCopy) {
                listener.fireOnComplete(this.event);
            }
        } finally {
            this.context.fireRequestDestroyEvent(this.request.getRequest());
            clearServletRequestResponse();
            this.context.unbind(Globals.IS_SECURITY_ENABLED, oldCL);
        }
    }

    public boolean timeout() {
        AtomicBoolean result = new AtomicBoolean();
        this.request.getCoyoteRequest().action(ActionCode.ASYNC_TIMEOUT, result);
        Context context = this.context;
        if (result.get()) {
            ClassLoader oldCL = context.bind(false, null);
            try {
                List<AsyncListenerWrapper> listenersCopy = new ArrayList<>();
                listenersCopy.addAll(this.listeners);
                for (AsyncListenerWrapper listener : listenersCopy) {
                    listener.fireOnTimeout(this.event);
                }
                this.request.getCoyoteRequest().action(ActionCode.ASYNC_IS_TIMINGOUT, result);
                context.unbind(false, oldCL);
            } catch (Throwable th) {
                context.unbind(false, oldCL);
                throw th;
            }
        }
        return !result.get();
    }

    @Override // javax.servlet.AsyncContext
    public void dispatch() {
        String path;
        String cpath;
        check();
        ServletRequest servletRequest = getRequest();
        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest sr = (HttpServletRequest) servletRequest;
            path = sr.getRequestURI();
            cpath = sr.getContextPath();
        } else {
            path = this.request.getRequestURI();
            cpath = this.request.getContextPath();
        }
        if (cpath.length() > 1) {
            path = path.substring(cpath.length());
        }
        if (!this.context.getDispatchersUseEncodedPaths()) {
            path = UDecoder.URLDecode(path, StandardCharsets.UTF_8);
        }
        dispatch(path);
    }

    @Override // javax.servlet.AsyncContext
    public void dispatch(String path) {
        check();
        dispatch(getRequest().getServletContext(), path);
    }

    @Override // javax.servlet.AsyncContext
    public void dispatch(ServletContext context, String path) {
        synchronized (this.asyncContextLock) {
            if (log.isDebugEnabled()) {
                logDebug("dispatch   ");
            }
            check();
            if (this.dispatch != null) {
                throw new IllegalStateException(sm.getString("asyncContextImpl.dispatchingStarted"));
            }
            if (this.request.getAttribute(AsyncContext.ASYNC_REQUEST_URI) == null) {
                this.request.setAttribute(AsyncContext.ASYNC_REQUEST_URI, this.request.getRequestURI());
                this.request.setAttribute(AsyncContext.ASYNC_CONTEXT_PATH, this.request.getContextPath());
                this.request.setAttribute(AsyncContext.ASYNC_SERVLET_PATH, this.request.getServletPath());
                this.request.setAttribute(AsyncContext.ASYNC_PATH_INFO, this.request.getPathInfo());
                this.request.setAttribute(AsyncContext.ASYNC_QUERY_STRING, this.request.getQueryString());
            }
            RequestDispatcher requestDispatcher = context.getRequestDispatcher(path);
            if (!(requestDispatcher instanceof AsyncDispatcher)) {
                throw new UnsupportedOperationException(sm.getString("asyncContextImpl.noAsyncDispatcher"));
            }
            AsyncDispatcher applicationDispatcher = (AsyncDispatcher) requestDispatcher;
            ServletRequest servletRequest = getRequest();
            ServletResponse servletResponse = getResponse();
            this.dispatch = new AsyncRunnable(this.request, applicationDispatcher, servletRequest, servletResponse);
            this.request.getCoyoteRequest().action(ActionCode.ASYNC_DISPATCH, null);
            clearServletRequestResponse();
        }
    }

    @Override // javax.servlet.AsyncContext
    public ServletRequest getRequest() {
        check();
        if (this.servletRequest == null) {
            throw new IllegalStateException(sm.getString("asyncContextImpl.request.ise"));
        }
        return this.servletRequest;
    }

    @Override // javax.servlet.AsyncContext
    public ServletResponse getResponse() {
        check();
        if (this.servletResponse == null) {
            throw new IllegalStateException(sm.getString("asyncContextImpl.response.ise"));
        }
        return this.servletResponse;
    }

    @Override // javax.servlet.AsyncContext
    public void start(Runnable run) {
        if (log.isDebugEnabled()) {
            logDebug("start      ");
        }
        check();
        Runnable wrapper = new RunnableWrapper(run, this.context, this.request.getCoyoteRequest());
        this.request.getCoyoteRequest().action(ActionCode.ASYNC_RUN, wrapper);
    }

    @Override // javax.servlet.AsyncContext
    public void addListener(AsyncListener listener) {
        check();
        AsyncListenerWrapper wrapper = new AsyncListenerWrapper();
        wrapper.setListener(listener);
        this.listeners.add(wrapper);
    }

    @Override // javax.servlet.AsyncContext
    public void addListener(AsyncListener listener, ServletRequest servletRequest, ServletResponse servletResponse) {
        check();
        AsyncListenerWrapper wrapper = new AsyncListenerWrapper();
        wrapper.setListener(listener);
        wrapper.setServletRequest(servletRequest);
        wrapper.setServletResponse(servletResponse);
        this.listeners.add(wrapper);
    }

    @Override // javax.servlet.AsyncContext
    public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException {
        check();
        try {
            T listener = (T) this.context.getInstanceManager().newInstance(clazz.getName(), clazz.getClassLoader());
            return listener;
        } catch (ReflectiveOperationException | NamingException e) {
            ServletException se = new ServletException(e);
            throw se;
        } catch (Exception e2) {
            ExceptionUtils.handleThrowable(e2.getCause());
            ServletException se2 = new ServletException(e2);
            throw se2;
        }
    }

    public void recycle() {
        if (log.isDebugEnabled()) {
            logDebug("recycle    ");
        }
        this.context = null;
        this.dispatch = null;
        this.event = null;
        this.hasOriginalRequestAndResponse = true;
        this.listeners.clear();
        this.request = null;
        clearServletRequestResponse();
        this.timeout = -1L;
    }

    private void clearServletRequestResponse() {
        this.servletRequest = null;
        this.servletResponse = null;
    }

    public boolean isStarted() {
        AtomicBoolean result = new AtomicBoolean(false);
        this.request.getCoyoteRequest().action(ActionCode.ASYNC_IS_STARTED, result);
        return result.get();
    }

    public void setStarted(Context context, ServletRequest request, ServletResponse response, boolean originalRequestResponse) {
        synchronized (this.asyncContextLock) {
            this.request.getCoyoteRequest().action(ActionCode.ASYNC_START, this);
            this.context = context;
            this.servletRequest = request;
            this.servletResponse = response;
            this.hasOriginalRequestAndResponse = originalRequestResponse;
            this.event = new AsyncEvent(this, request, response);
            List<AsyncListenerWrapper> listenersCopy = new ArrayList<>();
            listenersCopy.addAll(this.listeners);
            this.listeners.clear();
            for (AsyncListenerWrapper listener : listenersCopy) {
                listener.fireOnStartAsync(this.event);
            }
        }
    }

    @Override // javax.servlet.AsyncContext
    public boolean hasOriginalRequestAndResponse() {
        check();
        return this.hasOriginalRequestAndResponse;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void doInternalDispatch() throws ServletException, IOException {
        if (log.isDebugEnabled()) {
            logDebug("intDispatch");
        }
        try {
            Runnable runnable = this.dispatch;
            this.dispatch = null;
            runnable.run();
            if (!this.request.isAsync()) {
                fireOnComplete();
            }
        } catch (RuntimeException x) {
            if (x.getCause() instanceof ServletException) {
                throw ((ServletException) x.getCause());
            }
            if (x.getCause() instanceof IOException) {
                throw ((IOException) x.getCause());
            }
            throw new ServletException(x);
        }
    }

    @Override // javax.servlet.AsyncContext
    public long getTimeout() {
        check();
        return this.timeout;
    }

    @Override // javax.servlet.AsyncContext
    public void setTimeout(long timeout) {
        check();
        this.timeout = timeout;
        this.request.getCoyoteRequest().action(ActionCode.ASYNC_SETTIMEOUT, Long.valueOf(timeout));
    }

    public void setErrorState(Throwable t, boolean fireOnError) {
        if (t != null) {
            this.request.setAttribute("javax.servlet.error.exception", t);
        }
        this.request.getCoyoteRequest().action(ActionCode.ASYNC_ERROR, null);
        if (fireOnError) {
            AsyncEvent errorEvent = new AsyncEvent(this.event.getAsyncContext(), this.event.getSuppliedRequest(), this.event.getSuppliedResponse(), t);
            List<AsyncListenerWrapper> listenersCopy = new ArrayList<>();
            listenersCopy.addAll(this.listeners);
            for (AsyncListenerWrapper listener : listenersCopy) {
                try {
                    listener.fireOnError(errorEvent);
                } catch (Throwable t2) {
                    ExceptionUtils.handleThrowable(t2);
                    log.warn("onError() failed for listener of type [" + listener.getClass().getName() + "]", t2);
                }
            }
        }
        AtomicBoolean result = new AtomicBoolean();
        this.request.getCoyoteRequest().action(ActionCode.ASYNC_IS_ERROR, result);
        if (result.get()) {
            ServletResponse servletResponse = this.servletResponse;
            if (servletResponse instanceof HttpServletResponse) {
                ((HttpServletResponse) servletResponse).setStatus(500);
            }
            Host host = (Host) this.context.getParent();
            Valve stdHostValve = host.getPipeline().getBasic();
            if (stdHostValve instanceof StandardHostValve) {
                ((StandardHostValve) stdHostValve).throwable(this.request, this.request.getResponse(), t);
            }
            this.request.getCoyoteRequest().action(ActionCode.ASYNC_IS_ERROR, result);
            if (result.get()) {
                complete();
            }
        }
    }

    private void logDebug(String method) {
        String rHashCode;
        String crHashCode;
        String rpHashCode;
        String stage;
        StringBuilder uri = new StringBuilder();
        if (this.request == null) {
            rHashCode = BeanDefinitionParserDelegate.NULL_ELEMENT;
            crHashCode = BeanDefinitionParserDelegate.NULL_ELEMENT;
            rpHashCode = BeanDefinitionParserDelegate.NULL_ELEMENT;
            stage = "-";
            uri.append("N/A");
        } else {
            rHashCode = Integer.toHexString(this.request.hashCode());
            org.apache.coyote.Request coyoteRequest = this.request.getCoyoteRequest();
            if (coyoteRequest == null) {
                crHashCode = BeanDefinitionParserDelegate.NULL_ELEMENT;
                rpHashCode = BeanDefinitionParserDelegate.NULL_ELEMENT;
                stage = "-";
            } else {
                crHashCode = Integer.toHexString(coyoteRequest.hashCode());
                RequestInfo rp = coyoteRequest.getRequestProcessor();
                if (rp == null) {
                    rpHashCode = BeanDefinitionParserDelegate.NULL_ELEMENT;
                    stage = "-";
                } else {
                    rpHashCode = Integer.toHexString(rp.hashCode());
                    stage = Integer.toString(rp.getStage());
                }
            }
            uri.append(this.request.getRequestURI());
            if (this.request.getQueryString() != null) {
                uri.append('?');
                uri.append(this.request.getQueryString());
            }
        }
        String threadName = Thread.currentThread().getName();
        int len = threadName.length();
        if (len > 20) {
            threadName = threadName.substring(len - 20, len);
        }
        String msg = String.format("Req: %1$8s  CReq: %2$8s  RP: %3$8s  Stage: %4$s  Thread: %5$20s  State: %6$20s  Method: %7$11s  URI: %8$s", rHashCode, crHashCode, rpHashCode, stage, threadName, "N/A", method, uri);
        if (log.isTraceEnabled()) {
            log.trace(msg, new DebugException());
        } else {
            log.debug(msg);
        }
    }

    private void check() {
        if (this.request == null) {
            throw new IllegalStateException(sm.getString("asyncContextImpl.requestEnded"));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/AsyncContextImpl$DebugException.class */
    public static class DebugException extends Exception {
        private static final long serialVersionUID = 1;

        private DebugException() {
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/AsyncContextImpl$RunnableWrapper.class */
    private static class RunnableWrapper implements Runnable {
        private final Runnable wrapped;
        private final Context context;
        private final org.apache.coyote.Request coyoteRequest;

        public RunnableWrapper(Runnable wrapped, Context ctxt, org.apache.coyote.Request coyoteRequest) {
            this.wrapped = wrapped;
            this.context = ctxt;
            this.coyoteRequest = coyoteRequest;
        }

        @Override // java.lang.Runnable
        public void run() {
            ClassLoader oldCL = this.context.bind(Globals.IS_SECURITY_ENABLED, null);
            try {
                this.wrapped.run();
                this.coyoteRequest.action(ActionCode.DISPATCH_EXECUTE, null);
            } finally {
                this.context.unbind(Globals.IS_SECURITY_ENABLED, oldCL);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/AsyncContextImpl$AsyncRunnable.class */
    public static class AsyncRunnable implements Runnable {
        private final AsyncDispatcher applicationDispatcher;
        private final Request request;
        private final ServletRequest servletRequest;
        private final ServletResponse servletResponse;

        public AsyncRunnable(Request request, AsyncDispatcher applicationDispatcher, ServletRequest servletRequest, ServletResponse servletResponse) {
            this.request = request;
            this.applicationDispatcher = applicationDispatcher;
            this.servletRequest = servletRequest;
            this.servletResponse = servletResponse;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.request.getCoyoteRequest().action(ActionCode.ASYNC_DISPATCHED, null);
            try {
                this.applicationDispatcher.dispatch(this.servletRequest, this.servletResponse);
            } catch (Exception x) {
                throw new RuntimeException(x);
            }
        }
    }
}