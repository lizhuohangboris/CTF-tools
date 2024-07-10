package org.apache.catalina.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.management.ObjectName;
import org.apache.catalina.AccessLog;
import org.apache.catalina.Cluster;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerEvent;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Loader;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Realm;
import org.apache.catalina.Valve;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.ContextName;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.MultiThrowable;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.threads.InlineExecutorService;
import org.springframework.beans.PropertyAccessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/ContainerBase.class */
public abstract class ContainerBase extends LifecycleMBeanBase implements Container {
    protected final HashMap<String, Container> children = new HashMap<>();
    protected int backgroundProcessorDelay = -1;
    protected final List<ContainerListener> listeners = new CopyOnWriteArrayList();
    protected Log logger = null;
    protected String logName = null;
    protected Cluster cluster = null;
    private final ReadWriteLock clusterLock = new ReentrantReadWriteLock();
    protected String name = null;
    protected Container parent = null;
    protected ClassLoader parentClassLoader = null;
    protected final Pipeline pipeline = new StandardPipeline(this);
    private volatile Realm realm = null;
    private final ReadWriteLock realmLock = new ReentrantReadWriteLock();
    protected boolean startChildren = true;
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private Thread thread = null;
    private volatile boolean threadDone = false;
    protected volatile AccessLog accessLog = null;
    private volatile boolean accessLogScanComplete = false;
    private int startStopThreads = 1;
    protected ExecutorService startStopExecutor;
    private static final Log log = LogFactory.getLog(ContainerBase.class);
    protected static final StringManager sm = StringManager.getManager(Constants.Package);

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/ContainerBase$PrivilegedAddChild.class */
    public class PrivilegedAddChild implements PrivilegedAction<Void> {
        private final Container child;

        PrivilegedAddChild(Container child) {
            ContainerBase.this = this$0;
            this.child = child;
        }

        @Override // java.security.PrivilegedAction
        public Void run() {
            ContainerBase.this.addChildInternal(this.child);
            return null;
        }
    }

    @Override // org.apache.catalina.Container
    public int getStartStopThreads() {
        return this.startStopThreads;
    }

    private int getStartStopThreadsInternal() {
        int result = getStartStopThreads();
        if (result > 0) {
            return result;
        }
        int result2 = Runtime.getRuntime().availableProcessors() + result;
        if (result2 < 1) {
            result2 = 1;
        }
        return result2;
    }

    @Override // org.apache.catalina.Container
    public void setStartStopThreads(int startStopThreads) {
        int oldStartStopThreads = this.startStopThreads;
        this.startStopThreads = startStopThreads;
        if (oldStartStopThreads != startStopThreads && this.startStopExecutor != null) {
            reconfigureStartStopExecutor(getStartStopThreadsInternal());
        }
    }

    @Override // org.apache.catalina.Container
    public int getBackgroundProcessorDelay() {
        return this.backgroundProcessorDelay;
    }

    @Override // org.apache.catalina.Container
    public void setBackgroundProcessorDelay(int delay) {
        this.backgroundProcessorDelay = delay;
    }

    @Override // org.apache.catalina.Container
    public Log getLogger() {
        if (this.logger != null) {
            return this.logger;
        }
        this.logger = LogFactory.getLog(getLogName());
        return this.logger;
    }

    @Override // org.apache.catalina.Container
    public String getLogName() {
        if (this.logName != null) {
            return this.logName;
        }
        String loggerName = null;
        Container container = this;
        while (true) {
            Container current = container;
            if (current != null) {
                String name = current.getName();
                if (name == null || name.equals("")) {
                    name = "/";
                } else if (name.startsWith("##")) {
                    name = "/" + name;
                }
                loggerName = PropertyAccessor.PROPERTY_KEY_PREFIX + name + "]" + (loggerName != null ? "." + loggerName : "");
                container = current.getParent();
            } else {
                this.logName = ContainerBase.class.getName() + "." + loggerName;
                return this.logName;
            }
        }
    }

    @Override // org.apache.catalina.Container
    public Cluster getCluster() {
        Lock readLock = this.clusterLock.readLock();
        readLock.lock();
        try {
            if (this.cluster != null) {
                return this.cluster;
            }
            if (this.parent != null) {
                return this.parent.getCluster();
            }
            return null;
        } finally {
            readLock.unlock();
        }
    }

    protected Cluster getClusterInternal() {
        Lock readLock = this.clusterLock.readLock();
        readLock.lock();
        try {
            return this.cluster;
        } finally {
            readLock.unlock();
        }
    }

    @Override // org.apache.catalina.Container
    public void setCluster(Cluster cluster) {
        Lock writeLock = this.clusterLock.writeLock();
        writeLock.lock();
        try {
            Cluster oldCluster = this.cluster;
            if (oldCluster == cluster) {
                return;
            }
            this.cluster = cluster;
            if (getState().isAvailable() && oldCluster != null && (oldCluster instanceof Lifecycle)) {
                try {
                    ((Lifecycle) oldCluster).stop();
                } catch (LifecycleException e) {
                    log.error("ContainerBase.setCluster: stop: ", e);
                }
            }
            if (cluster != null) {
                cluster.setContainer(this);
            }
            if (getState().isAvailable() && cluster != null && (cluster instanceof Lifecycle)) {
                try {
                    ((Lifecycle) cluster).start();
                } catch (LifecycleException e2) {
                    log.error("ContainerBase.setCluster: start: ", e2);
                }
            }
            writeLock.unlock();
            this.support.firePropertyChange("cluster", oldCluster, cluster);
        } finally {
            writeLock.unlock();
        }
    }

    @Override // org.apache.catalina.Container
    public String getName() {
        return this.name;
    }

    @Override // org.apache.catalina.Container
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException(sm.getString("containerBase.nullName"));
        }
        String oldName = this.name;
        this.name = name;
        this.support.firePropertyChange("name", oldName, this.name);
    }

    public boolean getStartChildren() {
        return this.startChildren;
    }

    public void setStartChildren(boolean startChildren) {
        boolean oldStartChildren = this.startChildren;
        this.startChildren = startChildren;
        this.support.firePropertyChange("startChildren", oldStartChildren, this.startChildren);
    }

    @Override // org.apache.catalina.Container
    public Container getParent() {
        return this.parent;
    }

    @Override // org.apache.catalina.Container
    public void setParent(Container container) {
        Container oldParent = this.parent;
        this.parent = container;
        this.support.firePropertyChange("parent", oldParent, this.parent);
    }

    @Override // org.apache.catalina.Container
    public ClassLoader getParentClassLoader() {
        if (this.parentClassLoader != null) {
            return this.parentClassLoader;
        }
        if (this.parent != null) {
            return this.parent.getParentClassLoader();
        }
        return ClassLoader.getSystemClassLoader();
    }

    @Override // org.apache.catalina.Container
    public void setParentClassLoader(ClassLoader parent) {
        ClassLoader oldParentClassLoader = this.parentClassLoader;
        this.parentClassLoader = parent;
        this.support.firePropertyChange("parentClassLoader", oldParentClassLoader, this.parentClassLoader);
    }

    @Override // org.apache.catalina.Container
    public Pipeline getPipeline() {
        return this.pipeline;
    }

    @Override // org.apache.catalina.Container
    public Realm getRealm() {
        Lock l = this.realmLock.readLock();
        l.lock();
        try {
            if (this.realm != null) {
                return this.realm;
            }
            if (this.parent != null) {
                return this.parent.getRealm();
            }
            return null;
        } finally {
            l.unlock();
        }
    }

    public Realm getRealmInternal() {
        Lock l = this.realmLock.readLock();
        l.lock();
        try {
            return this.realm;
        } finally {
            l.unlock();
        }
    }

    @Override // org.apache.catalina.Container
    public void setRealm(Realm realm) {
        Lock l = this.realmLock.writeLock();
        l.lock();
        try {
            Realm oldRealm = this.realm;
            if (oldRealm == realm) {
                return;
            }
            this.realm = realm;
            if (getState().isAvailable() && oldRealm != null && (oldRealm instanceof Lifecycle)) {
                try {
                    ((Lifecycle) oldRealm).stop();
                } catch (LifecycleException e) {
                    log.error("ContainerBase.setRealm: stop: ", e);
                }
            }
            if (realm != null) {
                realm.setContainer(this);
            }
            if (getState().isAvailable() && realm != null && (realm instanceof Lifecycle)) {
                try {
                    ((Lifecycle) realm).start();
                } catch (LifecycleException e2) {
                    log.error("ContainerBase.setRealm: start: ", e2);
                }
            }
            this.support.firePropertyChange("realm", oldRealm, this.realm);
            l.unlock();
        } finally {
            l.unlock();
        }
    }

    @Override // org.apache.catalina.Container
    public void addChild(Container child) {
        if (Globals.IS_SECURITY_ENABLED) {
            PrivilegedAction<Void> dp = new PrivilegedAddChild(child);
            AccessController.doPrivileged(dp);
            return;
        }
        addChildInternal(child);
    }

    public void addChildInternal(Container child) {
        if (log.isDebugEnabled()) {
            log.debug("Add child " + child + " " + this);
        }
        synchronized (this.children) {
            if (this.children.get(child.getName()) != null) {
                throw new IllegalArgumentException("addChild:  Child name '" + child.getName() + "' is not unique");
            }
            child.setParent(this);
            this.children.put(child.getName(), child);
        }
        try {
            try {
                if ((getState().isAvailable() || LifecycleState.STARTING_PREP.equals(getState())) && this.startChildren) {
                    child.start();
                }
            } catch (LifecycleException e) {
                log.error("ContainerBase.addChild: start: ", e);
                throw new IllegalStateException("ContainerBase.addChild: start: " + e);
            }
        } finally {
            fireContainerEvent(Container.ADD_CHILD_EVENT, child);
        }
    }

    @Override // org.apache.catalina.Container
    public void addContainerListener(ContainerListener listener) {
        this.listeners.add(listener);
    }

    @Override // org.apache.catalina.Container
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    @Override // org.apache.catalina.Container
    public Container findChild(String name) {
        Container container;
        if (name == null) {
            return null;
        }
        synchronized (this.children) {
            container = this.children.get(name);
        }
        return container;
    }

    @Override // org.apache.catalina.Container
    public Container[] findChildren() {
        Container[] containerArr;
        synchronized (this.children) {
            Container[] results = new Container[this.children.size()];
            containerArr = (Container[]) this.children.values().toArray(results);
        }
        return containerArr;
    }

    @Override // org.apache.catalina.Container
    public ContainerListener[] findContainerListeners() {
        ContainerListener[] results = new ContainerListener[0];
        return (ContainerListener[]) this.listeners.toArray(results);
    }

    @Override // org.apache.catalina.Container
    public void removeChild(Container child) {
        if (child == null) {
            return;
        }
        try {
            if (child.getState().isAvailable()) {
                child.stop();
            }
        } catch (LifecycleException e) {
            log.error("ContainerBase.removeChild: stop: ", e);
        }
        try {
            if (!LifecycleState.DESTROYING.equals(child.getState())) {
                child.destroy();
            }
        } catch (LifecycleException e2) {
            log.error("ContainerBase.removeChild: destroy: ", e2);
        }
        synchronized (this.children) {
            if (this.children.get(child.getName()) == null) {
                return;
            }
            this.children.remove(child.getName());
            fireContainerEvent(Container.REMOVE_CHILD_EVENT, child);
        }
    }

    @Override // org.apache.catalina.Container
    public void removeContainerListener(ContainerListener listener) {
        this.listeners.remove(listener);
    }

    @Override // org.apache.catalina.Container
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
        reconfigureStartStopExecutor(getStartStopThreadsInternal());
        super.initInternal();
    }

    private void reconfigureStartStopExecutor(int threads) {
        if (threads == 1) {
            if (!(this.startStopExecutor instanceof InlineExecutorService)) {
                this.startStopExecutor = new InlineExecutorService();
            }
        } else if (this.startStopExecutor instanceof ThreadPoolExecutor) {
            ((ThreadPoolExecutor) this.startStopExecutor).setMaximumPoolSize(threads);
            ((ThreadPoolExecutor) this.startStopExecutor).setCorePoolSize(threads);
        } else {
            BlockingQueue<Runnable> startStopQueue = new LinkedBlockingQueue<>();
            ThreadPoolExecutor tpe = new ThreadPoolExecutor(threads, threads, 10L, TimeUnit.SECONDS, startStopQueue, new StartStopThreadFactory(getName() + "-startStop-"));
            tpe.allowCoreThreadTimeOut(true);
            this.startStopExecutor = tpe;
        }
    }

    @Override // org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        this.logger = null;
        getLogger();
        Cluster cluster = getClusterInternal();
        if (cluster instanceof Lifecycle) {
            ((Lifecycle) cluster).start();
        }
        Realm realm = getRealmInternal();
        if (realm instanceof Lifecycle) {
            ((Lifecycle) realm).start();
        }
        Container[] children = findChildren();
        List<Future<Void>> results = new ArrayList<>();
        for (Container container : children) {
            results.add(this.startStopExecutor.submit(new StartChild(container)));
        }
        MultiThrowable multiThrowable = new MultiThrowable();
        for (Future<Void> result : results) {
            try {
                result.get();
            } catch (Throwable e) {
                log.error(sm.getString("containerBase.threadedStartFailed"), e);
                multiThrowable.add(e);
            }
        }
        if (multiThrowable.size() > 0) {
            throw new LifecycleException(sm.getString("containerBase.threadedStartFailed"), multiThrowable.getThrowable());
        }
        if (this.pipeline instanceof Lifecycle) {
            ((Lifecycle) this.pipeline).start();
        }
        setState(LifecycleState.STARTING);
        threadStart();
    }

    @Override // org.apache.catalina.util.LifecycleBase
    public synchronized void stopInternal() throws LifecycleException {
        threadStop();
        setState(LifecycleState.STOPPING);
        if ((this.pipeline instanceof Lifecycle) && ((Lifecycle) this.pipeline).getState().isAvailable()) {
            ((Lifecycle) this.pipeline).stop();
        }
        Container[] children = findChildren();
        List<Future<Void>> results = new ArrayList<>();
        for (Container container : children) {
            results.add(this.startStopExecutor.submit(new StopChild(container)));
        }
        boolean fail = false;
        for (Future<Void> result : results) {
            try {
                result.get();
            } catch (Exception e) {
                log.error(sm.getString("containerBase.threadedStopFailed"), e);
                fail = true;
            }
        }
        if (fail) {
            throw new LifecycleException(sm.getString("containerBase.threadedStopFailed"));
        }
        Realm realm = getRealmInternal();
        if (realm instanceof Lifecycle) {
            ((Lifecycle) realm).stop();
        }
        Cluster cluster = getClusterInternal();
        if (cluster instanceof Lifecycle) {
            ((Lifecycle) cluster).stop();
        }
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void destroyInternal() throws LifecycleException {
        Container[] findChildren;
        Realm realm = getRealmInternal();
        if (realm instanceof Lifecycle) {
            ((Lifecycle) realm).destroy();
        }
        Cluster cluster = getClusterInternal();
        if (cluster instanceof Lifecycle) {
            ((Lifecycle) cluster).destroy();
        }
        if (this.pipeline instanceof Lifecycle) {
            ((Lifecycle) this.pipeline).destroy();
        }
        for (Container child : findChildren()) {
            removeChild(child);
        }
        if (this.parent != null) {
            this.parent.removeChild(this);
        }
        if (this.startStopExecutor != null) {
            this.startStopExecutor.shutdownNow();
        }
        super.destroyInternal();
    }

    @Override // org.apache.catalina.Container
    public void logAccess(Request request, Response response, long time, boolean useDefault) {
        boolean logged = false;
        if (getAccessLog() != null) {
            getAccessLog().log(request, response, time);
            logged = true;
        }
        if (getParent() != null) {
            getParent().logAccess(request, response, time, useDefault && !logged);
        }
    }

    @Override // org.apache.catalina.Container
    public AccessLog getAccessLog() {
        if (this.accessLogScanComplete) {
            return this.accessLog;
        }
        AccessLogAdapter adapter = null;
        Valve[] valves = getPipeline().getValves();
        for (Valve valve : valves) {
            if (valve instanceof AccessLog) {
                if (adapter == null) {
                    adapter = new AccessLogAdapter((AccessLog) valve);
                } else {
                    adapter.add((AccessLog) valve);
                }
            }
        }
        if (adapter != null) {
            this.accessLog = adapter;
        }
        this.accessLogScanComplete = true;
        return this.accessLog;
    }

    public synchronized void addValve(Valve valve) {
        this.pipeline.addValve(valve);
    }

    @Override // org.apache.catalina.Container
    public void backgroundProcess() {
        if (!getState().isAvailable()) {
            return;
        }
        Cluster cluster = getClusterInternal();
        if (cluster != null) {
            try {
                cluster.backgroundProcess();
            } catch (Exception e) {
                log.warn(sm.getString("containerBase.backgroundProcess.cluster", cluster), e);
            }
        }
        Realm realm = getRealmInternal();
        if (realm != null) {
            try {
                realm.backgroundProcess();
            } catch (Exception e2) {
                log.warn(sm.getString("containerBase.backgroundProcess.realm", realm), e2);
            }
        }
        Valve first = this.pipeline.getFirst();
        while (true) {
            Valve current = first;
            if (current != null) {
                try {
                    current.backgroundProcess();
                } catch (Exception e3) {
                    log.warn(sm.getString("containerBase.backgroundProcess.valve", current), e3);
                }
                first = current.getNext();
            } else {
                fireLifecycleEvent(Lifecycle.PERIODIC_EVENT, null);
                return;
            }
        }
    }

    @Override // org.apache.catalina.Container
    public File getCatalinaBase() {
        if (this.parent == null) {
            return null;
        }
        return this.parent.getCatalinaBase();
    }

    @Override // org.apache.catalina.Container
    public File getCatalinaHome() {
        if (this.parent == null) {
            return null;
        }
        return this.parent.getCatalinaHome();
    }

    @Override // org.apache.catalina.Container
    public void fireContainerEvent(String type, Object data) {
        if (this.listeners.size() < 1) {
            return;
        }
        ContainerEvent event = new ContainerEvent(this, type, data);
        for (ContainerListener listener : this.listeners) {
            listener.containerEvent(event);
        }
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getDomainInternal() {
        Container p = getParent();
        if (p == null) {
            return null;
        }
        return p.getDomain();
    }

    @Override // org.apache.catalina.Container
    public String getMBeanKeyProperties() {
        Container c = this;
        StringBuilder keyProperties = new StringBuilder();
        int containerCount = 0;
        while (true) {
            if (c instanceof Engine) {
                break;
            }
            if (c instanceof Wrapper) {
                keyProperties.insert(0, ",servlet=");
                keyProperties.insert(9, c.getName());
            } else if (c instanceof Context) {
                keyProperties.insert(0, ",context=");
                ContextName cn = new ContextName(c.getName(), false);
                keyProperties.insert(9, cn.getDisplayName());
            } else if (c instanceof Host) {
                keyProperties.insert(0, ",host=");
                keyProperties.insert(6, c.getName());
            } else if (c == null) {
                keyProperties.append(",container");
                int i = containerCount;
                int i2 = containerCount + 1;
                keyProperties.append(i);
                keyProperties.append("=null");
                break;
            } else {
                keyProperties.append(",container");
                int i3 = containerCount;
                containerCount++;
                keyProperties.append(i3);
                keyProperties.append('=');
                keyProperties.append(c.getName());
            }
            c = c.getParent();
        }
        return keyProperties.toString();
    }

    public ObjectName[] getChildren() {
        List<ObjectName> names = new ArrayList<>(this.children.size());
        for (Container next : this.children.values()) {
            if (next instanceof ContainerBase) {
                names.add(next.getObjectName());
            }
        }
        return (ObjectName[]) names.toArray(new ObjectName[names.size()]);
    }

    public void threadStart() {
        if (this.thread != null || this.backgroundProcessorDelay <= 0) {
            return;
        }
        this.threadDone = false;
        String threadName = "ContainerBackgroundProcessor[" + toString() + "]";
        this.thread = new Thread(new ContainerBackgroundProcessor(), threadName);
        this.thread.setDaemon(true);
        this.thread.start();
    }

    public void threadStop() {
        if (this.thread == null) {
            return;
        }
        this.threadDone = true;
        this.thread.interrupt();
        try {
            this.thread.join();
        } catch (InterruptedException e) {
        }
        this.thread = null;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        Container parent = getParent();
        if (parent != null) {
            sb.append(parent.toString());
            sb.append('.');
        }
        sb.append(getClass().getSimpleName());
        sb.append('[');
        sb.append(getName());
        sb.append(']');
        return sb.toString();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/ContainerBase$ContainerBackgroundProcessor.class */
    public class ContainerBackgroundProcessor implements Runnable {
        protected ContainerBackgroundProcessor() {
            ContainerBase.this = this$0;
        }

        @Override // java.lang.Runnable
        public void run() {
            Throwable t = null;
            String unexpectedDeathMessage = ContainerBase.sm.getString("containerBase.backgroundProcess.unexpectedThreadDeath", Thread.currentThread().getName());
            while (!ContainerBase.this.threadDone) {
                try {
                    try {
                        try {
                            Thread.sleep(ContainerBase.this.backgroundProcessorDelay * 1000);
                        } catch (InterruptedException e) {
                        }
                        if (!ContainerBase.this.threadDone) {
                            processChildren(ContainerBase.this);
                        }
                    } catch (Error | RuntimeException e2) {
                        t = e2;
                        throw e2;
                    }
                } catch (Throwable th) {
                    if (!ContainerBase.this.threadDone) {
                        ContainerBase.log.error(unexpectedDeathMessage, t);
                    }
                    throw th;
                }
            }
            if (!ContainerBase.this.threadDone) {
                ContainerBase.log.error(unexpectedDeathMessage, null);
            }
        }

        protected void processChildren(Container container) {
            ClassLoader originalClassLoader = null;
            try {
                if (container instanceof Context) {
                    Loader loader = ((Context) container).getLoader();
                    if (loader == null) {
                        if (container instanceof Context) {
                            ((Context) container).unbind(false, null);
                            return;
                        }
                        return;
                    }
                    originalClassLoader = ((Context) container).bind(false, null);
                }
                container.backgroundProcess();
                Container[] children = container.findChildren();
                for (int i = 0; i < children.length; i++) {
                    if (children[i].getBackgroundProcessorDelay() <= 0) {
                        processChildren(children[i]);
                    }
                }
            } catch (Throwable t) {
                try {
                    ExceptionUtils.handleThrowable(t);
                    ContainerBase.log.error("Exception invoking periodic operation: ", t);
                    if (container instanceof Context) {
                        ((Context) container).unbind(false, originalClassLoader);
                    }
                } finally {
                    if (container instanceof Context) {
                        ((Context) container).unbind(false, originalClassLoader);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/ContainerBase$StartChild.class */
    public static class StartChild implements Callable<Void> {
        private Container child;

        public StartChild(Container child) {
            this.child = child;
        }

        @Override // java.util.concurrent.Callable
        public Void call() throws LifecycleException {
            this.child.start();
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/ContainerBase$StopChild.class */
    public static class StopChild implements Callable<Void> {
        private Container child;

        public StopChild(Container child) {
            this.child = child;
        }

        @Override // java.util.concurrent.Callable
        public Void call() throws LifecycleException {
            if (this.child.getState().isAvailable()) {
                this.child.stop();
                return null;
            }
            return null;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/ContainerBase$StartStopThreadFactory.class */
    public static class StartStopThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        public StartStopThreadFactory(String namePrefix) {
            SecurityManager s = System.getSecurityManager();
            this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = namePrefix;
        }

        @Override // java.util.concurrent.ThreadFactory
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        }
    }
}