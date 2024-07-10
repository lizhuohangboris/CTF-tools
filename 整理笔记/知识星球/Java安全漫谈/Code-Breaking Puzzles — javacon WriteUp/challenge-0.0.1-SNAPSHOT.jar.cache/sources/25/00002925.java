package org.thymeleaf.standard.expression;

import org.thymeleaf.context.IExpressionContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/IStandardExpression.class */
public interface IStandardExpression {
    String getStringRepresentation();

    Object execute(IExpressionContext iExpressionContext);

    Object execute(IExpressionContext iExpressionContext, StandardExpressionExecutionContext standardExpressionExecutionContext);
}