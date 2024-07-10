package org.springframework.cglib.transform;

import org.springframework.asm.ClassVisitor;
import org.springframework.cglib.core.Constants;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/transform/AbstractClassTransformer.class */
public abstract class AbstractClassTransformer extends ClassTransformer {
    public AbstractClassTransformer() {
        super(Constants.ASM_API);
    }

    @Override // org.springframework.cglib.transform.ClassTransformer
    public void setTarget(ClassVisitor target) {
        this.cv = target;
    }
}