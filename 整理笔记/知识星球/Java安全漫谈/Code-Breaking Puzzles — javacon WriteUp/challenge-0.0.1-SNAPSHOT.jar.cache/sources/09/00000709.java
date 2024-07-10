package javax.validation.spi;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import javax.validation.ClockProvider;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.TraversableResolver;
import javax.validation.valueextraction.ValueExtractor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/spi/ConfigurationState.class */
public interface ConfigurationState {
    boolean isIgnoreXmlConfiguration();

    MessageInterpolator getMessageInterpolator();

    Set<InputStream> getMappingStreams();

    Set<ValueExtractor<?>> getValueExtractors();

    ConstraintValidatorFactory getConstraintValidatorFactory();

    TraversableResolver getTraversableResolver();

    ParameterNameProvider getParameterNameProvider();

    ClockProvider getClockProvider();

    Map<String, String> getProperties();
}