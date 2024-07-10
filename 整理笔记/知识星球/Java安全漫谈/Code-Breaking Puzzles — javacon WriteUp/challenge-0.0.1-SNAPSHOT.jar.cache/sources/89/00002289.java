package org.springframework.remoting.support;

import org.aopalliance.intercept.MethodInvocation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/remoting/support/RemoteInvocationFactory.class */
public interface RemoteInvocationFactory {
    RemoteInvocation createRemoteInvocation(MethodInvocation methodInvocation);
}