package javax.validation;

import java.util.Locale;
import javax.validation.metadata.ConstraintDescriptor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/MessageInterpolator.class */
public interface MessageInterpolator {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/MessageInterpolator$Context.class */
    public interface Context {
        ConstraintDescriptor<?> getConstraintDescriptor();

        Object getValidatedValue();

        <T> T unwrap(Class<T> cls);
    }

    String interpolate(String str, Context context);

    String interpolate(String str, Context context, Locale locale);
}