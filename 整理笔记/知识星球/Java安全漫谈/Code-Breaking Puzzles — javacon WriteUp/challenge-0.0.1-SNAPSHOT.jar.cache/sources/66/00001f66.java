package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/StringLiteral.class */
public class StringLiteral extends Literal {
    private final TypedValue value;

    public StringLiteral(String payload, int pos, String value) {
        super(payload, pos);
        String valueWithinQuotes = value.substring(1, value.length() - 1);
        this.value = new TypedValue(StringUtils.replace(StringUtils.replace(valueWithinQuotes, "''", "'"), "\"\"", "\""));
        this.exitTypeDescriptor = "Ljava/lang/String";
    }

    @Override // org.springframework.expression.spel.ast.Literal
    public TypedValue getLiteralValue() {
        return this.value;
    }

    @Override // org.springframework.expression.spel.ast.Literal
    public String toString() {
        return "'" + getLiteralValue().getValue() + "'";
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        return true;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        mv.visitLdcInsn(this.value.getValue());
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}