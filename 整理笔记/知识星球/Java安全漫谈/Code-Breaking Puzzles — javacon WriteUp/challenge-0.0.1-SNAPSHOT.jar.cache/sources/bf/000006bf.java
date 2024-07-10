package javax.validation.bootstrap;

import javax.validation.Configuration;
import javax.validation.ValidationProviderResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/bootstrap/GenericBootstrap.class */
public interface GenericBootstrap {
    GenericBootstrap providerResolver(ValidationProviderResolver validationProviderResolver);

    Configuration<?> configure();
}