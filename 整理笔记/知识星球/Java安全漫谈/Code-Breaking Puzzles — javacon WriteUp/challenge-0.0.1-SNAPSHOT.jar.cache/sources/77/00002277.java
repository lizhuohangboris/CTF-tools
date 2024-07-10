package org.springframework.remoting.rmi;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.lang.Nullable;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteInvocationFailureException;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.support.RemoteInvocationBasedAccessor;
import org.springframework.remoting.support.RemoteInvocationUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/remoting/rmi/RmiClientInterceptor.class */
public class RmiClientInterceptor extends RemoteInvocationBasedAccessor implements MethodInterceptor {
    private RMIClientSocketFactory registryClientSocketFactory;
    private Remote cachedStub;
    private boolean lookupStubOnStartup = true;
    private boolean cacheStub = true;
    private boolean refreshStubOnConnectFailure = false;
    private final Object stubMonitor = new Object();

    public void setLookupStubOnStartup(boolean lookupStubOnStartup) {
        this.lookupStubOnStartup = lookupStubOnStartup;
    }

    public void setCacheStub(boolean cacheStub) {
        this.cacheStub = cacheStub;
    }

    public void setRefreshStubOnConnectFailure(boolean refreshStubOnConnectFailure) {
        this.refreshStubOnConnectFailure = refreshStubOnConnectFailure;
    }

    public void setRegistryClientSocketFactory(RMIClientSocketFactory registryClientSocketFactory) {
        this.registryClientSocketFactory = registryClientSocketFactory;
    }

    @Override // org.springframework.remoting.support.UrlBasedRemoteAccessor, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        prepare();
    }

    public void prepare() throws RemoteLookupFailureException {
        if (this.lookupStubOnStartup) {
            Remote remoteObj = lookupStub();
            if (this.logger.isDebugEnabled()) {
                if (remoteObj instanceof RmiInvocationHandler) {
                    this.logger.debug("RMI stub [" + getServiceUrl() + "] is an RMI invoker");
                } else if (getServiceInterface() != null) {
                    boolean isImpl = getServiceInterface().isInstance(remoteObj);
                    this.logger.debug("Using service interface [" + getServiceInterface().getName() + "] for RMI stub [" + getServiceUrl() + "] - " + (!isImpl ? "not " : "") + "directly implemented");
                }
            }
            if (this.cacheStub) {
                this.cachedStub = remoteObj;
            }
        }
    }

    protected Remote lookupStub() throws RemoteLookupFailureException {
        Remote stub;
        try {
            if (this.registryClientSocketFactory != null) {
                URL url = new URL((URL) null, getServiceUrl(), new DummyURLStreamHandler());
                String protocol = url.getProtocol();
                if (protocol != null && !"rmi".equals(protocol)) {
                    throw new MalformedURLException("Invalid URL scheme '" + protocol + "'");
                }
                String host = url.getHost();
                int port = url.getPort();
                String name = url.getPath();
                if (name != null && name.startsWith("/")) {
                    name = name.substring(1);
                }
                Registry registry = LocateRegistry.getRegistry(host, port, this.registryClientSocketFactory);
                stub = registry.lookup(name);
            } else {
                stub = Naming.lookup(getServiceUrl());
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Located RMI stub with URL [" + getServiceUrl() + "]");
            }
            return stub;
        } catch (RemoteException ex) {
            throw new RemoteLookupFailureException("Lookup of RMI stub failed", ex);
        } catch (NotBoundException ex2) {
            throw new RemoteLookupFailureException("Could not find RMI service [" + getServiceUrl() + "] in RMI registry", ex2);
        } catch (MalformedURLException ex3) {
            throw new RemoteLookupFailureException("Service URL [" + getServiceUrl() + "] is invalid", ex3);
        }
    }

    protected Remote getStub() throws RemoteLookupFailureException {
        Remote remote;
        if (!this.cacheStub || (this.lookupStubOnStartup && !this.refreshStubOnConnectFailure)) {
            return this.cachedStub != null ? this.cachedStub : lookupStub();
        }
        synchronized (this.stubMonitor) {
            if (this.cachedStub == null) {
                this.cachedStub = lookupStub();
            }
            remote = this.cachedStub;
        }
        return remote;
    }

    @Override // org.aopalliance.intercept.MethodInterceptor
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Remote stub = getStub();
        try {
            return doInvoke(invocation, stub);
        } catch (RemoteConnectFailureException ex) {
            return handleRemoteConnectFailure(invocation, ex);
        } catch (RemoteException ex2) {
            if (isConnectFailure(ex2)) {
                return handleRemoteConnectFailure(invocation, ex2);
            }
            throw ex2;
        }
    }

    protected boolean isConnectFailure(RemoteException ex) {
        return RmiClientInterceptorUtils.isConnectFailure(ex);
    }

    @Nullable
    private Object handleRemoteConnectFailure(MethodInvocation invocation, Exception ex) throws Throwable {
        if (this.refreshStubOnConnectFailure) {
            String msg = "Could not connect to RMI service [" + getServiceUrl() + "] - retrying";
            if (this.logger.isDebugEnabled()) {
                this.logger.warn(msg, ex);
            } else if (this.logger.isWarnEnabled()) {
                this.logger.warn(msg);
            }
            return refreshAndRetry(invocation);
        }
        throw ex;
    }

    @Nullable
    protected Object refreshAndRetry(MethodInvocation invocation) throws Throwable {
        Remote freshStub;
        synchronized (this.stubMonitor) {
            this.cachedStub = null;
            freshStub = lookupStub();
            if (this.cacheStub) {
                this.cachedStub = freshStub;
            }
        }
        return doInvoke(invocation, freshStub);
    }

    @Nullable
    protected Object doInvoke(MethodInvocation invocation, Remote stub) throws Throwable {
        if (stub instanceof RmiInvocationHandler) {
            try {
                return doInvoke(invocation, (RmiInvocationHandler) stub);
            } catch (InvocationTargetException ex) {
                Throwable exToThrow = ex.getTargetException();
                RemoteInvocationUtils.fillInClientStackTraceIfPossible(exToThrow);
                throw exToThrow;
            } catch (RemoteException ex2) {
                throw RmiClientInterceptorUtils.convertRmiAccessException(invocation.getMethod(), ex2, isConnectFailure(ex2), getServiceUrl());
            } catch (Throwable ex3) {
                throw new RemoteInvocationFailureException("Invocation of method [" + invocation.getMethod() + "] failed in RMI service [" + getServiceUrl() + "]", ex3);
            }
        }
        try {
            return RmiClientInterceptorUtils.invokeRemoteMethod(invocation, stub);
        } catch (InvocationTargetException ex4) {
            Throwable targetEx = ex4.getTargetException();
            if (targetEx instanceof RemoteException) {
                RemoteException rex = (RemoteException) targetEx;
                throw RmiClientInterceptorUtils.convertRmiAccessException(invocation.getMethod(), rex, isConnectFailure(rex), getServiceUrl());
            }
            throw targetEx;
        }
    }

    @Nullable
    protected Object doInvoke(MethodInvocation methodInvocation, RmiInvocationHandler invocationHandler) throws RemoteException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (AopUtils.isToStringMethod(methodInvocation.getMethod())) {
            return "RMI invoker proxy for service URL [" + getServiceUrl() + "]";
        }
        return invocationHandler.invoke(createRemoteInvocation(methodInvocation));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/remoting/rmi/RmiClientInterceptor$DummyURLStreamHandler.class */
    public static class DummyURLStreamHandler extends URLStreamHandler {
        private DummyURLStreamHandler() {
        }

        @Override // java.net.URLStreamHandler
        protected URLConnection openConnection(URL url) throws IOException {
            throw new UnsupportedOperationException();
        }
    }
}