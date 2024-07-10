package org.hibernate.validator.internal.cfg.context;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintValidator;
import org.hibernate.validator.cfg.context.ConstraintDefinitionContext;
import org.hibernate.validator.internal.engine.constraintdefinition.ConstraintDefinitionContribution;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor;
import org.hibernate.validator.internal.util.CollectionHelper;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/cfg/context/ConstraintDefinitionContextImpl.class */
public class ConstraintDefinitionContextImpl<A extends Annotation> extends ConstraintContextImplBase implements ConstraintDefinitionContext<A> {
    private final Class<A> annotationType;
    private boolean includeExistingValidators;
    private final Set<ConstraintValidatorDescriptor<A>> validatorDescriptors;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConstraintDefinitionContextImpl(DefaultConstraintMapping mapping, Class<A> annotationType) {
        super(mapping);
        this.includeExistingValidators = true;
        this.validatorDescriptors = new HashSet();
        this.annotationType = annotationType;
    }

    @Override // org.hibernate.validator.cfg.context.ConstraintDefinitionContext
    public ConstraintDefinitionContext<A> includeExistingValidators(boolean includeExistingValidators) {
        this.includeExistingValidators = includeExistingValidators;
        return this;
    }

    @Override // org.hibernate.validator.cfg.context.ConstraintDefinitionContext
    public ConstraintDefinitionContext<A> validatedBy(Class<? extends ConstraintValidator<A, ?>> validator) {
        this.validatorDescriptors.add(ConstraintValidatorDescriptor.forClass(validator, this.annotationType));
        return this;
    }

    @Override // org.hibernate.validator.cfg.context.ConstraintDefinitionContext
    public <T> ConstraintDefinitionContext.ConstraintValidatorDefinitionContext<A, T> validateType(Class<T> type) {
        return new ConstraintValidatorDefinitionContextImpl(type);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConstraintDefinitionContribution<A> build() {
        return new ConstraintDefinitionContribution<>(this.annotationType, CollectionHelper.newArrayList(this.validatorDescriptors), this.includeExistingValidators);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/cfg/context/ConstraintDefinitionContextImpl$ConstraintValidatorDefinitionContextImpl.class */
    private class ConstraintValidatorDefinitionContextImpl<T> implements ConstraintDefinitionContext.ConstraintValidatorDefinitionContext<A, T> {
        private final Class<T> type;

        public ConstraintValidatorDefinitionContextImpl(Class<T> type) {
            this.type = type;
        }

        @Override // org.hibernate.validator.cfg.context.ConstraintDefinitionContext.ConstraintValidatorDefinitionContext
        public ConstraintDefinitionContext<A> with(ConstraintDefinitionContext.ValidationCallable<T> vc) {
            ConstraintDefinitionContextImpl.this.validatorDescriptors.add(ConstraintValidatorDescriptor.forLambda(ConstraintDefinitionContextImpl.this.annotationType, this.type, vc));
            return ConstraintDefinitionContextImpl.this;
        }
    }
}