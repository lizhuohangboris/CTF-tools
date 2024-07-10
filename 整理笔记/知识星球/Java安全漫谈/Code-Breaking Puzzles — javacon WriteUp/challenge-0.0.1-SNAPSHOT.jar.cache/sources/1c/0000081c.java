package org.apache.catalina.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import javax.management.ObjectName;
import org.apache.catalina.Container;
import org.apache.catalina.Engine;
import org.apache.catalina.Executor;
import org.apache.catalina.JmxEnabled;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.mapper.Mapper;
import org.apache.catalina.mapper.MapperListener;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/StandardService.class */
public class StandardService extends LifecycleMBeanBase implements Service {
    private String name = null;
    private Server server = null;
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);
    protected Connector[] connectors = new Connector[0];
    private final Object connectorsLock = new Object();
    protected final ArrayList<Executor> executors = new ArrayList<>();
    private Engine engine = null;
    private ClassLoader parentClassLoader = null;
    protected final Mapper mapper = new Mapper();
    protected final MapperListener mapperListener = new MapperListener(this);
    private static final Log log = LogFactory.getLog(StandardService.class);
    private static final StringManager sm = StringManager.getManager(Constants.Package);

    @Override // org.apache.catalina.Service
    public Mapper getMapper() {
        return this.mapper;
    }

    @Override // org.apache.catalina.Service
    public Engine getContainer() {
        return this.engine;
    }

    @Override // org.apache.catalina.Service
    public void setContainer(Engine engine) {
        Engine oldEngine = this.engine;
        if (oldEngine != null) {
            oldEngine.setService(null);
        }
        this.engine = engine;
        if (this.engine != null) {
            this.engine.setService(this);
        }
        if (getState().isAvailable()) {
            if (this.engine != null) {
                try {
                    this.engine.start();
                } catch (LifecycleException e) {
                    log.warn(sm.getString("standardService.engine.startFailed"), e);
                }
            }
            try {
                this.mapperListener.stop();
            } catch (LifecycleException e2) {
                log.warn(sm.getString("standardService.mapperListener.stopFailed"), e2);
            }
            try {
                this.mapperListener.start();
            } catch (LifecycleException e3) {
                log.warn(sm.getString("standardService.mapperListener.startFailed"), e3);
            }
            if (oldEngine != null) {
                try {
                    oldEngine.stop();
                } catch (LifecycleException e4) {
                    log.warn(sm.getString("standardService.engine.stopFailed"), e4);
                }
            }
        }
        this.support.firePropertyChange("container", oldEngine, this.engine);
    }

    @Override // org.apache.catalina.Service
    public String getName() {
        return this.name;
    }

    @Override // org.apache.catalina.Service
    public void setName(String name) {
        this.name = name;
    }

    @Override // org.apache.catalina.Service
    public Server getServer() {
        return this.server;
    }

    @Override // org.apache.catalina.Service
    public void setServer(Server server) {
        this.server = server;
    }

    @Override // org.apache.catalina.Service
    public void addConnector(Connector connector) {
        synchronized (this.connectorsLock) {
            connector.setService(this);
            Connector[] results = new Connector[this.connectors.length + 1];
            System.arraycopy(this.connectors, 0, results, 0, this.connectors.length);
            results[this.connectors.length] = connector;
            this.connectors = results;
            if (getState().isAvailable()) {
                try {
                    connector.start();
                } catch (LifecycleException e) {
                    log.error(sm.getString("standardService.connector.startFailed", connector), e);
                }
            }
            this.support.firePropertyChange("connector", (Object) null, connector);
        }
    }

    public ObjectName[] getConnectorNames() {
        ObjectName[] results = new ObjectName[this.connectors.length];
        for (int i = 0; i < results.length; i++) {
            results[i] = this.connectors[i].getObjectName();
        }
        return results;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    @Override // org.apache.catalina.Service
    public Connector[] findConnectors() {
        return this.connectors;
    }

    @Override // org.apache.catalina.Service
    public void removeConnector(Connector connector) {
        synchronized (this.connectorsLock) {
            int j = -1;
            int i = 0;
            while (true) {
                if (i < this.connectors.length) {
                    if (connector != this.connectors[i]) {
                        i++;
                    } else {
                        j = i;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (j < 0) {
                return;
            }
            if (this.connectors[j].getState().isAvailable()) {
                try {
                    this.connectors[j].stop();
                } catch (LifecycleException e) {
                    log.error(sm.getString("standardService.connector.stopFailed", this.connectors[j]), e);
                }
            }
            connector.setService(null);
            int k = 0;
            Connector[] results = new Connector[this.connectors.length - 1];
            for (int i2 = 0; i2 < this.connectors.length; i2++) {
                if (i2 != j) {
                    int i3 = k;
                    k++;
                    results[i3] = this.connectors[i2];
                }
            }
            this.connectors = results;
            this.support.firePropertyChange("connector", connector, (Object) null);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    public String toString() {
        return "StandardService[" + getName() + "]";
    }

    @Override // org.apache.catalina.Service
    public void addExecutor(Executor ex) {
        synchronized (this.executors) {
            if (!this.executors.contains(ex)) {
                this.executors.add(ex);
                if (getState().isAvailable()) {
                    try {
                        ex.start();
                    } catch (LifecycleException x) {
                        log.error("Executor.start", x);
                    }
                }
            }
        }
    }

    @Override // org.apache.catalina.Service
    public Executor[] findExecutors() {
        Executor[] arr;
        synchronized (this.executors) {
            arr = new Executor[this.executors.size()];
            this.executors.toArray(arr);
        }
        return arr;
    }

    @Override // org.apache.catalina.Service
    public Executor getExecutor(String executorName) {
        synchronized (this.executors) {
            Iterator<Executor> it = this.executors.iterator();
            while (it.hasNext()) {
                Executor executor = it.next();
                if (executorName.equals(executor.getName())) {
                    return executor;
                }
            }
            return null;
        }
    }

    @Override // org.apache.catalina.Service
    public void removeExecutor(Executor ex) {
        synchronized (this.executors) {
            if (this.executors.remove(ex) && getState().isAvailable()) {
                try {
                    ex.stop();
                } catch (LifecycleException e) {
                    log.error("Executor.stop", e);
                }
            }
        }
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void startInternal() throws LifecycleException {
        Connector[] connectorArr;
        if (log.isInfoEnabled()) {
            log.info(sm.getString("standardService.start.name", this.name));
        }
        setState(LifecycleState.STARTING);
        if (this.engine != null) {
            synchronized (this.engine) {
                this.engine.start();
            }
        }
        synchronized (this.executors) {
            Iterator<Executor> it = this.executors.iterator();
            while (it.hasNext()) {
                Executor executor = it.next();
                executor.start();
            }
        }
        this.mapperListener.start();
        synchronized (this.connectorsLock) {
            for (Connector connector : this.connectors) {
                if (connector.getState() != LifecycleState.FAILED) {
                    connector.start();
                }
            }
        }
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void stopInternal() throws LifecycleException {
        Connector[] connectorArr;
        Connector[] connectorArr2;
        synchronized (this.connectorsLock) {
            for (Connector connector : this.connectors) {
                connector.pause();
                connector.getProtocolHandler().closeServerSocketGraceful();
            }
        }
        if (log.isInfoEnabled()) {
            log.info(sm.getString("standardService.stop.name", this.name));
        }
        setState(LifecycleState.STOPPING);
        if (this.engine != null) {
            synchronized (this.engine) {
                this.engine.stop();
            }
        }
        synchronized (this.connectorsLock) {
            for (Connector connector2 : this.connectors) {
                if (LifecycleState.STARTED.equals(connector2.getState())) {
                    connector2.stop();
                }
            }
        }
        if (this.mapperListener.getState() != LifecycleState.INITIALIZED) {
            this.mapperListener.stop();
        }
        synchronized (this.executors) {
            Iterator<Executor> it = this.executors.iterator();
            while (it.hasNext()) {
                Executor executor = it.next();
                executor.stop();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
        Executor[] findExecutors;
        Connector[] connectorArr;
        super.initInternal();
        if (this.engine != null) {
            this.engine.init();
        }
        for (Executor executor : findExecutors()) {
            if (executor instanceof JmxEnabled) {
                ((JmxEnabled) executor).setDomain(getDomain());
            }
            executor.init();
        }
        this.mapperListener.init();
        synchronized (this.connectorsLock) {
            for (Connector connector : this.connectors) {
                connector.init();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void destroyInternal() throws LifecycleException {
        Connector[] connectorArr;
        Executor[] findExecutors;
        this.mapperListener.destroy();
        synchronized (this.connectorsLock) {
            for (Connector connector : this.connectors) {
                connector.destroy();
            }
        }
        for (Executor executor : findExecutors()) {
            executor.destroy();
        }
        if (this.engine != null) {
            this.engine.destroy();
        }
        super.destroyInternal();
    }

    @Override // org.apache.catalina.Service
    public ClassLoader getParentClassLoader() {
        if (this.parentClassLoader != null) {
            return this.parentClassLoader;
        }
        if (this.server != null) {
            return this.server.getParentClassLoader();
        }
        return ClassLoader.getSystemClassLoader();
    }

    @Override // org.apache.catalina.Service
    public void setParentClassLoader(ClassLoader parent) {
        ClassLoader oldParentClassLoader = this.parentClassLoader;
        this.parentClassLoader = parent;
        this.support.firePropertyChange("parentClassLoader", oldParentClassLoader, this.parentClassLoader);
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getDomainInternal() {
        String domain = null;
        Container engine = getContainer();
        if (engine != null) {
            domain = engine.getName();
        }
        if (domain == null) {
            domain = getName();
        }
        return domain;
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    public final String getObjectNameKeyProperties() {
        return "type=Service";
    }
}