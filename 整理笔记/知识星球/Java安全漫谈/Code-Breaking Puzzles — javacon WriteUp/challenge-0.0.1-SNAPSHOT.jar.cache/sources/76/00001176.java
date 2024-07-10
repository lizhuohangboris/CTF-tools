package org.hibernate.validator.internal.util.annotation;

import java.lang.annotation.Annotation;
import java.util.Map;
import javax.validation.ConstraintTarget;
import javax.validation.Payload;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.util.annotation.AnnotationDescriptor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/annotation/ConstraintAnnotationDescriptor.class */
public class ConstraintAnnotationDescriptor<A extends Annotation> extends AnnotationDescriptor<A> {
    public ConstraintAnnotationDescriptor(A annotation) {
        super(annotation);
    }

    public ConstraintAnnotationDescriptor(AnnotationDescriptor<A> descriptor) {
        super(descriptor);
    }

    public String getMessage() {
        return (String) getMandatoryAttribute(ConstraintHelper.MESSAGE, String.class);
    }

    public Class<?>[] getGroups() {
        return (Class[]) getMandatoryAttribute(ConstraintHelper.GROUPS, Class[].class);
    }

    public Class<? extends Payload>[] getPayload() {
        return (Class[]) getMandatoryAttribute(ConstraintHelper.PAYLOAD, Class[].class);
    }

    public ConstraintTarget getValidationAppliesTo() {
        return (ConstraintTarget) getAttribute(ConstraintHelper.VALIDATION_APPLIES_TO, ConstraintTarget.class);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/annotation/ConstraintAnnotationDescriptor$Builder.class */
    public static class Builder<S extends Annotation> extends AnnotationDescriptor.Builder<S> {
        public Builder(Class<S> type) {
            super(type);
        }

        public Builder(Class<S> type, Map<String, Object> attributes) {
            super(type, attributes);
        }

        public Builder(S annotation) {
            super(annotation);
        }

        public Builder<S> setMessage(String message) {
            setAttribute(ConstraintHelper.MESSAGE, message);
            return this;
        }

        public Builder<S> setGroups(Class<?>[] groups) {
            setAttribute(ConstraintHelper.GROUPS, groups);
            return this;
        }

        public Builder<S> setPayload(Class<?>[] payload) {
            setAttribute(ConstraintHelper.PAYLOAD, payload);
            return this;
        }

        public Builder<S> setValidationAppliesTo(ConstraintTarget validationAppliesTo) {
            setAttribute(ConstraintHelper.VALIDATION_APPLIES_TO, validationAppliesTo);
            return this;
        }

        @Override // org.hibernate.validator.internal.util.annotation.AnnotationDescriptor.Builder
        public ConstraintAnnotationDescriptor<S> build() {
            return new ConstraintAnnotationDescriptor<>(super.build());
        }
    }
}