package org.thymeleaf.standard.expression;

import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/NoOpToken.class */
public final class NoOpToken implements Serializable {
    private static final long serialVersionUID = -202985073804127L;
    public static final NoOpToken VALUE = new NoOpToken();

    private NoOpToken() {
    }

    public String toString() {
        return "_";
    }
}