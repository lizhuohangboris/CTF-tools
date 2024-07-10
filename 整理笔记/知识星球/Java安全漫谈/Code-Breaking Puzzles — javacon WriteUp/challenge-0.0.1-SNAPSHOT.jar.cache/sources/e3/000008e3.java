package org.apache.catalina.ssi;

import org.apache.el.parser.ELParserConstants;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/ExpressionTokenizer.class */
public class ExpressionTokenizer {
    public static final int TOKEN_STRING = 0;
    public static final int TOKEN_AND = 1;
    public static final int TOKEN_OR = 2;
    public static final int TOKEN_NOT = 3;
    public static final int TOKEN_EQ = 4;
    public static final int TOKEN_NOT_EQ = 5;
    public static final int TOKEN_RBRACE = 6;
    public static final int TOKEN_LBRACE = 7;
    public static final int TOKEN_GE = 8;
    public static final int TOKEN_LE = 9;
    public static final int TOKEN_GT = 10;
    public static final int TOKEN_LT = 11;
    public static final int TOKEN_END = 12;
    private final char[] expr;
    private String tokenVal = null;
    private int index;
    private final int length;

    public ExpressionTokenizer(String expr) {
        this.expr = expr.trim().toCharArray();
        this.length = this.expr.length;
    }

    public boolean hasMoreTokens() {
        return this.index < this.length;
    }

    public int getIndex() {
        return this.index;
    }

    protected boolean isMetaChar(char c) {
        return Character.isWhitespace(c) || c == '(' || c == ')' || c == '!' || c == '<' || c == '>' || c == '|' || c == '&' || c == '=';
    }

    public int nextToken() {
        int end;
        boolean z;
        boolean z2;
        while (this.index < this.length && Character.isWhitespace(this.expr[this.index])) {
            this.index++;
        }
        this.tokenVal = null;
        if (this.index == this.length) {
            return 12;
        }
        int start = this.index;
        char currentChar = this.expr[this.index];
        char nextChar = 0;
        this.index++;
        if (this.index < this.length) {
            nextChar = this.expr[this.index];
        }
        switch (currentChar) {
            case '!':
                if (nextChar == '=') {
                    this.index++;
                    return 5;
                }
                return 3;
            case '&':
                if (nextChar == '&') {
                    this.index++;
                    return 1;
                }
                break;
            case '(':
                return 7;
            case ')':
                return 6;
            case ELParserConstants.DIGIT /* 60 */:
                if (nextChar == '=') {
                    this.index++;
                    return 9;
                }
                return 11;
            case '=':
                return 4;
            case '>':
                if (nextChar == '=') {
                    this.index++;
                    return 8;
                }
                return 10;
            case '|':
                if (nextChar == '|') {
                    this.index++;
                    return 2;
                }
                break;
        }
        int i = this.index;
        if (currentChar == '\"' || currentChar == '\'') {
            boolean escaped = false;
            start++;
            while (this.index < this.length) {
                if (this.expr[this.index] != '\\' || escaped) {
                    if (this.expr[this.index] != currentChar || escaped) {
                        z = false;
                    } else {
                        end = this.index;
                        this.index++;
                    }
                } else {
                    z = true;
                }
                escaped = z;
                this.index++;
            }
            end = this.index;
            this.index++;
        } else if (currentChar == '/') {
            boolean escaped2 = false;
            while (this.index < this.length) {
                if (this.expr[this.index] != '\\' || escaped2) {
                    if (this.expr[this.index] != currentChar || escaped2) {
                        z2 = false;
                    } else {
                        int i2 = this.index + 1;
                        this.index = i2;
                        end = i2;
                    }
                } else {
                    z2 = true;
                }
                escaped2 = z2;
                this.index++;
            }
            int i22 = this.index + 1;
            this.index = i22;
            end = i22;
        } else {
            while (this.index < this.length && !isMetaChar(this.expr[this.index])) {
                this.index++;
            }
            end = this.index;
        }
        this.tokenVal = new String(this.expr, start, end - start);
        return 0;
    }

    public String getTokenValue() {
        return this.tokenVal;
    }
}