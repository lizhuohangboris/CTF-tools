package org.springframework.cglib.transform;

import org.springframework.asm.ClassVisitor;
import org.springframework.cglib.core.ClassGenerator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/transform/TransformingClassGenerator.class */
public class TransformingClassGenerator implements ClassGenerator {
    private ClassGenerator gen;
    private ClassTransformer t;

    public TransformingClassGenerator(ClassGenerator gen, ClassTransformer t) {
        this.gen = gen;
        this.t = t;
    }

    @Override // org.springframework.cglib.core.ClassGenerator
    public void generateClass(ClassVisitor v) throws Exception {
        this.t.setTarget(v);
        this.gen.generateClass(this.t);
    }
}