package org.springframework.remoting.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/remoting/rmi/RmiRegistryFactoryBean.class */
public class RmiRegistryFactoryBean implements FactoryBean<Registry>, InitializingBean, DisposableBean {
    private String host;
    private RMIClientSocketFactory clientSocketFactory;
    private RMIServerSocketFactory serverSocketFactory;
    private Registry registry;
    protected final Log logger = LogFactory.getLog(getClass());
    private int port = 1099;
    private boolean alwaysCreate = false;
    private boolean created = false;

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return this.host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return this.port;
    }

    public void setClientSocketFactory(RMIClientSocketFactory clientSocketFactory) {
        this.clientSocketFactory = clientSocketFactory;
    }

    public void setServerSocketFactory(RMIServerSocketFactory serverSocketFactory) {
        this.serverSocketFactory = serverSocketFactory;
    }

    public void setAlwaysCreate(boolean alwaysCreate) {
        this.alwaysCreate = alwaysCreate;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        if (this.clientSocketFactory instanceof RMIServerSocketFactory) {
            this.serverSocketFactory = this.clientSocketFactory;
        }
        if ((this.clientSocketFactory != null && this.serverSocketFactory == null) || (this.clientSocketFactory == null && this.serverSocketFactory != null)) {
            throw new IllegalArgumentException("Both RMIClientSocketFactory and RMIServerSocketFactory or none required");
        }
        this.registry = getRegistry(this.host, this.port, this.clientSocketFactory, this.serverSocketFactory);
    }

    protected Registry getRegistry(String registryHost, int registryPort, @Nullable RMIClientSocketFactory clientSocketFactory, @Nullable RMIServerSocketFactory serverSocketFactory) throws RemoteException {
        if (registryHost != null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Looking for RMI registry at port '" + registryPort + "' of host [" + registryHost + "]");
            }
            Registry reg = LocateRegistry.getRegistry(registryHost, registryPort, clientSocketFactory);
            testRegistry(reg);
            return reg;
        }
        return getRegistry(registryPort, clientSocketFactory, serverSocketFactory);
    }

    protected Registry getRegistry(int registryPort, @Nullable RMIClientSocketFactory clientSocketFactory, @Nullable RMIServerSocketFactory serverSocketFactory) throws RemoteException {
        Registry reg;
        if (clientSocketFactory != null) {
            if (this.alwaysCreate) {
                this.logger.debug("Creating new RMI registry");
                this.created = true;
                return LocateRegistry.createRegistry(registryPort, clientSocketFactory, serverSocketFactory);
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Looking for RMI registry at port '" + registryPort + "', using custom socket factory");
            }
            synchronized (LocateRegistry.class) {
                try {
                    reg = LocateRegistry.getRegistry((String) null, registryPort, clientSocketFactory);
                    testRegistry(reg);
                } catch (RemoteException e) {
                    this.logger.trace("RMI registry access threw exception", e);
                    this.logger.debug("Could not detect RMI registry - creating new one");
                    this.created = true;
                    return LocateRegistry.createRegistry(registryPort, clientSocketFactory, serverSocketFactory);
                }
            }
            return reg;
        }
        return getRegistry(registryPort);
    }

    protected Registry getRegistry(int registryPort) throws RemoteException {
        Registry reg;
        if (this.alwaysCreate) {
            this.logger.debug("Creating new RMI registry");
            this.created = true;
            return LocateRegistry.createRegistry(registryPort);
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Looking for RMI registry at port '" + registryPort + "'");
        }
        synchronized (LocateRegistry.class) {
            try {
                reg = LocateRegistry.getRegistry(registryPort);
                testRegistry(reg);
            } catch (RemoteException e) {
                this.logger.trace("RMI registry access threw exception", e);
                this.logger.debug("Could not detect RMI registry - creating new one");
                this.created = true;
                return LocateRegistry.createRegistry(registryPort);
            }
        }
        return reg;
    }

    protected void testRegistry(Registry registry) throws RemoteException {
        registry.list();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    public Registry getObject() throws Exception {
        return this.registry;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<? extends Registry> getObjectType() {
        return this.registry != null ? this.registry.getClass() : Registry.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() throws RemoteException {
        if (this.created) {
            this.logger.debug("Unexporting RMI registry");
            UnicastRemoteObject.unexportObject(this.registry, true);
        }
    }
}