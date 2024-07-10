package org.springframework.expression.spel;

import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.expression.PropertyAccessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/CompilablePropertyAccessor.class */
public interface CompilablePropertyAccessor extends PropertyAccessor, Opcodes {
    boolean isCompilable();

    Class<?> getPropertyType();

    void generateCode(String str, MethodVisitor methodVisitor, CodeFlow codeFlow);
}