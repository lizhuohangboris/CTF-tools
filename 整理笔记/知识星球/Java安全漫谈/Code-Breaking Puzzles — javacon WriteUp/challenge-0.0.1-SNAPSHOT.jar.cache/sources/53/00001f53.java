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
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/OpOr.class */
public class OpOr extends Operator {
    public OpOr(int pos, SpelNodeImpl... operands) {
        super("or", pos, operands);
        this.exitTypeDescriptor = "Z";
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        if (getBooleanValue(state, getLeftOperand())) {
            return BooleanTypedValue.TRUE;
        }
        return BooleanTypedValue.forValue(getBooleanValue(state, getRightOperand()));
    }

    private boolean getBooleanValue(ExpressionState state, SpelNodeImpl operand) {
        try {
            Boolean value = (Boolean) operand.getValue(state, Boolean.class);
            assertValueNotNull(value);
            return value.booleanValue();
        } catch (SpelEvaluationException ee) {
            ee.setPosition(operand.getStartPosition());
            throw ee;
        }
    }

    private void assertValueNotNull(@Nullable Boolean value) {
        if (value == null) {
            throw new SpelEvaluationException(SpelMessage.TYPE_CONVERSION_ERROR, BeanDefinitionParserDelegate.NULL_ELEMENT, "boolean");
        }
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        SpelNodeImpl left = getLeftOperand();
        SpelNodeImpl right = getRightOperand();
        return left.isCompilable() && right.isCompilable() && CodeFlow.isBooleanCompatible(left.exitTypeDescriptor) && CodeFlow.isBooleanCompatible(right.exitTypeDescriptor);
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        Label elseTarget = new Label();
        Label endOfIf = new Label();
        cf.enterCompilationScope();
        getLeftOperand().generateCode(mv, cf);
        cf.unboxBooleanIfNecessary(mv);
        cf.exitCompilationScope();
        mv.visitJumpInsn(153, elseTarget);
        mv.visitLdcInsn(1);
        mv.visitJumpInsn(167, endOfIf);
        mv.visitLabel(elseTarget);
        cf.enterCompilationScope();
        getRightOperand().generateCode(mv, cf);
        cf.unboxBooleanIfNecessary(mv);
        cf.exitCompilationScope();
        mv.visitLabel(endOfIf);
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}