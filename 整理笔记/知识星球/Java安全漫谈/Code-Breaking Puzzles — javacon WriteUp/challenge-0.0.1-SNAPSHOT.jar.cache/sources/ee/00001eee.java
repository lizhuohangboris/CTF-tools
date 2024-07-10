package org.springframework.ejb.access;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import javax.ejb.EJBHome;
import javax.ejb.EJBObject;
import javax.naming.NamingException;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.lang.Nullable;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.rmi.RmiClientInterceptorUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/ejb/access/AbstractRemoteSlsbInvokerInterceptor.class */
public abstract class AbstractRemoteSlsbInvokerInterceptor extends AbstractSlsbInvokerInterceptor {
    private boolean refreshHomeOnConnectFailure = false;
    private volatile boolean homeAsComponent = false;

    @Nullable
    protected abstract Object doInvoke(MethodInvocation methodInvocation) throws Throwable;

    public void setRefreshHomeOnConnectFailure(boolean refreshHomeOnConnectFailure) {
        this.refreshHomeOnConnectFailure = refreshHomeOnConnectFailure;
    }

    @Override // org.springframework.ejb.access.AbstractSlsbInvokerInterceptor
    protected boolean isHomeRefreshable() {
        return this.refreshHomeOnConnectFailure;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.ejb.access.AbstractSlsbInvokerInterceptor
    public Method getCreateMethod(Object home) throws EjbAccessException {
        if (this.homeAsComponent) {
            return null;
        }
        if (!(home instanceof EJBHome)) {
            this.homeAsComponent = true;
            return null;
        }
        return super.getCreateMethod(home);
    }

    @Override // org.springframework.ejb.access.AbstractSlsbInvokerInterceptor
    @Nullable
    public Object invokeInContext(MethodInvocation invocation) throws Throwable {
        try {
            return doInvoke(invocation);
        } catch (RemoteException ex) {
            if (isConnectFailure(ex)) {
                return handleRemoteConnectFailure(invocation, ex);
            }
            throw ex;
        } catch (RemoteConnectFailureException ex2) {
            return handleRemoteConnectFailure(invocation, ex2);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isConnectFailure(RemoteException ex) {
        return RmiClientInterceptorUtils.isConnectFailure(ex);
    }

    @Nullable
    private Object handleRemoteConnectFailure(MethodInvocation invocation, Exception ex) throws Throwable {
        if (this.refreshHomeOnConnectFailure) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Could not connect to remote EJB [" + getJndiName() + "] - retrying", ex);
            } else if (this.logger.isWarnEnabled()) {
                this.logger.warn("Could not connect to remote EJB [" + getJndiName() + "] - retrying");
            }
            return refreshAndRetry(invocation);
        }
        throw ex;
    }

    @Nullable
    protected Object refreshAndRetry(MethodInvocation invocation) throws Throwable {
        try {
            refreshHome();
            return doInvoke(invocation);
        } catch (NamingException ex) {
            throw new RemoteLookupFailureException("Failed to locate remote EJB [" + getJndiName() + "]", ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object newSessionBeanInstance() throws NamingException, InvocationTargetException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Trying to create reference to remote EJB");
        }
        Object ejbInstance = create();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Obtained reference to remote EJB: " + ejbInstance);
        }
        return ejbInstance;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void removeSessionBeanInstance(@Nullable EJBObject ejb) {
        if (ejb != null && !this.homeAsComponent) {
            try {
                ejb.remove();
            } catch (Throwable ex) {
                this.logger.warn("Could not invoke 'remove' on remote EJB proxy", ex);
            }
        }
    }
}