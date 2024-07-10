package org.springframework.cglib.transform;

import org.springframework.asm.ClassVisitor;
import org.springframework.asm.MethodVisitor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/transform/MethodFilterTransformer.class */
public class MethodFilterTransformer extends AbstractClassTransformer {
    private MethodFilter filter;
    private ClassTransformer pass;
    private ClassVisitor direct;

    public MethodFilterTransformer(MethodFilter filter, ClassTransformer pass) {
        this.filter = filter;
        this.pass = pass;
        super.setTarget(pass);
    }

    @Override // org.springframework.asm.ClassVisitor
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return (this.filter.accept(access, name, desc, signature, exceptions) ? this.pass : this.direct).visitMethod(access, name, desc, signature, exceptions);
    }

    @Override // org.springframework.cglib.transform.AbstractClassTransformer, org.springframework.cglib.transform.ClassTransformer
    public void setTarget(ClassVisitor target) {
        this.pass.setTarget(target);
        this.direct = target;
    }
}