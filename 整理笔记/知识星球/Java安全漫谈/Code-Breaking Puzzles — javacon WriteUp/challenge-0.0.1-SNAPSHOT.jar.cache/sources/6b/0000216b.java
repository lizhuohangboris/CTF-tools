package org.springframework.http.server.reactive;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.DispatcherType;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/ServletHttpHandlerAdapter.class */
public class ServletHttpHandlerAdapter implements Servlet {
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private final HttpHandler httpHandler;
    @Nullable
    private String servletPath;
    private static final Log logger = HttpLogging.forLogName(ServletHttpHandlerAdapter.class);
    private static final String WRITE_ERROR_ATTRIBUTE_NAME = ServletHttpHandlerAdapter.class.getName() + ".ERROR";
    private int bufferSize = 8192;
    private DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory(false);

    public ServletHttpHandlerAdapter(HttpHandler httpHandler) {
        Assert.notNull(httpHandler, "HttpHandler must not be null");
        this.httpHandler = httpHandler;
    }

    public void setBufferSize(int bufferSize) {
        Assert.isTrue(bufferSize > 0, "Buffer size must be larger than zero");
        this.bufferSize = bufferSize;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    @Nullable
    public String getServletPath() {
        return this.servletPath;
    }

    public void setDataBufferFactory(DataBufferFactory dataBufferFactory) {
        Assert.notNull(dataBufferFactory, "DataBufferFactory must not be null");
        this.dataBufferFactory = dataBufferFactory;
    }

    public DataBufferFactory getDataBufferFactory() {
        return this.dataBufferFactory;
    }

    @Override // javax.servlet.Servlet
    public void init(ServletConfig config) {
        this.servletPath = getServletPath(config);
    }

    private String getServletPath(ServletConfig config) {
        String name = config.getServletName();
        ServletRegistration registration = config.getServletContext().getServletRegistration(name);
        if (registration == null) {
            throw new IllegalStateException("ServletRegistration not found for Servlet '" + name + "'");
        }
        Collection<String> mappings = registration.getMappings();
        if (mappings.size() == 1) {
            String mapping = mappings.iterator().next();
            if (mapping.equals("/")) {
                return "";
            }
            if (mapping.endsWith("/*")) {
                String path = mapping.substring(0, mapping.length() - 2);
                if (!path.isEmpty() && logger.isDebugEnabled()) {
                    logger.debug("Found servlet mapping prefix '" + path + "' for '" + name + "'");
                }
                return path;
            }
        }
        throw new IllegalArgumentException("Expected a single Servlet mapping: either the default Servlet mapping (i.e. '/'), or a path based mapping (e.g. '/*', '/foo/*'). Actual mappings: " + mappings + " for Servlet '" + name + "'");
    }

    @Override // javax.servlet.Servlet
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if (DispatcherType.ASYNC.equals(request.getDispatcherType())) {
            Throwable ex = (Throwable) request.getAttribute(WRITE_ERROR_ATTRIBUTE_NAME);
            throw new ServletException("Failed to create response content", ex);
        }
        AsyncContext asyncContext = request.startAsync();
        asyncContext.setTimeout(-1L);
        try {
            ServletServerHttpRequest httpRequest = createRequest((HttpServletRequest) request, asyncContext);
            ServerHttpResponse httpResponse = createResponse((HttpServletResponse) response, asyncContext, httpRequest);
            if (httpRequest.getMethod() == HttpMethod.HEAD) {
                httpResponse = new HttpHeadResponseDecorator(httpResponse);
            }
            AtomicBoolean isCompleted = new AtomicBoolean();
            HandlerResultAsyncListener listener = new HandlerResultAsyncListener(isCompleted, httpRequest);
            asyncContext.addListener(listener);
            HandlerResultSubscriber subscriber = new HandlerResultSubscriber(asyncContext, isCompleted, httpRequest);
            this.httpHandler.handle(httpRequest, httpResponse).subscribe(subscriber);
        } catch (URISyntaxException ex2) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to get request  URL: " + ex2.getMessage());
            }
            ((HttpServletResponse) response).setStatus(400);
            asyncContext.complete();
        }
    }

    protected ServletServerHttpRequest createRequest(HttpServletRequest request, AsyncContext context) throws IOException, URISyntaxException {
        Assert.notNull(this.servletPath, "Servlet path is not initialized");
        return new ServletServerHttpRequest(request, context, this.servletPath, getDataBufferFactory(), getBufferSize());
    }

    protected ServletServerHttpResponse createResponse(HttpServletResponse response, AsyncContext context, ServletServerHttpRequest request) throws IOException {
        return new ServletServerHttpResponse(response, context, getDataBufferFactory(), getBufferSize(), request);
    }

    @Override // javax.servlet.Servlet
    public String getServletInfo() {
        return "";
    }

    @Override // javax.servlet.Servlet
    @Nullable
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override // javax.servlet.Servlet
    public void destroy() {
    }

    public static void runIfAsyncNotComplete(AsyncContext asyncContext, AtomicBoolean isCompleted, Runnable task) {
        try {
            if (asyncContext.getRequest().isAsyncStarted() && isCompleted.compareAndSet(false, true)) {
                task.run();
            }
        } catch (IllegalStateException e) {
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/ServletHttpHandlerAdapter$HandlerResultAsyncListener.class */
    private static class HandlerResultAsyncListener implements AsyncListener {
        private final AtomicBoolean isCompleted;
        private final String logPrefix;

        public HandlerResultAsyncListener(AtomicBoolean isCompleted, ServletServerHttpRequest httpRequest) {
            this.isCompleted = isCompleted;
            this.logPrefix = httpRequest.getLogPrefix();
        }

        @Override // javax.servlet.AsyncListener
        public void onTimeout(AsyncEvent event) {
            ServletHttpHandlerAdapter.logger.debug(this.logPrefix + "Timeout notification");
            AsyncContext context = event.getAsyncContext();
            AtomicBoolean atomicBoolean = this.isCompleted;
            context.getClass();
            ServletHttpHandlerAdapter.runIfAsyncNotComplete(context, atomicBoolean, this::complete);
        }

        @Override // javax.servlet.AsyncListener
        public void onError(AsyncEvent event) {
            Throwable ex = event.getThrowable();
            ServletHttpHandlerAdapter.logger.debug(this.logPrefix + "Error notification: " + (ex != null ? ex : "<no Throwable>"));
            AsyncContext context = event.getAsyncContext();
            AtomicBoolean atomicBoolean = this.isCompleted;
            context.getClass();
            ServletHttpHandlerAdapter.runIfAsyncNotComplete(context, atomicBoolean, this::complete);
        }

        @Override // javax.servlet.AsyncListener
        public void onStartAsync(AsyncEvent event) {
        }

        @Override // javax.servlet.AsyncListener
        public void onComplete(AsyncEvent event) {
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/ServletHttpHandlerAdapter$HandlerResultSubscriber.class */
    private class HandlerResultSubscriber implements Subscriber<Void> {
        private final AsyncContext asyncContext;
        private final AtomicBoolean isCompleted;
        private final String logPrefix;

        public HandlerResultSubscriber(AsyncContext asyncContext, AtomicBoolean isCompleted, ServletServerHttpRequest httpRequest) {
            ServletHttpHandlerAdapter.this = r4;
            this.asyncContext = asyncContext;
            this.isCompleted = isCompleted;
            this.logPrefix = httpRequest.getLogPrefix();
        }

        public void onSubscribe(Subscription subscription) {
            subscription.request(Long.MAX_VALUE);
        }

        public void onNext(Void aVoid) {
        }

        public void onError(Throwable ex) {
            ServletHttpHandlerAdapter.logger.trace(this.logPrefix + "Failed to complete: " + ex.getMessage());
            ServletHttpHandlerAdapter.runIfAsyncNotComplete(this.asyncContext, this.isCompleted, () -> {
                if (this.asyncContext.getResponse().isCommitted()) {
                    ServletHttpHandlerAdapter.logger.trace(this.logPrefix + "Dispatch to container, to raise the error on servlet thread");
                    this.asyncContext.getRequest().setAttribute(ServletHttpHandlerAdapter.WRITE_ERROR_ATTRIBUTE_NAME, ex);
                    this.asyncContext.dispatch();
                    return;
                }
                try {
                    ServletHttpHandlerAdapter.logger.trace(this.logPrefix + "Setting ServletResponse status to 500 Server Error");
                    this.asyncContext.getResponse().resetBuffer();
                    ((HttpServletResponse) this.asyncContext.getResponse()).setStatus(500);
                } finally {
                    this.asyncContext.complete();
                }
            });
        }

        public void onComplete() {
            ServletHttpHandlerAdapter.logger.trace(this.logPrefix + "Handling completed");
            AsyncContext asyncContext = this.asyncContext;
            AtomicBoolean atomicBoolean = this.isCompleted;
            AsyncContext asyncContext2 = this.asyncContext;
            asyncContext2.getClass();
            ServletHttpHandlerAdapter.runIfAsyncNotComplete(asyncContext, atomicBoolean, this::complete);
        }
    }
}