package org.thymeleaf.standard.expression;

import java.lang.reflect.Method;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/MultiplicationDivisionRemainderExpression.class */
public abstract class MultiplicationDivisionRemainderExpression extends BinaryOperationExpression {
    private static final long serialVersionUID = -1364531602981256885L;
    protected static final String MULTIPLICATION_OPERATOR = "*";
    protected static final String DIVISION_OPERATOR = "/";
    protected static final String REMAINDER_OPERATOR = "%";
    private static final Method LEFT_ALLOWED_METHOD;
    private static final Method RIGHT_ALLOWED_METHOD;
    protected static final String DIVISION_OPERATOR_2 = "div";
    protected static final String REMAINDER_OPERATOR_2 = "mod";
    static final String[] OPERATORS = {"*", "/", DIVISION_OPERATOR_2, "%", REMAINDER_OPERATOR_2};
    private static final boolean[] LENIENCIES = {false, false, false, false, false};
    private static final Class<? extends BinaryOperationExpression>[] OPERATOR_CLASSES = {MultiplicationExpression.class, DivisionExpression.class, DivisionExpression.class, RemainderExpression.class, RemainderExpression.class};

    static {
        try {
            LEFT_ALLOWED_METHOD = MultiplicationDivisionRemainderExpression.class.getDeclaredMethod("isLeftAllowed", IStandardExpression.class);
            RIGHT_ALLOWED_METHOD = MultiplicationDivisionRemainderExpression.class.getDeclaredMethod("isRightAllowed", IStandardExpression.class);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MultiplicationDivisionRemainderExpression(IStandardExpression left, IStandardExpression right) {
        super(left, right);
    }

    static boolean isRightAllowed(IStandardExpression right) {
        return right != null && (!(right instanceof Token) || (right instanceof NumberTokenExpression)) && !(right instanceof TextLiteralExpression);
    }

    static boolean isLeftAllowed(IStandardExpression left) {
        return left != null && (!(left instanceof Token) || (left instanceof NumberTokenExpression)) && !(left instanceof TextLiteralExpression);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ExpressionParsingState composeMultiplicationDivisionRemainderExpression(ExpressionParsingState state, int nodeIndex) {
        return composeBinaryOperationExpression(state, nodeIndex, OPERATORS, LENIENCIES, OPERATOR_CLASSES, LEFT_ALLOWED_METHOD, RIGHT_ALLOWED_METHOD);
    }
}