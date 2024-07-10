package org.springframework.remoting.rmi;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import org.springframework.lang.Nullable;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.util.Assert;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/remoting/rmi/RmiInvocationWrapper.class */
public class RmiInvocationWrapper implements RmiInvocationHandler {
    private final Object wrappedObject;
    private final RmiBasedExporter rmiExporter;

    public RmiInvocationWrapper(Object wrappedObject, RmiBasedExporter rmiExporter) {
        Assert.notNull(wrappedObject, "Object to wrap is required");
        Assert.notNull(rmiExporter, "RMI exporter is required");
        this.wrappedObject = wrappedObject;
        this.rmiExporter = rmiExporter;
    }

    @Override // org.springframework.remoting.rmi.RmiInvocationHandler
    @Nullable
    public String getTargetInterfaceName() {
        Class<?> ifc = this.rmiExporter.getServiceInterface();
        if (ifc != null) {
            return ifc.getName();
        }
        return null;
    }

    @Override // org.springframework.remoting.rmi.RmiInvocationHandler
    @Nullable
    public Object invoke(RemoteInvocation invocation) throws RemoteException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return this.rmiExporter.invoke(invocation, this.wrappedObject);
    }
}