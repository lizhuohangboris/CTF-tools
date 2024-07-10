package javax.validation.bootstrap;

import javax.validation.Configuration;
import javax.validation.ValidationProviderResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/bootstrap/ProviderSpecificBootstrap.class */
public interface ProviderSpecificBootstrap<T extends Configuration<T>> {
    ProviderSpecificBootstrap<T> providerResolver(ValidationProviderResolver validationProviderResolver);

    T configure();
}