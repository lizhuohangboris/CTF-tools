package org.hibernate.validator.internal.metadata.location;

import java.lang.reflect.Member;
import java.lang.reflect.Type;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;
import org.hibernate.validator.internal.util.TypeHelper;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/location/BeanConstraintLocation.class */
public class BeanConstraintLocation implements ConstraintLocation {
    private final Class<?> declaringClass;
    private final Type typeForValidatorResolution;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BeanConstraintLocation(Class<?> declaringClass) {
        this.declaringClass = declaringClass;
        this.typeForValidatorResolution = declaringClass.getTypeParameters().length == 0 ? declaringClass : TypeHelper.parameterizedType(declaringClass, declaringClass.getTypeParameters());
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public Class<?> getDeclaringClass() {
        return this.declaringClass;
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public Member getMember() {
        return null;
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public Type getTypeForValidatorResolution() {
        return this.typeForValidatorResolution;
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public void appendTo(ExecutableParameterNameProvider parameterNameProvider, PathImpl path) {
        path.addBeanNode();
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public Object getValue(Object parent) {
        return parent;
    }

    public String toString() {
        return "BeanConstraintLocation [declaringClass=" + this.declaringClass + ", typeForValidatorResolution=" + this.typeForValidatorResolution + "]";
    }

    public int hashCode() {
        int result = (31 * 1) + (this.declaringClass == null ? 0 : this.declaringClass.hashCode());
        return (31 * result) + (this.typeForValidatorResolution == null ? 0 : this.typeForValidatorResolution.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BeanConstraintLocation other = (BeanConstraintLocation) obj;
        if (this.declaringClass == null) {
            if (other.declaringClass != null) {
                return false;
            }
        } else if (!this.declaringClass.equals(other.declaringClass)) {
            return false;
        }
        if (this.typeForValidatorResolution == null) {
            if (other.typeForValidatorResolution != null) {
                return false;
            }
            return true;
        } else if (!this.typeForValidatorResolution.equals(other.typeForValidatorResolution)) {
            return false;
        } else {
            return true;
        }
    }
}