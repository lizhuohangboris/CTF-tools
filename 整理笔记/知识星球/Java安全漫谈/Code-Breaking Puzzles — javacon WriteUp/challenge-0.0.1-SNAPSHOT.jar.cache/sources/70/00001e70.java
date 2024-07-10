package org.springframework.core.io;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/ContextResource.class */
public interface ContextResource extends Resource {
    String getPathWithinContext();
}