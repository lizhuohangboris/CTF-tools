package org.hibernate.validator.internal.xml.mapping;

import java.lang.annotation.ElementType;
import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.xml.AbstractStaxBuilder;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/CrossParameterStaxBuilder.class */
public class CrossParameterStaxBuilder extends AbstractStaxBuilder {
    private static final String CROSS_PARAMETER_QNAME_LOCAL_PART = "cross-parameter";
    private static final QName IGNORE_ANNOTATIONS_QNAME = new QName("ignore-annotations");
    protected final ClassLoadingHelper classLoadingHelper;
    protected final ConstraintHelper constraintHelper;
    protected final TypeResolutionHelper typeResolutionHelper;
    protected final ValueExtractorManager valueExtractorManager;
    protected final DefaultPackageStaxBuilder defaultPackageStaxBuilder;
    protected final AnnotationProcessingOptionsImpl annotationProcessingOptions;
    protected Optional<Boolean> ignoreAnnotations;
    protected final List<ConstraintTypeStaxBuilder> constraintTypeStaxBuilders = new ArrayList();

    /* JADX INFO: Access modifiers changed from: package-private */
    public CrossParameterStaxBuilder(ClassLoadingHelper classLoadingHelper, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, DefaultPackageStaxBuilder defaultPackageStaxBuilder, AnnotationProcessingOptionsImpl annotationProcessingOptions) {
        this.classLoadingHelper = classLoadingHelper;
        this.defaultPackageStaxBuilder = defaultPackageStaxBuilder;
        this.constraintHelper = constraintHelper;
        this.typeResolutionHelper = typeResolutionHelper;
        this.valueExtractorManager = valueExtractorManager;
        this.annotationProcessingOptions = annotationProcessingOptions;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    public String getAcceptableQName() {
        return CROSS_PARAMETER_QNAME_LOCAL_PART;
    }

    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
        this.ignoreAnnotations = readAttribute(xmlEvent.asStartElement(), IGNORE_ANNOTATIONS_QNAME).map(Boolean::parseBoolean);
        ConstraintTypeStaxBuilder constraintTypeStaxBuilder = getNewConstraintTypeStaxBuilder();
        while (true) {
            if (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(getAcceptableQName())) {
                xmlEvent = xmlEventReader.nextEvent();
                if (constraintTypeStaxBuilder.process(xmlEventReader, xmlEvent)) {
                    this.constraintTypeStaxBuilders.add(constraintTypeStaxBuilder);
                    constraintTypeStaxBuilder = getNewConstraintTypeStaxBuilder();
                }
            } else {
                return;
            }
        }
    }

    private ConstraintTypeStaxBuilder getNewConstraintTypeStaxBuilder() {
        return new ConstraintTypeStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Set<MetaConstraint<?>> build(Executable executable) {
        ConstraintLocation constraintLocation = ConstraintLocation.forCrossParameter(executable);
        Set<MetaConstraint<?>> crossParameterConstraints = (Set) this.constraintTypeStaxBuilders.stream().map(builder -> {
            return builder.build(constraintLocation, ElementType.METHOD, ConstraintDescriptorImpl.ConstraintType.CROSS_PARAMETER);
        }).collect(Collectors.toSet());
        if (this.ignoreAnnotations.isPresent()) {
            this.annotationProcessingOptions.ignoreConstraintAnnotationsForCrossParameterConstraint(executable, this.ignoreAnnotations.get());
        }
        return crossParameterConstraints;
    }
}