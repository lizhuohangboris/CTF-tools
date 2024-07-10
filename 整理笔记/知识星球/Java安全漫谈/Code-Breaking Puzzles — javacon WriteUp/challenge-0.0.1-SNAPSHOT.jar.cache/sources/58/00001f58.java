package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.Type;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.support.BooleanTypedValue;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/OperatorInstanceof.class */
public class OperatorInstanceof extends Operator {
    @Nullable
    private Class<?> type;

    public OperatorInstanceof(int pos, SpelNodeImpl... operands) {
        super("instanceof", pos, operands);
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        BooleanTypedValue result;
        SpelNodeImpl rightOperand = getRightOperand();
        TypedValue left = getLeftOperand().getValueInternal(state);
        TypedValue right = rightOperand.getValueInternal(state);
        Object leftValue = left.getValue();
        Object rightValue = right.getValue();
        if (rightValue == null || !(rightValue instanceof Class)) {
            int startPosition = getRightOperand().getStartPosition();
            SpelMessage spelMessage = SpelMessage.INSTANCEOF_OPERATOR_NEEDS_CLASS_OPERAND;
            Object[] objArr = new Object[1];
            objArr[0] = rightValue == null ? BeanDefinitionParserDelegate.NULL_ELEMENT : rightValue.getClass().getName();
            throw new SpelEvaluationException(startPosition, spelMessage, objArr);
        }
        Class<?> rightClass = (Class) rightValue;
        if (leftValue == null) {
            result = BooleanTypedValue.FALSE;
        } else {
            result = BooleanTypedValue.forValue(rightClass.isAssignableFrom(leftValue.getClass()));
        }
        this.type = rightClass;
        if (rightOperand instanceof TypeReference) {
            this.exitTypeDescriptor = "Z";
        }
        return result;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        return this.exitTypeDescriptor != null && getLeftOperand().isCompilable();
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        getLeftOperand().generateCode(mv, cf);
        CodeFlow.insertBoxIfNecessary(mv, cf.lastDescriptor());
        Assert.state(this.type != null, "No type available");
        if (this.type.isPrimitive()) {
            mv.visitInsn(87);
            mv.visitInsn(3);
        } else {
            mv.visitTypeInsn(Opcodes.INSTANCEOF, Type.getInternalName(this.type));
        }
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}