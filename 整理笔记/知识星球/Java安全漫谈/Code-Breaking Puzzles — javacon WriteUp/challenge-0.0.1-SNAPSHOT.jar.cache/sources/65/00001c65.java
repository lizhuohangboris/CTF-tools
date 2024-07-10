package org.springframework.cglib.transform.impl;

import org.springframework.asm.Type;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/transform/impl/InterceptFieldFilter.class */
public interface InterceptFieldFilter {
    boolean acceptRead(Type type, String str);

    boolean acceptWrite(Type type, String str);
}