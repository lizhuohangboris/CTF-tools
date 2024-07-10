package org.thymeleaf.standard.expression;

import org.thymeleaf.context.IExpressionContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/IStandardExpressionParser.class */
public interface IStandardExpressionParser {
    IStandardExpression parseExpression(IExpressionContext iExpressionContext, String str);
}