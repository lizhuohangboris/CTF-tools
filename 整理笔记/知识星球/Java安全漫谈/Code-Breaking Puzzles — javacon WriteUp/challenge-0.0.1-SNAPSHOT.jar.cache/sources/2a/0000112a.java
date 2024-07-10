package org.hibernate.validator.internal.metadata.location;

import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;
import org.hibernate.validator.internal.util.ReflectionHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/location/ParameterConstraintLocation.class */
public class ParameterConstraintLocation implements ConstraintLocation {
    private final Executable executable;
    private final int index;
    private final Type typeForValidatorResolution;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ParameterConstraintLocation(Executable executable, int index) {
        this.executable = executable;
        this.index = index;
        this.typeForValidatorResolution = ReflectionHelper.boxedType(ReflectionHelper.typeOf(executable, index));
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

    public int getIndex() {
        return this.index;
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public void appendTo(ExecutableParameterNameProvider parameterNameProvider, PathImpl path) {
        String name = parameterNameProvider.getParameterNames(this.executable).get(this.index);
        path.addParameterNode(name, this.index);
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public Object getValue(Object parent) {
        return ((Object[]) parent)[this.index];
    }

    public String toString() {
        return "ParameterConstraintLocation [executable=" + this.executable + ", index=" + this.index + ", typeForValidatorResolution=" + this.typeForValidatorResolution + "]";
    }

    public int hashCode() {
        int result = (31 * 1) + (this.executable == null ? 0 : this.executable.hashCode());
        return (31 * result) + this.index;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ParameterConstraintLocation other = (ParameterConstraintLocation) obj;
        if (this.executable == null) {
            if (other.executable != null) {
                return false;
            }
        } else if (!this.executable.equals(other.executable)) {
            return false;
        }
        if (this.index != other.index) {
            return false;
        }
        return true;
    }
}