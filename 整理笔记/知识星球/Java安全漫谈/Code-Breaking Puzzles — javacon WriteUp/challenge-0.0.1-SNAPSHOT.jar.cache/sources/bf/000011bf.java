package org.hibernate.validator.internal.xml.mapping;

import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Executable;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ValidationException;
import javax.xml.namespace.QName;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;
import org.hibernate.validator.internal.metadata.raw.ConstrainedParameter;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.xml.mapping.ContainerElementTypeConfigurationBuilder;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/ConstrainedParameterStaxBuilder.class */
public class ConstrainedParameterStaxBuilder extends AbstractConstrainedElementStaxBuilder {
    private static final String PARAMETER_QNAME_LOCAL_PART = "parameter";
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final QName TYPE_QNAME = new QName("type");

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConstrainedParameterStaxBuilder(ClassLoadingHelper classLoadingHelper, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, DefaultPackageStaxBuilder defaultPackageStaxBuilder, AnnotationProcessingOptionsImpl annotationProcessingOptions) {
        super(classLoadingHelper, constraintHelper, typeResolutionHelper, valueExtractorManager, defaultPackageStaxBuilder, annotationProcessingOptions);
    }

    @Override // org.hibernate.validator.internal.xml.mapping.AbstractConstrainedElementStaxBuilder
    Optional<QName> getMainAttributeValueQname() {
        return Optional.of(TYPE_QNAME);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    public String getAcceptableQName() {
        return PARAMETER_QNAME_LOCAL_PART;
    }

    public Class<?> getParameterType(Class<?> beanClass) {
        try {
            return this.classLoadingHelper.loadClass(this.mainAttributeValue, this.defaultPackageStaxBuilder.build().orElse(""));
        } catch (ValidationException e) {
            throw LOG.getInvalidParameterTypeException(this.mainAttributeValue, beanClass);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConstrainedParameter build(Executable executable, int index) {
        ConstraintLocation constraintLocation = ConstraintLocation.forParameter(executable, index);
        Type type = ReflectionHelper.typeOf(executable, index);
        Set<MetaConstraint<?>> metaConstraints = (Set) this.constraintTypeStaxBuilders.stream().map(builder -> {
            return builder.build(constraintLocation, ElementType.PARAMETER, null);
        }).collect(Collectors.toSet());
        ContainerElementTypeConfigurationBuilder.ContainerElementTypeConfiguration containerElementTypeConfiguration = getContainerElementTypeConfiguration(type, constraintLocation);
        if (this.ignoreAnnotations.isPresent()) {
            this.annotationProcessingOptions.ignoreConstraintAnnotationsOnParameter(executable, index, this.ignoreAnnotations.get());
        }
        ConstrainedParameter constrainedParameter = new ConstrainedParameter(ConfigurationSource.XML, executable, type, index, metaConstraints, containerElementTypeConfiguration.getMetaConstraints(), getCascadingMetaData(containerElementTypeConfiguration.getTypeParametersCascadingMetaData(), type));
        return constrainedParameter;
    }
}