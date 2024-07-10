package org.hibernate.validator.internal.util.privilegedactions;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/privilegedactions/GetMethodFromPropertyName.class */
public final class GetMethodFromPropertyName implements PrivilegedAction<Method> {
    private final Class<?> clazz;
    private final String property;

    public static GetMethodFromPropertyName action(Class<?> clazz, String property) {
        return new GetMethodFromPropertyName(clazz, property);
    }

    private GetMethodFromPropertyName(Class<?> clazz, String property) {
        this.clazz = clazz;
        this.property = property;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.security.PrivilegedAction
    public Method run() {
        try {
            char[] string = this.property.toCharArray();
            string[0] = Character.toUpperCase(string[0]);
            String fullMethodName = new String(string);
            try {
                return this.clazz.getMethod(BeanUtil.PREFIX_GETTER_GET + fullMethodName, new Class[0]);
            } catch (NoSuchMethodException e) {
                return this.clazz.getMethod(BeanUtil.PREFIX_GETTER_IS + fullMethodName, new Class[0]);
            }
        } catch (NoSuchMethodException e2) {
            return null;
        }
    }
}