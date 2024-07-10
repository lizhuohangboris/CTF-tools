package org.springframework.web.servlet.mvc.condition;

import java.util.Collection;
import java.util.Iterator;
import org.springframework.beans.PropertyAccessor;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/condition/AbstractRequestCondition.class */
public abstract class AbstractRequestCondition<T extends AbstractRequestCondition<T>> implements RequestCondition<T> {
    protected abstract Collection<?> getContent();

    protected abstract String getToStringInfix();

    public boolean isEmpty() {
        return getContent().isEmpty();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return getContent().equals(((AbstractRequestCondition) other).getContent());
    }

    public int hashCode() {
        return getContent().hashCode();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(PropertyAccessor.PROPERTY_KEY_PREFIX);
        Iterator<?> iterator = getContent().iterator();
        while (iterator.hasNext()) {
            Object expression = iterator.next();
            builder.append(expression.toString());
            if (iterator.hasNext()) {
                builder.append(getToStringInfix());
            }
        }
        builder.append("]");
        return builder.toString();
    }
}