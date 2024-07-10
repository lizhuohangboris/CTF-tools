package org.hibernate.validator.internal.engine.constraintvalidation;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintViolation;
import org.hibernate.validator.constraints.CompositionType;
import org.hibernate.validator.internal.engine.ValidationContext;
import org.hibernate.validator.internal.engine.ValueContext;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/constraintvalidation/ComposingConstraintTree.class */
public class ComposingConstraintTree<B extends Annotation> extends ConstraintTree<B> {
    private static final Log LOG;
    private final List<ConstraintTree<?>> children;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !ComposingConstraintTree.class.desiredAssertionStatus();
        LOG = LoggerFactory.make(MethodHandles.lookup());
    }

    public ComposingConstraintTree(ConstraintDescriptorImpl<B> descriptor, Type validatedValueType) {
        super(descriptor, validatedValueType);
        this.children = (List) descriptor.getComposingConstraintImpls().stream().map(desc -> {
            return createConstraintTree(desc);
        }).collect(Collectors.collectingAndThen(Collectors.toList(), CollectionHelper::toImmutableList));
    }

    private <U extends Annotation> ConstraintTree<U> createConstraintTree(ConstraintDescriptorImpl<U> composingDescriptor) {
        if (composingDescriptor.getComposingConstraintImpls().isEmpty()) {
            return new SimpleConstraintTree(composingDescriptor, getValidatedValueType());
        }
        return new ComposingConstraintTree(composingDescriptor, getValidatedValueType());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.engine.constraintvalidation.ConstraintTree
    public <T> void validateConstraints(ValidationContext<T> validationContext, ValueContext<?, ?> valueContext, Set<ConstraintViolation<T>> constraintViolations) {
        Set<ConstraintViolation<T>> localViolations;
        CompositionResult compositionResult = validateComposingConstraints(validationContext, valueContext, constraintViolations);
        if (mainConstraintNeedsEvaluation(validationContext, constraintViolations)) {
            if (LOG.isTraceEnabled()) {
                LOG.tracef("Validating value %s against constraint defined by %s.", valueContext.getCurrentValidatedValue(), this.descriptor);
            }
            ConstraintValidator initializedConstraintValidator = getInitializedConstraintValidator(validationContext, valueContext);
            ConstraintValidatorContextImpl constraintValidatorContext = new ConstraintValidatorContextImpl(validationContext.getParameterNames(), validationContext.getClockProvider(), valueContext.getPropertyPath(), this.descriptor, validationContext.getConstraintValidatorPayload());
            localViolations = validateSingleConstraint(validationContext, valueContext, constraintValidatorContext, initializedConstraintValidator);
            if (localViolations.isEmpty()) {
                compositionResult.setAtLeastOneTrue(true);
            } else {
                compositionResult.setAllTrue(false);
            }
        } else {
            localViolations = Collections.emptySet();
        }
        if (!passesCompositionTypeRequirement(constraintViolations, compositionResult)) {
            prepareFinalConstraintViolations(validationContext, valueContext, constraintViolations, localViolations);
        }
    }

    private <T> boolean mainConstraintNeedsEvaluation(ValidationContext<T> executionContext, Set<ConstraintViolation<T>> constraintViolations) {
        if (!this.descriptor.getComposingConstraints().isEmpty() && this.descriptor.getMatchingConstraintValidatorDescriptors().isEmpty()) {
            return false;
        }
        if (constraintViolations.isEmpty()) {
            return true;
        }
        if ((this.descriptor.isReportAsSingleViolation() && this.descriptor.getCompositionType() == CompositionType.AND) || executionContext.isFailFastModeEnabled()) {
            return false;
        }
        return true;
    }

    private <T> void prepareFinalConstraintViolations(ValidationContext<T> executionContext, ValueContext<?, ?> valueContext, Set<ConstraintViolation<T>> constraintViolations, Set<ConstraintViolation<T>> localViolations) {
        if (reportAsSingleViolation()) {
            constraintViolations.clear();
            if (localViolations.isEmpty()) {
                String message = getDescriptor().getMessageTemplate();
                ConstraintViolationCreationContext constraintViolationCreationContext = new ConstraintViolationCreationContext(message, valueContext.getPropertyPath());
                ConstraintViolation<T> violation = executionContext.createConstraintViolation(valueContext, constraintViolationCreationContext, this.descriptor);
                constraintViolations.add(violation);
            }
        }
        constraintViolations.addAll(localViolations);
    }

    private <T> CompositionResult validateComposingConstraints(ValidationContext<T> executionContext, ValueContext<?, ?> valueContext, Set<ConstraintViolation<T>> constraintViolations) {
        CompositionResult compositionResult = new CompositionResult(true, false);
        for (ConstraintTree<?> tree : this.children) {
            Set<ConstraintViolation<T>> tmpViolations = CollectionHelper.newHashSet(5);
            tree.validateConstraints(executionContext, valueContext, tmpViolations);
            constraintViolations.addAll(tmpViolations);
            if (tmpViolations.isEmpty()) {
                compositionResult.setAtLeastOneTrue(true);
                if (this.descriptor.getCompositionType() == CompositionType.OR) {
                    break;
                }
            } else {
                compositionResult.setAllTrue(false);
                if (this.descriptor.getCompositionType() == CompositionType.AND) {
                    if (executionContext.isFailFastModeEnabled() || this.descriptor.isReportAsSingleViolation()) {
                        break;
                    }
                } else {
                    continue;
                }
            }
        }
        return compositionResult;
    }

    private boolean passesCompositionTypeRequirement(Set<?> constraintViolations, CompositionResult compositionResult) {
        CompositionType compositionType = getDescriptor().getCompositionType();
        boolean passedValidation = false;
        switch (compositionType) {
            case OR:
                passedValidation = compositionResult.isAtLeastOneTrue();
                break;
            case AND:
                passedValidation = compositionResult.isAllTrue();
                break;
            case ALL_FALSE:
                passedValidation = !compositionResult.isAtLeastOneTrue();
                break;
        }
        if ($assertionsDisabled || !passedValidation || compositionType != CompositionType.AND || constraintViolations.isEmpty()) {
            if (passedValidation) {
                constraintViolations.clear();
            }
            return passedValidation;
        }
        throw new AssertionError();
    }

    private boolean reportAsSingleViolation() {
        return getDescriptor().isReportAsSingleViolation() || getDescriptor().getCompositionType() == CompositionType.ALL_FALSE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/constraintvalidation/ComposingConstraintTree$CompositionResult.class */
    public static final class CompositionResult {
        private boolean allTrue;
        private boolean atLeastOneTrue;

        CompositionResult(boolean allTrue, boolean atLeastOneTrue) {
            this.allTrue = allTrue;
            this.atLeastOneTrue = atLeastOneTrue;
        }

        public boolean isAllTrue() {
            return this.allTrue;
        }

        public boolean isAtLeastOneTrue() {
            return this.atLeastOneTrue;
        }

        public void setAllTrue(boolean allTrue) {
            this.allTrue = allTrue;
        }

        public void setAtLeastOneTrue(boolean atLeastOneTrue) {
            this.atLeastOneTrue = atLeastOneTrue;
        }
    }
}