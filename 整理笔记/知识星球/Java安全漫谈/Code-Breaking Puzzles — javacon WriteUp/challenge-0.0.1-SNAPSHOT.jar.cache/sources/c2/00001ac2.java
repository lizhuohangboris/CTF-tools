package org.springframework.boot.web.reactive.context;

import java.util.function.Supplier;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.web.context.ConfigurableWebServerApplicationContext;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationEvent;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/reactive/context/ReactiveWebServerApplicationContext.class */
public class ReactiveWebServerApplicationContext extends GenericReactiveWebApplicationContext implements ConfigurableWebServerApplicationContext {
    private volatile ServerManager serverManager;
    private String serverNamespace;

    public ReactiveWebServerApplicationContext() {
    }

    public ReactiveWebServerApplicationContext(DefaultListableBeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override // org.springframework.context.support.AbstractApplicationContext, org.springframework.context.ConfigurableApplicationContext
    public final void refresh() throws BeansException, IllegalStateException {
        try {
            super.refresh();
        } catch (RuntimeException ex) {
            stopAndReleaseReactiveWebServer();
            throw ex;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.context.support.AbstractApplicationContext
    public void onRefresh() {
        super.onRefresh();
        try {
            createWebServer();
        } catch (Throwable ex) {
            throw new ApplicationContextException("Unable to start reactive web server", ex);
        }
    }

    private void createWebServer() {
        ServerManager serverManager = this.serverManager;
        if (serverManager == null) {
            this.serverManager = ServerManager.get(getWebServerFactory());
        }
        initPropertySources();
    }

    protected ReactiveWebServerFactory getWebServerFactory() {
        String[] beanNames = getBeanFactory().getBeanNamesForType(ReactiveWebServerFactory.class);
        if (beanNames.length == 0) {
            throw new ApplicationContextException("Unable to start ReactiveWebApplicationContext due to missing ReactiveWebServerFactory bean.");
        }
        if (beanNames.length > 1) {
            throw new ApplicationContextException("Unable to start ReactiveWebApplicationContext due to multiple ReactiveWebServerFactory beans : " + StringUtils.arrayToCommaDelimitedString(beanNames));
        }
        return (ReactiveWebServerFactory) getBeanFactory().getBean(beanNames[0], ReactiveWebServerFactory.class);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.context.support.AbstractApplicationContext
    public void finishRefresh() {
        super.finishRefresh();
        WebServer webServer = startReactiveWebServer();
        if (webServer != null) {
            publishEvent((ApplicationEvent) new ReactiveWebServerInitializedEvent(webServer, this));
        }
    }

    private WebServer startReactiveWebServer() {
        ServerManager serverManager = this.serverManager;
        ServerManager.start(serverManager, this::getHttpHandler);
        return ServerManager.getWebServer(serverManager);
    }

    protected HttpHandler getHttpHandler() {
        String[] beanNames = getBeanFactory().getBeanNamesForType(HttpHandler.class);
        if (beanNames.length == 0) {
            throw new ApplicationContextException("Unable to start ReactiveWebApplicationContext due to missing HttpHandler bean.");
        }
        if (beanNames.length > 1) {
            throw new ApplicationContextException("Unable to start ReactiveWebApplicationContext due to multiple HttpHandler beans : " + StringUtils.arrayToCommaDelimitedString(beanNames));
        }
        return (HttpHandler) getBeanFactory().getBean(beanNames[0], HttpHandler.class);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.context.support.AbstractApplicationContext
    public void onClose() {
        super.onClose();
        stopAndReleaseReactiveWebServer();
    }

    private void stopAndReleaseReactiveWebServer() {
        ServerManager serverManager = this.serverManager;
        try {
            ServerManager.stop(serverManager);
        } finally {
            this.serverManager = null;
        }
    }

    @Override // org.springframework.boot.web.context.WebServerApplicationContext
    public WebServer getWebServer() {
        return ServerManager.getWebServer(this.serverManager);
    }

    @Override // org.springframework.boot.web.context.WebServerApplicationContext
    public String getServerNamespace() {
        return this.serverNamespace;
    }

    @Override // org.springframework.boot.web.context.ConfigurableWebServerApplicationContext
    public void setServerNamespace(String serverNamespace) {
        this.serverNamespace = serverNamespace;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/reactive/context/ReactiveWebServerApplicationContext$ServerManager.class */
    public static final class ServerManager implements HttpHandler {
        private final WebServer server;
        private volatile HttpHandler handler = this::handleUninitialized;

        private ServerManager(ReactiveWebServerFactory factory) {
            this.server = factory.getWebServer(this);
        }

        private Mono<Void> handleUninitialized(ServerHttpRequest request, ServerHttpResponse response) {
            throw new IllegalStateException("The HttpHandler has not yet been initialized");
        }

        @Override // org.springframework.http.server.reactive.HttpHandler
        public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
            return this.handler.handle(request, response);
        }

        public HttpHandler getHandler() {
            return this.handler;
        }

        public static ServerManager get(ReactiveWebServerFactory factory) {
            return new ServerManager(factory);
        }

        public static WebServer getWebServer(ServerManager manager) {
            if (manager != null) {
                return manager.server;
            }
            return null;
        }

        public static void start(ServerManager manager, Supplier<HttpHandler> handlerSupplier) {
            if (manager != null && manager.server != null) {
                manager.handler = handlerSupplier.get();
                manager.server.start();
            }
        }

        public static void stop(ServerManager manager) {
            if (manager != null && manager.server != null) {
                try {
                    manager.server.stop();
                } catch (Exception ex) {
                    throw new IllegalStateException(ex);
                }
            }
        }
    }
}