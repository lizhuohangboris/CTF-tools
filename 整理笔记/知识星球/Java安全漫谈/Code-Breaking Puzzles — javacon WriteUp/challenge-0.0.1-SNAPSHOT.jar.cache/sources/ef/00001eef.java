package org.springframework.ejb.access;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.naming.Context;
import javax.naming.NamingException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.jndi.JndiObjectLocator;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/ejb/access/AbstractSlsbInvokerInterceptor.class */
public abstract class AbstractSlsbInvokerInterceptor extends JndiObjectLocator implements MethodInterceptor {
    @Nullable
    private Object cachedHome;
    @Nullable
    private Method createMethod;
    private boolean lookupHomeOnStartup = true;
    private boolean cacheHome = true;
    private boolean exposeAccessContext = false;
    private final Object homeMonitor = new Object();

    @Nullable
    protected abstract Object invokeInContext(MethodInvocation methodInvocation) throws Throwable;

    public void setLookupHomeOnStartup(boolean lookupHomeOnStartup) {
        this.lookupHomeOnStartup = lookupHomeOnStartup;
    }

    public void setCacheHome(boolean cacheHome) {
        this.cacheHome = cacheHome;
    }

    public void setExposeAccessContext(boolean exposeAccessContext) {
        this.exposeAccessContext = exposeAccessContext;
    }

    @Override // org.springframework.jndi.JndiObjectLocator, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws NamingException {
        super.afterPropertiesSet();
        if (this.lookupHomeOnStartup) {
            refreshHome();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void refreshHome() throws NamingException {
        synchronized (this.homeMonitor) {
            Object home = lookup();
            if (this.cacheHome) {
                this.cachedHome = home;
                this.createMethod = getCreateMethod(home);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Method getCreateMethod(Object home) throws EjbAccessException {
        try {
            return home.getClass().getMethod("create", new Class[0]);
        } catch (NoSuchMethodException e) {
            throw new EjbAccessException("EJB home [" + home + "] has no no-arg create() method");
        }
    }

    protected Object getHome() throws NamingException {
        Object obj;
        if (!this.cacheHome || (this.lookupHomeOnStartup && !isHomeRefreshable())) {
            return this.cachedHome != null ? this.cachedHome : lookup();
        }
        synchronized (this.homeMonitor) {
            if (this.cachedHome == null) {
                this.cachedHome = lookup();
                this.createMethod = getCreateMethod(this.cachedHome);
            }
            obj = this.cachedHome;
        }
        return obj;
    }

    protected boolean isHomeRefreshable() {
        return false;
    }

    @Override // org.aopalliance.intercept.MethodInterceptor
    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Context ctx = this.exposeAccessContext ? getJndiTemplate().getContext() : null;
        try {
            Object invokeInContext = invokeInContext(invocation);
            getJndiTemplate().releaseContext(ctx);
            return invokeInContext;
        } catch (Throwable th) {
            getJndiTemplate().releaseContext(ctx);
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object create() throws NamingException, InvocationTargetException {
        try {
            Object home = getHome();
            Method createMethodToUse = this.createMethod;
            if (createMethodToUse == null) {
                createMethodToUse = getCreateMethod(home);
            }
            if (createMethodToUse == null) {
                return home;
            }
            return createMethodToUse.invoke(home, null);
        } catch (IllegalAccessException ex) {
            throw new EjbAccessException("Could not access EJB home create() method", ex);
        }
    }
}