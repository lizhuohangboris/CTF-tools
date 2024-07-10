package org.springframework.cglib.proxy;

import java.lang.reflect.Method;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/proxy/CallbackFilter.class */
public interface CallbackFilter {
    int accept(Method method);

    boolean equals(Object obj);
}