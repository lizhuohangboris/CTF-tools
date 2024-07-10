package org.hibernate.validator.internal.xml.mapping;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.xml.AbstractStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.ContainerElementTypeConfigurationBuilder;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/AbstractConstrainedElementStaxBuilder.class */
public abstract class AbstractConstrainedElementStaxBuilder extends AbstractStaxBuilder {
    private static final QName IGNORE_ANNOTATIONS_QNAME = new QName("ignore-annotations");
    protected final ClassLoadingHelper classLoadingHelper;
    protected final ConstraintHelper constraintHelper;
    protected final TypeResolutionHelper typeResolutionHelper;
    protected final ValueExtractorManager valueExtractorManager;
    protected final DefaultPackageStaxBuilder defaultPackageStaxBuilder;
    protected final AnnotationProcessingOptionsImpl annotationProcessingOptions;
    protected String mainAttributeValue;
    protected Optional<Boolean> ignoreAnnotations;
    protected final GroupConversionStaxBuilder groupConversionBuilder;
    protected final ValidStaxBuilder validStaxBuilder = new ValidStaxBuilder();
    protected final ContainerElementTypeConfigurationBuilder containerElementTypeConfigurationBuilder = new ContainerElementTypeConfigurationBuilder();
    protected final List<ConstraintTypeStaxBuilder> constraintTypeStaxBuilders = new ArrayList();

    abstract Optional<QName> getMainAttributeValueQname();

    public AbstractConstrainedElementStaxBuilder(ClassLoadingHelper classLoadingHelper, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, DefaultPackageStaxBuilder defaultPackageStaxBuilder, AnnotationProcessingOptionsImpl annotationProcessingOptions) {
        this.classLoadingHelper = classLoadingHelper;
        this.defaultPackageStaxBuilder = defaultPackageStaxBuilder;
        this.constraintHelper = constraintHelper;
        this.typeResolutionHelper = typeResolutionHelper;
        this.valueExtractorManager = valueExtractorManager;
        this.groupConversionBuilder = new GroupConversionStaxBuilder(classLoadingHelper, defaultPackageStaxBuilder);
        this.annotationProcessingOptions = annotationProcessingOptions;
    }

    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
        Optional<QName> mainAttributeValueQname = getMainAttributeValueQname();
        if (mainAttributeValueQname.isPresent()) {
            this.mainAttributeValue = readAttribute(xmlEvent.asStartElement(), mainAttributeValueQname.get()).get();
        }
        this.ignoreAnnotations = readAttribute(xmlEvent.asStartElement(), IGNORE_ANNOTATIONS_QNAME).map(Boolean::parseBoolean);
        ConstraintTypeStaxBuilder constraintTypeStaxBuilder = getNewConstraintTypeStaxBuilder();
        ContainerElementTypeStaxBuilder containerElementTypeStaxBuilder = getNewContainerElementTypeStaxBuilder();
        while (true) {
            if (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(getAcceptableQName())) {
                xmlEvent = xmlEventReader.nextEvent();
                this.validStaxBuilder.process(xmlEventReader, xmlEvent);
                this.groupConversionBuilder.process(xmlEventReader, xmlEvent);
                if (constraintTypeStaxBuilder.process(xmlEventReader, xmlEvent)) {
                    this.constraintTypeStaxBuilders.add(constraintTypeStaxBuilder);
                    constraintTypeStaxBuilder = getNewConstraintTypeStaxBuilder();
                }
                if (containerElementTypeStaxBuilder.process(xmlEventReader, xmlEvent)) {
                    this.containerElementTypeConfigurationBuilder.add(containerElementTypeStaxBuilder);
                    containerElementTypeStaxBuilder = getNewContainerElementTypeStaxBuilder();
                }
            } else {
                return;
            }
        }
    }

    private ConstraintTypeStaxBuilder getNewConstraintTypeStaxBuilder() {
        return new ConstraintTypeStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder);
    }

    private ContainerElementTypeStaxBuilder getNewContainerElementTypeStaxBuilder() {
        return new ContainerElementTypeStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder);
    }

    public ContainerElementTypeConfigurationBuilder.ContainerElementTypeConfiguration getContainerElementTypeConfiguration(Type type, ConstraintLocation constraintLocation) {
        return this.containerElementTypeConfigurationBuilder.build(constraintLocation, type);
    }

    public CascadingMetaDataBuilder getCascadingMetaData(Map<TypeVariable<?>, CascadingMetaDataBuilder> containerElementTypesCascadingMetaData, Type type) {
        return CascadingMetaDataBuilder.annotatedObject(type, this.validStaxBuilder.build(), containerElementTypesCascadingMetaData, this.groupConversionBuilder.build());
    }
}