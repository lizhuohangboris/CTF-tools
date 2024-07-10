package org.hibernate.validator.spi.scripting;

import javax.validation.ValidationException;
import org.hibernate.validator.Incubating;

@Incubating
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/spi/scripting/ScriptEvaluatorNotFoundException.class */
public class ScriptEvaluatorNotFoundException extends ValidationException {
    public ScriptEvaluatorNotFoundException() {
    }

    public ScriptEvaluatorNotFoundException(String message) {
        super(message);
    }

    public ScriptEvaluatorNotFoundException(Throwable cause) {
        super(cause);
    }

    public ScriptEvaluatorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}