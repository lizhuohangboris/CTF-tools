package javax.validation;

import java.io.InputStream;
import javax.validation.Configuration;
import javax.validation.valueextraction.ValueExtractor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/Configuration.class */
public interface Configuration<T extends Configuration<T>> {
    T ignoreXmlConfiguration();

    T messageInterpolator(MessageInterpolator messageInterpolator);

    T traversableResolver(TraversableResolver traversableResolver);

    T constraintValidatorFactory(ConstraintValidatorFactory constraintValidatorFactory);

    T parameterNameProvider(ParameterNameProvider parameterNameProvider);

    T clockProvider(ClockProvider clockProvider);

    T addValueExtractor(ValueExtractor<?> valueExtractor);

    T addMapping(InputStream inputStream);

    T addProperty(String str, String str2);

    MessageInterpolator getDefaultMessageInterpolator();

    TraversableResolver getDefaultTraversableResolver();

    ConstraintValidatorFactory getDefaultConstraintValidatorFactory();

    ParameterNameProvider getDefaultParameterNameProvider();

    ClockProvider getDefaultClockProvider();

    BootstrapConfiguration getBootstrapConfiguration();

    ValidatorFactory buildValidatorFactory();
}