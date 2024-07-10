package javax.validation.executable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;
import javax.validation.ConstraintViolation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/executable/ExecutableValidator.class */
public interface ExecutableValidator {
    <T> Set<ConstraintViolation<T>> validateParameters(T t, Method method, Object[] objArr, Class<?>... clsArr);

    <T> Set<ConstraintViolation<T>> validateReturnValue(T t, Method method, Object obj, Class<?>... clsArr);

    <T> Set<ConstraintViolation<T>> validateConstructorParameters(Constructor<? extends T> constructor, Object[] objArr, Class<?>... clsArr);

    <T> Set<ConstraintViolation<T>> validateConstructorReturnValue(Constructor<? extends T> constructor, T t, Class<?>... clsArr);
}