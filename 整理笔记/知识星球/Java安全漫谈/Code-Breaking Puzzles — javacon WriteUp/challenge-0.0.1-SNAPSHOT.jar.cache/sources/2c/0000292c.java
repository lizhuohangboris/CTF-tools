package org.thymeleaf.standard.expression;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/LiteralSubstitutionUtil.class */
final class LiteralSubstitutionUtil {
    private static final char LITERAL_SUBSTITUTION_DELIMITER = '|';

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String performLiteralSubstitution(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder strBuilder = null;
        boolean inLiteralSubstitution = false;
        boolean inLiteralSubstitutionInsertion = false;
        int expLevel = 0;
        boolean inLiteral = false;
        boolean inNothing = true;
        int inputLen = input.length();
        int i = 0;
        while (i < inputLen) {
            char c = input.charAt(i);
            if (c == '|' && !inLiteralSubstitution && inNothing) {
                if (strBuilder == null) {
                    strBuilder = new StringBuilder(inputLen + 20);
                    strBuilder.append((CharSequence) input, 0, i);
                }
                inLiteralSubstitution = true;
            } else if (c == '|' && inLiteralSubstitution && inNothing) {
                if (inLiteralSubstitutionInsertion) {
                    strBuilder.append('\'');
                    inLiteralSubstitutionInsertion = false;
                }
                inLiteralSubstitution = false;
            } else if (inNothing && ((c == '$' || c == '*' || c == '#' || c == '@') && i + 1 < inputLen && input.charAt(i + 1) == '{')) {
                if (inLiteralSubstitution && inLiteralSubstitutionInsertion) {
                    strBuilder.append("' + ");
                    inLiteralSubstitutionInsertion = false;
                } else if (inLiteralSubstitution && i > 0 && input.charAt(i - 1) == '}') {
                    strBuilder.append(" + '' + ");
                }
                if (strBuilder != null) {
                    strBuilder.append(c);
                    strBuilder.append('{');
                }
                expLevel = 1;
                i++;
                inNothing = false;
            } else if (expLevel == 1 && c == '}') {
                if (strBuilder != null) {
                    strBuilder.append('}');
                }
                expLevel = 0;
                inNothing = true;
            } else if (expLevel > 0 && c == '{') {
                if (strBuilder != null) {
                    strBuilder.append('{');
                }
                expLevel++;
            } else if (expLevel > 1 && c == '}') {
                if (strBuilder != null) {
                    strBuilder.append('}');
                }
                expLevel--;
            } else if (expLevel > 0) {
                if (strBuilder != null) {
                    strBuilder.append(c);
                }
            } else if (inNothing && !inLiteralSubstitution && c == '\'' && !TextLiteralExpression.isDelimiterEscaped(input, i)) {
                inNothing = false;
                inLiteral = true;
                if (strBuilder != null) {
                    strBuilder.append(c);
                }
            } else if (inLiteral && !inLiteralSubstitution && c == '\'' && !TextLiteralExpression.isDelimiterEscaped(input, i)) {
                inLiteral = false;
                inNothing = true;
                if (strBuilder != null) {
                    strBuilder.append(c);
                }
            } else if (inLiteralSubstitution && inNothing) {
                if (!inLiteralSubstitutionInsertion) {
                    if (input.charAt(i - 1) != '|') {
                        strBuilder.append(" + ");
                    }
                    strBuilder.append('\'');
                    inLiteralSubstitutionInsertion = true;
                }
                if (c == '\'') {
                    strBuilder.append('\\');
                } else if (c == '\\') {
                    strBuilder.append('\\');
                }
                strBuilder.append(c);
            } else if (strBuilder != null) {
                strBuilder.append(c);
            }
            i++;
        }
        if (strBuilder == null) {
            return input;
        }
        return strBuilder.toString();
    }

    private LiteralSubstitutionUtil() {
    }
}