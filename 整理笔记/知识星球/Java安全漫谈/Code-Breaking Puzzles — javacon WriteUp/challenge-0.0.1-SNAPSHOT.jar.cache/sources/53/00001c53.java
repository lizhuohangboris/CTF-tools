package org.springframework.cglib.transform;

import org.springframework.asm.ClassReader;
import org.springframework.cglib.core.ClassGenerator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/transform/TransformingClassLoader.class */
public class TransformingClassLoader extends AbstractClassLoader {
    private ClassTransformerFactory t;

    public TransformingClassLoader(ClassLoader parent, ClassFilter filter, ClassTransformerFactory t) {
        super(parent, parent, filter);
        this.t = t;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.cglib.transform.AbstractClassLoader
    public ClassGenerator getGenerator(ClassReader r) {
        ClassTransformer t2 = this.t.newInstance();
        return new TransformingClassGenerator(super.getGenerator(r), t2);
    }
}