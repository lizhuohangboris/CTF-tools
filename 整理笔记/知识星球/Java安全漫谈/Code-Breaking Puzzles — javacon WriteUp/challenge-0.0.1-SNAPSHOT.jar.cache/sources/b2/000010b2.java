package org.hibernate.validator.internal.engine.resolver;

import javax.validation.Path;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/resolver/AbstractTraversableHolder.class */
abstract class AbstractTraversableHolder {
    private final Object traversableObject;
    private final Path.Node traversableProperty;
    private final int hashCode = buildHashCode();

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractTraversableHolder(Object traversableObject, Path.Node traversableProperty) {
        this.traversableObject = traversableObject;
        this.traversableProperty = traversableProperty;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof AbstractTraversableHolder)) {
            return false;
        }
        AbstractTraversableHolder that = (AbstractTraversableHolder) o;
        if (this.traversableObject != null) {
            if (this.traversableObject != that.traversableObject) {
                return false;
            }
        } else if (that.traversableObject != null) {
            return false;
        }
        if (!this.traversableProperty.equals(that.traversableProperty)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.hashCode;
    }

    public int buildHashCode() {
        int result = this.traversableObject != null ? System.identityHashCode(this.traversableObject) : 0;
        return (31 * result) + this.traversableProperty.hashCode();
    }
}