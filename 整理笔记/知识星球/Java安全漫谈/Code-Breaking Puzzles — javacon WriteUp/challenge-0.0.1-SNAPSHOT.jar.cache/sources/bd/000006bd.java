package javax.validation;

import javax.validation.valueextraction.ValueExtractor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/ValidatorContext.class */
public interface ValidatorContext {
    ValidatorContext messageInterpolator(MessageInterpolator messageInterpolator);

    ValidatorContext traversableResolver(TraversableResolver traversableResolver);

    ValidatorContext constraintValidatorFactory(ConstraintValidatorFactory constraintValidatorFactory);

    ValidatorContext parameterNameProvider(ParameterNameProvider parameterNameProvider);

    ValidatorContext clockProvider(ClockProvider clockProvider);

    ValidatorContext addValueExtractor(ValueExtractor<?> valueExtractor);

    Validator getValidator();
}