package org.springframework.core.type;

import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/type/AnnotationMetadata.class */
public interface AnnotationMetadata extends ClassMetadata, AnnotatedTypeMetadata {
    Set<String> getAnnotationTypes();

    Set<String> getMetaAnnotationTypes(String str);

    boolean hasAnnotation(String str);

    boolean hasMetaAnnotation(String str);

    boolean hasAnnotatedMethods(String str);

    Set<MethodMetadata> getAnnotatedMethods(String str);
}