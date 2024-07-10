package javax.validation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/UnexpectedTypeException.class */
public class UnexpectedTypeException extends ConstraintDeclarationException {
    public UnexpectedTypeException(String message) {
        super(message);
    }

    public UnexpectedTypeException() {
    }

    public UnexpectedTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedTypeException(Throwable cause) {
        super(cause);
    }
}