package org.hibernate.validator;

import java.time.Duration;
import javax.validation.ValidatorFactory;
import org.hibernate.validator.spi.scripting.ScriptEvaluatorFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/HibernateValidatorFactory.class */
public interface HibernateValidatorFactory extends ValidatorFactory {
    @Incubating
    ScriptEvaluatorFactory getScriptEvaluatorFactory();

    @Incubating
    Duration getTemporalValidationTolerance();

    @Override // javax.validation.ValidatorFactory
    HibernateValidatorContext usingContext();
}