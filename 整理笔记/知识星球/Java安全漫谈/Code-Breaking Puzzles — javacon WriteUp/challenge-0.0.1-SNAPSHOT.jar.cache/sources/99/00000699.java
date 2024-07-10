package javax.validation;

import javax.validation.metadata.ConstraintDescriptor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/ConstraintViolation.class */
public interface ConstraintViolation<T> {
    String getMessage();

    String getMessageTemplate();

    T getRootBean();

    Class<T> getRootBeanClass();

    Object getLeafBean();

    Object[] getExecutableParameters();

    Object getExecutableReturnValue();

    Path getPropertyPath();

    Object getInvalidValue();

    ConstraintDescriptor<?> getConstraintDescriptor();

    <U> U unwrap(Class<U> cls);
}