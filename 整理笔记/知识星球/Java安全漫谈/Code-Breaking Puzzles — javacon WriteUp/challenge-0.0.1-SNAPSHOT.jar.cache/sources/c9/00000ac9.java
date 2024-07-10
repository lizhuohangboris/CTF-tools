package org.apache.el.parser;

import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/Token.class */
public class Token implements Serializable {
    private static final long serialVersionUID = 1;
    public int kind;
    public int beginLine;
    public int beginColumn;
    public int endLine;
    public int endColumn;
    public String image;
    public Token next;
    public Token specialToken;

    public Object getValue() {
        return null;
    }

    public Token() {
    }

    public Token(int kind) {
        this(kind, null);
    }

    public Token(int kind, String image) {
        this.kind = kind;
        this.image = image;
    }

    public String toString() {
        return this.image;
    }

    public static Token newToken(int ofKind, String image) {
        switch (ofKind) {
            default:
                return new Token(ofKind, image);
        }
    }

    public static Token newToken(int ofKind) {
        return newToken(ofKind, null);
    }
}