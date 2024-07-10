package org.springframework.expression;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/ExpressionParser.class */
public interface ExpressionParser {
    Expression parseExpression(String str) throws ParseException;

    Expression parseExpression(String str, ParserContext parserContext) throws ParseException;
}