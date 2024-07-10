package org.springframework.cglib.transform.impl;

import org.springframework.asm.Label;
import org.springframework.asm.Opcodes;
import org.springframework.asm.Type;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.Local;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.transform.ClassEmitterTransformer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/transform/impl/InterceptFieldTransformer.class */
public class InterceptFieldTransformer extends ClassEmitterTransformer {
    private static final String CALLBACK_FIELD = "$CGLIB_READ_WRITE_CALLBACK";
    private static final Type CALLBACK = TypeUtils.parseType("org.springframework.cglib.transform.impl.InterceptFieldCallback");
    private static final Type ENABLED = TypeUtils.parseType("org.springframework.cglib.transform.impl.InterceptFieldEnabled");
    private static final Signature ENABLED_SET = new Signature("setInterceptFieldCallback", Type.VOID_TYPE, new Type[]{CALLBACK});
    private static final Signature ENABLED_GET = new Signature("getInterceptFieldCallback", CALLBACK, new Type[0]);
    private InterceptFieldFilter filter;

    public InterceptFieldTransformer(InterceptFieldFilter filter) {
        this.filter = filter;
    }

    @Override // org.springframework.cglib.core.ClassEmitter
    public void begin_class(int version, int access, String className, Type superType, Type[] interfaces, String sourceFile) {
        if (!TypeUtils.isInterface(access)) {
            super.begin_class(version, access, className, superType, TypeUtils.add(interfaces, ENABLED), sourceFile);
            super.declare_field(130, CALLBACK_FIELD, CALLBACK, null);
            CodeEmitter e = super.begin_method(1, ENABLED_GET, null);
            e.load_this();
            e.getfield(CALLBACK_FIELD);
            e.return_value();
            e.end_method();
            CodeEmitter e2 = super.begin_method(1, ENABLED_SET, null);
            e2.load_this();
            e2.load_arg(0);
            e2.putfield(CALLBACK_FIELD);
            e2.return_value();
            e2.end_method();
            return;
        }
        super.begin_class(version, access, className, superType, interfaces, sourceFile);
    }

    @Override // org.springframework.cglib.core.ClassEmitter
    public void declare_field(int access, String name, Type type, Object value) {
        super.declare_field(access, name, type, value);
        if (!TypeUtils.isStatic(access)) {
            if (this.filter.acceptRead(getClassType(), name)) {
                addReadMethod(name, type);
            }
            if (this.filter.acceptWrite(getClassType(), name)) {
                addWriteMethod(name, type);
            }
        }
    }

    private void addReadMethod(String name, Type type) {
        CodeEmitter e = super.begin_method(1, readMethodSig(name, type.getDescriptor()), null);
        e.load_this();
        e.getfield(name);
        e.load_this();
        e.invoke_interface(ENABLED, ENABLED_GET);
        Label intercept = e.make_label();
        e.ifnonnull(intercept);
        e.return_value();
        e.mark(intercept);
        Local result = e.make_local(type);
        e.store_local(result);
        e.load_this();
        e.invoke_interface(ENABLED, ENABLED_GET);
        e.load_this();
        e.push(name);
        e.load_local(result);
        e.invoke_interface(CALLBACK, readCallbackSig(type));
        if (!TypeUtils.isPrimitive(type)) {
            e.checkcast(type);
        }
        e.return_value();
        e.end_method();
    }

    private void addWriteMethod(String name, Type type) {
        CodeEmitter e = super.begin_method(1, writeMethodSig(name, type.getDescriptor()), null);
        e.load_this();
        e.dup();
        e.invoke_interface(ENABLED, ENABLED_GET);
        Label skip = e.make_label();
        e.ifnull(skip);
        e.load_this();
        e.invoke_interface(ENABLED, ENABLED_GET);
        e.load_this();
        e.push(name);
        e.load_this();
        e.getfield(name);
        e.load_arg(0);
        e.invoke_interface(CALLBACK, writeCallbackSig(type));
        if (!TypeUtils.isPrimitive(type)) {
            e.checkcast(type);
        }
        Label go = e.make_label();
        e.goTo(go);
        e.mark(skip);
        e.load_arg(0);
        e.mark(go);
        e.putfield(name);
        e.return_value();
        e.end_method();
    }

    @Override // org.springframework.cglib.core.ClassEmitter
    public CodeEmitter begin_method(int access, Signature sig, Type[] exceptions) {
        return new CodeEmitter(super.begin_method(access, sig, exceptions)) { // from class: org.springframework.cglib.transform.impl.InterceptFieldTransformer.1
            @Override // org.springframework.asm.MethodVisitor
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                Type towner = TypeUtils.fromInternalName(owner);
                switch (opcode) {
                    case Opcodes.GETFIELD /* 180 */:
                        if (InterceptFieldTransformer.this.filter.acceptRead(towner, name)) {
                            helper(towner, InterceptFieldTransformer.readMethodSig(name, desc));
                            return;
                        }
                        break;
                    case Opcodes.PUTFIELD /* 181 */:
                        if (InterceptFieldTransformer.this.filter.acceptWrite(towner, name)) {
                            helper(towner, InterceptFieldTransformer.writeMethodSig(name, desc));
                            return;
                        }
                        break;
                }
                super.visitFieldInsn(opcode, owner, name, desc);
            }

            private void helper(Type owner, Signature sig2) {
                invoke_virtual(owner, sig2);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Signature readMethodSig(String name, String desc) {
        return new Signature("$cglib_read_" + name, "()" + desc);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Signature writeMethodSig(String name, String desc) {
        return new Signature("$cglib_write_" + name, "(" + desc + ")V");
    }

    private static Signature readCallbackSig(Type type) {
        Type remap = remap(type);
        return new Signature("read" + callbackName(remap), remap, new Type[]{Constants.TYPE_OBJECT, Constants.TYPE_STRING, remap});
    }

    private static Signature writeCallbackSig(Type type) {
        Type remap = remap(type);
        return new Signature("write" + callbackName(remap), remap, new Type[]{Constants.TYPE_OBJECT, Constants.TYPE_STRING, remap, remap});
    }

    private static Type remap(Type type) {
        switch (type.getSort()) {
            case 9:
            case 10:
                return Constants.TYPE_OBJECT;
            default:
                return type;
        }
    }

    private static String callbackName(Type type) {
        return type == Constants.TYPE_OBJECT ? "Object" : TypeUtils.upperFirst(TypeUtils.getClassName(type));
    }
}