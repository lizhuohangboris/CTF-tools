package org.hibernate.validator.internal.xml.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.xml.AbstractStaxBuilder;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/AbstractConstrainedExecutableElementStaxBuilder.class */
public abstract class AbstractConstrainedExecutableElementStaxBuilder extends AbstractStaxBuilder {
    private static final QName IGNORE_ANNOTATIONS_QNAME = new QName("ignore-annotations");
    protected final ClassLoadingHelper classLoadingHelper;
    protected final ConstraintHelper constraintHelper;
    protected final TypeResolutionHelper typeResolutionHelper;
    protected final ValueExtractorManager valueExtractorManager;
    protected final DefaultPackageStaxBuilder defaultPackageStaxBuilder;
    protected final AnnotationProcessingOptionsImpl annotationProcessingOptions;
    protected String mainAttributeValue;
    protected Optional<Boolean> ignoreAnnotations;
    protected final List<ConstrainedParameterStaxBuilder> constrainedParameterStaxBuilders = new ArrayList();
    private CrossParameterStaxBuilder crossParameterStaxBuilder;
    private ReturnValueStaxBuilder returnValueStaxBuilder;

    abstract Optional<QName> getMainAttributeValueQname();

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractConstrainedExecutableElementStaxBuilder(ClassLoadingHelper classLoadingHelper, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, DefaultPackageStaxBuilder defaultPackageStaxBuilder, AnnotationProcessingOptionsImpl annotationProcessingOptions) {
        this.classLoadingHelper = classLoadingHelper;
        this.defaultPackageStaxBuilder = defaultPackageStaxBuilder;
        this.constraintHelper = constraintHelper;
        this.typeResolutionHelper = typeResolutionHelper;
        this.valueExtractorManager = valueExtractorManager;
        this.annotationProcessingOptions = annotationProcessingOptions;
    }

    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
        Optional<QName> mainAttributeValueQname = getMainAttributeValueQname();
        if (mainAttributeValueQname.isPresent()) {
            this.mainAttributeValue = readAttribute(xmlEvent.asStartElement(), mainAttributeValueQname.get()).get();
        }
        this.ignoreAnnotations = readAttribute(xmlEvent.asStartElement(), IGNORE_ANNOTATIONS_QNAME).map(Boolean::parseBoolean);
        ConstrainedParameterStaxBuilder constrainedParameterStaxBuilder = getNewConstrainedParameterStaxBuilder();
        ReturnValueStaxBuilder localReturnValueStaxBuilder = getNewReturnValueStaxBuilder();
        CrossParameterStaxBuilder localCrossParameterStaxBuilder = getNewCrossParameterStaxBuilder();
        while (true) {
            if (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(getAcceptableQName())) {
                xmlEvent = xmlEventReader.nextEvent();
                if (constrainedParameterStaxBuilder.process(xmlEventReader, xmlEvent)) {
                    this.constrainedParameterStaxBuilders.add(constrainedParameterStaxBuilder);
                    constrainedParameterStaxBuilder = getNewConstrainedParameterStaxBuilder();
                } else if (localReturnValueStaxBuilder.process(xmlEventReader, xmlEvent)) {
                    this.returnValueStaxBuilder = localReturnValueStaxBuilder;
                } else if (localCrossParameterStaxBuilder.process(xmlEventReader, xmlEvent)) {
                    this.crossParameterStaxBuilder = localCrossParameterStaxBuilder;
                }
            } else {
                return;
            }
        }
    }

    private ConstrainedParameterStaxBuilder getNewConstrainedParameterStaxBuilder() {
        return new ConstrainedParameterStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder, this.annotationProcessingOptions);
    }

    private CrossParameterStaxBuilder getNewCrossParameterStaxBuilder() {
        return new CrossParameterStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder, this.annotationProcessingOptions);
    }

    private ReturnValueStaxBuilder getNewReturnValueStaxBuilder() {
        return new ReturnValueStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder, this.annotationProcessingOptions);
    }

    public Optional<ReturnValueStaxBuilder> getReturnValueStaxBuilder() {
        return Optional.ofNullable(this.returnValueStaxBuilder);
    }

    public Optional<CrossParameterStaxBuilder> getCrossParameterStaxBuilder() {
        return Optional.ofNullable(this.crossParameterStaxBuilder);
    }
}