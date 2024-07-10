package org.springframework.core.convert.support;

import ch.qos.logback.core.CoreConstants;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/support/ObjectToObjectConverter.class */
public final class ObjectToObjectConverter implements ConditionalGenericConverter {
    private static final Map<Class<?>, Member> conversionMemberCache = new ConcurrentReferenceHashMap(32);

    @Override // org.springframework.core.convert.converter.GenericConverter
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Object.class, Object.class));
    }

    @Override // org.springframework.core.convert.converter.ConditionalConverter
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return sourceType.getType() != targetType.getType() && hasConversionMethodOrConstructor(targetType.getType(), sourceType.getType());
    }

    @Override // org.springframework.core.convert.converter.GenericConverter
    @Nullable
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        Class<?> sourceClass = sourceType.getType();
        Class<?> targetClass = targetType.getType();
        Member member = getValidatedMember(targetClass, sourceClass);
        try {
            if (member instanceof Method) {
                Method method = (Method) member;
                ReflectionUtils.makeAccessible(method);
                if (!Modifier.isStatic(method.getModifiers())) {
                    return method.invoke(source, new Object[0]);
                }
                return method.invoke(null, source);
            } else if (member instanceof Constructor) {
                Constructor<?> ctor = (Constructor) member;
                ReflectionUtils.makeAccessible(ctor);
                return ctor.newInstance(source);
            } else {
                throw new IllegalStateException(String.format("No to%3$s() method exists on %1$s, and no static valueOf/of/from(%1$s) method or %3$s(%1$s) constructor exists on %2$s.", sourceClass.getName(), targetClass.getName(), targetClass.getSimpleName()));
            }
        } catch (InvocationTargetException ex) {
            throw new ConversionFailedException(sourceType, targetType, source, ex.getTargetException());
        } catch (Throwable ex2) {
            throw new ConversionFailedException(sourceType, targetType, source, ex2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean hasConversionMethodOrConstructor(Class<?> targetClass, Class<?> sourceClass) {
        return getValidatedMember(targetClass, sourceClass) != null;
    }

    @Nullable
    private static Member getValidatedMember(Class<?> targetClass, Class<?> sourceClass) {
        Member member = conversionMemberCache.get(targetClass);
        if (isApplicable(member, sourceClass)) {
            return member;
        }
        Member member2 = determineToMethod(targetClass, sourceClass);
        if (member2 == null) {
            member2 = determineFactoryMethod(targetClass, sourceClass);
            if (member2 == null) {
                member2 = determineFactoryConstructor(targetClass, sourceClass);
                if (member2 == null) {
                    return null;
                }
            }
        }
        conversionMemberCache.put(targetClass, member2);
        return member2;
    }

    private static boolean isApplicable(Member member, Class<?> sourceClass) {
        if (member instanceof Method) {
            Method method = (Method) member;
            if (Modifier.isStatic(method.getModifiers())) {
                return method.getParameterTypes()[0] == sourceClass;
            }
            return ClassUtils.isAssignable(method.getDeclaringClass(), sourceClass);
        } else if (member instanceof Constructor) {
            Constructor<?> ctor = (Constructor) member;
            return ctor.getParameterTypes()[0] == sourceClass;
        } else {
            return false;
        }
    }

    @Nullable
    private static Method determineToMethod(Class<?> targetClass, Class<?> sourceClass) {
        Method method;
        if (String.class == targetClass || String.class == sourceClass || (method = ClassUtils.getMethodIfAvailable(sourceClass, "to" + targetClass.getSimpleName(), new Class[0])) == null || Modifier.isStatic(method.getModifiers()) || !ClassUtils.isAssignable(targetClass, method.getReturnType())) {
            return null;
        }
        return method;
    }

    @Nullable
    private static Method determineFactoryMethod(Class<?> targetClass, Class<?> sourceClass) {
        if (String.class == targetClass) {
            return null;
        }
        Method method = ClassUtils.getStaticMethod(targetClass, CoreConstants.VALUE_OF, sourceClass);
        if (method == null) {
            method = ClassUtils.getStaticMethod(targetClass, "of", sourceClass);
            if (method == null) {
                method = ClassUtils.getStaticMethod(targetClass, "from", sourceClass);
            }
        }
        return method;
    }

    @Nullable
    private static Constructor<?> determineFactoryConstructor(Class<?> targetClass, Class<?> sourceClass) {
        return ClassUtils.getConstructorIfAvailable(targetClass, sourceClass);
    }
}