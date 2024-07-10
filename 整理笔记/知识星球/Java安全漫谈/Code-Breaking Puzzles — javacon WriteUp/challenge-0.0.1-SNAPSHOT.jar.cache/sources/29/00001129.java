package org.hibernate.validator.internal.metadata.location;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.hibernate.validator.HibernateValidatorPermission;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.StringHelper;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredMethod;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/location/GetterConstraintLocation.class */
public class GetterConstraintLocation implements ConstraintLocation {
    private final Method method;
    private final Method accessibleMethod;
    private final String propertyName;
    private final Type typeForValidatorResolution;
    private final Class<?> declaringClass;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GetterConstraintLocation(Class<?> declaringClass, Method method) {
        this.method = method;
        this.accessibleMethod = getAccessible(method);
        this.propertyName = ReflectionHelper.getPropertyName(method);
        this.typeForValidatorResolution = ReflectionHelper.boxedType(ReflectionHelper.typeOf(method));
        this.declaringClass = declaringClass;
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public Class<?> getDeclaringClass() {
        return this.declaringClass;
    }

    @Override // org.hibernate.validator.internal.metadata.location.ConstraintLocation
    public Method getMember() {
        return this.method;
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
        return ReflectionHelper.getValue(this.accessibleMethod, parent);
    }

    public String toString() {
        return "GetterConstraintLocation [method=" + StringHelper.toShortString((Member) this.method) + ", typeForValidatorResolution=" + StringHelper.toShortString(this.typeForValidatorResolution) + "]";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GetterConstraintLocation that = (GetterConstraintLocation) o;
        if (this.method != null) {
            if (!this.method.equals(that.method)) {
                return false;
            }
        } else if (that.method != null) {
            return false;
        }
        if (!this.typeForValidatorResolution.equals(that.typeForValidatorResolution)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = this.method.hashCode();
        return (31 * result) + this.typeForValidatorResolution.hashCode();
    }

    private static Method getAccessible(Method original) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(HibernateValidatorPermission.ACCESS_PRIVATE_MEMBERS);
        }
        Class<?> clazz = original.getDeclaringClass();
        Method accessibleMethod = (Method) run(GetDeclaredMethod.andMakeAccessible(clazz, original.getName(), new Class[0]));
        return accessibleMethod;
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? (T) AccessController.doPrivileged(action) : action.run();
    }
}