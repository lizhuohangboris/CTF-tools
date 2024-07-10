package org.hibernate.validator.internal.engine.constraintvalidation;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintViolation;
import org.hibernate.validator.internal.engine.ValidationContext;
import org.hibernate.validator.internal.engine.ValueContext;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/constraintvalidation/SimpleConstraintTree.class */
public class SimpleConstraintTree<B extends Annotation> extends ConstraintTree<B> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());

    public SimpleConstraintTree(ConstraintDescriptorImpl<B> descriptor, Type validatedValueType) {
        super(descriptor, validatedValueType);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.engine.constraintvalidation.ConstraintTree
    public <T> void validateConstraints(ValidationContext<T> validationContext, ValueContext<?, ?> valueContext, Set<ConstraintViolation<T>> constraintViolations) {
        if (LOG.isTraceEnabled()) {
            LOG.tracef("Validating value %s against constraint defined by %s.", valueContext.getCurrentValidatedValue(), this.descriptor);
        }
        ConstraintValidator initializedConstraintValidator = getInitializedConstraintValidator(validationContext, valueContext);
        ConstraintValidatorContextImpl constraintValidatorContext = new ConstraintValidatorContextImpl(validationContext.getParameterNames(), validationContext.getClockProvider(), valueContext.getPropertyPath(), this.descriptor, validationContext.getConstraintValidatorPayload());
        constraintViolations.addAll(validateSingleConstraint(validationContext, valueContext, constraintValidatorContext, initializedConstraintValidator));
    }
}