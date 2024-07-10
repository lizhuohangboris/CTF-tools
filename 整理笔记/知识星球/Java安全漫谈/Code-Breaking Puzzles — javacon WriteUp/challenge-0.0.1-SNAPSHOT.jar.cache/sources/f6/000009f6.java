package org.apache.coyote;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistration;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.WebConnection;
import org.apache.juli.logging.Log;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.collections.SynchronizedStack;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/AbstractProtocol.class */
public abstract class AbstractProtocol<S> implements ProtocolHandler, MBeanRegistration {
    private static final StringManager sm = StringManager.getManager(AbstractProtocol.class);
    private static final AtomicInteger nameCounter = new AtomicInteger(0);
    private final AbstractEndpoint<S, ?> endpoint;
    private AbstractEndpoint.Handler<S> handler;
    protected Adapter adapter;
    protected String domain;
    protected ObjectName oname;
    protected MBeanServer mserver;
    protected ObjectName rgOname = null;
    private int nameIndex = 0;
    private final Set<Processor> waitingProcessors = Collections.newSetFromMap(new ConcurrentHashMap());
    private AbstractProtocol<S>.AsyncTimeout asyncTimeout = null;
    protected int processorCache = 200;
    private String clientCertProvider = null;
    private int maxHeaderCount = 100;

    public abstract Log getLog();

    protected abstract String getNamePrefix();

    protected abstract String getProtocolName();

    protected abstract UpgradeProtocol getNegotiatedProtocol(String str);

    protected abstract UpgradeProtocol getUpgradeProtocol(String str);

    protected abstract Processor createProcessor();

    protected abstract Processor createUpgradeProcessor(SocketWrapperBase<?> socketWrapperBase, UpgradeToken upgradeToken);

    public AbstractProtocol(AbstractEndpoint<S, ?> endpoint) {
        this.endpoint = endpoint;
        setConnectionLinger(-1);
        setTcpNoDelay(true);
    }

    public boolean setProperty(String name, String value) {
        return this.endpoint.setProperty(name, value);
    }

    public String getProperty(String name) {
        return this.endpoint.getProperty(name);
    }

    @Override // org.apache.coyote.ProtocolHandler
    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }

    @Override // org.apache.coyote.ProtocolHandler
    public Adapter getAdapter() {
        return this.adapter;
    }

    public int getProcessorCache() {
        return this.processorCache;
    }

    public void setProcessorCache(int processorCache) {
        this.processorCache = processorCache;
    }

    public String getClientCertProvider() {
        return this.clientCertProvider;
    }

    public void setClientCertProvider(String s) {
        this.clientCertProvider = s;
    }

    public int getMaxHeaderCount() {
        return this.maxHeaderCount;
    }

    public void setMaxHeaderCount(int maxHeaderCount) {
        this.maxHeaderCount = maxHeaderCount;
    }

    @Override // org.apache.coyote.ProtocolHandler
    public boolean isAprRequired() {
        return false;
    }

    @Override // org.apache.coyote.ProtocolHandler
    public boolean isSendfileSupported() {
        return this.endpoint.getUseSendfile();
    }

    public AbstractProtocol<S>.AsyncTimeout getAsyncTimeout() {
        return this.asyncTimeout;
    }

    @Override // org.apache.coyote.ProtocolHandler
    public Executor getExecutor() {
        return this.endpoint.getExecutor();
    }

    public void setExecutor(Executor executor) {
        this.endpoint.setExecutor(executor);
    }

    public int getMaxThreads() {
        return this.endpoint.getMaxThreads();
    }

    public void setMaxThreads(int maxThreads) {
        this.endpoint.setMaxThreads(maxThreads);
    }

    public int getMaxConnections() {
        return this.endpoint.getMaxConnections();
    }

    public void setMaxConnections(int maxConnections) {
        this.endpoint.setMaxConnections(maxConnections);
    }

    public int getMinSpareThreads() {
        return this.endpoint.getMinSpareThreads();
    }

    public void setMinSpareThreads(int minSpareThreads) {
        this.endpoint.setMinSpareThreads(minSpareThreads);
    }

    public int getThreadPriority() {
        return this.endpoint.getThreadPriority();
    }

    public void setThreadPriority(int threadPriority) {
        this.endpoint.setThreadPriority(threadPriority);
    }

    public int getAcceptCount() {
        return this.endpoint.getAcceptCount();
    }

    public void setAcceptCount(int acceptCount) {
        this.endpoint.setAcceptCount(acceptCount);
    }

    public boolean getTcpNoDelay() {
        return this.endpoint.getTcpNoDelay();
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.endpoint.setTcpNoDelay(tcpNoDelay);
    }

    public int getConnectionLinger() {
        return this.endpoint.getConnectionLinger();
    }

    public void setConnectionLinger(int connectionLinger) {
        this.endpoint.setConnectionLinger(connectionLinger);
    }

    public int getKeepAliveTimeout() {
        return this.endpoint.getKeepAliveTimeout();
    }

    public void setKeepAliveTimeout(int keepAliveTimeout) {
        this.endpoint.setKeepAliveTimeout(keepAliveTimeout);
    }

    public InetAddress getAddress() {
        return this.endpoint.getAddress();
    }

    public void setAddress(InetAddress ia) {
        this.endpoint.setAddress(ia);
    }

    public int getPort() {
        return this.endpoint.getPort();
    }

    public void setPort(int port) {
        this.endpoint.setPort(port);
    }

    public int getLocalPort() {
        return this.endpoint.getLocalPort();
    }

    public int getConnectionTimeout() {
        return this.endpoint.getConnectionTimeout();
    }

    public void setConnectionTimeout(int timeout) {
        this.endpoint.setConnectionTimeout(timeout);
    }

    public long getConnectionCount() {
        return this.endpoint.getConnectionCount();
    }

    public void setAcceptorThreadCount(int threadCount) {
        this.endpoint.setAcceptorThreadCount(threadCount);
    }

    public int getAcceptorThreadCount() {
        return this.endpoint.getAcceptorThreadCount();
    }

    public void setAcceptorThreadPriority(int threadPriority) {
        this.endpoint.setAcceptorThreadPriority(threadPriority);
    }

    public int getAcceptorThreadPriority() {
        return this.endpoint.getAcceptorThreadPriority();
    }

    public synchronized int getNameIndex() {
        if (this.nameIndex == 0) {
            this.nameIndex = nameCounter.incrementAndGet();
        }
        return this.nameIndex;
    }

    public String getName() {
        return ObjectName.quote(getNameInternal());
    }

    private String getNameInternal() {
        StringBuilder name = new StringBuilder(getNamePrefix());
        name.append('-');
        if (getAddress() != null) {
            name.append(getAddress().getHostAddress());
            name.append('-');
        }
        int port = getPort();
        if (port == 0) {
            name.append("auto-");
            name.append(getNameIndex());
            int port2 = getLocalPort();
            if (port2 != -1) {
                name.append('-');
                name.append(port2);
            }
        } else {
            name.append(port);
        }
        return name.toString();
    }

    public void addWaitingProcessor(Processor processor) {
        this.waitingProcessors.add(processor);
    }

    public void removeWaitingProcessor(Processor processor) {
        this.waitingProcessors.remove(processor);
    }

    public AbstractEndpoint<S, ?> getEndpoint() {
        return this.endpoint;
    }

    protected AbstractEndpoint.Handler<S> getHandler() {
        return this.handler;
    }

    public void setHandler(AbstractEndpoint.Handler<S> handler) {
        this.handler = handler;
    }

    public ObjectName getObjectName() {
        return this.oname;
    }

    public String getDomain() {
        return this.domain;
    }

    public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
        this.oname = name;
        this.mserver = server;
        this.domain = name.getDomain();
        return name;
    }

    public void postRegister(Boolean registrationDone) {
    }

    public void preDeregister() throws Exception {
    }

    public void postDeregister() {
    }

    private ObjectName createObjectName() throws MalformedObjectNameException {
        this.domain = getAdapter().getDomain();
        if (this.domain == null) {
            return null;
        }
        StringBuilder name = new StringBuilder(getDomain());
        name.append(":type=ProtocolHandler,port=");
        int port = getPort();
        if (port > 0) {
            name.append(getPort());
        } else {
            name.append("auto-");
            name.append(getNameIndex());
        }
        InetAddress address = getAddress();
        if (address != null) {
            name.append(",address=");
            name.append(ObjectName.quote(address.getHostAddress()));
        }
        return new ObjectName(name.toString());
    }

    @Override // org.apache.coyote.ProtocolHandler
    public void init() throws Exception {
        if (getLog().isInfoEnabled()) {
            getLog().info(sm.getString("abstractProtocolHandler.init", getName()));
        }
        if (this.oname == null) {
            this.oname = createObjectName();
            if (this.oname != null) {
                Registry.getRegistry(null, null).registerComponent(this, this.oname, (String) null);
            }
        }
        if (this.domain != null) {
            this.rgOname = new ObjectName(this.domain + ":type=GlobalRequestProcessor,name=" + getName());
            Registry.getRegistry(null, null).registerComponent(getHandler().getGlobal(), this.rgOname, (String) null);
        }
        String endpointName = getName();
        this.endpoint.setName(endpointName.substring(1, endpointName.length() - 1));
        this.endpoint.setDomain(this.domain);
        this.endpoint.init();
    }

    @Override // org.apache.coyote.ProtocolHandler
    public void start() throws Exception {
        if (getLog().isInfoEnabled()) {
            getLog().info(sm.getString("abstractProtocolHandler.start", getName()));
        }
        this.endpoint.start();
        this.asyncTimeout = new AsyncTimeout();
        Thread timeoutThread = new Thread(this.asyncTimeout, getNameInternal() + "-AsyncTimeout");
        int priority = this.endpoint.getThreadPriority();
        if (priority < 1 || priority > 10) {
            priority = 5;
        }
        timeoutThread.setPriority(priority);
        timeoutThread.setDaemon(true);
        timeoutThread.start();
    }

    @Override // org.apache.coyote.ProtocolHandler
    public void pause() throws Exception {
        if (getLog().isInfoEnabled()) {
            getLog().info(sm.getString("abstractProtocolHandler.pause", getName()));
        }
        this.endpoint.pause();
    }

    public boolean isPaused() {
        return this.endpoint.isPaused();
    }

    @Override // org.apache.coyote.ProtocolHandler
    public void resume() throws Exception {
        if (getLog().isInfoEnabled()) {
            getLog().info(sm.getString("abstractProtocolHandler.resume", getName()));
        }
        this.endpoint.resume();
    }

    @Override // org.apache.coyote.ProtocolHandler
    public void stop() throws Exception {
        if (getLog().isInfoEnabled()) {
            getLog().info(sm.getString("abstractProtocolHandler.stop", getName()));
        }
        if (this.asyncTimeout != null) {
            this.asyncTimeout.stop();
        }
        this.endpoint.stop();
    }

    /* JADX WARN: Finally extract failed */
    @Override // org.apache.coyote.ProtocolHandler
    public void destroy() throws Exception {
        if (getLog().isInfoEnabled()) {
            getLog().info(sm.getString("abstractProtocolHandler.destroy", getName()));
        }
        try {
            this.endpoint.destroy();
            if (this.oname != null) {
                if (this.mserver == null) {
                    Registry.getRegistry(null, null).unregisterComponent(this.oname);
                } else {
                    try {
                        this.mserver.unregisterMBean(this.oname);
                    } catch (MBeanRegistrationException | InstanceNotFoundException e) {
                        getLog().info(sm.getString("abstractProtocol.mbeanDeregistrationFailed", this.oname, this.mserver));
                    }
                }
            }
            if (this.rgOname != null) {
                Registry.getRegistry(null, null).unregisterComponent(this.rgOname);
            }
        } catch (Throwable th) {
            if (this.oname != null) {
                if (this.mserver == null) {
                    Registry.getRegistry(null, null).unregisterComponent(this.oname);
                } else {
                    try {
                        this.mserver.unregisterMBean(this.oname);
                    } catch (MBeanRegistrationException | InstanceNotFoundException e2) {
                        getLog().info(sm.getString("abstractProtocol.mbeanDeregistrationFailed", this.oname, this.mserver));
                    }
                }
            }
            if (this.rgOname != null) {
                Registry.getRegistry(null, null).unregisterComponent(this.rgOname);
            }
            throw th;
        }
    }

    @Override // org.apache.coyote.ProtocolHandler
    public void closeServerSocketGraceful() {
        this.endpoint.closeServerSocketGraceful();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/AbstractProtocol$ConnectionHandler.class */
    public static class ConnectionHandler<S> implements AbstractEndpoint.Handler<S> {
        private final AbstractProtocol<S> proto;
        private final RequestGroupInfo global = new RequestGroupInfo();
        private final AtomicLong registerCount = new AtomicLong(0);
        private final Map<S, Processor> connections = new ConcurrentHashMap();
        private final RecycledProcessors recycledProcessors = new RecycledProcessors(this);

        public ConnectionHandler(AbstractProtocol<S> proto) {
            this.proto = proto;
        }

        protected AbstractProtocol<S> getProtocol() {
            return this.proto;
        }

        protected Log getLog() {
            return getProtocol().getLog();
        }

        @Override // org.apache.tomcat.util.net.AbstractEndpoint.Handler
        public Object getGlobal() {
            return this.global;
        }

        @Override // org.apache.tomcat.util.net.AbstractEndpoint.Handler
        public void recycle() {
            this.recycledProcessors.clear();
        }

        /* JADX WARN: Finally extract failed */
        @Override // org.apache.tomcat.util.net.AbstractEndpoint.Handler
        public AbstractEndpoint.Handler.SocketState process(SocketWrapperBase<S> wrapper, SocketEvent status) {
            AbstractEndpoint.Handler.SocketState state;
            UpgradeToken upgradeToken;
            ClassLoader oldCL;
            if (getLog().isDebugEnabled()) {
                getLog().debug(AbstractProtocol.sm.getString("abstractConnectionHandler.process", wrapper.getSocket(), status));
            }
            if (wrapper == null) {
                return AbstractEndpoint.Handler.SocketState.CLOSED;
            }
            S socket = wrapper.getSocket();
            Processor processor = this.connections.get(socket);
            if (getLog().isDebugEnabled()) {
                getLog().debug(AbstractProtocol.sm.getString("abstractConnectionHandler.connectionsGet", processor, socket));
            }
            if (SocketEvent.TIMEOUT != status || (processor != 0 && processor.isAsync() && processor.checkAsyncTimeoutGeneration())) {
                if (processor != 0) {
                    getProtocol().removeWaitingProcessor(processor);
                } else if (status == SocketEvent.DISCONNECT || status == SocketEvent.ERROR) {
                    return AbstractEndpoint.Handler.SocketState.CLOSED;
                }
                ContainerThreadMarker.set();
                try {
                    if (processor == 0) {
                        try {
                            try {
                                String negotiatedProtocol = wrapper.getNegotiatedProtocol();
                                if (negotiatedProtocol != null) {
                                    UpgradeProtocol upgradeProtocol = getProtocol().getNegotiatedProtocol(negotiatedProtocol);
                                    if (upgradeProtocol != null) {
                                        processor = upgradeProtocol.getProcessor(wrapper, getProtocol().getAdapter());
                                    } else if (!negotiatedProtocol.equals("http/1.1")) {
                                        if (getLog().isDebugEnabled()) {
                                            getLog().debug(AbstractProtocol.sm.getString("abstractConnectionHandler.negotiatedProcessor.fail", negotiatedProtocol));
                                        }
                                        AbstractEndpoint.Handler.SocketState socketState = AbstractEndpoint.Handler.SocketState.CLOSED;
                                        ContainerThreadMarker.clear();
                                        return socketState;
                                    }
                                }
                            } catch (SocketException e) {
                                getLog().debug(AbstractProtocol.sm.getString("abstractConnectionHandler.socketexception.debug"), e);
                                ContainerThreadMarker.clear();
                                this.connections.remove(socket);
                                release(processor);
                                return AbstractEndpoint.Handler.SocketState.CLOSED;
                            } catch (ProtocolException e2) {
                                getLog().debug(AbstractProtocol.sm.getString("abstractConnectionHandler.protocolexception.debug"), e2);
                                ContainerThreadMarker.clear();
                                this.connections.remove(socket);
                                release(processor);
                                return AbstractEndpoint.Handler.SocketState.CLOSED;
                            }
                        } catch (IOException e3) {
                            getLog().debug(AbstractProtocol.sm.getString("abstractConnectionHandler.ioexception.debug"), e3);
                            ContainerThreadMarker.clear();
                            this.connections.remove(socket);
                            release(processor);
                            return AbstractEndpoint.Handler.SocketState.CLOSED;
                        } catch (Throwable e4) {
                            ExceptionUtils.handleThrowable(e4);
                            getLog().error(AbstractProtocol.sm.getString("abstractConnectionHandler.error"), e4);
                            ContainerThreadMarker.clear();
                            this.connections.remove(socket);
                            release(processor);
                            return AbstractEndpoint.Handler.SocketState.CLOSED;
                        }
                    }
                    if (processor == null) {
                        processor = this.recycledProcessors.pop();
                        if (getLog().isDebugEnabled()) {
                            getLog().debug(AbstractProtocol.sm.getString("abstractConnectionHandler.processorPop", processor));
                        }
                    }
                    if (processor == null) {
                        processor = getProtocol().createProcessor();
                        register(processor);
                    }
                    processor.setSslSupport(wrapper.getSslSupport(getProtocol().getClientCertProvider()));
                    this.connections.put(socket, processor);
                    AbstractEndpoint.Handler.SocketState socketState2 = AbstractEndpoint.Handler.SocketState.CLOSED;
                    do {
                        state = processor.process(wrapper, status);
                        if (state == AbstractEndpoint.Handler.SocketState.UPGRADING) {
                            upgradeToken = processor.getUpgradeToken();
                            ByteBuffer leftOverInput = processor.getLeftoverInput();
                            if (upgradeToken == null) {
                                UpgradeProtocol upgradeProtocol2 = getProtocol().getUpgradeProtocol("h2c");
                                if (upgradeProtocol2 == null) {
                                    if (getLog().isDebugEnabled()) {
                                        getLog().debug(AbstractProtocol.sm.getString("abstractConnectionHandler.negotiatedProcessor.fail", "h2c"));
                                    }
                                    AbstractEndpoint.Handler.SocketState socketState3 = AbstractEndpoint.Handler.SocketState.CLOSED;
                                    ContainerThreadMarker.clear();
                                    return socketState3;
                                }
                                processor = upgradeProtocol2.getProcessor(wrapper, getProtocol().getAdapter());
                                wrapper.unRead(leftOverInput);
                                this.connections.put(socket, processor);
                            } else {
                                HttpUpgradeHandler httpUpgradeHandler = upgradeToken.getHttpUpgradeHandler();
                                release(processor);
                                processor = getProtocol().createUpgradeProcessor(wrapper, upgradeToken);
                                if (getLog().isDebugEnabled()) {
                                    getLog().debug(AbstractProtocol.sm.getString("abstractConnectionHandler.upgradeCreate", processor, wrapper));
                                }
                                wrapper.unRead(leftOverInput);
                                wrapper.setUpgraded(true);
                                this.connections.put(socket, processor);
                                if (upgradeToken.getInstanceManager() == null) {
                                    httpUpgradeHandler.init((WebConnection) processor);
                                } else {
                                    oldCL = upgradeToken.getContextBind().bind(false, null);
                                    try {
                                        httpUpgradeHandler.init((WebConnection) processor);
                                        upgradeToken.getContextBind().unbind(false, oldCL);
                                    } finally {
                                        upgradeToken.getContextBind().unbind(false, oldCL);
                                    }
                                }
                            }
                        }
                    } while (state == AbstractEndpoint.Handler.SocketState.UPGRADING);
                    if (state == AbstractEndpoint.Handler.SocketState.LONG) {
                        longPoll(wrapper, processor);
                        if (processor.isAsync()) {
                            getProtocol().addWaitingProcessor(processor);
                        }
                    } else if (state == AbstractEndpoint.Handler.SocketState.OPEN) {
                        this.connections.remove(socket);
                        release(processor);
                        wrapper.registerReadInterest();
                    } else if (state != AbstractEndpoint.Handler.SocketState.SENDFILE) {
                        if (state == AbstractEndpoint.Handler.SocketState.UPGRADED) {
                            if (status != SocketEvent.OPEN_WRITE) {
                                longPoll(wrapper, processor);
                            }
                        } else if (state != AbstractEndpoint.Handler.SocketState.SUSPENDED) {
                            this.connections.remove(socket);
                            if (processor.isUpgrade()) {
                                upgradeToken = processor.getUpgradeToken();
                                HttpUpgradeHandler httpUpgradeHandler2 = upgradeToken.getHttpUpgradeHandler();
                                InstanceManager instanceManager = upgradeToken.getInstanceManager();
                                if (instanceManager == null) {
                                    httpUpgradeHandler2.destroy();
                                } else {
                                    oldCL = upgradeToken.getContextBind().bind(false, null);
                                    try {
                                        httpUpgradeHandler2.destroy();
                                        try {
                                            instanceManager.destroyInstance(httpUpgradeHandler2);
                                        } catch (Throwable e5) {
                                            ExceptionUtils.handleThrowable(e5);
                                            getLog().error(AbstractProtocol.sm.getString("abstractConnectionHandler.error"), e5);
                                        }
                                        upgradeToken.getContextBind().unbind(false, oldCL);
                                    } catch (Throwable th) {
                                        try {
                                            instanceManager.destroyInstance(httpUpgradeHandler2);
                                        } catch (Throwable e6) {
                                            ExceptionUtils.handleThrowable(e6);
                                            getLog().error(AbstractProtocol.sm.getString("abstractConnectionHandler.error"), e6);
                                        }
                                        throw th;
                                    }
                                }
                            } else {
                                release(processor);
                            }
                        }
                    }
                    ContainerThreadMarker.clear();
                    return state;
                } catch (Throwable th2) {
                    ContainerThreadMarker.clear();
                    throw th2;
                }
            }
            return AbstractEndpoint.Handler.SocketState.OPEN;
        }

        protected void longPoll(SocketWrapperBase<?> socket, Processor processor) {
            if (!processor.isAsync()) {
                socket.registerReadInterest();
            }
        }

        @Override // org.apache.tomcat.util.net.AbstractEndpoint.Handler
        public Set<S> getOpenSockets() {
            return this.connections.keySet();
        }

        private void release(Processor processor) {
            if (processor != null) {
                processor.recycle();
                if (!processor.isUpgrade()) {
                    this.recycledProcessors.push(processor);
                    getLog().debug("Pushed Processor [" + processor + "]");
                }
            }
        }

        @Override // org.apache.tomcat.util.net.AbstractEndpoint.Handler
        public void release(SocketWrapperBase<S> socketWrapper) {
            S socket = socketWrapper.getSocket();
            Processor processor = this.connections.remove(socket);
            release(processor);
        }

        protected void register(Processor processor) {
            if (getProtocol().getDomain() != null) {
                synchronized (this) {
                    try {
                        long count = this.registerCount.incrementAndGet();
                        RequestInfo rp = processor.getRequest().getRequestProcessor();
                        rp.setGlobalProcessor(this.global);
                        ObjectName rpName = new ObjectName(getProtocol().getDomain() + ":type=RequestProcessor,worker=" + getProtocol().getName() + ",name=" + getProtocol().getProtocolName() + "Request" + count);
                        if (getLog().isDebugEnabled()) {
                            getLog().debug("Register " + rpName);
                        }
                        Registry.getRegistry(null, null).registerComponent(rp, rpName, (String) null);
                        rp.setRpName(rpName);
                    } catch (Exception e) {
                        getLog().warn("Error registering request");
                    }
                }
            }
        }

        protected void unregister(Processor processor) {
            Request r;
            if (getProtocol().getDomain() != null) {
                synchronized (this) {
                    try {
                        r = processor.getRequest();
                    } catch (Exception e) {
                        getLog().warn("Error unregistering request", e);
                    }
                    if (r == null) {
                        return;
                    }
                    RequestInfo rp = r.getRequestProcessor();
                    rp.setGlobalProcessor(null);
                    ObjectName rpName = rp.getRpName();
                    if (getLog().isDebugEnabled()) {
                        getLog().debug("Unregister " + rpName);
                    }
                    Registry.getRegistry(null, null).unregisterComponent(rpName);
                    rp.setRpName(null);
                }
            }
        }

        @Override // org.apache.tomcat.util.net.AbstractEndpoint.Handler
        public final void pause() {
            for (Processor processor : this.connections.values()) {
                processor.pause();
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/AbstractProtocol$RecycledProcessors.class */
    public static class RecycledProcessors extends SynchronizedStack<Processor> {
        private final transient ConnectionHandler<?> handler;
        protected final AtomicInteger size = new AtomicInteger(0);

        public RecycledProcessors(ConnectionHandler<?> handler) {
            this.handler = handler;
        }

        @Override // org.apache.tomcat.util.collections.SynchronizedStack
        public boolean push(Processor processor) {
            int cacheSize = this.handler.getProtocol().getProcessorCache();
            boolean offer = cacheSize == -1 ? true : this.size.get() < cacheSize;
            boolean result = false;
            if (offer) {
                result = super.push((RecycledProcessors) processor);
                if (result) {
                    this.size.incrementAndGet();
                }
            }
            if (!result) {
                this.handler.unregister(processor);
            }
            return result;
        }

        @Override // org.apache.tomcat.util.collections.SynchronizedStack
        public Processor pop() {
            Processor result = (Processor) super.pop();
            if (result != null) {
                this.size.decrementAndGet();
            }
            return result;
        }

        @Override // org.apache.tomcat.util.collections.SynchronizedStack
        public synchronized void clear() {
            Processor pop = pop();
            while (true) {
                Processor next = pop;
                if (next != null) {
                    this.handler.unregister(next);
                    pop = pop();
                } else {
                    super.clear();
                    this.size.set(0);
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/AbstractProtocol$AsyncTimeout.class */
    public class AsyncTimeout implements Runnable {
        private volatile boolean asyncTimeoutRunning = true;

        protected AsyncTimeout() {
            AbstractProtocol.this = this$0;
        }

        @Override // java.lang.Runnable
        public void run() {
            while (this.asyncTimeoutRunning) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                }
                long now = System.currentTimeMillis();
                for (Processor processor : AbstractProtocol.this.waitingProcessors) {
                    processor.timeoutAsync(now);
                }
                while (AbstractProtocol.this.endpoint.isPaused() && this.asyncTimeoutRunning) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e2) {
                    }
                }
            }
        }

        protected void stop() {
            this.asyncTimeoutRunning = false;
            for (Processor processor : AbstractProtocol.this.waitingProcessors) {
                processor.timeoutAsync(-1L);
            }
        }
    }
}