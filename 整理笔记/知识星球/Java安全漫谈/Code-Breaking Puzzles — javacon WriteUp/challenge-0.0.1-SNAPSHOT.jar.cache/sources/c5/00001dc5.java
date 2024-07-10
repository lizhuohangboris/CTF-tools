package org.springframework.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/StandardReflectionParameterNameDiscoverer.class */
public class StandardReflectionParameterNameDiscoverer implements ParameterNameDiscoverer {
    @Override // org.springframework.core.ParameterNameDiscoverer
    @Nullable
    public String[] getParameterNames(Method method) {
        return getParameterNames(method.getParameters());
    }

    @Override // org.springframework.core.ParameterNameDiscoverer
    @Nullable
    public String[] getParameterNames(Constructor<?> ctor) {
        return getParameterNames(ctor.getParameters());
    }

    @Nullable
    private String[] getParameterNames(Parameter[] parameters) {
        String[] parameterNames = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            if (!param.isNamePresent()) {
                return null;
            }
            parameterNames[i] = param.getName();
        }
        return parameterNames;
    }
}