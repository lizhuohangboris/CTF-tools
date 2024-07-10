package org.springframework.remoting.httpinvoker;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/remoting/httpinvoker/HttpInvokerClientConfiguration.class */
public interface HttpInvokerClientConfiguration {
    String getServiceUrl();

    @Nullable
    String getCodebaseUrl();
}