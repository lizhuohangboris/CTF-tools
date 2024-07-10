package org.springframework.remoting.support;

import org.aopalliance.intercept.MethodInvocation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/remoting/support/DefaultRemoteInvocationFactory.class */
public class DefaultRemoteInvocationFactory implements RemoteInvocationFactory {
    @Override // org.springframework.remoting.support.RemoteInvocationFactory
    public RemoteInvocation createRemoteInvocation(MethodInvocation methodInvocation) {
        return new RemoteInvocation(methodInvocation);
    }
}