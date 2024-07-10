package javax.validation;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/ConstraintViolationException.class */
public class ConstraintViolationException extends ValidationException {
    private final Set<ConstraintViolation<?>> constraintViolations;

    public ConstraintViolationException(String message, Set<? extends ConstraintViolation<?>> constraintViolations) {
        super(message);
        if (constraintViolations == null) {
            this.constraintViolations = null;
        } else {
            this.constraintViolations = new HashSet(constraintViolations);
        }
    }

    public ConstraintViolationException(Set<? extends ConstraintViolation<?>> constraintViolations) {
        this(constraintViolations != null ? toString(constraintViolations) : null, constraintViolations);
    }

    public Set<ConstraintViolation<?>> getConstraintViolations() {
        return this.constraintViolations;
    }

    private static String toString(Set<? extends ConstraintViolation<?>> constraintViolations) {
        return (String) constraintViolations.stream().map(cv -> {
            return cv == null ? BeanDefinitionParserDelegate.NULL_ELEMENT : cv.getPropertyPath() + ": " + cv.getMessage();
        }).collect(Collectors.joining(", "));
    }
}