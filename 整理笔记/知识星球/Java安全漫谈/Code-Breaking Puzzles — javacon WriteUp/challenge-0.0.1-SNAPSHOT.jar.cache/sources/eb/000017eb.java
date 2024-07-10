package org.springframework.boot.autoconfigure.session;

import org.springframework.boot.WebApplicationType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/ReactiveSessionCondition.class */
class ReactiveSessionCondition extends AbstractSessionCondition {
    ReactiveSessionCondition() {
        super(WebApplicationType.REACTIVE);
    }
}