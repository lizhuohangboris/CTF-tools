package org.apache.catalina.core;

import ch.qos.logback.core.spi.AbstractComponentTracker;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlException;
import java.util.Random;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.naming.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.catalina.mbeans.MBeanFactory;
import org.apache.catalina.startup.Catalina;
import org.apache.catalina.util.ExtensionValidator;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.catalina.util.ServerInfo;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.StringCache;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/StandardServer.class */
public final class StandardServer extends LifecycleMBeanBase implements Server {
    private NamingResourcesImpl globalNamingResources;
    private final NamingContextListener namingContextListener;
    private ObjectName onameStringCache;
    private ObjectName onameMBeanFactory;
    private static final Log log = LogFactory.getLog(StandardServer.class);
    private static final StringManager sm = StringManager.getManager(Constants.Package);
    private Context globalNamingContext = null;
    private int port = 8005;
    private String address = "localhost";
    private Random random = null;
    private Service[] services = new Service[0];
    private final Object servicesLock = new Object();
    private String shutdown = "SHUTDOWN";
    final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private volatile boolean stopAwait = false;
    private Catalina catalina = null;
    private ClassLoader parentClassLoader = null;
    private volatile Thread awaitThread = null;
    private volatile ServerSocket awaitSocket = null;
    private File catalinaHome = null;
    private File catalinaBase = null;
    private final Object namingToken = new Object();

    public StandardServer() {
        this.globalNamingResources = null;
        this.globalNamingResources = new NamingResourcesImpl();
        this.globalNamingResources.setContainer(this);
        if (isUseNaming()) {
            this.namingContextListener = new NamingContextListener();
            addLifecycleListener(this.namingContextListener);
            return;
        }
        this.namingContextListener = null;
    }

    @Override // org.apache.catalina.Server
    public Object getNamingToken() {
        return this.namingToken;
    }

    @Override // org.apache.catalina.Server
    public Context getGlobalNamingContext() {
        return this.globalNamingContext;
    }

    public void setGlobalNamingContext(Context globalNamingContext) {
        this.globalNamingContext = globalNamingContext;
    }

    @Override // org.apache.catalina.Server
    public NamingResourcesImpl getGlobalNamingResources() {
        return this.globalNamingResources;
    }

    @Override // org.apache.catalina.Server
    public void setGlobalNamingResources(NamingResourcesImpl globalNamingResources) {
        NamingResourcesImpl oldGlobalNamingResources = this.globalNamingResources;
        this.globalNamingResources = globalNamingResources;
        this.globalNamingResources.setContainer(this);
        this.support.firePropertyChange("globalNamingResources", oldGlobalNamingResources, this.globalNamingResources);
    }

    public String getServerInfo() {
        return ServerInfo.getServerInfo();
    }

    public String getServerBuilt() {
        return ServerInfo.getServerBuilt();
    }

    public String getServerNumber() {
        return ServerInfo.getServerNumber();
    }

    @Override // org.apache.catalina.Server
    public int getPort() {
        return this.port;
    }

    @Override // org.apache.catalina.Server
    public void setPort(int port) {
        this.port = port;
    }

    @Override // org.apache.catalina.Server
    public String getAddress() {
        return this.address;
    }

    @Override // org.apache.catalina.Server
    public void setAddress(String address) {
        this.address = address;
    }

    @Override // org.apache.catalina.Server
    public String getShutdown() {
        return this.shutdown;
    }

    @Override // org.apache.catalina.Server
    public void setShutdown(String shutdown) {
        this.shutdown = shutdown;
    }

    @Override // org.apache.catalina.Server
    public Catalina getCatalina() {
        return this.catalina;
    }

    @Override // org.apache.catalina.Server
    public void setCatalina(Catalina catalina) {
        this.catalina = catalina;
    }

    @Override // org.apache.catalina.Server
    public void addService(Service service) {
        service.setServer(this);
        synchronized (this.servicesLock) {
            Service[] results = new Service[this.services.length + 1];
            System.arraycopy(this.services, 0, results, 0, this.services.length);
            results[this.services.length] = service;
            this.services = results;
            if (getState().isAvailable()) {
                try {
                    service.start();
                } catch (LifecycleException e) {
                }
            }
            this.support.firePropertyChange("service", (Object) null, service);
        }
    }

    public void stopAwait() {
        this.stopAwait = true;
        Thread t = this.awaitThread;
        if (t != null) {
            ServerSocket s = this.awaitSocket;
            if (s != null) {
                this.awaitSocket = null;
                try {
                    s.close();
                } catch (IOException e) {
                }
            }
            t.interrupt();
            try {
                t.join(1000L);
            } catch (InterruptedException e2) {
            }
        }
    }

    /* JADX WARN: Finally extract failed */
    @Override // org.apache.catalina.Server
    public void await() {
        ServerSocket serverSocket;
        boolean match;
        int ch2;
        if (this.port == -2) {
            return;
        }
        if (this.port == -1) {
            try {
                this.awaitThread = Thread.currentThread();
                while (!this.stopAwait) {
                    try {
                        Thread.sleep(AbstractComponentTracker.LINGERING_TIMEOUT);
                    } catch (InterruptedException e) {
                    }
                }
                this.awaitThread = null;
                return;
            } catch (Throwable th) {
                this.awaitThread = null;
                throw th;
            }
        }
        try {
            this.awaitSocket = new ServerSocket(this.port, 1, InetAddress.getByName(this.address));
            try {
                this.awaitThread = Thread.currentThread();
                while (true) {
                    if (this.stopAwait || (serverSocket = this.awaitSocket) == null) {
                        break;
                    }
                    Socket socket = null;
                    StringBuilder command = new StringBuilder();
                    long acceptStartTime = System.currentTimeMillis();
                    try {
                        socket = serverSocket.accept();
                        socket.setSoTimeout(10000);
                        InputStream stream = socket.getInputStream();
                        int expected = 1024;
                        while (expected < this.shutdown.length()) {
                            if (this.random == null) {
                                this.random = new Random();
                            }
                            expected += this.random.nextInt() % 1024;
                        }
                        while (expected > 0) {
                            try {
                                ch2 = stream.read();
                            } catch (IOException e2) {
                                log.warn("StandardServer.await: read: ", e2);
                                ch2 = -1;
                            }
                            if (ch2 < 32 || ch2 == 127) {
                                break;
                            }
                            command.append((char) ch2);
                            expected--;
                        }
                        if (socket != null) {
                            try {
                                socket.close();
                            } catch (IOException e3) {
                            }
                        }
                        match = command.toString().equals(this.shutdown);
                    } catch (SocketTimeoutException ste) {
                        log.warn(sm.getString("standardServer.accept.timeout", Long.valueOf(System.currentTimeMillis() - acceptStartTime)), ste);
                        if (socket != null) {
                            try {
                                socket.close();
                            } catch (IOException e4) {
                            }
                        }
                    } catch (IOException e5) {
                        if (!this.stopAwait) {
                            log.error("StandardServer.await: accept: ", e5);
                            if (socket != null) {
                                try {
                                    socket.close();
                                } catch (IOException e6) {
                                }
                            }
                        } else if (socket != null) {
                            try {
                                socket.close();
                            } catch (IOException e7) {
                            }
                        }
                    } catch (AccessControlException ace) {
                        log.warn("StandardServer.accept security exception: " + ace.getMessage(), ace);
                        if (socket != null) {
                            try {
                                socket.close();
                            } catch (IOException e8) {
                            }
                        }
                    }
                    if (match) {
                        log.info(sm.getString("standardServer.shutdownViaPort"));
                        break;
                    }
                    log.warn("StandardServer.await: Invalid command '" + command.toString() + "' received");
                }
                ServerSocket serverSocket2 = this.awaitSocket;
                this.awaitThread = null;
                this.awaitSocket = null;
                if (serverSocket2 != null) {
                    try {
                        serverSocket2.close();
                    } catch (IOException e9) {
                    }
                }
            } catch (Throwable th2) {
                ServerSocket serverSocket3 = this.awaitSocket;
                this.awaitThread = null;
                this.awaitSocket = null;
                if (serverSocket3 != null) {
                    try {
                        serverSocket3.close();
                    } catch (IOException e10) {
                    }
                }
                throw th2;
            }
        } catch (IOException e11) {
            log.error("StandardServer.await: create[" + this.address + ":" + this.port + "]: ", e11);
        }
    }

    @Override // org.apache.catalina.Server
    public Service findService(String name) {
        if (name == null) {
            return null;
        }
        synchronized (this.servicesLock) {
            for (int i = 0; i < this.services.length; i++) {
                if (name.equals(this.services[i].getName())) {
                    return this.services[i];
                }
            }
            return null;
        }
    }

    @Override // org.apache.catalina.Server
    public Service[] findServices() {
        return this.services;
    }

    public ObjectName[] getServiceNames() {
        ObjectName[] onames = new ObjectName[this.services.length];
        for (int i = 0; i < this.services.length; i++) {
            onames[i] = ((StandardService) this.services[i]).getObjectName();
        }
        return onames;
    }

    @Override // org.apache.catalina.Server
    public void removeService(Service service) {
        synchronized (this.servicesLock) {
            int j = -1;
            int i = 0;
            while (true) {
                if (i < this.services.length) {
                    if (service != this.services[i]) {
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
            try {
                this.services[j].stop();
            } catch (LifecycleException e) {
            }
            int k = 0;
            Service[] results = new Service[this.services.length - 1];
            for (int i2 = 0; i2 < this.services.length; i2++) {
                if (i2 != j) {
                    int i3 = k;
                    k++;
                    results[i3] = this.services[i2];
                }
            }
            this.services = results;
            this.support.firePropertyChange("service", service, (Object) null);
        }
    }

    @Override // org.apache.catalina.Server
    public File getCatalinaBase() {
        if (this.catalinaBase != null) {
            return this.catalinaBase;
        }
        this.catalinaBase = getCatalinaHome();
        return this.catalinaBase;
    }

    @Override // org.apache.catalina.Server
    public void setCatalinaBase(File catalinaBase) {
        this.catalinaBase = catalinaBase;
    }

    @Override // org.apache.catalina.Server
    public File getCatalinaHome() {
        return this.catalinaHome;
    }

    @Override // org.apache.catalina.Server
    public void setCatalinaHome(File catalinaHome) {
        this.catalinaHome = catalinaHome;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    public String toString() {
        return "StandardServer[" + getPort() + "]";
    }

    public synchronized void storeConfig() throws InstanceNotFoundException, MBeanException {
        try {
            ObjectName sname = new ObjectName("Catalina:type=StoreConfig");
            if (this.mserver.isRegistered(sname)) {
                this.mserver.invoke(sname, "storeConfig", (Object[]) null, (String[]) null);
            } else {
                log.error(sm.getString("standardServer.storeConfig.notAvailable", sname));
            }
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            log.error(t);
        }
    }

    public synchronized void storeContext(org.apache.catalina.Context context) throws InstanceNotFoundException, MBeanException {
        try {
            ObjectName sname = new ObjectName("Catalina:type=StoreConfig");
            if (this.mserver.isRegistered(sname)) {
                this.mserver.invoke(sname, "store", new Object[]{context}, new String[]{"java.lang.String"});
            } else {
                log.error(sm.getString("standardServer.storeConfig.notAvailable", sname));
            }
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            log.error(t);
        }
    }

    private boolean isUseNaming() {
        boolean useNaming = true;
        String useNamingProperty = System.getProperty("catalina.useNaming");
        if (useNamingProperty != null && useNamingProperty.equals("false")) {
            useNaming = false;
        }
        return useNaming;
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void startInternal() throws LifecycleException {
        fireLifecycleEvent(Lifecycle.CONFIGURE_START_EVENT, null);
        setState(LifecycleState.STARTING);
        this.globalNamingResources.start();
        synchronized (this.servicesLock) {
            for (int i = 0; i < this.services.length; i++) {
                this.services[i].start();
            }
        }
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
        fireLifecycleEvent(Lifecycle.CONFIGURE_STOP_EVENT, null);
        for (int i = 0; i < this.services.length; i++) {
            this.services[i].stop();
        }
        this.globalNamingResources.stop();
        stopAwait();
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
        super.initInternal();
        this.onameStringCache = register(new StringCache(), "type=StringCache");
        MBeanFactory factory = new MBeanFactory();
        factory.setContainer(this);
        this.onameMBeanFactory = register(factory, "type=MBeanFactory");
        this.globalNamingResources.init();
        if (getCatalina() != null) {
            ClassLoader parentClassLoader = getCatalina().getParentClassLoader();
            while (true) {
                ClassLoader cl = parentClassLoader;
                if (cl == null || cl == ClassLoader.getSystemClassLoader()) {
                    break;
                }
                if (cl instanceof URLClassLoader) {
                    URL[] urls = ((URLClassLoader) cl).getURLs();
                    for (URL url : urls) {
                        if (url.getProtocol().equals("file")) {
                            try {
                                File f = new File(url.toURI());
                                if (f.isFile() && f.getName().endsWith(".jar")) {
                                    ExtensionValidator.addSystemResource(f);
                                }
                            } catch (IOException e) {
                            } catch (URISyntaxException e2) {
                            }
                        }
                    }
                }
                parentClassLoader = cl.getParent();
            }
        }
        for (int i = 0; i < this.services.length; i++) {
            this.services[i].init();
        }
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void destroyInternal() throws LifecycleException {
        for (int i = 0; i < this.services.length; i++) {
            this.services[i].destroy();
        }
        this.globalNamingResources.destroy();
        unregister(this.onameMBeanFactory);
        unregister(this.onameStringCache);
        super.destroyInternal();
    }

    @Override // org.apache.catalina.Server
    public ClassLoader getParentClassLoader() {
        if (this.parentClassLoader != null) {
            return this.parentClassLoader;
        }
        if (this.catalina != null) {
            return this.catalina.getParentClassLoader();
        }
        return ClassLoader.getSystemClassLoader();
    }

    @Override // org.apache.catalina.Server
    public void setParentClassLoader(ClassLoader parent) {
        ClassLoader oldParentClassLoader = this.parentClassLoader;
        this.parentClassLoader = parent;
        this.support.firePropertyChange("parentClassLoader", oldParentClassLoader, this.parentClassLoader);
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getDomainInternal() {
        Service service;
        String domain = null;
        Service[] services = findServices();
        if (services.length > 0 && (service = services[0]) != null) {
            domain = service.getDomain();
        }
        return domain;
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected final String getObjectNameKeyProperties() {
        return "type=Server";
    }
}