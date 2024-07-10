package org.thymeleaf.standard.expression;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.util.EvaluationUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/AndExpression.class */
public final class AndExpression extends BinaryOperationExpression {
    private static final long serialVersionUID = -6085038102412415337L;
    private static final Logger logger = LoggerFactory.getLogger(AndExpression.class);
    private static final String OPERATOR = "and";
    static final String[] OPERATORS = {OPERATOR};
    private static final boolean[] LENIENCIES = {false};
    private static final Class<? extends BinaryOperationExpression>[] OPERATOR_CLASSES = {AndExpression.class};
    private static final Method LEFT_ALLOWED_METHOD;
    private static final Method RIGHT_ALLOWED_METHOD;

    static {
        try {
            LEFT_ALLOWED_METHOD = AndExpression.class.getDeclaredMethod("isLeftAllowed", IStandardExpression.class);
            RIGHT_ALLOWED_METHOD = AndExpression.class.getDeclaredMethod("isRightAllowed", IStandardExpression.class);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public AndExpression(IStandardExpression left, IStandardExpression right) {
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
    public static ExpressionParsingState composeAndExpression(ExpressionParsingState state, int nodeIndex) {
        return composeBinaryOperationExpression(state, nodeIndex, OPERATORS, LENIENCIES, OPERATOR_CLASSES, LEFT_ALLOWED_METHOD, RIGHT_ALLOWED_METHOD);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object executeAnd(IExpressionContext context, AndExpression expression, StandardExpressionExecutionContext expContext) {
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating AND expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        Object leftValue = expression.getLeft().execute(context, expContext);
        boolean leftBooleanValue = EvaluationUtils.evaluateAsBoolean(leftValue);
        if (!leftBooleanValue) {
            return Boolean.FALSE;
        }
        Object rightValue = expression.getRight().execute(context, expContext);
        boolean rightBooleanValue = EvaluationUtils.evaluateAsBoolean(rightValue);
        return Boolean.valueOf(rightBooleanValue);
    }
}