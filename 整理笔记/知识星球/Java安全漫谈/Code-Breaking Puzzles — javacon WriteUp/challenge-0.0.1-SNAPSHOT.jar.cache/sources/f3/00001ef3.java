package org.springframework.ejb.access;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBObject;
import javax.naming.NamingException;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.lang.Nullable;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.rmi.RmiClientInterceptorUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/ejb/access/SimpleRemoteSlsbInvokerInterceptor.class */
public class SimpleRemoteSlsbInvokerInterceptor extends AbstractRemoteSlsbInvokerInterceptor implements DisposableBean {
    @Nullable
    private Object beanInstance;
    private boolean cacheSessionBean = false;
    private final Object beanInstanceMonitor = new Object();

    public void setCacheSessionBean(boolean cacheSessionBean) {
        this.cacheSessionBean = cacheSessionBean;
    }

    @Override // org.springframework.ejb.access.AbstractRemoteSlsbInvokerInterceptor
    @Nullable
    protected Object doInvoke(MethodInvocation invocation) throws Throwable {
        Object ejb = null;
        try {
            try {
                ejb = getSessionBeanInstance();
                Object invokeRemoteMethod = RmiClientInterceptorUtils.invokeRemoteMethod(invocation, ejb);
                if (ejb instanceof EJBObject) {
                    releaseSessionBeanInstance((EJBObject) ejb);
                }
                return invokeRemoteMethod;
            } catch (InvocationTargetException ex) {
                Throwable targetEx = ex.getTargetException();
                if (targetEx instanceof RemoteException) {
                    RemoteException rex = (RemoteException) targetEx;
                    throw RmiClientInterceptorUtils.convertRmiAccessException(invocation.getMethod(), rex, isConnectFailure(rex), getJndiName());
                } else if (targetEx instanceof CreateException) {
                    throw RmiClientInterceptorUtils.convertRmiAccessException(invocation.getMethod(), targetEx, "Could not create remote EJB [" + getJndiName() + "]");
                } else {
                    throw targetEx;
                }
            } catch (NamingException ex2) {
                throw new RemoteLookupFailureException("Failed to locate remote EJB [" + getJndiName() + "]", ex2);
            }
        } catch (Throwable th) {
            if (ejb instanceof EJBObject) {
                releaseSessionBeanInstance((EJBObject) ejb);
            }
            throw th;
        }
    }

    protected Object getSessionBeanInstance() throws NamingException, InvocationTargetException {
        Object obj;
        if (this.cacheSessionBean) {
            synchronized (this.beanInstanceMonitor) {
                if (this.beanInstance == null) {
                    this.beanInstance = newSessionBeanInstance();
                }
                obj = this.beanInstance;
            }
            return obj;
        }
        return newSessionBeanInstance();
    }

    protected void releaseSessionBeanInstance(EJBObject ejb) {
        if (!this.cacheSessionBean) {
            removeSessionBeanInstance(ejb);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.ejb.access.AbstractSlsbInvokerInterceptor
    public void refreshHome() throws NamingException {
        super.refreshHome();
        if (this.cacheSessionBean) {
            synchronized (this.beanInstanceMonitor) {
                this.beanInstance = null;
            }
        }
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        if (this.cacheSessionBean) {
            synchronized (this.beanInstanceMonitor) {
                if (this.beanInstance instanceof EJBObject) {
                    removeSessionBeanInstance((EJBObject) this.beanInstance);
                }
            }
        }
    }
}