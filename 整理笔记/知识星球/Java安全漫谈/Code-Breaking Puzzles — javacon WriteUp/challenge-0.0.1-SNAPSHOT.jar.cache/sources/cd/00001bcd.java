package org.springframework.cglib.core;

import org.springframework.asm.Type;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/core/HashCodeCustomizer.class */
public interface HashCodeCustomizer extends KeyFactoryCustomizer {
    boolean customize(CodeEmitter codeEmitter, Type type);
}