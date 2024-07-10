package org.springframework.cglib.transform;

import org.springframework.asm.ClassVisitor;
import org.springframework.cglib.core.Constants;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/transform/ClassTransformer.class */
public abstract class ClassTransformer extends ClassVisitor {
    public abstract void setTarget(ClassVisitor classVisitor);

    public ClassTransformer() {
        super(Constants.ASM_API);
    }

    public ClassTransformer(int opcode) {
        super(opcode);
    }
}