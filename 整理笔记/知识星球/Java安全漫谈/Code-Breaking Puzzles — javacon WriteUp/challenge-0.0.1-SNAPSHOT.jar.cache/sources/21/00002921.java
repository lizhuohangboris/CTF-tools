package org.thymeleaf.standard.expression;

import java.lang.reflect.Method;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/GreaterLesserExpression.class */
public abstract class GreaterLesserExpression extends BinaryOperationExpression {
    private static final long serialVersionUID = 3488922833645278122L;
    protected static final String GREATER_THAN_OPERATOR = ">";
    protected static final String GREATER_OR_EQUAL_TO_OPERATOR = ">=";
    protected static final String LESS_THAN_OPERATOR = "<";
    protected static final String LESS_OR_EQUAL_TO_OPERATOR = "<=";
    protected static final String GREATER_THAN_OPERATOR_2 = "gt";
    protected static final String GREATER_OR_EQUAL_TO_OPERATOR_2 = "ge";
    protected static final String LESS_THAN_OPERATOR_2 = "lt";
    protected static final String LESS_OR_EQUAL_TO_OPERATOR_2 = "le";
    static final String[] OPERATORS = {GREATER_THAN_OPERATOR, GREATER_OR_EQUAL_TO_OPERATOR, LESS_THAN_OPERATOR, LESS_OR_EQUAL_TO_OPERATOR, GREATER_THAN_OPERATOR_2, GREATER_OR_EQUAL_TO_OPERATOR_2, LESS_THAN_OPERATOR_2, LESS_OR_EQUAL_TO_OPERATOR_2};
    private static final boolean[] LENIENCIES = {false, false, false, false, false, false, false, false};
    private static final Class<? extends BinaryOperationExpression>[] OPERATOR_CLASSES = {GreaterThanExpression.class, GreaterOrEqualToExpression.class, LessThanExpression.class, LessOrEqualToExpression.class, GreaterThanExpression.class, GreaterOrEqualToExpression.class, LessThanExpression.class, LessOrEqualToExpression.class};
    private static final Method LEFT_ALLOWED_METHOD;
    private static final Method RIGHT_ALLOWED_METHOD;

    static {
        try {
            LEFT_ALLOWED_METHOD = GreaterLesserExpression.class.getDeclaredMethod("isLeftAllowed", IStandardExpression.class);
            RIGHT_ALLOWED_METHOD = GreaterLesserExpression.class.getDeclaredMethod("isRightAllowed", IStandardExpression.class);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public GreaterLesserExpression(IStandardExpression left, IStandardExpression right) {
        super(left, right);
    }

    static boolean isRightAllowed(IStandardExpression right) {
        return right != null && (!(right instanceof Token) || (right instanceof NumberTokenExpression) || (right instanceof GenericTokenExpression));
    }

    static boolean isLeftAllowed(IStandardExpression left) {
        return left != null && (!(left instanceof Token) || (left instanceof NumberTokenExpression) || (left instanceof GenericTokenExpression));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static ExpressionParsingState composeGreaterLesserExpression(ExpressionParsingState state, int nodeIndex) {
        return composeBinaryOperationExpression(state, nodeIndex, OPERATORS, LENIENCIES, OPERATOR_CLASSES, LEFT_ALLOWED_METHOD, RIGHT_ALLOWED_METHOD);
    }
}