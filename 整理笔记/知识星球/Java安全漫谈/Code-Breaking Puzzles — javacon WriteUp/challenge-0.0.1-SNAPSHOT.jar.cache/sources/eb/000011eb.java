package org.hibernate.validator.spi.scripting;

import javax.validation.ValidationException;
import org.hibernate.validator.Incubating;

@Incubating
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/spi/scripting/ScriptEvaluationException.class */
public class ScriptEvaluationException extends ValidationException {
    public ScriptEvaluationException() {
    }

    public ScriptEvaluationException(String message) {
        super(message);
    }

    public ScriptEvaluationException(Throwable cause) {
        super(cause);
    }

    public ScriptEvaluationException(String message, Throwable cause) {
        super(message, cause);
    }
}