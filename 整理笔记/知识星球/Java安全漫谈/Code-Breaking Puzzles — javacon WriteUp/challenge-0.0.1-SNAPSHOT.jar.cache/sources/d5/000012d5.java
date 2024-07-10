package org.springframework.aop.framework;

import java.util.LinkedList;
import java.util.List;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/ProxyCreatorSupport.class */
public class ProxyCreatorSupport extends AdvisedSupport {
    private AopProxyFactory aopProxyFactory;
    private final List<AdvisedSupportListener> listeners;
    private boolean active;

    public ProxyCreatorSupport() {
        this.listeners = new LinkedList();
        this.active = false;
        this.aopProxyFactory = new DefaultAopProxyFactory();
    }

    public ProxyCreatorSupport(AopProxyFactory aopProxyFactory) {
        this.listeners = new LinkedList();
        this.active = false;
        Assert.notNull(aopProxyFactory, "AopProxyFactory must not be null");
        this.aopProxyFactory = aopProxyFactory;
    }

    public void setAopProxyFactory(AopProxyFactory aopProxyFactory) {
        Assert.notNull(aopProxyFactory, "AopProxyFactory must not be null");
        this.aopProxyFactory = aopProxyFactory;
    }

    public AopProxyFactory getAopProxyFactory() {
        return this.aopProxyFactory;
    }

    public void addListener(AdvisedSupportListener listener) {
        Assert.notNull(listener, "AdvisedSupportListener must not be null");
        this.listeners.add(listener);
    }

    public void removeListener(AdvisedSupportListener listener) {
        Assert.notNull(listener, "AdvisedSupportListener must not be null");
        this.listeners.remove(listener);
    }

    public final synchronized AopProxy createAopProxy() {
        if (!this.active) {
            activate();
        }
        return getAopProxyFactory().createAopProxy(this);
    }

    private void activate() {
        this.active = true;
        for (AdvisedSupportListener listener : this.listeners) {
            listener.activated(this);
        }
    }

    @Override // org.springframework.aop.framework.AdvisedSupport
    public void adviceChanged() {
        super.adviceChanged();
        synchronized (this) {
            if (this.active) {
                for (AdvisedSupportListener listener : this.listeners) {
                    listener.adviceChanged(this);
                }
            }
        }
    }

    protected final synchronized boolean isActive() {
        return this.active;
    }
}