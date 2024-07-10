package org.thymeleaf.standard.expression;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/ExpressionSequence.class */
public final class ExpressionSequence implements Iterable<IStandardExpression>, Serializable {
    private static final long serialVersionUID = -6069208208568731809L;
    private final List<IStandardExpression> expressions;

    public ExpressionSequence(List<? extends IStandardExpression> expressions) {
        Validate.notNull(expressions, "Expression list cannot be null");
        Validate.containsNoNulls(expressions, "Expression list cannot contain any nulls");
        this.expressions = Collections.unmodifiableList(expressions);
    }

    public List<IStandardExpression> getExpressions() {
        return this.expressions;
    }

    public int size() {
        return this.expressions.size();
    }

    @Override // java.lang.Iterable
    public Iterator<IStandardExpression> iterator() {
        return this.expressions.iterator();
    }

    public String getStringRepresentation() {
        StringBuilder sb = new StringBuilder();
        if (this.expressions.size() > 0) {
            sb.append(this.expressions.get(0));
            for (int i = 1; i < this.expressions.size(); i++) {
                sb.append(',');
                sb.append(this.expressions.get(i));
            }
        }
        return sb.toString();
    }

    public String toString() {
        return getStringRepresentation();
    }
}