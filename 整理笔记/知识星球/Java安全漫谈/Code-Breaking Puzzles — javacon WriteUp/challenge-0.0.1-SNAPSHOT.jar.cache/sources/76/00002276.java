package org.springframework.remoting.rmi;

import java.lang.reflect.InvocationTargetException;
import java.rmi.Remote;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationBasedExporter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/remoting/rmi/RmiBasedExporter.class */
public abstract class RmiBasedExporter extends RemoteInvocationBasedExporter {
    /* JADX INFO: Access modifiers changed from: protected */
    public Remote getObjectToExport() {
        if ((getService() instanceof Remote) && (getServiceInterface() == null || Remote.class.isAssignableFrom(getServiceInterface()))) {
            return (Remote) getService();
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("RMI service [" + getService() + "] is an RMI invoker");
        }
        return new RmiInvocationWrapper(getProxyForService(), this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.remoting.support.RemoteInvocationBasedExporter
    public Object invoke(RemoteInvocation invocation, Object targetObject) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return super.invoke(invocation, targetObject);
    }
}