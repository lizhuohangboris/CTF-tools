package javax.validation.spi;

import javax.validation.ValidationProviderResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/spi/BootstrapState.class */
public interface BootstrapState {
    ValidationProviderResolver getValidationProviderResolver();

    ValidationProviderResolver getDefaultValidationProviderResolver();
}