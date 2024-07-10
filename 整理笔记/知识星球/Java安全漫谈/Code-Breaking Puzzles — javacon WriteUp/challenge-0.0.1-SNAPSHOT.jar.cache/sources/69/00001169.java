package org.hibernate.validator.internal.util;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/StringHelper.class */
public class StringHelper {
    private static final Pattern DOT = Pattern.compile("\\.");

    private StringHelper() {
    }

    public static String join(Object[] array, String separator) {
        if (array != null) {
            return join(Arrays.asList(array), separator);
        }
        return null;
    }

    public static String join(Iterable<?> iterable, String separator) {
        if (iterable == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (Object object : iterable) {
            if (!isFirst) {
                sb.append(separator);
            } else {
                isFirst = false;
            }
            sb.append(object);
        }
        return sb.toString();
    }

    public static String decapitalize(String string) {
        if (string == null || string.isEmpty() || startsWithSeveralUpperCaseLetters(string)) {
            return string;
        }
        return string.substring(0, 1).toLowerCase(Locale.ROOT) + string.substring(1);
    }

    public static boolean isNullOrEmptyString(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static String toShortString(Member member) {
        if (member instanceof Field) {
            return toShortString((Field) member);
        }
        if (member instanceof Method) {
            return toShortString((Method) member);
        }
        return member.toString();
    }

    private static String toShortString(Field field) {
        return toShortString(field.getGenericType()) + " " + toShortString(field.getDeclaringClass()) + "#" + field.getName();
    }

    private static String toShortString(Method method) {
        return toShortString(method.getGenericReturnType()) + " " + method.getName() + ((String) Arrays.stream(method.getGenericParameterTypes()).map(StringHelper::toShortString).collect(Collectors.joining(", ", "(", ")")));
    }

    public static String toShortString(Type type) {
        if (type instanceof Class) {
            return toShortString((Class<?>) type);
        }
        if (type instanceof ParameterizedType) {
            return toShortString((ParameterizedType) type);
        }
        return type.toString();
    }

    private static String toShortString(Class<?> type) {
        if (type.isArray()) {
            return toShortString(type.getComponentType()) + ClassUtils.ARRAY_SUFFIX;
        }
        if (type.getEnclosingClass() != null) {
            return toShortString(type.getEnclosingClass()) + PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX + type.getSimpleName();
        }
        if (type.getPackage() == null) {
            return type.getName();
        }
        return toShortString(type.getPackage()) + "." + type.getSimpleName();
    }

    private static String toShortString(ParameterizedType parameterizedType) {
        Class<?> rawType = ReflectionHelper.getClassFromType(parameterizedType);
        if (rawType.getPackage() == null) {
            return parameterizedType.toString();
        }
        String typeArgumentsString = (String) Arrays.stream(parameterizedType.getActualTypeArguments()).map(t -> {
            return toShortString(t);
        }).collect(Collectors.joining(", ", "<", ">"));
        return toShortString(rawType) + typeArgumentsString;
    }

    private static String toShortString(Package pakkage) {
        String[] packageParts = DOT.split(pakkage.getName());
        return (String) Arrays.stream(packageParts).map(n -> {
            return n.substring(0, 1);
        }).collect(Collectors.joining("."));
    }

    private static boolean startsWithSeveralUpperCaseLetters(String string) {
        return string.length() > 1 && Character.isUpperCase(string.charAt(0)) && Character.isUpperCase(string.charAt(1));
    }
}