package org.thymeleaf.standard.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.thymeleaf.context.IExpressionContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/StandardExpressionPreprocessor.class */
public final class StandardExpressionPreprocessor {
    private static final char PREPROCESS_DELIMITER = '_';
    private static final String PREPROCESS_EVAL = "\\_\\_(.*?)\\_\\_";
    private static final Pattern PREPROCESS_EVAL_PATTERN = Pattern.compile(PREPROCESS_EVAL, 32);

    public static String preprocess(IExpressionContext context, String input) {
        if (input.indexOf(95) == -1) {
            return input;
        }
        IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());
        if (!(expressionParser instanceof StandardExpressionParser)) {
            return input;
        }
        Matcher matcher = PREPROCESS_EVAL_PATTERN.matcher(input);
        if (matcher.find()) {
            StringBuilder strBuilder = new StringBuilder(input.length() + 24);
            int curr = 0;
            do {
                String previousText = checkPreprocessingMarkUnescaping(input.substring(curr, matcher.start(0)));
                String expressionText = checkPreprocessingMarkUnescaping(matcher.group(1));
                strBuilder.append(previousText);
                IStandardExpression expression = StandardExpressionParser.parseExpression(context, expressionText, false);
                if (expression == null) {
                    return null;
                }
                Object result = expression.execute(context, StandardExpressionExecutionContext.RESTRICTED);
                strBuilder.append(result);
                curr = matcher.end(0);
            } while (matcher.find());
            String remaining = checkPreprocessingMarkUnescaping(input.substring(curr));
            strBuilder.append(remaining);
            return strBuilder.toString().trim();
        }
        return checkPreprocessingMarkUnescaping(input);
    }

    private static String checkPreprocessingMarkUnescaping(String input) {
        byte b;
        boolean structureFound = false;
        byte state = 0;
        int inputLen = input.length();
        int i = 0;
        while (true) {
            if (i >= inputLen) {
                break;
            }
            char c = input.charAt(i);
            if (c == '\\' && (state == 0 || state == 2)) {
                b = (byte) (state + 1);
            } else if (c == '_' && state == 1) {
                b = (byte) (state + 1);
            } else if (c == '_' && state == 3) {
                structureFound = true;
                break;
            } else {
                b = 0;
            }
            state = b;
            i++;
        }
        if (!structureFound) {
            return input;
        }
        byte state2 = 0;
        StringBuilder strBuilder = new StringBuilder(inputLen + 6);
        for (int i2 = 0; i2 < inputLen; i2++) {
            char c2 = input.charAt(i2);
            if (c2 == '\\' && (state2 == 0 || state2 == 2)) {
                state2 = (byte) (state2 + 1);
                strBuilder.append('\\');
            } else if (c2 == '_' && state2 == 1) {
                state2 = (byte) (state2 + 1);
                strBuilder.append('_');
            } else if (c2 == '_' && state2 == 3) {
                state2 = 0;
                int builderLen = strBuilder.length();
                strBuilder.delete(builderLen - 3, builderLen);
                strBuilder.append("__");
            } else {
                state2 = 0;
                strBuilder.append(c2);
            }
        }
        return strBuilder.toString();
    }

    private StandardExpressionPreprocessor() {
    }
}