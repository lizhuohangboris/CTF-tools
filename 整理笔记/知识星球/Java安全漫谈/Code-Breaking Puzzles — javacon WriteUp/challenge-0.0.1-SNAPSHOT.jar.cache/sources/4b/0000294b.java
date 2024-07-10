package org.thymeleaf.standard.expression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/TextLiteralExpression.class */
public final class TextLiteralExpression extends SimpleExpression {
    private static final Logger logger = LoggerFactory.getLogger(TextLiteralExpression.class);
    private static final long serialVersionUID = 6511847028638506552L;
    static final char ESCAPE_PREFIX = '\\';
    static final char DELIMITER = '\'';
    private final LiteralValue value;

    public TextLiteralExpression(String value) {
        Validate.notNull(value, "Value cannot be null");
        this.value = new LiteralValue(unwrapLiteral(value));
    }

    public LiteralValue getValue() {
        return this.value;
    }

    private static String unwrapLiteral(String input) {
        int inputLen = input.length();
        if (inputLen > 1 && input.charAt(0) == '\'' && input.charAt(inputLen - 1) == '\'') {
            return unescapeLiteral(input.substring(1, inputLen - 1));
        }
        return input;
    }

    @Override // org.thymeleaf.standard.expression.Expression, org.thymeleaf.standard.expression.IStandardExpression
    public String getStringRepresentation() {
        return String.valueOf('\'') + this.value.getValue().replace(String.valueOf('\''), "\\'") + String.valueOf('\'');
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static TextLiteralExpression parseTextLiteralExpression(String input) {
        return new TextLiteralExpression(input);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object executeTextLiteralExpression(IExpressionContext context, TextLiteralExpression expression, StandardExpressionExecutionContext expContext) {
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating text literal: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        return expression.getValue();
    }

    public static String wrapStringIntoLiteral(String str) {
        if (str == null) {
            return null;
        }
        int n = str.length();
        do {
            int i = n;
            n--;
            if (i == 0) {
                return '\'' + str + '\'';
            }
        } while (str.charAt(n) != '\'');
        StringBuilder strBuilder = new StringBuilder(str.length() + 5);
        strBuilder.append('\'');
        int strLen = str.length();
        for (int i2 = 0; i2 < strLen; i2++) {
            char c = str.charAt(i2);
            if (c == '\'') {
                strBuilder.append('\\');
            }
            strBuilder.append(c);
        }
        strBuilder.append('\'');
        return strBuilder.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isDelimiterEscaped(String input, int pos) {
        if (pos == 0 || input.charAt(pos - 1) != '\\') {
            return false;
        }
        boolean odd = false;
        for (int i = pos - 1; i >= 0; i--) {
            if (input.charAt(i) == '\\') {
                odd = !odd;
            } else {
                return odd;
            }
        }
        return odd;
    }

    private static String unescapeLiteral(String text) {
        if (text == null) {
            return null;
        }
        StringBuilder strBuilder = null;
        int max = text.length();
        int readOffset = 0;
        int referenceOffset = 0;
        int i = 0;
        while (i < max) {
            char c = text.charAt(i);
            if (c == '\\' && i + 1 < max) {
                if (c == '\\') {
                    switch (text.charAt(i + 1)) {
                        case '\'':
                            c = '\'';
                            referenceOffset = i + 1;
                            break;
                        case '\\':
                            c = '\\';
                            referenceOffset = i + 1;
                            break;
                        default:
                            referenceOffset = i;
                            break;
                    }
                }
                if (strBuilder == null) {
                    strBuilder = new StringBuilder(max + 5);
                }
                if (i - readOffset > 0) {
                    strBuilder.append((CharSequence) text, readOffset, i);
                }
                i = referenceOffset;
                readOffset = i + 1;
                strBuilder.append(c);
            }
            i++;
        }
        if (strBuilder == null) {
            return text;
        }
        if (max - readOffset > 0) {
            strBuilder.append((CharSequence) text, readOffset, max);
        }
        return strBuilder.toString();
    }
}