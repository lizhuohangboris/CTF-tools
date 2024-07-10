package org.springframework.beans.factory.support;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Set;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/AutowireUtils.class */
abstract class AutowireUtils {
    AutowireUtils() {
    }

    public static void sortConstructors(Constructor<?>[] constructors) {
        Arrays.sort(constructors, c1, c2 -> {
            boolean p1 = Modifier.isPublic(c1.getModifiers());
            boolean p2 = Modifier.isPublic(c2.getModifiers());
            if (p1 != p2) {
                return p1 ? -1 : 1;
            }
            int c1pl = c1.getParameterCount();
            int c2pl = c2.getParameterCount();
            if (c1pl < c2pl) {
                return 1;
            }
            return c1pl > c2pl ? -1 : 0;
        });
    }

    public static void sortFactoryMethods(Method[] factoryMethods) {
        Arrays.sort(factoryMethods, fm1, fm2 -> {
            boolean p1 = Modifier.isPublic(fm1.getModifiers());
            boolean p2 = Modifier.isPublic(fm2.getModifiers());
            if (p1 != p2) {
                return p1 ? -1 : 1;
            }
            int c1pl = fm1.getParameterCount();
            int c2pl = fm2.getParameterCount();
            if (c1pl < c2pl) {
                return 1;
            }
            return c1pl > c2pl ? -1 : 0;
        });
    }

    public static boolean isExcludedFromDependencyCheck(PropertyDescriptor pd) {
        Method wm = pd.getWriteMethod();
        if (wm == null || !wm.getDeclaringClass().getName().contains(ClassUtils.CGLIB_CLASS_SEPARATOR)) {
            return false;
        }
        Class<?> superclass = wm.getDeclaringClass().getSuperclass();
        return !ClassUtils.hasMethod(superclass, wm.getName(), wm.getParameterTypes());
    }

    public static boolean isSetterDefinedInInterface(PropertyDescriptor pd, Set<Class<?>> interfaces) {
        Method setter = pd.getWriteMethod();
        if (setter != null) {
            Class<?> targetClass = setter.getDeclaringClass();
            for (Class<?> ifc : interfaces) {
                if (ifc.isAssignableFrom(targetClass) && ClassUtils.hasMethod(ifc, setter.getName(), setter.getParameterTypes())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public static Object resolveAutowiringValue(Object autowiringValue, Class<?> requiredType) {
        if ((autowiringValue instanceof ObjectFactory) && !requiredType.isInstance(autowiringValue)) {
            ObjectFactory<?> factory = (ObjectFactory) autowiringValue;
            if ((autowiringValue instanceof Serializable) && requiredType.isInterface()) {
                autowiringValue = Proxy.newProxyInstance(requiredType.getClassLoader(), new Class[]{requiredType}, new ObjectFactoryDelegatingInvocationHandler(factory));
            } else {
                return factory.getObject();
            }
        }
        return autowiringValue;
    }

    public static Class<?> resolveReturnTypeForFactoryMethod(Method method, Object[] args, @Nullable ClassLoader classLoader) {
        TypedStringValue typedValue;
        String targetTypeName;
        Assert.notNull(method, "Method must not be null");
        Assert.notNull(args, "Argument array must not be null");
        TypeVariable<Method>[] declaredTypeVariables = method.getTypeParameters();
        Type genericReturnType = method.getGenericReturnType();
        Type[] methodParameterTypes = method.getGenericParameterTypes();
        Assert.isTrue(args.length == methodParameterTypes.length, "Argument array does not match parameter count");
        boolean locallyDeclaredTypeVariableMatchesReturnType = false;
        int length = declaredTypeVariables.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            TypeVariable<Method> currentTypeVariable = declaredTypeVariables[i];
            if (!currentTypeVariable.equals(genericReturnType)) {
                i++;
            } else {
                locallyDeclaredTypeVariableMatchesReturnType = true;
                break;
            }
        }
        if (locallyDeclaredTypeVariableMatchesReturnType) {
            for (int i2 = 0; i2 < methodParameterTypes.length; i2++) {
                Type methodParameterType = methodParameterTypes[i2];
                Object arg = args[i2];
                if (methodParameterType.equals(genericReturnType)) {
                    if (arg instanceof TypedStringValue) {
                        TypedStringValue typedValue2 = (TypedStringValue) arg;
                        if (typedValue2.hasTargetType()) {
                            return typedValue2.getTargetType();
                        }
                        try {
                            Class<?> resolvedType = typedValue2.resolveTargetType(classLoader);
                            if (resolvedType != null) {
                                return resolvedType;
                            }
                        } catch (ClassNotFoundException ex) {
                            throw new IllegalStateException("Failed to resolve value type [" + typedValue2.getTargetTypeName() + "] for factory method argument", ex);
                        }
                    } else if (arg != null && !(arg instanceof BeanMetadataElement)) {
                        return arg.getClass();
                    }
                    return method.getReturnType();
                }
                if (methodParameterType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) methodParameterType;
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    for (Type typeArg : actualTypeArguments) {
                        if (typeArg.equals(genericReturnType)) {
                            if (arg instanceof Class) {
                                return (Class) arg;
                            } else {
                                String className = null;
                                if (arg instanceof String) {
                                    className = (String) arg;
                                } else if ((arg instanceof TypedStringValue) && ((targetTypeName = (typedValue = (TypedStringValue) arg).getTargetTypeName()) == null || Class.class.getName().equals(targetTypeName))) {
                                    className = typedValue.getValue();
                                }
                                if (className != null) {
                                    try {
                                        return ClassUtils.forName(className, classLoader);
                                    } catch (ClassNotFoundException ex2) {
                                        throw new IllegalStateException("Could not resolve class name [" + arg + "] for factory method argument", ex2);
                                    }
                                }
                                return method.getReturnType();
                            }
                        }
                    }
                    continue;
                }
            }
        }
        return method.getReturnType();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/AutowireUtils$ObjectFactoryDelegatingInvocationHandler.class */
    private static class ObjectFactoryDelegatingInvocationHandler implements InvocationHandler, Serializable {
        private final ObjectFactory<?> objectFactory;

        public ObjectFactoryDelegatingInvocationHandler(ObjectFactory<?> objectFactory) {
            this.objectFactory = objectFactory;
        }

        @Override // java.lang.reflect.InvocationHandler
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            if (methodName.equals("equals")) {
                return Boolean.valueOf(proxy == args[0]);
            } else if (methodName.equals(IdentityNamingStrategy.HASH_CODE_KEY)) {
                return Integer.valueOf(System.identityHashCode(proxy));
            } else {
                if (methodName.equals("toString")) {
                    return this.objectFactory.toString();
                }
                try {
                    return method.invoke(this.objectFactory.getObject(), args);
                } catch (InvocationTargetException ex) {
                    throw ex.getTargetException();
                }
            }
        }
    }
}