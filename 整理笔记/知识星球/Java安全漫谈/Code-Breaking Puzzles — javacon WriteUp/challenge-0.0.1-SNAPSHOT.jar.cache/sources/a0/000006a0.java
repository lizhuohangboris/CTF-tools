package javax.validation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/NoProviderFoundException.class */
public class NoProviderFoundException extends ValidationException {
    public NoProviderFoundException() {
    }

    public NoProviderFoundException(String message) {
        super(message);
    }

    public NoProviderFoundException(Throwable cause) {
        super(cause);
    }

    public NoProviderFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}