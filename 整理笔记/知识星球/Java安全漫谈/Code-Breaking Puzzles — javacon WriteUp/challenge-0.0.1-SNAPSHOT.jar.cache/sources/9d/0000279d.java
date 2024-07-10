package org.thymeleaf.cache;

import java.io.Serializable;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/cache/ExpressionCacheKey.class */
public final class ExpressionCacheKey implements Serializable {
    private static final long serialVersionUID = 872451230923L;
    private final String type;
    private final String expression0;
    private final String expression1;
    private final int h;

    public ExpressionCacheKey(String type, String expression0) {
        this(type, expression0, null);
    }

    public ExpressionCacheKey(String type, String expression0, String expression1) {
        Validate.notNull(type, "Type cannot be null");
        Validate.notNull(expression0, "Expression cannot be null");
        this.type = type;
        this.expression0 = expression0;
        this.expression1 = expression1;
        this.h = computeHashCode();
    }

    public String getType() {
        return this.type;
    }

    public String getExpression0() {
        return this.expression0;
    }

    public String getExpression1() {
        return this.expression1;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExpressionCacheKey)) {
            return false;
        }
        ExpressionCacheKey that = (ExpressionCacheKey) o;
        if (this.h == that.h && this.type.equals(that.type) && this.expression0.equals(that.expression0)) {
            return this.expression1 != null ? this.expression1.equals(that.expression1) : that.expression1 == null;
        }
        return false;
    }

    public int hashCode() {
        return this.h;
    }

    private int computeHashCode() {
        int result = this.type.hashCode();
        return (31 * ((31 * result) + this.expression0.hashCode())) + (this.expression1 != null ? this.expression1.hashCode() : 0);
    }

    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(this.type);
        strBuilder.append('|');
        strBuilder.append(this.expression0);
        if (this.expression1 != null) {
            strBuilder.append('|');
            strBuilder.append(this.expression1);
        }
        return strBuilder.toString();
    }
}