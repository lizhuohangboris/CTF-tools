package org.hibernate.validator.internal.engine.constraintvalidation;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;
import javax.validation.ConstraintDeclarationException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import org.hibernate.validator.internal.engine.ValidationContext;
import org.hibernate.validator.internal.engine.ValueContext;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/constraintvalidation/ConstraintTree.class */
public abstract class ConstraintTree<A extends Annotation> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    protected final ConstraintDescriptorImpl<A> descriptor;
    private final Type validatedValueType;
    private volatile ConstraintValidator<A, ?> constraintValidatorForDefaultConstraintValidatorFactoryAndInitializationContext;

    public abstract <T> void validateConstraints(ValidationContext<T> validationContext, ValueContext<?, ?> valueContext, Set<ConstraintViolation<T>> set);

    public ConstraintTree(ConstraintDescriptorImpl<A> descriptor, Type validatedValueType) {
        this.descriptor = descriptor;
        this.validatedValueType = validatedValueType;
    }

    public static <U extends Annotation> ConstraintTree<U> of(ConstraintDescriptorImpl<U> composingDescriptor, Type validatedValueType) {
        if (composingDescriptor.getComposingConstraintImpls().isEmpty()) {
            return new SimpleConstraintTree(composingDescriptor, validatedValueType);
        }
        return new ComposingConstraintTree(composingDescriptor, validatedValueType);
    }

    public final <T> boolean validateConstraints(ValidationContext<T> executionContext, ValueContext<?, ?> valueContext) {
        Set<ConstraintViolation<T>> constraintViolations = CollectionHelper.newHashSet(5);
        validateConstraints(executionContext, valueContext, constraintViolations);
        if (!constraintViolations.isEmpty()) {
            executionContext.addConstraintFailures(constraintViolations);
            return false;
        }
        return true;
    }

    public final ConstraintDescriptorImpl<A> getDescriptor() {
        return this.descriptor;
    }

    public final Type getValidatedValueType() {
        return this.validatedValueType;
    }

    private ValidationException getExceptionForNullValidator(Type validatedValueType, String path) {
        if (this.descriptor.getConstraintType() == ConstraintDescriptorImpl.ConstraintType.CROSS_PARAMETER) {
            return LOG.getValidatorForCrossParameterConstraintMustEitherValidateObjectOrObjectArrayException(this.descriptor.getAnnotationType());
        }
        String className = validatedValueType.toString();
        if (validatedValueType instanceof Class) {
            Class<?> clazz = (Class) validatedValueType;
            if (clazz.isArray()) {
                className = clazz.getComponentType().toString() + ClassUtils.ARRAY_SUFFIX;
            } else {
                className = clazz.getName();
            }
        }
        return LOG.getNoValidatorFoundForTypeException(this.descriptor.getAnnotationType(), className, path);
    }

    public final <T> ConstraintValidator<A, ?> getInitializedConstraintValidator(ValidationContext<T> validationContext, ValueContext<?, ?> valueContext) {
        ConstraintValidator<A, ?> validator;
        if (validationContext.getConstraintValidatorFactory() == validationContext.getConstraintValidatorManager().getDefaultConstraintValidatorFactory() && validationContext.getConstraintValidatorInitializationContext() == validationContext.getConstraintValidatorManager().getDefaultConstraintValidatorInitializationContext()) {
            validator = this.constraintValidatorForDefaultConstraintValidatorFactoryAndInitializationContext;
            if (validator == null) {
                synchronized (this) {
                    validator = this.constraintValidatorForDefaultConstraintValidatorFactoryAndInitializationContext;
                    if (validator == null) {
                        validator = getInitializedConstraintValidator(validationContext);
                        this.constraintValidatorForDefaultConstraintValidatorFactoryAndInitializationContext = validator;
                    }
                }
            }
        } else {
            validator = getInitializedConstraintValidator(validationContext);
        }
        if (validator == ConstraintValidatorManager.DUMMY_CONSTRAINT_VALIDATOR) {
            throw getExceptionForNullValidator(this.validatedValueType, valueContext.getPropertyPath().asString());
        }
        return validator;
    }

    private ConstraintValidator<A, ?> getInitializedConstraintValidator(ValidationContext<?> validationContext) {
        ConstraintValidator<A, ?> validator = validationContext.getConstraintValidatorManager().getInitializedValidator(this.validatedValueType, this.descriptor, validationContext.getConstraintValidatorFactory(), validationContext.getConstraintValidatorInitializationContext());
        if (validator != null) {
            return validator;
        }
        return (ConstraintValidator<A, ?>) ConstraintValidatorManager.DUMMY_CONSTRAINT_VALIDATOR;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final <T, V> Set<ConstraintViolation<T>> validateSingleConstraint(ValidationContext<T> executionContext, ValueContext<?, ?> valueContext, ConstraintValidatorContextImpl constraintValidatorContext, ConstraintValidator<A, V> validator) {
        try {
            boolean isValid = validator.isValid(valueContext.getCurrentValidatedValue(), constraintValidatorContext);
            if (!isValid) {
                return executionContext.createConstraintViolations(valueContext, constraintValidatorContext);
            }
            return Collections.emptySet();
        } catch (RuntimeException e) {
            if (e instanceof ConstraintDeclarationException) {
                throw e;
            }
            throw LOG.getExceptionDuringIsValidCallException(e);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ConstraintTree");
        sb.append("{ descriptor=").append(this.descriptor);
        sb.append('}');
        return sb.toString();
    }
}