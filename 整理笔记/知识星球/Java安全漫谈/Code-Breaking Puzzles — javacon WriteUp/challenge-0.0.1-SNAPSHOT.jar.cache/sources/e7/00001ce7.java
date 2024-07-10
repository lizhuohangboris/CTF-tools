package org.springframework.context.annotation;

import org.springframework.beans.factory.Aware;
import org.springframework.core.type.AnnotationMetadata;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/ImportAware.class */
public interface ImportAware extends Aware {
    void setImportMetadata(AnnotationMetadata annotationMetadata);
}