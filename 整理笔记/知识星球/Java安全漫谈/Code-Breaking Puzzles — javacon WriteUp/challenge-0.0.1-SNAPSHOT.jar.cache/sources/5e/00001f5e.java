package org.springframework.expression.spel.ast;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.util.NumberUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/OperatorPower.class */
public class OperatorPower extends Operator {
    public OperatorPower(int pos, SpelNodeImpl... operands) {
        super("^", pos, operands);
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        SpelNodeImpl leftOp = getLeftOperand();
        SpelNodeImpl rightOp = getRightOperand();
        Object leftOperand = leftOp.getValueInternal(state).getValue();
        Object rightOperand = rightOp.getValueInternal(state).getValue();
        if ((leftOperand instanceof Number) && (rightOperand instanceof Number)) {
            Number leftNumber = (Number) leftOperand;
            Number rightNumber = (Number) rightOperand;
            if (leftNumber instanceof BigDecimal) {
                BigDecimal leftBigDecimal = (BigDecimal) NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
                return new TypedValue(leftBigDecimal.pow(rightNumber.intValue()));
            } else if (leftNumber instanceof BigInteger) {
                BigInteger leftBigInteger = (BigInteger) NumberUtils.convertNumberToTargetClass(leftNumber, BigInteger.class);
                return new TypedValue(leftBigInteger.pow(rightNumber.intValue()));
            } else if ((leftNumber instanceof Double) || (rightNumber instanceof Double)) {
                return new TypedValue(Double.valueOf(Math.pow(leftNumber.doubleValue(), rightNumber.doubleValue())));
            } else {
                if ((leftNumber instanceof Float) || (rightNumber instanceof Float)) {
                    return new TypedValue(Double.valueOf(Math.pow(leftNumber.floatValue(), rightNumber.floatValue())));
                }
                double d = Math.pow(leftNumber.doubleValue(), rightNumber.doubleValue());
                if (d > 2.147483647E9d || (leftNumber instanceof Long) || (rightNumber instanceof Long)) {
                    return new TypedValue(Long.valueOf((long) d));
                }
                return new TypedValue(Integer.valueOf((int) d));
            }
        }
        return state.operate(Operation.POWER, leftOperand, rightOperand);
    }
}