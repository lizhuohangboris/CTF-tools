package org.springframework.cglib.core;

import org.springframework.asm.Label;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/core/ObjectSwitchCallback.class */
public interface ObjectSwitchCallback {
    void processCase(Object obj, Label label) throws Exception;

    void processDefault() throws Exception;
}