package org.hibernate.validator.internal.xml.mapping;

import java.lang.reflect.Executable;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.ExecutableHelper;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.xml.mapping.ContainerElementTypeConfigurationBuilder;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/ReturnValueStaxBuilder.class */
public class ReturnValueStaxBuilder extends AbstractConstrainedElementStaxBuilder {
    private static final String RETURN_VALUE_QNAME_LOCAL_PART = "return-value";

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReturnValueStaxBuilder(ClassLoadingHelper classLoadingHelper, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, DefaultPackageStaxBuilder defaultPackageStaxBuilder, AnnotationProcessingOptionsImpl annotationProcessingOptions) {
        super(classLoadingHelper, constraintHelper, typeResolutionHelper, valueExtractorManager, defaultPackageStaxBuilder, annotationProcessingOptions);
    }

    @Override // org.hibernate.validator.internal.xml.mapping.AbstractConstrainedElementStaxBuilder
    Optional<QName> getMainAttributeValueQname() {
        return Optional.empty();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    public String getAcceptableQName() {
        return RETURN_VALUE_QNAME_LOCAL_PART;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CascadingMetaDataBuilder build(Executable executable, Set<MetaConstraint<?>> returnValueConstraints, Set<MetaConstraint<?>> returnValueTypeArgumentConstraints) {
        ConstraintLocation constraintLocation = ConstraintLocation.forReturnValue(executable);
        returnValueConstraints.addAll((Collection) this.constraintTypeStaxBuilders.stream().map(builder -> {
            return builder.build(constraintLocation, ExecutableHelper.getElementType(executable), ConstraintDescriptorImpl.ConstraintType.GENERIC);
        }).collect(Collectors.toSet()));
        ContainerElementTypeConfigurationBuilder.ContainerElementTypeConfiguration containerElementTypeConfiguration = getContainerElementTypeConfiguration(ReflectionHelper.typeOf(executable), constraintLocation);
        returnValueTypeArgumentConstraints.addAll(containerElementTypeConfiguration.getMetaConstraints());
        if (this.ignoreAnnotations.isPresent()) {
            this.annotationProcessingOptions.ignoreConstraintAnnotationsForReturnValue(executable, this.ignoreAnnotations.get());
        }
        return getCascadingMetaData(containerElementTypeConfiguration.getTypeParametersCascadingMetaData(), ReflectionHelper.typeOf(executable));
    }
}