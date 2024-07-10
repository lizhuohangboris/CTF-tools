package org.thymeleaf.standard.expression;

import java.io.Serializable;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/Each.class */
public final class Each implements Serializable {
    private static final long serialVersionUID = -4085690403057997591L;
    private final IStandardExpression iterVar;
    private final IStandardExpression statusVar;
    private final IStandardExpression iterable;

    public Each(IStandardExpression iterVar, IStandardExpression statusVar, IStandardExpression iterable) {
        Validate.notNull(iterVar, "Iteration variable cannot be null");
        Validate.notNull(iterable, "Iterable cannot be null");
        this.iterVar = iterVar;
        this.statusVar = statusVar;
        this.iterable = iterable;
    }

    public IStandardExpression getIterVar() {
        return this.iterVar;
    }

    public boolean hasStatusVar() {
        return this.statusVar != null;
    }

    public IStandardExpression getStatusVar() {
        return this.statusVar;
    }

    public IStandardExpression getIterable() {
        return this.iterable;
    }

    public String getStringRepresentation() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.iterVar);
        if (hasStatusVar()) {
            sb.append(',');
            sb.append(this.statusVar);
        }
        sb.append(" : ");
        sb.append(this.iterable);
        return sb.toString();
    }

    public String toString() {
        return getStringRepresentation();
    }
}