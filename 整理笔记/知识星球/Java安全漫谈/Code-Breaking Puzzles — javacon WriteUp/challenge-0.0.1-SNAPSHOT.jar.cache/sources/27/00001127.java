package org.hibernate.validator.internal.metadata.location;

import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/location/CrossParameterConstraintLocation.class */
public class CrossParameterConstraintLocation implements ConstraintLocation {
    private final Executable executable;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CrossParameterConstraintLocation(Executable executable) {
        this.executable = executable;
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
        return Object[].class;
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public void appendTo(ExecutableParameterNameProvider parameterNameProvider, PathImpl path) {
        path.addCrossParameterNode();
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public Object getValue(Object parent) {
        return parent;
    }

    public String toString() {
        return "CrossParameterConstraintLocation [executable=" + this.executable + "]";
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
        CrossParameterConstraintLocation other = (CrossParameterConstraintLocation) obj;
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