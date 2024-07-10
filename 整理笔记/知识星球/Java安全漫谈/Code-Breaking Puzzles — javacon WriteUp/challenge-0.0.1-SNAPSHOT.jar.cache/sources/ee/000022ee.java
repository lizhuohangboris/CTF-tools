package org.springframework.scripting.groovy;

import groovy.lang.GroovyObject;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scripting/groovy/GroovyObjectCustomizer.class */
public interface GroovyObjectCustomizer {
    void customize(GroovyObject groovyObject);
}