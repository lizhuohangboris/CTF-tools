package org.springframework.context.annotation;

import org.springframework.core.type.AnnotationMetadata;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/ImportSelector.class */
public interface ImportSelector {
    String[] selectImports(AnnotationMetadata annotationMetadata);
}