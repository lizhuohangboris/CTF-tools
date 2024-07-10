package org.springframework.core;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/BridgeMethodResolver.class */
public final class BridgeMethodResolver {
    private BridgeMethodResolver() {
    }

    public static Method findBridgedMethod(Method bridgeMethod) {
        if (!bridgeMethod.isBridge()) {
            return bridgeMethod;
        }
        List<Method> candidateMethods = new ArrayList<>();
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bridgeMethod.getDeclaringClass());
        for (Method candidateMethod : methods) {
            if (isBridgedCandidateFor(candidateMethod, bridgeMethod)) {
                candidateMethods.add(candidateMethod);
            }
        }
        if (candidateMethods.size() == 1) {
            return candidateMethods.get(0);
        }
        Method bridgedMethod = searchCandidates(candidateMethods, bridgeMethod);
        if (bridgedMethod != null) {
            return bridgedMethod;
        }
        return bridgeMethod;
    }

    private static boolean isBridgedCandidateFor(Method candidateMethod, Method bridgeMethod) {
        return !candidateMethod.isBridge() && !candidateMethod.equals(bridgeMethod) && candidateMethod.getName().equals(bridgeMethod.getName()) && candidateMethod.getParameterCount() == bridgeMethod.getParameterCount();
    }

    @Nullable
    private static Method searchCandidates(List<Method> candidateMethods, Method bridgeMethod) {
        if (candidateMethods.isEmpty()) {
            return null;
        }
        Method previousMethod = null;
        boolean sameSig = true;
        for (Method candidateMethod : candidateMethods) {
            if (isBridgeMethodFor(bridgeMethod, candidateMethod, bridgeMethod.getDeclaringClass())) {
                return candidateMethod;
            }
            if (previousMethod != null) {
                sameSig = sameSig && Arrays.equals(candidateMethod.getGenericParameterTypes(), previousMethod.getGenericParameterTypes());
            }
            previousMethod = candidateMethod;
        }
        if (sameSig) {
            return candidateMethods.get(0);
        }
        return null;
    }

    static boolean isBridgeMethodFor(Method bridgeMethod, Method candidateMethod, Class<?> declaringClass) {
        if (isResolvedTypeMatch(candidateMethod, bridgeMethod, declaringClass)) {
            return true;
        }
        Method method = findGenericDeclaration(bridgeMethod);
        return method != null && isResolvedTypeMatch(method, candidateMethod, declaringClass);
    }

    private static boolean isResolvedTypeMatch(Method genericMethod, Method candidateMethod, Class<?> declaringClass) {
        Type[] genericParameters = genericMethod.getGenericParameterTypes();
        Class<?>[] candidateParameters = candidateMethod.getParameterTypes();
        if (genericParameters.length != candidateParameters.length) {
            return false;
        }
        for (int i = 0; i < candidateParameters.length; i++) {
            ResolvableType genericParameter = ResolvableType.forMethodParameter(genericMethod, i, declaringClass);
            Class<?> candidateParameter = candidateParameters[i];
            if ((candidateParameter.isArray() && !candidateParameter.getComponentType().equals(genericParameter.getComponentType().toClass())) || !candidateParameter.equals(genericParameter.toClass())) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    private static Method findGenericDeclaration(Method bridgeMethod) {
        Class<?> superclass = bridgeMethod.getDeclaringClass().getSuperclass();
        while (true) {
            Class<?> superclass2 = superclass;
            if (superclass2 == null || Object.class == superclass2) {
                break;
            }
            Method method = searchForMatch(superclass2, bridgeMethod);
            if (method != null && !method.isBridge()) {
                return method;
            }
            superclass = superclass2.getSuperclass();
        }
        Class<?>[] interfaces = ClassUtils.getAllInterfacesForClass(bridgeMethod.getDeclaringClass());
        return searchInterfaces(interfaces, bridgeMethod);
    }

    @Nullable
    private static Method searchInterfaces(Class<?>[] interfaces, Method bridgeMethod) {
        for (Class<?> ifc : interfaces) {
            Method method = searchForMatch(ifc, bridgeMethod);
            if (method != null && !method.isBridge()) {
                return method;
            }
            Method method2 = searchInterfaces(ifc.getInterfaces(), bridgeMethod);
            if (method2 != null) {
                return method2;
            }
        }
        return null;
    }

    @Nullable
    private static Method searchForMatch(Class<?> type, Method bridgeMethod) {
        try {
            return type.getDeclaredMethod(bridgeMethod.getName(), bridgeMethod.getParameterTypes());
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static boolean isVisibilityBridgeMethodPair(Method bridgeMethod, Method bridgedMethod) {
        if (bridgeMethod == bridgedMethod) {
            return true;
        }
        return Arrays.equals(bridgeMethod.getParameterTypes(), bridgedMethod.getParameterTypes()) && bridgeMethod.getReturnType().equals(bridgedMethod.getReturnType());
    }
}