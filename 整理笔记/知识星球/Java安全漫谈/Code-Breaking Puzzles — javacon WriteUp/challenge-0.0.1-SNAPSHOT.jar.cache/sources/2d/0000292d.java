package org.thymeleaf.standard.expression;

import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/LiteralValue.class */
public final class LiteralValue implements Serializable {
    private static final long serialVersionUID = -4769586410724418224L;
    private final String value;

    public LiteralValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static Object unwrap(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof LiteralValue) {
            return ((LiteralValue) obj).getValue();
        }
        return obj;
    }
}