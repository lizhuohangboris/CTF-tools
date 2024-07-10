package org.springframework.cglib.core;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/core/GeneratorStrategy.class */
public interface GeneratorStrategy {
    byte[] generate(ClassGenerator classGenerator) throws Exception;

    boolean equals(Object obj);
}