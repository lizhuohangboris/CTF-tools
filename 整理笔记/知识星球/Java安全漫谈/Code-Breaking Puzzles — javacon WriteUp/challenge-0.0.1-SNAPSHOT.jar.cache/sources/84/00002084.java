package org.springframework.http.client.reactive;

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.netty.http.HttpResources;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/reactive/ReactorResourceFactory.class */
public class ReactorResourceFactory implements InitializingBean, DisposableBean {
    @Nullable
    private Consumer<HttpResources> globalResourcesConsumer;
    @Nullable
    private ConnectionProvider connectionProvider;
    @Nullable
    private LoopResources loopResources;
    private boolean useGlobalResources = true;
    private Supplier<ConnectionProvider> connectionProviderSupplier = () -> {
        return ConnectionProvider.elastic("webflux");
    };
    private Supplier<LoopResources> loopResourcesSupplier = () -> {
        return LoopResources.create("webflux-http");
    };
    private boolean manageConnectionProvider = false;
    private boolean manageLoopResources = false;

    public void setUseGlobalResources(boolean useGlobalResources) {
        this.useGlobalResources = useGlobalResources;
    }

    public boolean isUseGlobalResources() {
        return this.useGlobalResources;
    }

    public void addGlobalResourcesConsumer(Consumer<HttpResources> consumer) {
        this.useGlobalResources = true;
        this.globalResourcesConsumer = this.globalResourcesConsumer != null ? this.globalResourcesConsumer.andThen(consumer) : consumer;
    }

    public void setConnectionProviderSupplier(Supplier<ConnectionProvider> supplier) {
        this.connectionProviderSupplier = supplier;
    }

    public void setLoopResourcesSupplier(Supplier<LoopResources> supplier) {
        this.loopResourcesSupplier = supplier;
    }

    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public ConnectionProvider getConnectionProvider() {
        Assert.state(this.connectionProvider != null, "ConnectionProvider not initialized yet");
        return this.connectionProvider;
    }

    public void setLoopResources(LoopResources loopResources) {
        this.loopResources = loopResources;
    }

    public LoopResources getLoopResources() {
        Assert.state(this.loopResources != null, "LoopResources not initialized yet");
        return this.loopResources;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (this.useGlobalResources) {
            Assert.isTrue(this.loopResources == null && this.connectionProvider == null, "'useGlobalResources' is mutually exclusive with explicitly configured resources");
            HttpResources httpResources = HttpResources.get();
            if (this.globalResourcesConsumer != null) {
                this.globalResourcesConsumer.accept(httpResources);
            }
            this.connectionProvider = httpResources;
            this.loopResources = httpResources;
            return;
        }
        if (this.loopResources == null) {
            this.manageLoopResources = true;
            this.loopResources = this.loopResourcesSupplier.get();
        }
        if (this.connectionProvider == null) {
            this.manageConnectionProvider = true;
            this.connectionProvider = this.connectionProviderSupplier.get();
        }
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        if (this.useGlobalResources) {
            HttpResources.disposeLoopsAndConnections();
            return;
        }
        try {
            ConnectionProvider provider = this.connectionProvider;
            if (provider != null && this.manageConnectionProvider) {
                provider.dispose();
            }
        } catch (Throwable th) {
        }
        try {
            LoopResources resources = this.loopResources;
            if (resources != null && this.manageLoopResources) {
                resources.dispose();
            }
        } catch (Throwable th2) {
        }
    }
}