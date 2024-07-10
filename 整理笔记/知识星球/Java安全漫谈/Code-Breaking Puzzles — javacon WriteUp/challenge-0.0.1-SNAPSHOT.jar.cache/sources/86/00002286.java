package org.springframework.remoting.support;

import org.aopalliance.intercept.MethodInvocation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/remoting/support/RemoteInvocationBasedAccessor.class */
public abstract class RemoteInvocationBasedAccessor extends UrlBasedRemoteAccessor {
    private RemoteInvocationFactory remoteInvocationFactory = new DefaultRemoteInvocationFactory();

    public void setRemoteInvocationFactory(RemoteInvocationFactory remoteInvocationFactory) {
        this.remoteInvocationFactory = remoteInvocationFactory != null ? remoteInvocationFactory : new DefaultRemoteInvocationFactory();
    }

    public RemoteInvocationFactory getRemoteInvocationFactory() {
        return this.remoteInvocationFactory;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public RemoteInvocation createRemoteInvocation(MethodInvocation methodInvocation) {
        return getRemoteInvocationFactory().createRemoteInvocation(methodInvocation);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object recreateRemoteInvocationResult(RemoteInvocationResult result) throws Throwable {
        return result.recreate();
    }
}