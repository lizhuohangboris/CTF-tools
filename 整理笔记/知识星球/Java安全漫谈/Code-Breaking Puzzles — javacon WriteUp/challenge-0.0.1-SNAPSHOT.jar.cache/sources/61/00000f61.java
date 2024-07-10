package org.hibernate.validator.constraintvalidation;

import java.time.Duration;
import javax.validation.ClockProvider;
import org.hibernate.validator.Incubating;
import org.hibernate.validator.spi.scripting.ScriptEvaluator;

@Incubating
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/constraintvalidation/HibernateConstraintValidatorInitializationContext.class */
public interface HibernateConstraintValidatorInitializationContext {
    ScriptEvaluator getScriptEvaluatorForLanguage(String str);

    ClockProvider getClockProvider();

    @Incubating
    Duration getTemporalValidationTolerance();
}