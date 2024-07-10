package org.springframework.expression.spel.ast;

import java.util.List;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypeComparator;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.support.BooleanTypedValue;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/OperatorBetween.class */
public class OperatorBetween extends Operator {
    public OperatorBetween(int pos, SpelNodeImpl... operands) {
        super("between", pos, operands);
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        Object left = getLeftOperand().getValueInternal(state).getValue();
        Object right = getRightOperand().getValueInternal(state).getValue();
        if (!(right instanceof List) || ((List) right).size() != 2) {
            throw new SpelEvaluationException(getRightOperand().getStartPosition(), SpelMessage.BETWEEN_RIGHT_OPERAND_MUST_BE_TWO_ELEMENT_LIST, new Object[0]);
        }
        List<?> list = (List) right;
        Object low = list.get(0);
        Object high = list.get(1);
        TypeComparator comp = state.getTypeComparator();
        try {
            return BooleanTypedValue.forValue(comp.compare(left, low) >= 0 && comp.compare(left, high) <= 0);
        } catch (SpelEvaluationException ex) {
            ex.setPosition(getStartPosition());
            throw ex;
        }
    }
}