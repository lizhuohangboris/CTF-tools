package org.hibernate.validator.internal.engine.constraintvalidation;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import org.hibernate.validator.internal.util.TypeHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/constraintvalidation/ClassBasedValidatorDescriptor.class */
public class ClassBasedValidatorDescriptor<A extends Annotation> implements ConstraintValidatorDescriptor<A> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Class<? extends ConstraintValidator<A, ?>> validatorClass;
    private final Type validatedType;
    private final EnumSet<ValidationTarget> validationTargets;

    private ClassBasedValidatorDescriptor(Class<? extends ConstraintValidator<A, ?>> validatorClass) {
        this.validatorClass = validatorClass;
        this.validatedType = TypeHelper.extractValidatedType(validatorClass);
        this.validationTargets = determineValidationTargets(validatorClass);
    }

    public static <T extends Annotation> ClassBasedValidatorDescriptor<T> of(Class<? extends ConstraintValidator<T, ?>> validatorClass, Class<? extends Annotation> registeredConstraintAnnotationType) {
        Type definedConstraintAnnotationType = TypeHelper.extractConstraintType(validatorClass);
        if (!registeredConstraintAnnotationType.equals(definedConstraintAnnotationType)) {
            throw LOG.getConstraintValidatorDefinitionConstraintMismatchException(validatorClass, registeredConstraintAnnotationType, definedConstraintAnnotationType);
        }
        return new ClassBasedValidatorDescriptor<>(validatorClass);
    }

    private static EnumSet<ValidationTarget> determineValidationTargets(Class<? extends ConstraintValidator<?, ?>> validatorClass) {
        SupportedValidationTarget supportedTargetAnnotation = (SupportedValidationTarget) validatorClass.getAnnotation(SupportedValidationTarget.class);
        if (supportedTargetAnnotation == null) {
            return EnumSet.of(ValidationTarget.ANNOTATED_ELEMENT);
        }
        return EnumSet.copyOf((Collection) Arrays.asList(supportedTargetAnnotation.value()));
    }

    @Override // org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor
    public Class<? extends ConstraintValidator<A, ?>> getValidatorClass() {
        return this.validatorClass;
    }

    @Override // org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor
    public ConstraintValidator<A, ?> newInstance(ConstraintValidatorFactory constraintValidatorFactory) {
        ConstraintValidator<A, ?> constraintValidator = constraintValidatorFactory.getInstance(this.validatorClass);
        if (constraintValidator == null) {
            throw LOG.getConstraintValidatorFactoryMustNotReturnNullException(this.validatorClass);
        }
        return constraintValidator;
    }

    @Override // org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor
    public Type getValidatedType() {
        return this.validatedType;
    }

    @Override // org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor
    public EnumSet<ValidationTarget> getValidationTargets() {
        return this.validationTargets;
    }
}