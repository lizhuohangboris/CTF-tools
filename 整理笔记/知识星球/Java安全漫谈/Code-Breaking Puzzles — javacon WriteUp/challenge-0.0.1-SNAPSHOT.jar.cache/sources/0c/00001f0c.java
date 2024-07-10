package org.springframework.expression;

import org.springframework.context.expression.StandardBeanExpressionResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/ParserContext.class */
public interface ParserContext {
    public static final ParserContext TEMPLATE_EXPRESSION = new ParserContext() { // from class: org.springframework.expression.ParserContext.1
        @Override // org.springframework.expression.ParserContext
        public boolean isTemplate() {
            return true;
        }

        @Override // org.springframework.expression.ParserContext
        public String getExpressionPrefix() {
            return StandardBeanExpressionResolver.DEFAULT_EXPRESSION_PREFIX;
        }

        @Override // org.springframework.expression.ParserContext
        public String getExpressionSuffix() {
            return "}";
        }
    };

    boolean isTemplate();

    String getExpressionPrefix();

    String getExpressionSuffix();
}