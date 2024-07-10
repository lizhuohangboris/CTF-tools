package org.springframework.validation;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/validation/ObjectError.class */
public class ObjectError extends DefaultMessageSourceResolvable {
    private final String objectName;
    @Nullable
    private transient Object source;

    public ObjectError(String objectName, String defaultMessage) {
        this(objectName, null, null, defaultMessage);
    }

    public ObjectError(String objectName, @Nullable String[] codes, @Nullable Object[] arguments, @Nullable String defaultMessage) {
        super(codes, arguments, defaultMessage);
        Assert.notNull(objectName, "Object name must not be null");
        this.objectName = objectName;
    }

    public String getObjectName() {
        return this.objectName;
    }

    public void wrap(Object source) {
        if (this.source != null) {
            throw new IllegalStateException("Already wrapping " + this.source);
        }
        this.source = source;
    }

    public <T> T unwrap(Class<T> sourceType) {
        if (sourceType.isInstance(this.source)) {
            return sourceType.cast(this.source);
        }
        if (this.source instanceof Throwable) {
            Throwable cause = ((Throwable) this.source).getCause();
            if (sourceType.isInstance(cause)) {
                return sourceType.cast(cause);
            }
        }
        throw new IllegalArgumentException("No source object of the given type available: " + sourceType);
    }

    public boolean contains(Class<?> sourceType) {
        return sourceType.isInstance(this.source) || ((this.source instanceof Throwable) && sourceType.isInstance(((Throwable) this.source).getCause()));
    }

    @Override // org.springframework.context.support.DefaultMessageSourceResolvable
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || other.getClass() != getClass() || !super.equals(other)) {
            return false;
        }
        ObjectError otherError = (ObjectError) other;
        return getObjectName().equals(otherError.getObjectName());
    }

    @Override // org.springframework.context.support.DefaultMessageSourceResolvable
    public int hashCode() {
        return (super.hashCode() * 29) + getObjectName().hashCode();
    }

    @Override // org.springframework.context.support.DefaultMessageSourceResolvable
    public String toString() {
        return "Error in object '" + this.objectName + "': " + resolvableToString();
    }
}