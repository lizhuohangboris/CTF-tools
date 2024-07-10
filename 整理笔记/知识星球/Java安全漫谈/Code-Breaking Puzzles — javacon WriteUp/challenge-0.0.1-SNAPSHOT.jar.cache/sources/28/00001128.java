package org.hibernate.validator.internal.metadata.location;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.hibernate.validator.HibernateValidatorPermission;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.StringHelper;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredField;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/location/FieldConstraintLocation.class */
public class FieldConstraintLocation implements ConstraintLocation {
    private final Field field;
    private final Field accessibleField;
    private final String propertyName;
    private final Type typeForValidatorResolution;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FieldConstraintLocation(Field field) {
        this.field = field;
        this.accessibleField = getAccessible(field);
        this.propertyName = ReflectionHelper.getPropertyName(field);
        this.typeForValidatorResolution = ReflectionHelper.boxedType(ReflectionHelper.typeOf(field));
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public Class<?> getDeclaringClass() {
        return this.field.getDeclaringClass();
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public Member getMember() {
        return this.field;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public Type getTypeForValidatorResolution() {
        return this.typeForValidatorResolution;
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public void appendTo(ExecutableParameterNameProvider parameterNameProvider, PathImpl path) {
        path.addPropertyNode(this.propertyName);
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public Object getValue(Object parent) {
        return ReflectionHelper.getValue(this.accessibleField, parent);
    }

    public String toString() {
        return "FieldConstraintLocation [member=" + StringHelper.toShortString((Member) this.field) + ", typeForValidatorResolution=" + StringHelper.toShortString(this.typeForValidatorResolution) + "]";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FieldConstraintLocation that = (FieldConstraintLocation) o;
        if (this.field != null) {
            if (!this.field.equals(that.field)) {
                return false;
            }
        } else if (that.field != null) {
            return false;
        }
        if (!this.typeForValidatorResolution.equals(that.typeForValidatorResolution)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = this.field != null ? this.field.hashCode() : 0;
        return (31 * result) + this.typeForValidatorResolution.hashCode();
    }

    private static Field getAccessible(Field original) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(HibernateValidatorPermission.ACCESS_PRIVATE_MEMBERS);
        }
        Class<?> clazz = original.getDeclaringClass();
        return (Field) run(GetDeclaredField.andMakeAccessible(clazz, original.getName()));
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? (T) AccessController.doPrivileged(action) : action.run();
    }
}