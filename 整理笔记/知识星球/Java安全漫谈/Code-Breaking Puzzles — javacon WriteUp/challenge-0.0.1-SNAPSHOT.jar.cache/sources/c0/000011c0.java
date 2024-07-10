package org.hibernate.validator.internal.xml.mapping;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.xml.AbstractStaxBuilder;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/ConstraintDefinitionStaxBuilder.class */
public class ConstraintDefinitionStaxBuilder extends AbstractStaxBuilder {
    private static final String CONSTRAINT_DEFINITION_QNAME_LOCAL_PART = "constraint-definition";
    private final ClassLoadingHelper classLoadingHelper;
    private final ConstraintHelper constraintHelper;
    private final DefaultPackageStaxBuilder defaultPackageStaxBuilder;
    private String annotation;
    private ValidatedByStaxBuilder validatedByStaxBuilder;
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final QName ANNOTATION_QNAME = new QName("annotation");

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConstraintDefinitionStaxBuilder(ClassLoadingHelper classLoadingHelper, ConstraintHelper constraintHelper, DefaultPackageStaxBuilder defaultPackageStaxBuilder) {
        this.classLoadingHelper = classLoadingHelper;
        this.constraintHelper = constraintHelper;
        this.defaultPackageStaxBuilder = defaultPackageStaxBuilder;
        this.validatedByStaxBuilder = new ValidatedByStaxBuilder(classLoadingHelper, defaultPackageStaxBuilder);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    public String getAcceptableQName() {
        return CONSTRAINT_DEFINITION_QNAME_LOCAL_PART;
    }

    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
        this.annotation = readAttribute(xmlEvent.asStartElement(), ANNOTATION_QNAME).get();
        while (true) {
            if (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(getAcceptableQName())) {
                this.validatedByStaxBuilder.process(xmlEventReader, xmlEvent);
                xmlEvent = xmlEventReader.nextEvent();
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void build(Set<String> alreadyProcessedConstraintDefinitions) {
        checkProcessedAnnotations(alreadyProcessedConstraintDefinitions);
        String defaultPackage = this.defaultPackageStaxBuilder.build().orElse("");
        Class<?> clazz = this.classLoadingHelper.loadClass(this.annotation, defaultPackage);
        if (!clazz.isAnnotation()) {
            throw LOG.getIsNotAnAnnotationException(clazz);
        }
        addValidatorDefinitions(clazz);
    }

    private void checkProcessedAnnotations(Set<String> alreadyProcessedConstraintDefinitions) {
        if (alreadyProcessedConstraintDefinitions.contains(this.annotation)) {
            throw LOG.getOverridingConstraintDefinitionsInMultipleMappingFilesException(this.annotation);
        }
        alreadyProcessedConstraintDefinitions.add(this.annotation);
    }

    private <A extends Annotation> void addValidatorDefinitions(Class<A> annotationClass) {
        this.constraintHelper.putValidatorDescriptors(annotationClass, this.validatedByStaxBuilder.build(annotationClass), this.validatedByStaxBuilder.isIncludeExistingValidators());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/ConstraintDefinitionStaxBuilder$ValidatedByStaxBuilder.class */
    public static class ValidatedByStaxBuilder extends AbstractStaxBuilder {
        private static final String VALIDATED_BY_QNAME_LOCAL_PART = "validated-by";
        private static final String VALUE_QNAME_LOCAL_PART = "value";
        private static final QName INCLUDE_EXISTING_VALIDATORS_QNAME = new QName("include-existing-validators");
        private final ClassLoadingHelper classLoadingHelper;
        private final DefaultPackageStaxBuilder defaultPackageStaxBuilder;
        private boolean includeExistingValidators;
        private final List<String> values = new ArrayList();

        protected ValidatedByStaxBuilder(ClassLoadingHelper classLoadingHelper, DefaultPackageStaxBuilder defaultPackageStaxBuilder) {
            this.classLoadingHelper = classLoadingHelper;
            this.defaultPackageStaxBuilder = defaultPackageStaxBuilder;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
        public String getAcceptableQName() {
            return VALIDATED_BY_QNAME_LOCAL_PART;
        }

        @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
        protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
            this.includeExistingValidators = ((Boolean) readAttribute(xmlEvent.asStartElement(), INCLUDE_EXISTING_VALIDATORS_QNAME).map(Boolean::parseBoolean).orElse(true)).booleanValue();
            while (true) {
                if (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(getAcceptableQName())) {
                    if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equals("value")) {
                        this.values.add(readSingleElement(xmlEventReader));
                    }
                    xmlEvent = xmlEventReader.nextEvent();
                } else {
                    return;
                }
            }
        }

        <A extends Annotation> List<ConstraintValidatorDescriptor<A>> build(Class<A> annotation) {
            String defaultPackage = this.defaultPackageStaxBuilder.build().orElse("");
            return (List) this.values.stream().map(value -> {
                return this.classLoadingHelper.loadClass(value, defaultPackage);
            }).peek(this::checkValidatorAssignability).map(clazz -> {
                return clazz;
            }).map(validatorClass -> {
                return ConstraintValidatorDescriptor.forClass(validatorClass, annotation);
            }).collect(Collectors.toList());
        }

        public boolean isIncludeExistingValidators() {
            return this.includeExistingValidators;
        }

        private void checkValidatorAssignability(Class<?> validatorClass) {
            if (!ConstraintValidator.class.isAssignableFrom(validatorClass)) {
                throw ConstraintDefinitionStaxBuilder.LOG.getIsNotAConstraintValidatorClassException(validatorClass);
            }
        }
    }
}