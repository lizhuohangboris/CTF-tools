package org.springframework.aop.framework;

import org.springframework.core.NestedRuntimeException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/AopConfigException.class */
public class AopConfigException extends NestedRuntimeException {
    public AopConfigException(String msg) {
        super(msg);
    }

    public AopConfigException(String msg, Throwable cause) {
        super(msg, cause);
    }
}