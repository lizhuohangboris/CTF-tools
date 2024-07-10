package org.hibernate.validator.internal.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import javax.validation.ParameterNameProvider;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/ExecutableParameterNameProvider.class */
public class ExecutableParameterNameProvider {
    private final ParameterNameProvider delegate;

    public ExecutableParameterNameProvider(ParameterNameProvider delegate) {
        this.delegate = delegate;
    }

    public List<String> getParameterNames(Executable executable) {
        if (executable.getParameterCount() == 0) {
            return Collections.emptyList();
        }
        if (executable instanceof Method) {
            return this.delegate.getParameterNames((Method) executable);
        }
        return this.delegate.getParameterNames((Constructor) executable);
    }

    public ParameterNameProvider getDelegate() {
        return this.delegate;
    }

    public String toString() {
        return "ExecutableParameterNameProvider [delegate=" + this.delegate + "]";
    }

    public int hashCode() {
        int result = (31 * 1) + (this.delegate == null ? 0 : this.delegate.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ExecutableParameterNameProvider other = (ExecutableParameterNameProvider) obj;
        return this.delegate == other;
    }
}