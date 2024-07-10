package org.springframework.expression.spel.support;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import org.springframework.expression.OperatorOverloader;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/support/StandardOperatorOverloader.class */
public class StandardOperatorOverloader implements OperatorOverloader {
    @Override // org.springframework.expression.OperatorOverloader
    public boolean overridesOperation(Operation operation, @Nullable Object leftOperand, @Nullable Object rightOperand) throws EvaluationException {
        return false;
    }

    @Override // org.springframework.expression.OperatorOverloader
    public Object operate(Operation operation, @Nullable Object leftOperand, @Nullable Object rightOperand) throws EvaluationException {
        throw new EvaluationException("No operation overloaded by default");
    }
}