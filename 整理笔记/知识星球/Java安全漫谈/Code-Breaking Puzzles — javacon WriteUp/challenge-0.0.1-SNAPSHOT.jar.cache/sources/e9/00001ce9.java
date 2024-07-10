package org.springframework.context.annotation;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/ImportRegistry.class */
interface ImportRegistry {
    @Nullable
    AnnotationMetadata getImportingClassFor(String str);

    void removeImportingClass(String str);
}