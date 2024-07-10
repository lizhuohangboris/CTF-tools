package org.hibernate.validator.internal.util.logging.formatter;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import org.hibernate.validator.internal.util.ExecutableHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/logging/formatter/ExecutableFormatter.class */
public class ExecutableFormatter {
    private final String stringRepresentation;

    public ExecutableFormatter(Executable executable) {
        String name = ExecutableHelper.getSimpleName(executable);
        name = executable instanceof Method ? executable.getDeclaringClass().getSimpleName() + "#" + name : name;
        Class<?>[] parameterTypes = executable.getParameterTypes();
        this.stringRepresentation = ExecutableHelper.getExecutableAsString(name, parameterTypes);
    }

    public String toString() {
        return this.stringRepresentation;
    }
}