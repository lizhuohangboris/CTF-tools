package org.thymeleaf.standard.expression;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/Token.class */
public abstract class Token extends SimpleExpression {
    private static final long serialVersionUID = 4357087922344497120L;
    private final Object value;

    /* JADX INFO: Access modifiers changed from: protected */
    public Token(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    @Override // org.thymeleaf.standard.expression.Expression, org.thymeleaf.standard.expression.IStandardExpression
    public String getStringRepresentation() {
        return this.value.toString();
    }

    @Override // org.thymeleaf.standard.expression.Expression
    public String toString() {
        return getStringRepresentation();
    }

    public static boolean isTokenChar(String context, int pos) {
        char c = context.charAt(pos);
        if (c >= 'a' && c <= 'z') {
            return true;
        }
        if (c >= 'A' && c <= 'Z') {
            return true;
        }
        if (c >= '0' && c <= '9') {
            return true;
        }
        if (c == ' ' || c == '\n' || c == '(' || c == ')' || c == '\'' || c == '\"' || c == '<' || c == '>' || c == '{' || c == '}' || c == '=' || c == ',' || c == ';' || c == ':' || c == '+' || c == '*' || c == '$' || c == '%' || c == '&' || c == '#') {
            return false;
        }
        if (c == '[' || c == ']' || c == '.' || c == '_') {
            return true;
        }
        if (c == '-') {
            if (pos > 0) {
                for (int i = pos - 1; i >= 0 && isTokenChar(context, i); i--) {
                    char cc = context.charAt(i);
                    if ((cc < '0' || cc > '9') && cc != '.') {
                        return true;
                    }
                }
            }
            int contextLen = context.length();
            if (pos + 1 < contextLen) {
                for (int i2 = pos + 1; i2 < contextLen; i2++) {
                    char cc2 = context.charAt(i2);
                    if (cc2 == '-') {
                        return true;
                    }
                    if (isTokenChar(context, i2)) {
                        if ((cc2 < '0' || cc2 > '9') && cc2 != '.') {
                            return true;
                        }
                    } else {
                        return false;
                    }
                }
                return false;
            }
            return false;
        } else if (c == 183) {
            return true;
        } else {
            if (c >= 192 && c <= 214) {
                return true;
            }
            if (c >= 216 && c <= 246) {
                return true;
            }
            if (c >= 248 && c <= 767) {
                return true;
            }
            if (c >= 768 && c <= 879) {
                return true;
            }
            if (c >= 880 && c <= 893) {
                return true;
            }
            if (c >= 895 && c <= 8191) {
                return true;
            }
            if (c >= 8204 && c <= 8205) {
                return true;
            }
            if (c >= 8255 && c <= 8256) {
                return true;
            }
            if (c >= 8304 && c <= 8591) {
                return true;
            }
            if (c >= 11264 && c <= 12271) {
                return true;
            }
            if (c >= 12289 && c <= 55295) {
                return true;
            }
            if (c >= 63744 && c <= 64975) {
                return true;
            }
            if (c < 65008 || c > 65533) {
                return c >= 65008 && c <= 65533;
            }
            return true;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/Token$TokenParsingTracer.class */
    public static final class TokenParsingTracer {
        public static final char TOKEN_SUBSTITUTE = '#';

        private TokenParsingTracer() {
        }

        public static String trace(String input) {
            int inputLen = input.length();
            StringBuilder strBuilder = new StringBuilder(inputLen + 1);
            for (int i = 0; i < inputLen; i++) {
                if (Token.isTokenChar(input, i)) {
                    strBuilder.append('#');
                } else {
                    strBuilder.append(input.charAt(i));
                }
            }
            return strBuilder.toString();
        }
    }
}