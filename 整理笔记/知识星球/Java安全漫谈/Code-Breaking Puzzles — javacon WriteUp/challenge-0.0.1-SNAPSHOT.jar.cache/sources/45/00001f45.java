package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/NullLiteral.class */
public class NullLiteral extends Literal {
    public NullLiteral(int pos) {
        super(null, pos);
        this.exitTypeDescriptor = "Ljava/lang/Object";
    }

    @Override // org.springframework.expression.spel.ast.Literal
    public TypedValue getLiteralValue() {
        return TypedValue.NULL;
    }

    @Override // org.springframework.expression.spel.ast.Literal
    public String toString() {
        return BeanDefinitionParserDelegate.NULL_ELEMENT;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        return true;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        mv.visitInsn(1);
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}