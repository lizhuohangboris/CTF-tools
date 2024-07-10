package org.springframework.expression.spel.ast;

import java.lang.reflect.Array;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.Type;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/TypeReference.class */
public class TypeReference extends SpelNodeImpl {
    private final int dimensions;
    @Nullable
    private transient Class<?> type;

    public TypeReference(int pos, SpelNodeImpl qualifiedId) {
        this(pos, qualifiedId, 0);
    }

    public TypeReference(int pos, SpelNodeImpl qualifiedId, int dims) {
        super(pos, qualifiedId);
        this.dimensions = dims;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        TypeCode tc;
        String typeName = (String) this.children[0].getValueInternal(state).getValue();
        Assert.state(typeName != null, "No type name");
        if (!typeName.contains(".") && Character.isLowerCase(typeName.charAt(0)) && (tc = TypeCode.valueOf(typeName.toUpperCase())) != TypeCode.OBJECT) {
            Class<?> clazz = makeArrayIfNecessary(tc.getType());
            this.exitTypeDescriptor = "Ljava/lang/Class";
            this.type = clazz;
            return new TypedValue(clazz);
        }
        Class<?> clazz2 = makeArrayIfNecessary(state.findType(typeName));
        this.exitTypeDescriptor = "Ljava/lang/Class";
        this.type = clazz2;
        return new TypedValue(clazz2);
    }

    private Class<?> makeArrayIfNecessary(Class<?> clazz) {
        if (this.dimensions != 0) {
            for (int i = 0; i < this.dimensions; i++) {
                Object array = Array.newInstance(clazz, 0);
                clazz = array.getClass();
            }
        }
        return clazz;
    }

    @Override // org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        StringBuilder sb = new StringBuilder("T(");
        sb.append(getChild(0).toStringAST());
        for (int d = 0; d < this.dimensions; d++) {
            sb.append(ClassUtils.ARRAY_SUFFIX);
        }
        sb.append(")");
        return sb.toString();
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        return this.exitTypeDescriptor != null;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        Assert.state(this.type != null, "No type available");
        if (this.type.isPrimitive()) {
            if (this.type == Boolean.TYPE) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;");
            } else if (this.type == Byte.TYPE) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Byte", "TYPE", "Ljava/lang/Class;");
            } else if (this.type == Character.TYPE) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Character", "TYPE", "Ljava/lang/Class;");
            } else if (this.type == Double.TYPE) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Double", "TYPE", "Ljava/lang/Class;");
            } else if (this.type == Float.TYPE) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
            } else if (this.type == Integer.TYPE) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
            } else if (this.type == Long.TYPE) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Long", "TYPE", "Ljava/lang/Class;");
            } else if (this.type == Short.TYPE) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Short", "TYPE", "Ljava/lang/Class;");
            }
        } else {
            mv.visitLdcInsn(Type.getType(this.type));
        }
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}