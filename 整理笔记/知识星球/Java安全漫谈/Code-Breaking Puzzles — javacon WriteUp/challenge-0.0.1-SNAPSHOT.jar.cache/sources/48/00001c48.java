package org.springframework.cglib.transform;

import org.springframework.asm.Attribute;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.cglib.core.ClassGenerator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/transform/ClassReaderGenerator.class */
public class ClassReaderGenerator implements ClassGenerator {
    private final ClassReader r;
    private final Attribute[] attrs;
    private final int flags;

    public ClassReaderGenerator(ClassReader r, int flags) {
        this(r, null, flags);
    }

    public ClassReaderGenerator(ClassReader r, Attribute[] attrs, int flags) {
        this.r = r;
        this.attrs = attrs != null ? attrs : new Attribute[0];
        this.flags = flags;
    }

    @Override // org.springframework.cglib.core.ClassGenerator
    public void generateClass(ClassVisitor v) {
        this.r.accept(v, this.attrs, this.flags);
    }
}