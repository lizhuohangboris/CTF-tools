package org.springframework.cglib.transform.impl;

import java.lang.reflect.Constructor;
import org.springframework.asm.Type;
import org.springframework.cglib.core.Block;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.transform.ClassEmitterTransformer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/transform/impl/UndeclaredThrowableTransformer.class */
public class UndeclaredThrowableTransformer extends ClassEmitterTransformer {
    private Type wrapper;

    public UndeclaredThrowableTransformer(Class wrapper) {
        this.wrapper = Type.getType(wrapper);
        boolean found = false;
        Constructor[] cstructs = wrapper.getConstructors();
        int i = 0;
        while (true) {
            if (i >= cstructs.length) {
                break;
            }
            Class[] types = cstructs[i].getParameterTypes();
            if (types.length != 1 || !types[0].equals(Throwable.class)) {
                i++;
            } else {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException(wrapper + " does not have a single-arg constructor that takes a Throwable");
        }
    }

    @Override // org.springframework.cglib.core.ClassEmitter
    public CodeEmitter begin_method(int access, Signature sig, final Type[] exceptions) {
        CodeEmitter e = super.begin_method(access, sig, exceptions);
        if (TypeUtils.isAbstract(access) || sig.equals(Constants.SIG_STATIC)) {
            return e;
        }
        return new CodeEmitter(e) { // from class: org.springframework.cglib.transform.impl.UndeclaredThrowableTransformer.1
            private Block handler = begin_block();

            @Override // org.springframework.cglib.core.CodeEmitter, org.springframework.cglib.core.LocalVariablesSorter, org.springframework.asm.MethodVisitor
            public void visitMaxs(int maxStack, int maxLocals) {
                this.handler.end();
                EmitUtils.wrap_undeclared_throwable(this, this.handler, exceptions, UndeclaredThrowableTransformer.this.wrapper);
                super.visitMaxs(maxStack, maxLocals);
            }
        };
    }
}