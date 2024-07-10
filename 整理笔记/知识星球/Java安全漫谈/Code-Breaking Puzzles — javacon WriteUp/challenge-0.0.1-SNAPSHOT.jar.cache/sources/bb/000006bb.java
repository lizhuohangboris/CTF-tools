package javax.validation;

import java.util.List;
import javax.validation.spi.ValidationProvider;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/ValidationProviderResolver.class */
public interface ValidationProviderResolver {
    List<ValidationProvider<?>> getValidationProviders();
}