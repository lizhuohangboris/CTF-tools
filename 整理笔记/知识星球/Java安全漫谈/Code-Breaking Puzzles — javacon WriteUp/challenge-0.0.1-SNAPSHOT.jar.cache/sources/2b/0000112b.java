package org.hibernate.validator.internal.metadata.location;

import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;
import org.hibernate.validator.internal.util.ReflectionHelper;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/location/ReturnValueConstraintLocation.class */
public class ReturnValueConstraintLocation implements ConstraintLocation {
    private final Executable executable;
    private final Type typeForValidatorResolution;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReturnValueConstraintLocation(Executable executable) {
        this.executable = executable;
        this.typeForValidatorResolution = ReflectionHelper.boxedType(ReflectionHelper.typeOf(executable));
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public Class<?> getDeclaringClass() {
        return this.executable.getDeclaringClass();
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public Member getMember() {
        return this.executable;
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public Type getTypeForValidatorResolution() {
        return this.typeForValidatorResolution;
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public void appendTo(ExecutableParameterNameProvider parameterNameProvider, PathImpl path) {
        path.addReturnValueNode();
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public Object getValue(Object parent) {
        return parent;
    }

    public String toString() {
        return "ReturnValueConstraintLocation [executable=" + this.executable + "]";
    }

    public int hashCode() {
        int result = (31 * 1) + (this.executable == null ? 0 : this.executable.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ReturnValueConstraintLocation other = (ReturnValueConstraintLocation) obj;
        if (this.executable == null) {
            if (other.executable != null) {
                return false;
            }
            return true;
        } else if (!this.executable.equals(other.executable)) {
            return false;
        } else {
            return true;
        }
    }
}