package org.thymeleaf.standard.expression;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.util.EvaluationUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/OrExpression.class */
public final class OrExpression extends BinaryOperationExpression {
    private static final long serialVersionUID = -8085738202412415337L;
    private static final Logger logger = LoggerFactory.getLogger(OrExpression.class);
    private static final String OPERATOR = "or";
    static final String[] OPERATORS = {OPERATOR};
    private static final boolean[] LENIENCIES = {false};
    private static final Class<? extends BinaryOperationExpression>[] OPERATOR_CLASSES = {OrExpression.class};
    private static final Method LEFT_ALLOWED_METHOD;
    private static final Method RIGHT_ALLOWED_METHOD;

    static {
        try {
            LEFT_ALLOWED_METHOD = OrExpression.class.getDeclaredMethod("isLeftAllowed", IStandardExpression.class);
            RIGHT_ALLOWED_METHOD = OrExpression.class.getDeclaredMethod("isRightAllowed", IStandardExpression.class);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public OrExpression(IStandardExpression left, IStandardExpression right) {
        super(left, right);
    }

    @Override // org.thymeleaf.standard.expression.Expression, org.thymeleaf.standard.expression.IStandardExpression
    public String getStringRepresentation() {
        return getStringRepresentation(OPERATOR);
    }

    static boolean isRightAllowed(IStandardExpression right) {
        return right != null && (!(right instanceof Token) || (right instanceof BooleanTokenExpression));
    }

    static boolean isLeftAllowed(IStandardExpression left) {
        return left != null && (!(left instanceof Token) || (left instanceof BooleanTokenExpression));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ExpressionParsingState composeOrExpression(ExpressionParsingState state, int inputIndex) {
        return composeBinaryOperationExpression(state, inputIndex, OPERATORS, LENIENCIES, OPERATOR_CLASSES, LEFT_ALLOWED_METHOD, RIGHT_ALLOWED_METHOD);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object executeOr(IExpressionContext context, OrExpression expression, StandardExpressionExecutionContext expContext) {
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating OR expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        Object leftValue = expression.getLeft().execute(context, expContext);
        boolean leftBooleanValue = EvaluationUtils.evaluateAsBoolean(leftValue);
        if (leftBooleanValue) {
            return Boolean.TRUE;
        }
        Object rightValue = expression.getRight().execute(context, expContext);
        boolean rightBooleanValue = EvaluationUtils.evaluateAsBoolean(rightValue);
        return Boolean.valueOf(rightBooleanValue);
    }
}