package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/IntLiteral.class */
public class IntLiteral extends Literal {
    private final TypedValue value;

    public IntLiteral(String payload, int pos, int value) {
        super(payload, pos);
        this.value = new TypedValue(Integer.valueOf(value));
        this.exitTypeDescriptor = "I";
    }

    @Override // org.springframework.expression.spel.ast.Literal
    public TypedValue getLiteralValue() {
        return this.value;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        return true;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        Integer intValue = (Integer) this.value.getValue();
        Assert.state(intValue != null, "No int value");
        if (intValue.intValue() == -1) {
            mv.visitInsn(2);
        } else if (intValue.intValue() >= 0 && intValue.intValue() < 6) {
            mv.visitInsn(3 + intValue.intValue());
        } else {
            mv.visitLdcInsn(intValue);
        }
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}