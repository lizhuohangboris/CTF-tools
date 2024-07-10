package org.apache.catalina.core;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.DispatcherType;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.log.SystemLogHandler;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.http.HttpHeaders;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/StandardWrapperValve.class */
public final class StandardWrapperValve extends ValveBase {
    private volatile long processingTime;
    private volatile long maxTime;
    private volatile long minTime;
    private final AtomicInteger requestCount;
    private final AtomicInteger errorCount;
    private static final StringManager sm = StringManager.getManager(Constants.Package);

    public StandardWrapperValve() {
        super(true);
        this.minTime = Long.MAX_VALUE;
        this.requestCount = new AtomicInteger(0);
        this.errorCount = new AtomicInteger(0);
    }

    @Override // org.apache.catalina.Valve
    public final void invoke(Request request, Response response) throws IOException, ServletException {
        Throwable throwable;
        Throwable throwable2;
        boolean unavailable = false;
        Throwable th = null;
        long t1 = System.currentTimeMillis();
        this.requestCount.incrementAndGet();
        StandardWrapper wrapper = (StandardWrapper) getContainer();
        Servlet servlet = null;
        Context context = (Context) wrapper.getParent();
        if (!context.getState().isAvailable()) {
            response.sendError(503, sm.getString("standardContext.isUnavailable"));
            unavailable = true;
        }
        if (!unavailable && wrapper.isUnavailable()) {
            this.container.getLogger().info(sm.getString("standardWrapper.isUnavailable", wrapper.getName()));
            long available = wrapper.getAvailable();
            if (available > 0 && available < Long.MAX_VALUE) {
                response.setDateHeader(HttpHeaders.RETRY_AFTER, available);
                response.sendError(503, sm.getString("standardWrapper.isUnavailable", wrapper.getName()));
            } else if (available == Long.MAX_VALUE) {
                response.sendError(404, sm.getString("standardWrapper.notFound", wrapper.getName()));
            }
            unavailable = true;
        }
        if (!unavailable) {
            try {
                servlet = wrapper.allocate();
            } catch (UnavailableException e) {
                this.container.getLogger().error(sm.getString("standardWrapper.allocateException", wrapper.getName()), e);
                long available2 = wrapper.getAvailable();
                if (available2 <= 0 || available2 >= Long.MAX_VALUE) {
                    throwable = th;
                    if (available2 == Long.MAX_VALUE) {
                        response.sendError(404, sm.getString("standardWrapper.notFound", wrapper.getName()));
                        throwable = th;
                    }
                } else {
                    response.setDateHeader(HttpHeaders.RETRY_AFTER, available2);
                    response.sendError(503, sm.getString("standardWrapper.isUnavailable", wrapper.getName()));
                    throwable = th;
                }
            } catch (ServletException e2) {
                this.container.getLogger().error(sm.getString("standardWrapper.allocateException", wrapper.getName()), StandardWrapper.getRootCause(e2));
                throwable = e2;
                exception(request, response, e2);
            } catch (Throwable e3) {
                ExceptionUtils.handleThrowable(e3);
                this.container.getLogger().error(sm.getString("standardWrapper.allocateException", wrapper.getName()), e3);
                throwable = e3;
                exception(request, response, e3);
                servlet = null;
            }
        }
        throwable = th;
        MessageBytes requestPathMB = request.getRequestPathMB();
        DispatcherType dispatcherType = DispatcherType.REQUEST;
        if (request.getDispatcherType() == DispatcherType.ASYNC) {
            dispatcherType = DispatcherType.ASYNC;
        }
        request.setAttribute(Globals.DISPATCHER_TYPE_ATTR, dispatcherType);
        request.setAttribute(Globals.DISPATCHER_REQUEST_PATH_ATTR, requestPathMB);
        ApplicationFilterChain filterChain = ApplicationFilterFactory.createFilterChain(request, wrapper, servlet);
        if (servlet != null && filterChain != null) {
            try {
                if (context.getSwallowOutput()) {
                    try {
                        SystemLogHandler.startCapture();
                        if (request.isAsyncDispatching()) {
                            request.getAsyncContextInternal().doInternalDispatch();
                        } else {
                            filterChain.doFilter(request.getRequest(), response.getResponse());
                        }
                        String log = SystemLogHandler.stopCapture();
                        if (log != null && log.length() > 0) {
                            context.getLogger().info(log);
                        }
                    } catch (Throwable th2) {
                        String log2 = SystemLogHandler.stopCapture();
                        if (log2 != null && log2.length() > 0) {
                            context.getLogger().info(log2);
                        }
                        throw th2;
                    }
                } else if (request.isAsyncDispatching()) {
                    request.getAsyncContextInternal().doInternalDispatch();
                } else {
                    filterChain.doFilter(request.getRequest(), response.getResponse());
                }
            } catch (ServletException e4) {
                Throwable rootCause = StandardWrapper.getRootCause(e4);
                if (!(rootCause instanceof ClientAbortException)) {
                    this.container.getLogger().error(sm.getString("standardWrapper.serviceExceptionRoot", wrapper.getName(), context.getName(), e4.getMessage()), rootCause);
                }
                throwable2 = e4;
                exception(request, response, e4);
            } catch (ClientAbortException e5) {
                throwable2 = e5;
                exception(request, response, e5);
            } catch (IOException e6) {
                this.container.getLogger().error(sm.getString("standardWrapper.serviceException", wrapper.getName(), context.getName()), e6);
                throwable2 = e6;
                exception(request, response, e6);
            } catch (UnavailableException e7) {
                this.container.getLogger().error(sm.getString("standardWrapper.serviceException", wrapper.getName(), context.getName()), e7);
                wrapper.unavailable(e7);
                long available3 = wrapper.getAvailable();
                if (available3 <= 0 || available3 >= Long.MAX_VALUE) {
                    throwable2 = throwable;
                    if (available3 == Long.MAX_VALUE) {
                        response.sendError(404, sm.getString("standardWrapper.notFound", wrapper.getName()));
                        throwable2 = throwable;
                    }
                } else {
                    response.setDateHeader(HttpHeaders.RETRY_AFTER, available3);
                    response.sendError(503, sm.getString("standardWrapper.isUnavailable", wrapper.getName()));
                    throwable2 = throwable;
                }
            } catch (Throwable e8) {
                ExceptionUtils.handleThrowable(e8);
                this.container.getLogger().error(sm.getString("standardWrapper.serviceException", wrapper.getName(), context.getName()), e8);
                throwable2 = e8;
                exception(request, response, e8);
            }
        }
        throwable2 = throwable;
        if (filterChain != null) {
            filterChain.release();
        }
        if (servlet != null) {
            try {
                wrapper.deallocate(servlet);
            } catch (Throwable e9) {
                ExceptionUtils.handleThrowable(e9);
                this.container.getLogger().error(sm.getString("standardWrapper.deallocateException", wrapper.getName()), e9);
                if (throwable2 == null) {
                    throwable2 = e9;
                    exception(request, response, e9);
                }
            }
        }
        if (servlet != null) {
            try {
                if (wrapper.getAvailable() == Long.MAX_VALUE) {
                    wrapper.unload();
                }
            } catch (Throwable e10) {
                ExceptionUtils.handleThrowable(e10);
                this.container.getLogger().error(sm.getString("standardWrapper.unloadException", wrapper.getName()), e10);
                if (throwable2 == null) {
                    exception(request, response, e10);
                }
            }
        }
        long t2 = System.currentTimeMillis();
        long time = t2 - t1;
        this.processingTime += time;
        if (time > this.maxTime) {
            this.maxTime = time;
        }
        if (time < this.minTime) {
            this.minTime = time;
        }
    }

    private void exception(Request request, Response response, Throwable exception) {
        request.setAttribute("javax.servlet.error.exception", exception);
        response.setStatus(500);
        response.setError();
    }

    public long getProcessingTime() {
        return this.processingTime;
    }

    public long getMaxTime() {
        return this.maxTime;
    }

    public long getMinTime() {
        return this.minTime;
    }

    public int getRequestCount() {
        return this.requestCount.get();
    }

    public int getErrorCount() {
        return this.errorCount.get();
    }

    public void incrementErrorCount() {
        this.errorCount.incrementAndGet();
    }

    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
    }
}