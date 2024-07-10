package org.springframework.boot.context.annotation;

import java.util.Set;
import org.springframework.core.type.AnnotationMetadata;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/annotation/DeterminableImports.class */
public interface DeterminableImports {
    Set<Object> determineImports(AnnotationMetadata metadata);
}