package org.thymeleaf.standard.expression;

import java.lang.reflect.Method;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/AdditionSubtractionExpression.class */
public abstract class AdditionSubtractionExpression extends BinaryOperationExpression {
    private static final long serialVersionUID = -7977102096580376925L;
    protected static final String ADDITION_OPERATOR = "+";
    protected static final String SUBTRACTION_OPERATOR = "-";
    static final String[] OPERATORS = {"+", "-"};
    private static final boolean[] LENIENCIES = {false, true};
    private static final Class<? extends BinaryOperationExpression>[] OPERATOR_CLASSES = {AdditionExpression.class, SubtractionExpression.class};
    private static final Method LEFT_ALLOWED_METHOD;
    private static final Method RIGHT_ALLOWED_METHOD;

    static {
        try {
            LEFT_ALLOWED_METHOD = AdditionSubtractionExpression.class.getDeclaredMethod("isLeftAllowed", IStandardExpression.class);
            RIGHT_ALLOWED_METHOD = AdditionSubtractionExpression.class.getDeclaredMethod("isRightAllowed", IStandardExpression.class);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AdditionSubtractionExpression(IStandardExpression left, IStandardExpression right) {
        super(left, right);
    }

    static boolean isRightAllowed(IStandardExpression right) {
        return right != null && (!(right instanceof Token) || (right instanceof NumberTokenExpression) || (right instanceof GenericTokenExpression));
    }

    static boolean isLeftAllowed(IStandardExpression left) {
        return left != null && (!(left instanceof Token) || (left instanceof NumberTokenExpression) || (left instanceof GenericTokenExpression));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ExpressionParsingState composeAdditionSubtractionExpression(ExpressionParsingState state, int nodeIndex) {
        return composeBinaryOperationExpression(state, nodeIndex, OPERATORS, LENIENCIES, OPERATOR_CLASSES, LEFT_ALLOWED_METHOD, RIGHT_ALLOWED_METHOD);
    }
}