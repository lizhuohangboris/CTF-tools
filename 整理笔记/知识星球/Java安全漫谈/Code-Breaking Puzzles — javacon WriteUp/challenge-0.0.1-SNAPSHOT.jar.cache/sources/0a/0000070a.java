package javax.validation.spi;

import javax.validation.Configuration;
import javax.validation.ValidatorFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/spi/ValidationProvider.class */
public interface ValidationProvider<T extends Configuration<T>> {
    T createSpecializedConfiguration(BootstrapState bootstrapState);

    Configuration<?> createGenericConfiguration(BootstrapState bootstrapState);

    ValidatorFactory buildValidatorFactory(ConfigurationState configurationState);
}