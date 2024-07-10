package org.springframework.remoting.rmi;

import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/remoting/rmi/RmiServiceExporter.class */
public class RmiServiceExporter extends RmiBasedExporter implements InitializingBean, DisposableBean {
    private String serviceName;
    private RMIClientSocketFactory clientSocketFactory;
    private RMIServerSocketFactory serverSocketFactory;
    private Registry registry;
    private String registryHost;
    private RMIClientSocketFactory registryClientSocketFactory;
    private RMIServerSocketFactory registryServerSocketFactory;
    private Remote exportedObject;
    private int servicePort = 0;
    private int registryPort = 1099;
    private boolean alwaysCreateRegistry = false;
    private boolean replaceExistingBinding = true;
    private boolean createdRegistry = false;

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public void setClientSocketFactory(RMIClientSocketFactory clientSocketFactory) {
        this.clientSocketFactory = clientSocketFactory;
    }

    public void setServerSocketFactory(RMIServerSocketFactory serverSocketFactory) {
        this.serverSocketFactory = serverSocketFactory;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setRegistryHost(String registryHost) {
        this.registryHost = registryHost;
    }

    public void setRegistryPort(int registryPort) {
        this.registryPort = registryPort;
    }

    public void setRegistryClientSocketFactory(RMIClientSocketFactory registryClientSocketFactory) {
        this.registryClientSocketFactory = registryClientSocketFactory;
    }

    public void setRegistryServerSocketFactory(RMIServerSocketFactory registryServerSocketFactory) {
        this.registryServerSocketFactory = registryServerSocketFactory;
    }

    public void setAlwaysCreateRegistry(boolean alwaysCreateRegistry) {
        this.alwaysCreateRegistry = alwaysCreateRegistry;
    }

    public void setReplaceExistingBinding(boolean replaceExistingBinding) {
        this.replaceExistingBinding = replaceExistingBinding;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws RemoteException {
        prepare();
    }

    public void prepare() throws RemoteException {
        checkService();
        if (this.serviceName == null) {
            throw new IllegalArgumentException("Property 'serviceName' is required");
        }
        if (this.clientSocketFactory instanceof RMIServerSocketFactory) {
            this.serverSocketFactory = this.clientSocketFactory;
        }
        if ((this.clientSocketFactory != null && this.serverSocketFactory == null) || (this.clientSocketFactory == null && this.serverSocketFactory != null)) {
            throw new IllegalArgumentException("Both RMIClientSocketFactory and RMIServerSocketFactory or none required");
        }
        if (this.registryClientSocketFactory instanceof RMIServerSocketFactory) {
            this.registryServerSocketFactory = this.registryClientSocketFactory;
        }
        if (this.registryClientSocketFactory == null && this.registryServerSocketFactory != null) {
            throw new IllegalArgumentException("RMIServerSocketFactory without RMIClientSocketFactory for registry not supported");
        }
        this.createdRegistry = false;
        if (this.registry == null) {
            this.registry = getRegistry(this.registryHost, this.registryPort, this.registryClientSocketFactory, this.registryServerSocketFactory);
            this.createdRegistry = true;
        }
        this.exportedObject = getObjectToExport();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Binding service '" + this.serviceName + "' to RMI registry: " + this.registry);
        }
        if (this.clientSocketFactory != null) {
            UnicastRemoteObject.exportObject(this.exportedObject, this.servicePort, this.clientSocketFactory, this.serverSocketFactory);
        } else {
            UnicastRemoteObject.exportObject(this.exportedObject, this.servicePort);
        }
        try {
            if (this.replaceExistingBinding) {
                this.registry.rebind(this.serviceName, this.exportedObject);
            } else {
                this.registry.bind(this.serviceName, this.exportedObject);
            }
        } catch (RemoteException ex) {
            unexportObjectSilently();
            throw ex;
        } catch (AlreadyBoundException ex2) {
            unexportObjectSilently();
            throw new IllegalStateException("Already an RMI object bound for name '" + this.serviceName + "': " + ex2.toString());
        }
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
            if (this.alwaysCreateRegistry) {
                this.logger.debug("Creating new RMI registry");
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
                    return LocateRegistry.createRegistry(registryPort, clientSocketFactory, serverSocketFactory);
                }
            }
            return reg;
        }
        return getRegistry(registryPort);
    }

    protected Registry getRegistry(int registryPort) throws RemoteException {
        Registry reg;
        if (this.alwaysCreateRegistry) {
            this.logger.debug("Creating new RMI registry");
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
                return LocateRegistry.createRegistry(registryPort);
            }
        }
        return reg;
    }

    protected void testRegistry(Registry registry) throws RemoteException {
        registry.list();
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() throws RemoteException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Unbinding RMI service '" + this.serviceName + "' from registry" + (this.createdRegistry ? " at port '" + this.registryPort + "'" : ""));
        }
        try {
            this.registry.unbind(this.serviceName);
        } catch (NotBoundException e) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("RMI service '" + this.serviceName + "' is not bound to registry" + (this.createdRegistry ? " at port '" + this.registryPort + "' anymore" : ""), e);
            }
        } finally {
            unexportObjectSilently();
        }
    }

    private void unexportObjectSilently() {
        try {
            UnicastRemoteObject.unexportObject(this.exportedObject, true);
        } catch (NoSuchObjectException e) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("RMI object for service '" + this.serviceName + "' is not exported anymore", e);
            }
        }
    }
}