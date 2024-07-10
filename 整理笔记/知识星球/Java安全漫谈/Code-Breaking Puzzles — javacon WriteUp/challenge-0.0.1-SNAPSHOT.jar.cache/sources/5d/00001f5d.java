package org.springframework.expression.spel.ast;

import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.support.BooleanTypedValue;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/OperatorNot.class */
public class OperatorNot extends SpelNodeImpl {
    public OperatorNot(int pos, SpelNodeImpl operand) {
        super(pos, operand);
        this.exitTypeDescriptor = "Z";
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        try {
            Boolean value = (Boolean) this.children[0].getValue(state, Boolean.class);
            if (value == null) {
                throw new SpelEvaluationException(SpelMessage.TYPE_CONVERSION_ERROR, BeanDefinitionParserDelegate.NULL_ELEMENT, "boolean");
            }
            return BooleanTypedValue.forValue(!value.booleanValue());
        } catch (SpelEvaluationException ex) {
            ex.setPosition(getChild(0).getStartPosition());
            throw ex;
        }
    }

    @Override // org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        return "!" + getChild(0).toStringAST();
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        SpelNodeImpl child = this.children[0];
        return child.isCompilable() && CodeFlow.isBooleanCompatible(child.exitTypeDescriptor);
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        this.children[0].generateCode(mv, cf);
        cf.unboxBooleanIfNecessary(mv);
        Label elseTarget = new Label();
        Label endOfIf = new Label();
        mv.visitJumpInsn(154, elseTarget);
        mv.visitInsn(4);
        mv.visitJumpInsn(167, endOfIf);
        mv.visitLabel(elseTarget);
        mv.visitInsn(3);
        mv.visitLabel(endOfIf);
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}