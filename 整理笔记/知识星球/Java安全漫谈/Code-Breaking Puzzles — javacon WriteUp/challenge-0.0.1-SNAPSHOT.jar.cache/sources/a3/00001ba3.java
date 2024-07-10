package org.springframework.cglib.core;

import org.springframework.asm.ClassVisitor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/core/ClassGenerator.class */
public interface ClassGenerator {
    void generateClass(ClassVisitor classVisitor) throws Exception;
}