package org.springframework.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/SerializableTypeWrapper.class */
public final class SerializableTypeWrapper {
    private static final Class<?>[] SUPPORTED_SERIALIZABLE_TYPES = {GenericArrayType.class, ParameterizedType.class, TypeVariable.class, WildcardType.class};
    static final ConcurrentReferenceHashMap<Type, Type> cache = new ConcurrentReferenceHashMap<>(256);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/SerializableTypeWrapper$SerializableTypeProxy.class */
    public interface SerializableTypeProxy {
        TypeProvider getTypeProvider();
    }

    private SerializableTypeWrapper() {
    }

    @Nullable
    public static Type forField(Field field) {
        return forTypeProvider(new FieldTypeProvider(field));
    }

    @Nullable
    public static Type forMethodParameter(MethodParameter methodParameter) {
        return forTypeProvider(new MethodParameterTypeProvider(methodParameter));
    }

    public static <T extends Type> T unwrap(T type) {
        T t;
        Type type2 = type;
        while (true) {
            t = (T) type2;
            if (!(t instanceof SerializableTypeProxy)) {
                break;
            }
            type2 = ((SerializableTypeProxy) type).getTypeProvider().getType();
        }
        return t != null ? t : type;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public static Type forTypeProvider(TypeProvider provider) {
        Class<?>[] clsArr;
        Type providedType = provider.getType();
        if (providedType == null || (providedType instanceof Serializable)) {
            return providedType;
        }
        if (GraalDetector.inImageCode() || !Serializable.class.isAssignableFrom(Class.class)) {
            return providedType;
        }
        Type cached = cache.get(providedType);
        if (cached != null) {
            return cached;
        }
        for (Class<?> type : SUPPORTED_SERIALIZABLE_TYPES) {
            if (type.isInstance(providedType)) {
                ClassLoader classLoader = provider.getClass().getClassLoader();
                Class<?>[] interfaces = {type, SerializableTypeProxy.class, Serializable.class};
                InvocationHandler handler = new TypeProxyInvocationHandler(provider);
                Type cached2 = (Type) Proxy.newProxyInstance(classLoader, interfaces, handler);
                cache.put(providedType, cached2);
                return cached2;
            }
        }
        throw new IllegalArgumentException("Unsupported Type class: " + providedType.getClass().getName());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/SerializableTypeWrapper$TypeProvider.class */
    public interface TypeProvider extends Serializable {
        @Nullable
        Type getType();

        @Nullable
        default Object getSource() {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/SerializableTypeWrapper$TypeProxyInvocationHandler.class */
    public static class TypeProxyInvocationHandler implements InvocationHandler, Serializable {
        private final TypeProvider provider;

        public TypeProxyInvocationHandler(TypeProvider provider) {
            this.provider = provider;
        }

        @Override // java.lang.reflect.InvocationHandler
        @Nullable
        public Object invoke(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
            if (method.getName().equals("equals") && args != null) {
                Object other = args[0];
                if (other instanceof Type) {
                    other = SerializableTypeWrapper.unwrap((Type) other);
                }
                return Boolean.valueOf(ObjectUtils.nullSafeEquals(this.provider.getType(), other));
            } else if (method.getName().equals(IdentityNamingStrategy.HASH_CODE_KEY)) {
                return Integer.valueOf(ObjectUtils.nullSafeHashCode(this.provider.getType()));
            } else {
                if (method.getName().equals("getTypeProvider")) {
                    return this.provider;
                }
                if (Type.class == method.getReturnType() && args == null) {
                    return SerializableTypeWrapper.forTypeProvider(new MethodInvokeTypeProvider(this.provider, method, -1));
                }
                if (Type[].class == method.getReturnType() && args == null) {
                    Type[] result = new Type[((Type[]) method.invoke(this.provider.getType(), new Object[0])).length];
                    for (int i = 0; i < result.length; i++) {
                        result[i] = SerializableTypeWrapper.forTypeProvider(new MethodInvokeTypeProvider(this.provider, method, i));
                    }
                    return result;
                }
                try {
                    return method.invoke(this.provider.getType(), args);
                } catch (InvocationTargetException ex) {
                    throw ex.getTargetException();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/SerializableTypeWrapper$FieldTypeProvider.class */
    public static class FieldTypeProvider implements TypeProvider {
        private final String fieldName;
        private final Class<?> declaringClass;
        private transient Field field;

        public FieldTypeProvider(Field field) {
            this.fieldName = field.getName();
            this.declaringClass = field.getDeclaringClass();
            this.field = field;
        }

        @Override // org.springframework.core.SerializableTypeWrapper.TypeProvider
        public Type getType() {
            return this.field.getGenericType();
        }

        @Override // org.springframework.core.SerializableTypeWrapper.TypeProvider
        public Object getSource() {
            return this.field;
        }

        private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
            inputStream.defaultReadObject();
            try {
                this.field = this.declaringClass.getDeclaredField(this.fieldName);
            } catch (Throwable ex) {
                throw new IllegalStateException("Could not find original class structure", ex);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/SerializableTypeWrapper$MethodParameterTypeProvider.class */
    public static class MethodParameterTypeProvider implements TypeProvider {
        @Nullable
        private final String methodName;
        private final Class<?>[] parameterTypes;
        private final Class<?> declaringClass;
        private final int parameterIndex;
        private transient MethodParameter methodParameter;

        public MethodParameterTypeProvider(MethodParameter methodParameter) {
            this.methodName = methodParameter.getMethod() != null ? methodParameter.getMethod().getName() : null;
            this.parameterTypes = methodParameter.getExecutable().getParameterTypes();
            this.declaringClass = methodParameter.getDeclaringClass();
            this.parameterIndex = methodParameter.getParameterIndex();
            this.methodParameter = methodParameter;
        }

        @Override // org.springframework.core.SerializableTypeWrapper.TypeProvider
        public Type getType() {
            return this.methodParameter.getGenericParameterType();
        }

        @Override // org.springframework.core.SerializableTypeWrapper.TypeProvider
        public Object getSource() {
            return this.methodParameter;
        }

        private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
            inputStream.defaultReadObject();
            try {
                if (this.methodName != null) {
                    this.methodParameter = new MethodParameter(this.declaringClass.getDeclaredMethod(this.methodName, this.parameterTypes), this.parameterIndex);
                } else {
                    this.methodParameter = new MethodParameter(this.declaringClass.getDeclaredConstructor(this.parameterTypes), this.parameterIndex);
                }
            } catch (Throwable ex) {
                throw new IllegalStateException("Could not find original class structure", ex);
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/SerializableTypeWrapper$MethodInvokeTypeProvider.class */
    static class MethodInvokeTypeProvider implements TypeProvider {
        private final TypeProvider provider;
        private final String methodName;
        private final Class<?> declaringClass;
        private final int index;
        private transient Method method;
        @Nullable
        private volatile transient Object result;

        public MethodInvokeTypeProvider(TypeProvider provider, Method method, int index) {
            this.provider = provider;
            this.methodName = method.getName();
            this.declaringClass = method.getDeclaringClass();
            this.index = index;
            this.method = method;
        }

        @Override // org.springframework.core.SerializableTypeWrapper.TypeProvider
        @Nullable
        public Type getType() {
            Object result = this.result;
            if (result == null) {
                result = ReflectionUtils.invokeMethod(this.method, this.provider.getType());
                this.result = result;
            }
            return result instanceof Type[] ? ((Type[]) result)[this.index] : (Type) result;
        }

        @Override // org.springframework.core.SerializableTypeWrapper.TypeProvider
        @Nullable
        public Object getSource() {
            return null;
        }

        private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
            inputStream.defaultReadObject();
            Method method = ReflectionUtils.findMethod(this.declaringClass, this.methodName);
            if (method == null) {
                throw new IllegalStateException("Cannot find method on deserialization: " + this.methodName);
            }
            if (method.getReturnType() != Type.class && method.getReturnType() != Type[].class) {
                throw new IllegalStateException("Invalid return type on deserialized method - needs to be Type or Type[]: " + method);
            }
            this.method = method;
        }
    }
}