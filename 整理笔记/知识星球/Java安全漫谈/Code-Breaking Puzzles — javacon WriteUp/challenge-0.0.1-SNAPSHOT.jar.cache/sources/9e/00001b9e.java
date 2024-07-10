package org.springframework.cglib.core;

import java.util.HashMap;
import java.util.Map;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.FieldVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.Type;
import org.springframework.cglib.transform.ClassTransformer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/core/ClassEmitter.class */
public class ClassEmitter extends ClassTransformer {
    private ClassInfo classInfo;
    private Map fieldInfo;
    private static int hookCounter;
    private MethodVisitor rawStaticInit;
    private CodeEmitter staticInit;
    private CodeEmitter staticHook;
    private Signature staticHookSig;

    public ClassEmitter(ClassVisitor cv) {
        setTarget(cv);
    }

    public ClassEmitter() {
        super(Constants.ASM_API);
    }

    @Override // org.springframework.cglib.transform.ClassTransformer
    public void setTarget(ClassVisitor cv) {
        this.cv = cv;
        this.fieldInfo = new HashMap();
        this.staticHook = null;
        this.staticInit = null;
        this.staticHookSig = null;
    }

    private static synchronized int getNextHook() {
        int i = hookCounter + 1;
        hookCounter = i;
        return i;
    }

    public ClassInfo getClassInfo() {
        return this.classInfo;
    }

    public void begin_class(int version, final int access, String className, final Type superType, final Type[] interfaces, String source) {
        final Type classType = Type.getType("L" + className.replace('.', '/') + ";");
        this.classInfo = new ClassInfo() { // from class: org.springframework.cglib.core.ClassEmitter.1
            @Override // org.springframework.cglib.core.ClassInfo
            public Type getType() {
                return classType;
            }

            @Override // org.springframework.cglib.core.ClassInfo
            public Type getSuperType() {
                return superType != null ? superType : Constants.TYPE_OBJECT;
            }

            @Override // org.springframework.cglib.core.ClassInfo
            public Type[] getInterfaces() {
                return interfaces;
            }

            @Override // org.springframework.cglib.core.ClassInfo
            public int getModifiers() {
                return access;
            }
        };
        this.cv.visit(version, access, this.classInfo.getType().getInternalName(), null, this.classInfo.getSuperType().getInternalName(), TypeUtils.toInternalNames(interfaces));
        if (source != null) {
            this.cv.visitSource(source, null);
        }
        init();
    }

    public CodeEmitter getStaticHook() {
        if (TypeUtils.isInterface(getAccess())) {
            throw new IllegalStateException("static hook is invalid for this class");
        }
        if (this.staticHook == null) {
            this.staticHookSig = new Signature("CGLIB$STATICHOOK" + getNextHook(), "()V");
            this.staticHook = begin_method(8, this.staticHookSig, null);
            if (this.staticInit != null) {
                this.staticInit.invoke_static_this(this.staticHookSig);
            }
        }
        return this.staticHook;
    }

    protected void init() {
    }

    public int getAccess() {
        return this.classInfo.getModifiers();
    }

    public Type getClassType() {
        return this.classInfo.getType();
    }

    public Type getSuperType() {
        return this.classInfo.getSuperType();
    }

    public void end_class() {
        if (this.staticHook != null && this.staticInit == null) {
            begin_static();
        }
        if (this.staticInit != null) {
            this.staticHook.return_value();
            this.staticHook.end_method();
            this.rawStaticInit.visitInsn(Opcodes.RETURN);
            this.rawStaticInit.visitMaxs(0, 0);
            this.staticHook = null;
            this.staticInit = null;
            this.staticHookSig = null;
        }
        this.cv.visitEnd();
    }

    public CodeEmitter begin_method(int access, Signature sig, Type[] exceptions) {
        if (this.classInfo == null) {
            throw new IllegalStateException("classInfo is null! " + this);
        }
        MethodVisitor v = this.cv.visitMethod(access, sig.getName(), sig.getDescriptor(), null, TypeUtils.toInternalNames(exceptions));
        if (sig.equals(Constants.SIG_STATIC) && !TypeUtils.isInterface(getAccess())) {
            this.rawStaticInit = v;
            MethodVisitor wrapped = new MethodVisitor(Constants.ASM_API, v) { // from class: org.springframework.cglib.core.ClassEmitter.2
                @Override // org.springframework.asm.MethodVisitor
                public void visitMaxs(int maxStack, int maxLocals) {
                }

                @Override // org.springframework.asm.MethodVisitor
                public void visitInsn(int insn) {
                    if (insn != 177) {
                        super.visitInsn(insn);
                    }
                }
            };
            this.staticInit = new CodeEmitter(this, wrapped, access, sig, exceptions);
            if (this.staticHook == null) {
                getStaticHook();
            } else {
                this.staticInit.invoke_static_this(this.staticHookSig);
            }
            return this.staticInit;
        } else if (sig.equals(this.staticHookSig)) {
            return new CodeEmitter(this, v, access, sig, exceptions) { // from class: org.springframework.cglib.core.ClassEmitter.3
                @Override // org.springframework.cglib.core.CodeEmitter
                public boolean isStaticHook() {
                    return true;
                }
            };
        } else {
            return new CodeEmitter(this, v, access, sig, exceptions);
        }
    }

    public CodeEmitter begin_static() {
        return begin_method(8, Constants.SIG_STATIC, null);
    }

    public void declare_field(int access, String name, Type type, Object value) {
        FieldInfo existing = (FieldInfo) this.fieldInfo.get(name);
        FieldInfo info = new FieldInfo(access, name, type, value);
        if (existing != null) {
            if (!info.equals(existing)) {
                throw new IllegalArgumentException("Field \"" + name + "\" has been declared differently");
            }
            return;
        }
        this.fieldInfo.put(name, info);
        this.cv.visitField(access, name, type.getDescriptor(), null, value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isFieldDeclared(String name) {
        return this.fieldInfo.get(name) != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FieldInfo getFieldInfo(String name) {
        FieldInfo field = (FieldInfo) this.fieldInfo.get(name);
        if (field == null) {
            throw new IllegalArgumentException("Field " + name + " is not declared in " + getClassType().getClassName());
        }
        return field;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/core/ClassEmitter$FieldInfo.class */
    public static class FieldInfo {
        int access;
        String name;
        Type type;
        Object value;

        public FieldInfo(int access, String name, Type type, Object value) {
            this.access = access;
            this.name = name;
            this.type = type;
            this.value = value;
        }

        public boolean equals(Object o) {
            if (o == null || !(o instanceof FieldInfo)) {
                return false;
            }
            FieldInfo other = (FieldInfo) o;
            if (this.access == other.access && this.name.equals(other.name) && this.type.equals(other.type)) {
                if ((this.value == null) ^ (other.value == null)) {
                    return false;
                }
                if (this.value != null && !this.value.equals(other.value)) {
                    return false;
                }
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((this.access ^ this.name.hashCode()) ^ this.type.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
        }
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        begin_class(version, access, name.replace('/', '.'), TypeUtils.fromInternalName(superName), TypeUtils.fromInternalNames(interfaces), null);
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visitEnd() {
        end_class();
    }

    @Override // org.springframework.asm.ClassVisitor
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        declare_field(access, name, Type.getType(desc), value);
        return null;
    }

    @Override // org.springframework.asm.ClassVisitor
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return begin_method(access, new Signature(name, desc), TypeUtils.fromInternalNames(exceptions));
    }
}