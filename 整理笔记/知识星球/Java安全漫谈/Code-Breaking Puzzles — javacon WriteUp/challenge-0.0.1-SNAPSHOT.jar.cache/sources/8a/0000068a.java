package javax.validation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/ConstraintDefinitionException.class */
public class ConstraintDefinitionException extends ValidationException {
    public ConstraintDefinitionException(String message) {
        super(message);
    }

    public ConstraintDefinitionException() {
    }

    public ConstraintDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConstraintDefinitionException(Throwable cause) {
        super(cause);
    }
}