package org.hibernate.validator.internal.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.validation.ParameterNameProvider;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/DefaultParameterNameProvider.class */
public class DefaultParameterNameProvider implements ParameterNameProvider {
    @Override // javax.validation.ParameterNameProvider
    public List<String> getParameterNames(Constructor<?> constructor) {
        return doGetParameterNames(constructor);
    }

    @Override // javax.validation.ParameterNameProvider
    public List<String> getParameterNames(Method method) {
        return doGetParameterNames(method);
    }

    private List<String> doGetParameterNames(Executable executable) {
        Parameter[] parameters = executable.getParameters();
        List<String> parameterNames = new ArrayList<>(parameters.length);
        for (Parameter parameter : parameters) {
            parameterNames.add(parameter.getName());
        }
        return Collections.unmodifiableList(parameterNames);
    }
}