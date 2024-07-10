package org.springframework.context;

import org.springframework.beans.factory.Aware;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/MessageSourceAware.class */
public interface MessageSourceAware extends Aware {
    void setMessageSource(MessageSource messageSource);
}