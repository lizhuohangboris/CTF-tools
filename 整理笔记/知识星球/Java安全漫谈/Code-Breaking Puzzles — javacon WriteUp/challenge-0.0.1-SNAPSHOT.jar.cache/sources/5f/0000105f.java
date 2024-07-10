package org.hibernate.validator.internal.constraintvalidators.hv.time;

import java.time.Duration;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraints.time.DurationMax;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/hv/time/DurationMaxValidator.class */
public class DurationMaxValidator implements ConstraintValidator<DurationMax, Duration> {
    private Duration maxDuration;
    private boolean inclusive;

    @Override // javax.validation.ConstraintValidator
    public void initialize(DurationMax constraintAnnotation) {
        this.maxDuration = Duration.ofNanos(constraintAnnotation.nanos()).plusMillis(constraintAnnotation.millis()).plusSeconds(constraintAnnotation.seconds()).plusMinutes(constraintAnnotation.minutes()).plusHours(constraintAnnotation.hours()).plusDays(constraintAnnotation.days());
        this.inclusive = constraintAnnotation.inclusive();
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(Duration value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        int comparisonResult = this.maxDuration.compareTo(value);
        return this.inclusive ? comparisonResult >= 0 : comparisonResult > 0;
    }
}