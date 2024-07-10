package org.hibernate.validator.internal.cfg.context;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.core.MetaConstraints;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/cfg/context/ConstraintMappingContextImplBase.class */
public abstract class ConstraintMappingContextImplBase extends ConstraintContextImplBase {
    private final Set<ConfiguredConstraint<?>> constraints;

    protected abstract ConstraintDescriptorImpl.ConstraintType getConstraintType();

    public ConstraintMappingContextImplBase(DefaultConstraintMapping mapping) {
        super(mapping);
        this.constraints = CollectionHelper.newHashSet();
    }

    public DefaultConstraintMapping getConstraintMapping() {
        return this.mapping;
    }

    public void addConstraint(ConfiguredConstraint<?> constraint) {
        this.constraints.add(constraint);
    }

    public Set<MetaConstraint<?>> getConstraints(ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
        if (this.constraints == null) {
            return Collections.emptySet();
        }
        Set<MetaConstraint<?>> metaConstraints = CollectionHelper.newHashSet();
        for (ConfiguredConstraint<?> configuredConstraint : this.constraints) {
            metaConstraints.add(asMetaConstraint(configuredConstraint, constraintHelper, typeResolutionHelper, valueExtractorManager));
        }
        return metaConstraints;
    }

    private <A extends Annotation> MetaConstraint<A> asMetaConstraint(ConfiguredConstraint<A> config, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
        ConstraintDescriptorImpl<A> constraintDescriptor = new ConstraintDescriptorImpl<>(constraintHelper, config.getLocation().getMember(), config.createAnnotationDescriptor(), config.getElementType(), getConstraintType());
        return MetaConstraints.create(typeResolutionHelper, valueExtractorManager, constraintDescriptor, config.getLocation());
    }
}