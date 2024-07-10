package org.springframework.remoting.support;

import java.lang.reflect.InvocationTargetException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/remoting/support/RemoteInvocationExecutor.class */
public interface RemoteInvocationExecutor {
    Object invoke(RemoteInvocation remoteInvocation, Object obj) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;
}