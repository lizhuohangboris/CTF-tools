package org.springframework.remoting.rmi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Properties;
import javax.naming.NamingException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jndi.JndiTemplate;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/remoting/rmi/JndiRmiServiceExporter.class */
public class JndiRmiServiceExporter extends RmiBasedExporter implements InitializingBean, DisposableBean {
    @Nullable
    private static Method exportObject;
    @Nullable
    private static Method unexportObject;
    private JndiTemplate jndiTemplate = new JndiTemplate();
    private String jndiName;
    private Remote exportedObject;

    static {
        try {
            Class<?> portableRemoteObject = JndiRmiServiceExporter.class.getClassLoader().loadClass("javax.rmi.PortableRemoteObject");
            exportObject = portableRemoteObject.getMethod("exportObject", Remote.class);
            unexportObject = portableRemoteObject.getMethod("unexportObject", Remote.class);
        } catch (Throwable th) {
            exportObject = null;
            unexportObject = null;
        }
    }

    public void setJndiTemplate(JndiTemplate jndiTemplate) {
        this.jndiTemplate = jndiTemplate != null ? jndiTemplate : new JndiTemplate();
    }

    public void setJndiEnvironment(Properties jndiEnvironment) {
        this.jndiTemplate = new JndiTemplate(jndiEnvironment);
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws NamingException, RemoteException {
        prepare();
    }

    public void prepare() throws NamingException, RemoteException {
        if (this.jndiName == null) {
            throw new IllegalArgumentException("Property 'jndiName' is required");
        }
        this.exportedObject = getObjectToExport();
        invokePortableRemoteObject(exportObject);
        rebind();
    }

    public void rebind() throws NamingException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Binding RMI service to JNDI location [" + this.jndiName + "]");
        }
        this.jndiTemplate.rebind(this.jndiName, this.exportedObject);
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() throws NamingException, RemoteException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Unbinding RMI service from JNDI location [" + this.jndiName + "]");
        }
        this.jndiTemplate.unbind(this.jndiName);
        invokePortableRemoteObject(unexportObject);
    }

    private void invokePortableRemoteObject(@Nullable Method method) throws RemoteException {
        if (method != null) {
            try {
                method.invoke(null, this.exportedObject);
            } catch (InvocationTargetException ex) {
                RemoteException targetException = ex.getTargetException();
                if (targetException instanceof RemoteException) {
                    throw targetException;
                }
                ReflectionUtils.rethrowRuntimeException(targetException);
            } catch (Throwable ex2) {
                throw new IllegalStateException("PortableRemoteObject invocation failed", ex2);
            }
        }
    }
}