package org.springframework.context;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/HierarchicalMessageSource.class */
public interface HierarchicalMessageSource extends MessageSource {
    void setParentMessageSource(@Nullable MessageSource messageSource);

    @Nullable
    MessageSource getParentMessageSource();
}