package org.hibernate.validator.internal.constraintvalidators.hv.time;

import java.time.Duration;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraints.time.DurationMin;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/hv/time/DurationMinValidator.class */
public class DurationMinValidator implements ConstraintValidator<DurationMin, Duration> {
    private Duration minDuration;
    private boolean inclusive;

    @Override // javax.validation.ConstraintValidator
    public void initialize(DurationMin constraintAnnotation) {
        this.minDuration = Duration.ofNanos(constraintAnnotation.nanos()).plusMillis(constraintAnnotation.millis()).plusSeconds(constraintAnnotation.seconds()).plusMinutes(constraintAnnotation.minutes()).plusHours(constraintAnnotation.hours()).plusDays(constraintAnnotation.days());
        this.inclusive = constraintAnnotation.inclusive();
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(Duration value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        int comparisonResult = this.minDuration.compareTo(value);
        return this.inclusive ? comparisonResult <= 0 : comparisonResult < 0;
    }
}