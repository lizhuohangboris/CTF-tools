package org.springframework.expression.spel.ast;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.support.BooleanTypedValue;
import org.springframework.util.NumberUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/OpGT.class */
public class OpGT extends Operator {
    public OpGT(int pos, SpelNodeImpl... operands) {
        super(">", pos, operands);
        this.exitTypeDescriptor = "Z";
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        Object left = getLeftOperand().getValueInternal(state).getValue();
        Object right = getRightOperand().getValueInternal(state).getValue();
        this.leftActualDescriptor = CodeFlow.toDescriptorFromObject(left);
        this.rightActualDescriptor = CodeFlow.toDescriptorFromObject(right);
        if ((left instanceof Number) && (right instanceof Number)) {
            Number leftNumber = (Number) left;
            Number rightNumber = (Number) right;
            if ((leftNumber instanceof BigDecimal) || (rightNumber instanceof BigDecimal)) {
                BigDecimal leftBigDecimal = (BigDecimal) NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
                BigDecimal rightBigDecimal = (BigDecimal) NumberUtils.convertNumberToTargetClass(rightNumber, BigDecimal.class);
                return BooleanTypedValue.forValue(leftBigDecimal.compareTo(rightBigDecimal) > 0);
            } else if ((leftNumber instanceof Double) || (rightNumber instanceof Double)) {
                return BooleanTypedValue.forValue(leftNumber.doubleValue() > rightNumber.doubleValue());
            } else if ((leftNumber instanceof Float) || (rightNumber instanceof Float)) {
                return BooleanTypedValue.forValue(leftNumber.floatValue() > rightNumber.floatValue());
            } else if ((leftNumber instanceof BigInteger) || (rightNumber instanceof BigInteger)) {
                BigInteger leftBigInteger = (BigInteger) NumberUtils.convertNumberToTargetClass(leftNumber, BigInteger.class);
                BigInteger rightBigInteger = (BigInteger) NumberUtils.convertNumberToTargetClass(rightNumber, BigInteger.class);
                return BooleanTypedValue.forValue(leftBigInteger.compareTo(rightBigInteger) > 0);
            } else if ((leftNumber instanceof Long) || (rightNumber instanceof Long)) {
                return BooleanTypedValue.forValue(leftNumber.longValue() > rightNumber.longValue());
            } else if ((leftNumber instanceof Integer) || (rightNumber instanceof Integer)) {
                return BooleanTypedValue.forValue(leftNumber.intValue() > rightNumber.intValue());
            } else if ((leftNumber instanceof Short) || (rightNumber instanceof Short)) {
                return BooleanTypedValue.forValue(leftNumber.shortValue() > rightNumber.shortValue());
            } else if ((leftNumber instanceof Byte) || (rightNumber instanceof Byte)) {
                return BooleanTypedValue.forValue(leftNumber.byteValue() > rightNumber.byteValue());
            } else {
                return BooleanTypedValue.forValue(leftNumber.doubleValue() > rightNumber.doubleValue());
            }
        }
        if ((left instanceof CharSequence) && (right instanceof CharSequence)) {
            left = left.toString();
            right = right.toString();
        }
        return BooleanTypedValue.forValue(state.getTypeComparator().compare(left, right) > 0);
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        return isCompilableOperatorUsingNumerics();
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        generateComparisonCode(mv, cf, 158, Opcodes.IF_ICMPLE);
    }
}