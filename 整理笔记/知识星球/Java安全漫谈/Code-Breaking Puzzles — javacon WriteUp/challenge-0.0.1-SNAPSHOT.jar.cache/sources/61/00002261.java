package org.springframework.remoting.httpinvoker;

import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/remoting/httpinvoker/HttpInvokerRequestExecutor.class */
public interface HttpInvokerRequestExecutor {
    RemoteInvocationResult executeRequest(HttpInvokerClientConfiguration httpInvokerClientConfiguration, RemoteInvocation remoteInvocation) throws Exception;
}