package org.hibernate.validator.internal.metadata.location;

import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.StringHelper;
import org.hibernate.validator.internal.util.TypeHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/location/TypeArgumentConstraintLocation.class */
public class TypeArgumentConstraintLocation implements ConstraintLocation {
    private final ConstraintLocation delegate;
    private final TypeVariable<?> typeParameter;
    private final Type typeForValidatorResolution;
    private final Class<?> containerClass;
    private final ConstraintLocation outerDelegate;
    private final int hashCode;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TypeArgumentConstraintLocation(ConstraintLocation delegate, TypeVariable<?> typeParameter, Type typeOfAnnotatedElement) {
        this.delegate = delegate;
        this.typeParameter = typeParameter;
        this.typeForValidatorResolution = ReflectionHelper.boxedType(typeOfAnnotatedElement);
        this.containerClass = TypeHelper.getErasedReferenceType(delegate.getTypeForValidatorResolution());
        ConstraintLocation constraintLocation = delegate;
        while (true) {
            ConstraintLocation outerDelegate = constraintLocation;
            if (outerDelegate instanceof TypeArgumentConstraintLocation) {
                constraintLocation = ((TypeArgumentConstraintLocation) outerDelegate).delegate;
            } else {
                this.outerDelegate = outerDelegate;
                this.hashCode = buildHashCode(delegate, typeParameter);
                return;
            }
        }
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public Class<?> getDeclaringClass() {
        return this.delegate.getDeclaringClass();
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public Member getMember() {
        return this.delegate.getMember();
    }

    public TypeVariable<?> getTypeParameter() {
        return this.typeParameter;
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public Type getTypeForValidatorResolution() {
        return this.typeForValidatorResolution;
    }

    public Class<?> getContainerClass() {
        return this.containerClass;
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public void appendTo(ExecutableParameterNameProvider parameterNameProvider, PathImpl path) {
        this.delegate.appendTo(parameterNameProvider, path);
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public Object getValue(Object parent) {
        return this.delegate.getValue(parent);
    }

    public ConstraintLocation getDelegate() {
        return this.delegate;
    }

    public ConstraintLocation getOuterDelegate() {
        return this.outerDelegate;
    }

    public String toString() {
        return "TypeArgumentValueConstraintLocation [typeForValidatorResolution=" + StringHelper.toShortString(this.typeForValidatorResolution) + ", delegate=" + this.delegate + "]";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TypeArgumentConstraintLocation that = (TypeArgumentConstraintLocation) o;
        if (!this.typeParameter.equals(that.typeParameter) || !this.delegate.equals(that.delegate)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.hashCode;
    }

    private static int buildHashCode(ConstraintLocation delegate, TypeVariable<?> typeParameter) {
        int result = delegate.hashCode();
        return (31 * result) + typeParameter.hashCode();
    }
}