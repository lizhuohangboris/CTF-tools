package org.hibernate.validator.internal.xml.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.xml.AbstractStaxBuilder;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/ConstraintMappingsStaxBuilder.class */
public class ConstraintMappingsStaxBuilder extends AbstractStaxBuilder {
    private static final String CONSTRAINT_MAPPINGS_QNAME = "constraint-mappings";
    private final ClassLoadingHelper classLoadingHelper;
    private final ConstraintHelper constraintHelper;
    private final TypeResolutionHelper typeResolutionHelper;
    private final ValueExtractorManager valueExtractorManager;
    private final AnnotationProcessingOptionsImpl annotationProcessingOptions;
    private final Map<Class<?>, List<Class<?>>> defaultSequences;
    private final DefaultPackageStaxBuilder defaultPackageStaxBuilder = new DefaultPackageStaxBuilder();
    private final List<BeanStaxBuilder> beanStaxBuilders = new ArrayList();
    private final List<ConstraintDefinitionStaxBuilder> constraintDefinitionStaxBuilders = new ArrayList();

    public ConstraintMappingsStaxBuilder(ClassLoadingHelper classLoadingHelper, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, AnnotationProcessingOptionsImpl annotationProcessingOptions, Map<Class<?>, List<Class<?>>> defaultSequences) {
        this.classLoadingHelper = classLoadingHelper;
        this.constraintHelper = constraintHelper;
        this.typeResolutionHelper = typeResolutionHelper;
        this.valueExtractorManager = valueExtractorManager;
        this.annotationProcessingOptions = annotationProcessingOptions;
        this.defaultSequences = defaultSequences;
    }

    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    public String getAcceptableQName() {
        return CONSTRAINT_MAPPINGS_QNAME;
    }

    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
        BeanStaxBuilder beanStaxBuilder = getNewBeanStaxBuilder();
        ConstraintDefinitionStaxBuilder constraintDefinitionStaxBuilder = getNewConstraintDefinitionStaxBuilder();
        while (true) {
            if (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(getAcceptableQName())) {
                xmlEvent = xmlEventReader.nextEvent();
                if (beanStaxBuilder.process(xmlEventReader, xmlEvent)) {
                    this.beanStaxBuilders.add(beanStaxBuilder);
                    beanStaxBuilder = getNewBeanStaxBuilder();
                } else if (constraintDefinitionStaxBuilder.process(xmlEventReader, xmlEvent)) {
                    this.constraintDefinitionStaxBuilders.add(constraintDefinitionStaxBuilder);
                    constraintDefinitionStaxBuilder = getNewConstraintDefinitionStaxBuilder();
                }
                this.defaultPackageStaxBuilder.process(xmlEventReader, xmlEvent);
            } else {
                return;
            }
        }
    }

    private BeanStaxBuilder getNewBeanStaxBuilder() {
        return new BeanStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder, this.annotationProcessingOptions, this.defaultSequences);
    }

    private ConstraintDefinitionStaxBuilder getNewConstraintDefinitionStaxBuilder() {
        return new ConstraintDefinitionStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.defaultPackageStaxBuilder);
    }

    public void build(Set<Class<?>> processedClasses, Map<Class<?>, Set<ConstrainedElement>> constrainedElementsByType, Set<String> alreadyProcessedConstraintDefinitions) {
        this.constraintDefinitionStaxBuilders.forEach(builder -> {
            builder.build(alreadyProcessedConstraintDefinitions);
        });
        this.beanStaxBuilders.forEach(builder2 -> {
            builder2.build(processedClasses, constrainedElementsByType);
        });
    }
}