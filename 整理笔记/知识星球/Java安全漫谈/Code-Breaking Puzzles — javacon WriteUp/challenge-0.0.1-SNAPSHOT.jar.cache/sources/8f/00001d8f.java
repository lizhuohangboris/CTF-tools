package org.springframework.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import kotlin.reflect.KFunction;
import kotlin.reflect.KParameter;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/KotlinReflectionParameterNameDiscoverer.class */
public class KotlinReflectionParameterNameDiscoverer implements ParameterNameDiscoverer {
    @Override // org.springframework.core.ParameterNameDiscoverer
    @Nullable
    public String[] getParameterNames(Method method) {
        if (!KotlinDetector.isKotlinType(method.getDeclaringClass())) {
            return null;
        }
        try {
            KFunction<?> function = ReflectJvmMapping.getKotlinFunction(method);
            if (function != null) {
                return getParameterNames(function.getParameters());
            }
            return null;
        } catch (UnsupportedOperationException e) {
            return null;
        }
    }

    @Override // org.springframework.core.ParameterNameDiscoverer
    @Nullable
    public String[] getParameterNames(Constructor<?> ctor) {
        if (ctor.getDeclaringClass().isEnum() || !KotlinDetector.isKotlinType(ctor.getDeclaringClass())) {
            return null;
        }
        try {
            KFunction<?> function = ReflectJvmMapping.getKotlinFunction(ctor);
            if (function != null) {
                return getParameterNames(function.getParameters());
            }
            return null;
        } catch (UnsupportedOperationException e) {
            return null;
        }
    }

    @Nullable
    private String[] getParameterNames(List<KParameter> parameters) {
        List<KParameter> filteredParameters = (List) parameters.stream().filter(p -> {
            return KParameter.Kind.VALUE.equals(p.getKind()) || KParameter.Kind.EXTENSION_RECEIVER.equals(p.getKind());
        }).collect(Collectors.toList());
        String[] parameterNames = new String[filteredParameters.size()];
        for (int i = 0; i < filteredParameters.size(); i++) {
            KParameter parameter = filteredParameters.get(i);
            String name = KParameter.Kind.EXTENSION_RECEIVER.equals(parameter.getKind()) ? "$receiver" : parameter.getName();
            if (name == null) {
                return null;
            }
            parameterNames[i] = name;
        }
        return parameterNames;
    }
}