package org.thymeleaf.standard.expression;

import java.lang.reflect.Method;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/EqualsNotEqualsExpression.class */
public abstract class EqualsNotEqualsExpression extends BinaryOperationExpression {
    private static final long serialVersionUID = -8648395536336588140L;
    private static final Method LEFT_ALLOWED_METHOD;
    private static final Method RIGHT_ALLOWED_METHOD;
    protected static final String EQUALS_OPERATOR = "==";
    protected static final String NOT_EQUALS_OPERATOR = "!=";
    protected static final String EQUALS_OPERATOR_2 = "eq";
    protected static final String NOT_EQUALS_OPERATOR_2 = "neq";
    protected static final String NOT_EQUALS_OPERATOR_3 = "ne";
    static final String[] OPERATORS = {EQUALS_OPERATOR, NOT_EQUALS_OPERATOR, EQUALS_OPERATOR_2, NOT_EQUALS_OPERATOR_2, NOT_EQUALS_OPERATOR_3};
    private static final boolean[] LENIENCIES = {false, false, false, false, false};
    private static final Class<? extends BinaryOperationExpression>[] OPERATOR_CLASSES = {EqualsExpression.class, NotEqualsExpression.class, EqualsExpression.class, NotEqualsExpression.class, NotEqualsExpression.class};

    static {
        try {
            LEFT_ALLOWED_METHOD = EqualsNotEqualsExpression.class.getDeclaredMethod("isLeftAllowed", IStandardExpression.class);
            RIGHT_ALLOWED_METHOD = EqualsNotEqualsExpression.class.getDeclaredMethod("isRightAllowed", IStandardExpression.class);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public EqualsNotEqualsExpression(IStandardExpression left, IStandardExpression right) {
        super(left, right);
    }

    static boolean isRightAllowed(IStandardExpression right) {
        return true;
    }

    static boolean isLeftAllowed(IStandardExpression left) {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static ExpressionParsingState composeEqualsNotEqualsExpression(ExpressionParsingState state, int nodeIndex) {
        return composeBinaryOperationExpression(state, nodeIndex, OPERATORS, LENIENCIES, OPERATOR_CLASSES, LEFT_ALLOWED_METHOD, RIGHT_ALLOWED_METHOD);
    }
}