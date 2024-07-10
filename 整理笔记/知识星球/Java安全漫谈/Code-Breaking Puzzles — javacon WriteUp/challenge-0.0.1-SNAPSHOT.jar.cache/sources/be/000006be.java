package javax.validation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/ValidatorFactory.class */
public interface ValidatorFactory extends AutoCloseable {
    Validator getValidator();

    ValidatorContext usingContext();

    MessageInterpolator getMessageInterpolator();

    TraversableResolver getTraversableResolver();

    ConstraintValidatorFactory getConstraintValidatorFactory();

    ParameterNameProvider getParameterNameProvider();

    ClockProvider getClockProvider();

    <T> T unwrap(Class<T> cls);

    @Override // java.lang.AutoCloseable
    void close();
}