package org.springframework.remoting.rmi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import javax.naming.Context;
import javax.naming.NamingException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jndi.JndiObjectLocator;
import org.springframework.lang.Nullable;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteInvocationFailureException;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.support.DefaultRemoteInvocationFactory;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationFactory;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/remoting/rmi/JndiRmiClientInterceptor.class */
public class JndiRmiClientInterceptor extends JndiObjectLocator implements MethodInterceptor, InitializingBean {
    private Class<?> serviceInterface;
    private Object cachedStub;
    private RemoteInvocationFactory remoteInvocationFactory = new DefaultRemoteInvocationFactory();
    private boolean lookupStubOnStartup = true;
    private boolean cacheStub = true;
    private boolean refreshStubOnConnectFailure = false;
    private boolean exposeAccessContext = false;
    private final Object stubMonitor = new Object();

    public void setServiceInterface(Class<?> serviceInterface) {
        Assert.notNull(serviceInterface, "'serviceInterface' must not be null");
        Assert.isTrue(serviceInterface.isInterface(), "'serviceInterface' must be an interface");
        this.serviceInterface = serviceInterface;
    }

    public Class<?> getServiceInterface() {
        return this.serviceInterface;
    }

    public void setRemoteInvocationFactory(RemoteInvocationFactory remoteInvocationFactory) {
        this.remoteInvocationFactory = remoteInvocationFactory;
    }

    public RemoteInvocationFactory getRemoteInvocationFactory() {
        return this.remoteInvocationFactory;
    }

    public void setLookupStubOnStartup(boolean lookupStubOnStartup) {
        this.lookupStubOnStartup = lookupStubOnStartup;
    }

    public void setCacheStub(boolean cacheStub) {
        this.cacheStub = cacheStub;
    }

    public void setRefreshStubOnConnectFailure(boolean refreshStubOnConnectFailure) {
        this.refreshStubOnConnectFailure = refreshStubOnConnectFailure;
    }

    public void setExposeAccessContext(boolean exposeAccessContext) {
        this.exposeAccessContext = exposeAccessContext;
    }

    @Override // org.springframework.jndi.JndiObjectLocator, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws NamingException {
        super.afterPropertiesSet();
        prepare();
    }

    public void prepare() throws RemoteLookupFailureException {
        if (this.lookupStubOnStartup) {
            Object remoteObj = lookupStub();
            if (this.logger.isDebugEnabled()) {
                if (remoteObj instanceof RmiInvocationHandler) {
                    this.logger.debug("JNDI RMI object [" + getJndiName() + "] is an RMI invoker");
                } else if (getServiceInterface() != null) {
                    boolean isImpl = getServiceInterface().isInstance(remoteObj);
                    this.logger.debug("Using service interface [" + getServiceInterface().getName() + "] for JNDI RMI object [" + getJndiName() + "] - " + (!isImpl ? "not " : "") + "directly implemented");
                }
            }
            if (this.cacheStub) {
                this.cachedStub = remoteObj;
            }
        }
    }

    protected Object lookupStub() throws RemoteLookupFailureException {
        try {
            return lookup();
        } catch (NamingException ex) {
            throw new RemoteLookupFailureException("JNDI lookup for RMI service [" + getJndiName() + "] failed", ex);
        }
    }

    protected Object getStub() throws NamingException, RemoteLookupFailureException {
        Object obj;
        if (!this.cacheStub || (this.lookupStubOnStartup && !this.refreshStubOnConnectFailure)) {
            return this.cachedStub != null ? this.cachedStub : lookupStub();
        }
        synchronized (this.stubMonitor) {
            if (this.cachedStub == null) {
                this.cachedStub = lookupStub();
            }
            obj = this.cachedStub;
        }
        return obj;
    }

    @Override // org.aopalliance.intercept.MethodInterceptor
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            Object stub = getStub();
            Context ctx = this.exposeAccessContext ? getJndiTemplate().getContext() : null;
            try {
                try {
                    try {
                        Object doInvoke = doInvoke(invocation, stub);
                        getJndiTemplate().releaseContext(ctx);
                        return doInvoke;
                    } catch (RemoteConnectFailureException ex) {
                        Object handleRemoteConnectFailure = handleRemoteConnectFailure(invocation, ex);
                        getJndiTemplate().releaseContext(ctx);
                        return handleRemoteConnectFailure;
                    }
                } catch (RemoteException ex2) {
                    if (isConnectFailure(ex2)) {
                        Object handleRemoteConnectFailure2 = handleRemoteConnectFailure(invocation, ex2);
                        getJndiTemplate().releaseContext(ctx);
                        return handleRemoteConnectFailure2;
                    }
                    throw ex2;
                }
            } catch (Throwable th) {
                getJndiTemplate().releaseContext(ctx);
                throw th;
            }
        } catch (NamingException ex3) {
            throw new RemoteLookupFailureException("JNDI lookup for RMI service [" + getJndiName() + "] failed", ex3);
        }
    }

    protected boolean isConnectFailure(RemoteException ex) {
        return RmiClientInterceptorUtils.isConnectFailure(ex);
    }

    private Object handleRemoteConnectFailure(MethodInvocation invocation, Exception ex) throws Throwable {
        if (this.refreshStubOnConnectFailure) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Could not connect to RMI service [" + getJndiName() + "] - retrying", ex);
            } else if (this.logger.isInfoEnabled()) {
                this.logger.info("Could not connect to RMI service [" + getJndiName() + "] - retrying");
            }
            return refreshAndRetry(invocation);
        }
        throw ex;
    }

    @Nullable
    protected Object refreshAndRetry(MethodInvocation invocation) throws Throwable {
        Object freshStub;
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
    protected Object doInvoke(MethodInvocation invocation, Object stub) throws Throwable {
        if (stub instanceof RmiInvocationHandler) {
            try {
                return doInvoke(invocation, (RmiInvocationHandler) stub);
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            } catch (RemoteException ex2) {
                throw convertRmiAccessException(ex2, invocation.getMethod());
            } catch (Throwable ex3) {
                throw new RemoteInvocationFailureException("Invocation of method [" + invocation.getMethod() + "] failed in RMI service [" + getJndiName() + "]", ex3);
            }
        }
        try {
            return RmiClientInterceptorUtils.invokeRemoteMethod(invocation, stub);
        } catch (InvocationTargetException ex4) {
            Throwable targetEx = ex4.getTargetException();
            if (targetEx instanceof RemoteException) {
                throw convertRmiAccessException((RemoteException) targetEx, invocation.getMethod());
            }
            throw targetEx;
        }
    }

    protected Object doInvoke(MethodInvocation methodInvocation, RmiInvocationHandler invocationHandler) throws RemoteException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (AopUtils.isToStringMethod(methodInvocation.getMethod())) {
            return "RMI invoker proxy for service URL [" + getJndiName() + "]";
        }
        return invocationHandler.invoke(createRemoteInvocation(methodInvocation));
    }

    protected RemoteInvocation createRemoteInvocation(MethodInvocation methodInvocation) {
        return getRemoteInvocationFactory().createRemoteInvocation(methodInvocation);
    }

    private Exception convertRmiAccessException(RemoteException ex, Method method) {
        return RmiClientInterceptorUtils.convertRmiAccessException(method, ex, isConnectFailure(ex), getJndiName());
    }
}