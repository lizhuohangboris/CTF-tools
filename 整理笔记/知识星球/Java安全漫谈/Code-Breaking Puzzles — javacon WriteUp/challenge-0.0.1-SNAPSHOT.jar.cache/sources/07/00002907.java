package org.thymeleaf.standard.expression;

import java.io.Serializable;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/Assignation.class */
public final class Assignation implements Serializable {
    private static final long serialVersionUID = -20278893925937213L;
    private final IStandardExpression left;
    private final IStandardExpression right;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Assignation(IStandardExpression left, IStandardExpression right) {
        Validate.notNull(left, "Assignation left side cannot be null");
        this.left = left;
        this.right = right;
    }

    public IStandardExpression getLeft() {
        return this.left;
    }

    public IStandardExpression getRight() {
        return this.right;
    }

    public String getStringRepresentation() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(this.left.getStringRepresentation());
        if (this.right != null) {
            strBuilder.append('=');
            if (this.right instanceof ComplexExpression) {
                strBuilder.append('(');
                strBuilder.append(this.right.getStringRepresentation());
                strBuilder.append(')');
            } else {
                strBuilder.append(this.right.getStringRepresentation());
            }
        }
        return strBuilder.toString();
    }

    public String toString() {
        return getStringRepresentation();
    }
}