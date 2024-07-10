package org.apache.catalina.util;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/util/Introspection.class */
public class Introspection {
    private static final StringManager sm = StringManager.getManager("org.apache.catalina.util");

    public static String getPropertyName(Method setter) {
        return Introspector.decapitalize(setter.getName().substring(3));
    }

    public static boolean isValidSetter(Method method) {
        if (method.getName().startsWith("set") && method.getName().length() > 3 && method.getParameterTypes().length == 1 && method.getReturnType().getName().equals("void")) {
            return true;
        }
        return false;
    }

    public static boolean isValidLifecycleCallback(Method method) {
        if (method.getParameterTypes().length != 0 || Modifier.isStatic(method.getModifiers()) || method.getExceptionTypes().length > 0 || !method.getReturnType().getName().equals("void")) {
            return false;
        }
        return true;
    }

    public static Field[] getDeclaredFields(final Class<?> clazz) {
        Field[] fields;
        if (Globals.IS_SECURITY_ENABLED) {
            fields = (Field[]) AccessController.doPrivileged(new PrivilegedAction<Field[]>() { // from class: org.apache.catalina.util.Introspection.1
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.security.PrivilegedAction
                public Field[] run() {
                    return clazz.getDeclaredFields();
                }
            });
        } else {
            fields = clazz.getDeclaredFields();
        }
        return fields;
    }

    public static Method[] getDeclaredMethods(final Class<?> clazz) {
        Method[] methods;
        if (Globals.IS_SECURITY_ENABLED) {
            methods = (Method[]) AccessController.doPrivileged(new PrivilegedAction<Method[]>() { // from class: org.apache.catalina.util.Introspection.2
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.security.PrivilegedAction
                public Method[] run() {
                    return clazz.getDeclaredMethods();
                }
            });
        } else {
            methods = clazz.getDeclaredMethods();
        }
        return methods;
    }

    public static Class<?> loadClass(Context context, String className) {
        ClassLoader cl = context.getLoader().getClassLoader();
        Log log = context.getLogger();
        Class<?> clazz = null;
        try {
            clazz = cl.loadClass(className);
        } catch (ClassFormatError | ClassNotFoundException | NoClassDefFoundError e) {
            log.debug(sm.getString("introspection.classLoadFailed", className), e);
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            log.debug(sm.getString("introspection.classLoadFailed", className), t);
        }
        return clazz;
    }

    public static Class<?> convertPrimitiveType(Class<?> clazz) {
        if (clazz.equals(Character.TYPE)) {
            return Character.class;
        }
        if (clazz.equals(Integer.TYPE)) {
            return Integer.class;
        }
        if (clazz.equals(Boolean.TYPE)) {
            return Boolean.class;
        }
        if (clazz.equals(Double.TYPE)) {
            return Double.class;
        }
        if (clazz.equals(Byte.TYPE)) {
            return Byte.class;
        }
        if (clazz.equals(Short.TYPE)) {
            return Short.class;
        }
        if (clazz.equals(Long.TYPE)) {
            return Long.class;
        }
        if (clazz.equals(Float.TYPE)) {
            return Float.class;
        }
        return clazz;
    }
}