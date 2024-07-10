package org.springframework.context.expression;

import java.util.Map;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/expression/CachedExpressionEvaluator.class */
public abstract class CachedExpressionEvaluator {
    private final SpelExpressionParser parser;
    private final ParameterNameDiscoverer parameterNameDiscoverer;

    protected CachedExpressionEvaluator(SpelExpressionParser parser) {
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
        Assert.notNull(parser, "SpelExpressionParser must not be null");
        this.parser = parser;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CachedExpressionEvaluator() {
        this(new SpelExpressionParser());
    }

    protected SpelExpressionParser getParser() {
        return this.parser;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ParameterNameDiscoverer getParameterNameDiscoverer() {
        return this.parameterNameDiscoverer;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Expression getExpression(Map<ExpressionKey, Expression> cache, AnnotatedElementKey elementKey, String expression) {
        ExpressionKey expressionKey = createKey(elementKey, expression);
        Expression expr = cache.get(expressionKey);
        if (expr == null) {
            expr = getParser().parseExpression(expression);
            cache.put(expressionKey, expr);
        }
        return expr;
    }

    private ExpressionKey createKey(AnnotatedElementKey elementKey, String expression) {
        return new ExpressionKey(elementKey, expression);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/expression/CachedExpressionEvaluator$ExpressionKey.class */
    public static class ExpressionKey implements Comparable<ExpressionKey> {
        private final AnnotatedElementKey element;
        private final String expression;

        protected ExpressionKey(AnnotatedElementKey element, String expression) {
            Assert.notNull(element, "AnnotatedElementKey must not be null");
            Assert.notNull(expression, "Expression must not be null");
            this.element = element;
            this.expression = expression;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ExpressionKey)) {
                return false;
            }
            ExpressionKey otherKey = (ExpressionKey) other;
            return this.element.equals(otherKey.element) && ObjectUtils.nullSafeEquals(this.expression, otherKey.expression);
        }

        public int hashCode() {
            return (this.element.hashCode() * 29) + this.expression.hashCode();
        }

        public String toString() {
            return this.element + " with expression \"" + this.expression + "\"";
        }

        @Override // java.lang.Comparable
        public int compareTo(ExpressionKey other) {
            int result = this.element.toString().compareTo(other.element.toString());
            if (result == 0) {
                result = this.expression.compareTo(other.expression);
            }
            return result;
        }
    }
}