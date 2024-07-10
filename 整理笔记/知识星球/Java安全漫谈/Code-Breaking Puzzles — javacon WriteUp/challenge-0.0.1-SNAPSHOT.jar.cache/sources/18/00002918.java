package org.thymeleaf.standard.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.thymeleaf.util.ArrayUtils;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/ExpressionParsingUtil.class */
public final class ExpressionParsingUtil {
    private static final String[] PROTECTED_TOKENS;

    static {
        List<String> protectedTokenList = new ArrayList<>(30);
        protectedTokenList.addAll(Arrays.asList(AndExpression.OPERATORS));
        protectedTokenList.addAll(Arrays.asList(EqualsNotEqualsExpression.OPERATORS));
        protectedTokenList.addAll(Arrays.asList(GreaterLesserExpression.OPERATORS));
        protectedTokenList.addAll(Arrays.asList(MultiplicationDivisionRemainderExpression.OPERATORS));
        protectedTokenList.addAll(Arrays.asList(NegationExpression.OPERATORS));
        protectedTokenList.addAll(Arrays.asList(OrExpression.OPERATORS));
        PROTECTED_TOKENS = (String[]) protectedTokenList.toArray(new String[protectedTokenList.size()]);
    }

    public static ExpressionParsingState decompose(String input) {
        ExpressionParsingState state = decomposeSimpleExpressions(LiteralSubstitutionUtil.performLiteralSubstitution(input));
        return decomposeNestingParenthesis(state, 0);
    }

    private static ExpressionParsingState decomposeSimpleExpressions(String input) {
        Expression expr;
        if (input == null) {
            return null;
        }
        ExpressionParsingState state = new ExpressionParsingState();
        if (StringUtils.isEmptyOrWhitespace(input)) {
            state.addNode(input);
            return state;
        }
        StringBuilder decomposedInput = new StringBuilder(24);
        StringBuilder currentFragment = new StringBuilder(24);
        int currentIndex = 1;
        int expLevel = 0;
        boolean inLiteral = false;
        boolean inToken = false;
        boolean inNothing = true;
        int inputLen = input.length();
        int i = 0;
        while (i < inputLen) {
            if (inToken && !Token.isTokenChar(input, i)) {
                if (finishCurrentToken(currentIndex, state, decomposedInput, currentFragment) != null) {
                    currentIndex++;
                }
                inToken = false;
                inNothing = true;
            }
            char c = input.charAt(i);
            if (inNothing && c == '\'' && !TextLiteralExpression.isDelimiterEscaped(input, i)) {
                finishCurrentFragment(decomposedInput, currentFragment);
                currentFragment.append(c);
                inLiteral = true;
                inNothing = false;
            } else if (inLiteral && c == '\'' && !TextLiteralExpression.isDelimiterEscaped(input, i)) {
                currentFragment.append(c);
                TextLiteralExpression expr2 = TextLiteralExpression.parseTextLiteralExpression(currentFragment.toString());
                int i2 = currentIndex;
                currentIndex++;
                if (addExpressionAtIndex(expr2, i2, state, decomposedInput, currentFragment) == null) {
                    return null;
                }
                inLiteral = false;
                inNothing = true;
            } else if (inLiteral) {
                currentFragment.append(c);
            } else if (inNothing && ((c == '$' || c == '*' || c == '#' || c == '@' || c == '~') && i + 1 < inputLen && input.charAt(i + 1) == '{')) {
                finishCurrentFragment(decomposedInput, currentFragment);
                currentFragment.append(c);
                currentFragment.append('{');
                i++;
                expLevel = 1;
                inNothing = false;
            } else if (expLevel == 1 && c == '}') {
                currentFragment.append('}');
                char expSelectorChar = currentFragment.charAt(0);
                switch (expSelectorChar) {
                    case '#':
                        expr = MessageExpression.parseMessageExpression(currentFragment.toString());
                        break;
                    case '$':
                        expr = VariableExpression.parseVariableExpression(currentFragment.toString());
                        break;
                    case '*':
                        expr = SelectionVariableExpression.parseSelectionVariableExpression(currentFragment.toString());
                        break;
                    case '@':
                        expr = LinkExpression.parseLinkExpression(currentFragment.toString());
                        break;
                    case '~':
                        expr = FragmentExpression.parseFragmentExpression(currentFragment.toString());
                        break;
                    default:
                        return null;
                }
                int i3 = currentIndex;
                currentIndex++;
                if (addExpressionAtIndex(expr, i3, state, decomposedInput, currentFragment) == null) {
                    return null;
                }
                expLevel = 0;
                inNothing = true;
            } else if (expLevel > 0 && c == '{') {
                expLevel++;
                currentFragment.append('{');
            } else if (expLevel > 1 && c == '}') {
                expLevel--;
                currentFragment.append('}');
            } else if (expLevel > 0) {
                currentFragment.append(c);
            } else if (inNothing && Token.isTokenChar(input, i)) {
                finishCurrentFragment(decomposedInput, currentFragment);
                currentFragment.append(c);
                inToken = true;
                inNothing = false;
            } else {
                currentFragment.append(c);
            }
            i++;
        }
        if (inLiteral || expLevel > 0) {
            return null;
        }
        if (inToken) {
            int i4 = currentIndex;
            int currentIndex2 = currentIndex + 1;
            if (finishCurrentToken(i4, state, decomposedInput, currentFragment) != null) {
                int i5 = currentIndex2 + 1;
            }
        }
        decomposedInput.append((CharSequence) currentFragment);
        state.insertNode(0, decomposedInput.toString());
        return state;
    }

    private static Expression addExpressionAtIndex(Expression expression, int index, ExpressionParsingState state, StringBuilder decomposedInput, StringBuilder currentFragment) {
        if (expression == null) {
            return null;
        }
        decomposedInput.append((char) 167);
        decomposedInput.append(String.valueOf(index));
        decomposedInput.append((char) 167);
        state.addNode(expression);
        currentFragment.setLength(0);
        return expression;
    }

    private static void finishCurrentFragment(StringBuilder decomposedInput, StringBuilder currentFragment) {
        decomposedInput.append((CharSequence) currentFragment);
        currentFragment.setLength(0);
    }

    private static Expression finishCurrentToken(int currentIndex, ExpressionParsingState state, StringBuilder decomposedInput, StringBuilder currentFragment) {
        String token = currentFragment.toString();
        Expression expr = parseAsToken(token);
        if (addExpressionAtIndex(expr, currentIndex, state, decomposedInput, currentFragment) == null) {
            decomposedInput.append((CharSequence) currentFragment);
            currentFragment.setLength(0);
            return null;
        }
        return expr;
    }

    private static Expression parseAsToken(String token) {
        if (ArrayUtils.contains(PROTECTED_TOKENS, token.toLowerCase())) {
            return null;
        }
        NumberTokenExpression numberTokenExpr = NumberTokenExpression.parseNumberTokenExpression(token);
        if (numberTokenExpr != null) {
            return numberTokenExpr;
        }
        BooleanTokenExpression booleanTokenExpr = BooleanTokenExpression.parseBooleanTokenExpression(token);
        if (booleanTokenExpr != null) {
            return booleanTokenExpr;
        }
        NullTokenExpression nullTokenExpr = NullTokenExpression.parseNullTokenExpression(token);
        if (nullTokenExpr != null) {
            return nullTokenExpr;
        }
        NoOpTokenExpression noOpTokenExpr = NoOpTokenExpression.parseNoOpTokenExpression(token);
        if (noOpTokenExpr != null) {
            return noOpTokenExpr;
        }
        GenericTokenExpression genericTokenExpr = GenericTokenExpression.parseGenericTokenExpression(token);
        if (genericTokenExpr != null) {
            return genericTokenExpr;
        }
        return null;
    }

    public static ExpressionParsingState unnest(ExpressionParsingState state) {
        Validate.notNull(state, "Parsing state cannot be null");
        return decomposeNestingParenthesis(state, 0);
    }

    private static ExpressionParsingState decomposeNestingParenthesis(ExpressionParsingState state, int nodeIndex) {
        if (state == null || nodeIndex >= state.size()) {
            return null;
        }
        if (state.hasExpressionAt(nodeIndex)) {
            return state;
        }
        String input = state.get(nodeIndex).getInput();
        StringBuilder decomposedString = new StringBuilder(24);
        StringBuilder currentFragment = new StringBuilder(24);
        int currentIndex = state.size();
        List<Integer> nestedInputs = new ArrayList<>(6);
        int parLevel = 0;
        int inputLen = input.length();
        for (int i = 0; i < inputLen; i++) {
            char c = input.charAt(i);
            if (c == '(') {
                if (parLevel == 0) {
                    decomposedString.append((CharSequence) currentFragment);
                    currentFragment.setLength(0);
                } else {
                    currentFragment.append('(');
                }
                parLevel++;
            } else if (c == ')') {
                parLevel--;
                if (parLevel < 0) {
                    return null;
                }
                if (parLevel == 0) {
                    int nestedIndex = currentIndex;
                    currentIndex++;
                    nestedInputs.add(Integer.valueOf(nestedIndex));
                    decomposedString.append((char) 167);
                    decomposedString.append(String.valueOf(nestedIndex));
                    decomposedString.append((char) 167);
                    state.addNode(currentFragment.toString());
                    currentFragment.setLength(0);
                } else {
                    currentFragment.append(')');
                }
            } else {
                currentFragment.append(c);
            }
        }
        if (parLevel > 0) {
            return null;
        }
        decomposedString.append((CharSequence) currentFragment);
        state.setNode(nodeIndex, decomposedString.toString());
        for (Integer nestedInput : nestedInputs) {
            if (decomposeNestingParenthesis(state, nestedInput.intValue()) == null) {
                return null;
            }
        }
        return state;
    }

    public static ExpressionParsingState compose(ExpressionParsingState state) {
        return compose(state, 0);
    }

    static ExpressionParsingState compose(ExpressionParsingState state, int nodeIndex) {
        if (state == null || nodeIndex >= state.size()) {
            return null;
        }
        if (state.hasExpressionAt(nodeIndex)) {
            return state;
        }
        String input = state.get(nodeIndex).getInput();
        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }
        int parsedIndex = parseAsSimpleIndexPlaceholder(input);
        if (parsedIndex != -1) {
            if (compose(state, parsedIndex) == null || !state.hasExpressionAt(parsedIndex)) {
                return null;
            }
            state.setNode(nodeIndex, state.get(parsedIndex).getExpression());
            return state;
        } else if (ConditionalExpression.composeConditionalExpression(state, nodeIndex) == null) {
            return null;
        } else {
            if (state.hasExpressionAt(nodeIndex)) {
                return state;
            }
            if (DefaultExpression.composeDefaultExpression(state, nodeIndex) == null) {
                return null;
            }
            if (state.hasExpressionAt(nodeIndex)) {
                return state;
            }
            if (OrExpression.composeOrExpression(state, nodeIndex) == null) {
                return null;
            }
            if (state.hasExpressionAt(nodeIndex)) {
                return state;
            }
            if (AndExpression.composeAndExpression(state, nodeIndex) == null) {
                return null;
            }
            if (state.hasExpressionAt(nodeIndex)) {
                return state;
            }
            if (EqualsNotEqualsExpression.composeEqualsNotEqualsExpression(state, nodeIndex) == null) {
                return null;
            }
            if (state.hasExpressionAt(nodeIndex)) {
                return state;
            }
            if (GreaterLesserExpression.composeGreaterLesserExpression(state, nodeIndex) == null) {
                return null;
            }
            if (state.hasExpressionAt(nodeIndex)) {
                return state;
            }
            if (AdditionSubtractionExpression.composeAdditionSubtractionExpression(state, nodeIndex) == null) {
                return null;
            }
            if (state.hasExpressionAt(nodeIndex)) {
                return state;
            }
            if (MultiplicationDivisionRemainderExpression.composeMultiplicationDivisionRemainderExpression(state, nodeIndex) == null) {
                return null;
            }
            if (state.hasExpressionAt(nodeIndex)) {
                return state;
            }
            if (MinusExpression.composeMinusExpression(state, nodeIndex) == null) {
                return null;
            }
            if (state.hasExpressionAt(nodeIndex)) {
                return state;
            }
            if (NegationExpression.composeNegationExpression(state, nodeIndex) != null && state.hasExpressionAt(nodeIndex)) {
                return state;
            }
            return null;
        }
    }

    public static int parseAsSimpleIndexPlaceholder(String placeholder) {
        String str = placeholder.trim();
        int strLen = str.length();
        if (strLen <= 2 || str.charAt(0) != 167 || str.charAt(strLen - 1) != 167) {
            return -1;
        }
        for (int i = 1; i < strLen - 1; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return -1;
            }
        }
        return Integer.parseInt(str.substring(1, strLen - 1));
    }

    public static Expression parseAndCompose(ExpressionParsingState state, String parseTarget) {
        int index = parseAsSimpleIndexPlaceholder(parseTarget);
        if (index == -1) {
            index = state.size();
            state.addNode(parseTarget);
        }
        if (compose(state, index) == null || !state.hasExpressionAt(index)) {
            return null;
        }
        return state.get(index).getExpression();
    }

    private ExpressionParsingUtil() {
    }
}