package org.springframework.context.annotation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/AnnotationConfigRegistry.class */
public interface AnnotationConfigRegistry {
    void register(Class<?>... clsArr);

    void scan(String... strArr);
}