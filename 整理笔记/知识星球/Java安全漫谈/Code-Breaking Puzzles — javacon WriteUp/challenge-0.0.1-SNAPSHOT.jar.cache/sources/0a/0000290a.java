package org.thymeleaf.standard.expression;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/BinaryOperationExpression.class */
public abstract class BinaryOperationExpression extends ComplexExpression {
    private static final long serialVersionUID = 7524261639178859585L;
    private final IStandardExpression left;
    private final IStandardExpression right;

    /* JADX INFO: Access modifiers changed from: protected */
    public BinaryOperationExpression(IStandardExpression left, IStandardExpression right) {
        Validate.notNull(left, "Left-side expression cannot be null");
        Validate.notNull(right, "Right-side expression cannot be null");
        this.left = left;
        this.right = right;
    }

    public IStandardExpression getLeft() {
        return this.left;
    }

    public IStandardExpression getRight() {
        return this.right;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getStringRepresentation(String operator) {
        StringBuilder sb = new StringBuilder();
        if (this.left instanceof ComplexExpression) {
            sb.append('(');
            sb.append(this.left);
            sb.append(')');
        } else {
            sb.append(this.left);
        }
        sb.append(' ');
        sb.append(operator);
        sb.append(' ');
        if (this.right instanceof ComplexExpression) {
            sb.append('(');
            sb.append(this.right);
            sb.append(')');
        } else {
            sb.append(this.right);
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static ExpressionParsingState composeBinaryOperationExpression(ExpressionParsingState state, int nodeIndex, String[] operators, boolean[] leniencies, Class<? extends BinaryOperationExpression>[] operationClasses, Method leftAllowedMethod, Method rightAllowedMethod) {
        String input = state.get(nodeIndex).getInput();
        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }
        String lowerCase = input.toLowerCase();
        while (true) {
            String scannedInput = lowerCase;
            int operatorIndex = -1;
            int operatorPosFrom = -1;
            int operatorPosTo = Integer.MAX_VALUE;
            int operatorLen = 0;
            for (int i = 0; i < operators.length; i++) {
                int currentOperatorPosFrom = scannedInput.lastIndexOf(operators[i]);
                if (currentOperatorPosFrom != -1) {
                    int currentOperatorLen = operators[i].length();
                    int currentOperatorPosTo = currentOperatorPosFrom + currentOperatorLen;
                    if (operatorPosFrom == -1 || operatorPosTo < currentOperatorPosFrom || (currentOperatorLen > operatorLen && currentOperatorPosTo >= operatorPosTo)) {
                        operatorPosFrom = currentOperatorPosFrom;
                        operatorLen = operators[i].length();
                        operatorPosTo = currentOperatorPosFrom + operatorLen;
                        operatorIndex = i;
                    }
                }
            }
            if (operatorPosFrom == -1) {
                return state;
            }
            if (doComposeBinaryOperationExpression(state, nodeIndex, operators[operatorIndex], operationClasses[operatorIndex], leftAllowedMethod, rightAllowedMethod, input, operatorPosFrom) == null) {
                if (leniencies[operatorIndex]) {
                    lowerCase = scannedInput.substring(0, operatorPosFrom);
                } else {
                    return null;
                }
            } else {
                return state;
            }
        }
    }

    private static ExpressionParsingState doComposeBinaryOperationExpression(ExpressionParsingState state, int nodeIndex, String operator, Class<? extends BinaryOperationExpression> operationClass, Method leftAllowedMethod, Method rightAllowedMethod, String input, int operatorPos) {
        String leftStr = input.substring(0, operatorPos).trim();
        String rightStr = input.substring(operatorPos + operator.length()).trim();
        if (leftStr.length() == 0 || rightStr.length() == 0) {
            return null;
        }
        Expression leftExpr = ExpressionParsingUtil.parseAndCompose(state, leftStr);
        if (leftExpr == null) {
            return null;
        }
        try {
            if (!((Boolean) leftAllowedMethod.invoke(null, leftExpr)).booleanValue()) {
                return null;
            }
            Expression rightExpr = ExpressionParsingUtil.parseAndCompose(state, rightStr);
            if (rightExpr == null) {
                return null;
            }
            try {
                if (!((Boolean) rightAllowedMethod.invoke(null, rightExpr)).booleanValue()) {
                    return null;
                }
                try {
                    BinaryOperationExpression operationExpression = operationClass.getDeclaredConstructor(IStandardExpression.class, IStandardExpression.class).newInstance(leftExpr, rightExpr);
                    state.setNode(nodeIndex, operationExpression);
                    return state;
                } catch (TemplateProcessingException e) {
                    throw e;
                } catch (Exception e2) {
                    throw new TemplateProcessingException("Error during creation of Binary Operation expression for operator: \"" + operator + "\"", e2);
                }
            } catch (IllegalAccessException e3) {
                throw new TemplateProcessingException("Error invoking operand validation in binary operation", e3);
            } catch (InvocationTargetException e4) {
                throw new TemplateProcessingException("Error invoking operand validation in binary operation", e4);
            }
        } catch (IllegalAccessException e5) {
            throw new TemplateProcessingException("Error invoking operand validation in binary operation", e5);
        } catch (InvocationTargetException e6) {
            throw new TemplateProcessingException("Error invoking operand validation in binary operation", e6);
        }
    }
}