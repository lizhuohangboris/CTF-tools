package org.hibernate.validator.internal.xml.config;

import java.lang.invoke.MethodHandles;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.validation.BootstrapConfiguration;
import javax.validation.executable.ExecutableType;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.xml.AbstractStaxBuilder;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/config/ValidationConfigStaxBuilder.class */
class ValidationConfigStaxBuilder extends AbstractStaxBuilder {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final String VALIDATION_CONFIG_QNAME = "validation-config";
    private final SimpleConfigurationsStaxBuilder simpleConfigurationsStaxBuilder = new SimpleConfigurationsStaxBuilder();
    private final PropertyStaxBuilder propertyStaxBuilder = new PropertyStaxBuilder();
    private final ValueExtractorsStaxBuilder valueExtractorsStaxBuilder = new ValueExtractorsStaxBuilder();
    private final ConstraintMappingsStaxBuilder constraintMappingsStaxBuilder = new ConstraintMappingsStaxBuilder();
    private final ExecutableValidationStaxBuilder executableValidationStaxBuilder = new ExecutableValidationStaxBuilder();
    private final Map<String, AbstractStaxBuilder> builders = new HashMap();

    public ValidationConfigStaxBuilder(XMLEventReader xmlEventReader) throws XMLStreamException {
        this.builders.put(this.propertyStaxBuilder.getAcceptableQName(), this.propertyStaxBuilder);
        this.builders.put(this.valueExtractorsStaxBuilder.getAcceptableQName(), this.valueExtractorsStaxBuilder);
        this.builders.put(this.constraintMappingsStaxBuilder.getAcceptableQName(), this.constraintMappingsStaxBuilder);
        this.builders.put(this.executableValidationStaxBuilder.getAcceptableQName(), this.executableValidationStaxBuilder);
        for (String name : SimpleConfigurationsStaxBuilder.getProcessedElementNames()) {
            this.builders.put(name, this.simpleConfigurationsStaxBuilder);
        }
        while (xmlEventReader.hasNext()) {
            process(xmlEventReader, xmlEventReader.nextEvent());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    public String getAcceptableQName() {
        return VALIDATION_CONFIG_QNAME;
    }

    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
        while (true) {
            if (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(VALIDATION_CONFIG_QNAME)) {
                XMLEvent currentEvent = xmlEventReader.nextEvent();
                xmlEvent = currentEvent;
                if (currentEvent.isStartElement()) {
                    StartElement startElement = currentEvent.asStartElement();
                    String localPart = startElement.getName().getLocalPart();
                    AbstractStaxBuilder builder = this.builders.get(localPart);
                    if (builder != null) {
                        builder.process(xmlEventReader, xmlEvent);
                    } else {
                        LOG.logUnknownElementInXmlConfiguration(localPart);
                    }
                }
            } else {
                return;
            }
        }
    }

    public BootstrapConfiguration build() {
        Map<String, String> properties = this.propertyStaxBuilder.build();
        return new BootstrapConfigurationImpl(this.simpleConfigurationsStaxBuilder.getDefaultProvider(), this.simpleConfigurationsStaxBuilder.getConstraintValidatorFactory(), this.simpleConfigurationsStaxBuilder.getMessageInterpolator(), this.simpleConfigurationsStaxBuilder.getTraversableResolver(), this.simpleConfigurationsStaxBuilder.getParameterNameProvider(), this.simpleConfigurationsStaxBuilder.getClockProvider(), this.valueExtractorsStaxBuilder.build(), this.executableValidationStaxBuilder.build(), this.executableValidationStaxBuilder.isEnabled(), this.constraintMappingsStaxBuilder.build(), properties);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/config/ValidationConfigStaxBuilder$SimpleConfigurationsStaxBuilder.class */
    private static class SimpleConfigurationsStaxBuilder extends AbstractStaxBuilder {
        private static final String DEFAULT_PROVIDER = "default-provider";
        private static final String MESSAGE_INTERPOLATOR = "message-interpolator";
        private static final String TRAVERSABLE_RESOLVER = "traversable-resolver";
        private static final String CONSTRAINT_VALIDATOR_FACTORY = "constraint-validator-factory";
        private static final String PARAMETER_NAME_PROVIDER = "parameter-name-provider";
        private static final String CLOCK_PROVIDER = "clock-provider";
        private static final Set<String> SINGLE_ELEMENTS = CollectionHelper.toImmutableSet(CollectionHelper.asSet(DEFAULT_PROVIDER, MESSAGE_INTERPOLATOR, TRAVERSABLE_RESOLVER, CONSTRAINT_VALIDATOR_FACTORY, PARAMETER_NAME_PROVIDER, CLOCK_PROVIDER));
        private final Map<String, String> singleValuedElements;

        private SimpleConfigurationsStaxBuilder() {
            this.singleValuedElements = new HashMap();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
        public String getAcceptableQName() {
            throw new UnsupportedOperationException("this method shouldn't be called");
        }

        @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
        protected boolean accept(XMLEvent xmlEvent) {
            return xmlEvent.isStartElement() && SINGLE_ELEMENTS.contains(xmlEvent.asStartElement().getName().getLocalPart());
        }

        @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
        protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
            String localPart = xmlEvent.asStartElement().getName().getLocalPart();
            this.singleValuedElements.put(localPart, readSingleElement(xmlEventReader));
        }

        public String getDefaultProvider() {
            return this.singleValuedElements.get(DEFAULT_PROVIDER);
        }

        public String getMessageInterpolator() {
            return this.singleValuedElements.get(MESSAGE_INTERPOLATOR);
        }

        public String getTraversableResolver() {
            return this.singleValuedElements.get(TRAVERSABLE_RESOLVER);
        }

        public String getClockProvider() {
            return this.singleValuedElements.get(CLOCK_PROVIDER);
        }

        public String getConstraintValidatorFactory() {
            return this.singleValuedElements.get(CONSTRAINT_VALIDATOR_FACTORY);
        }

        public String getParameterNameProvider() {
            return this.singleValuedElements.get(PARAMETER_NAME_PROVIDER);
        }

        public static Set<String> getProcessedElementNames() {
            return SINGLE_ELEMENTS;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/config/ValidationConfigStaxBuilder$PropertyStaxBuilder.class */
    private static class PropertyStaxBuilder extends AbstractStaxBuilder {
        private static final String PROPERTY_QNAME_LOCAL_PART = "property";
        private static final QName NAME_QNAME = new QName("name");
        private final Map<String, String> properties;

        private PropertyStaxBuilder() {
            this.properties = new HashMap();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
        public String getAcceptableQName() {
            return "property";
        }

        @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
        protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
            StartElement startElement = xmlEvent.asStartElement();
            String name = readAttribute(startElement, NAME_QNAME).get();
            String value = readSingleElement(xmlEventReader);
            if (ValidationConfigStaxBuilder.LOG.isDebugEnabled()) {
                ValidationConfigStaxBuilder.LOG.debugf("Found property '%s' with value '%s' in validation.xml.", name, value);
            }
            this.properties.put(name, value);
        }

        public Map<String, String> build() {
            return this.properties;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/config/ValidationConfigStaxBuilder$ValueExtractorsStaxBuilder.class */
    private static class ValueExtractorsStaxBuilder extends AbstractStaxBuilder {
        private static final String VALUE_EXTRACTOR_QNAME_LOCAL_PART = "value-extractor";
        private final Set<String> valueExtractors;

        private ValueExtractorsStaxBuilder() {
            this.valueExtractors = new HashSet();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
        public String getAcceptableQName() {
            return VALUE_EXTRACTOR_QNAME_LOCAL_PART;
        }

        @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
        protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
            String value = readSingleElement(xmlEventReader);
            if (!this.valueExtractors.add(value)) {
                throw ValidationConfigStaxBuilder.LOG.getDuplicateDefinitionsOfValueExtractorException(value);
            }
        }

        public Set<String> build() {
            return this.valueExtractors;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/config/ValidationConfigStaxBuilder$ConstraintMappingsStaxBuilder.class */
    private static class ConstraintMappingsStaxBuilder extends AbstractStaxBuilder {
        private static final String CONSTRAINT_MAPPING_QNAME_LOCAL_PART = "constraint-mapping";
        private final Set<String> constraintMappings;

        private ConstraintMappingsStaxBuilder() {
            this.constraintMappings = new HashSet();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
        public String getAcceptableQName() {
            return CONSTRAINT_MAPPING_QNAME_LOCAL_PART;
        }

        @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
        protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
            String value = readSingleElement(xmlEventReader);
            this.constraintMappings.add(value);
        }

        public Set<String> build() {
            return this.constraintMappings;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/config/ValidationConfigStaxBuilder$ExecutableValidationStaxBuilder.class */
    private static class ExecutableValidationStaxBuilder extends AbstractStaxBuilder {
        private static final String EXECUTABLE_VALIDATION_QNAME_LOCAL_PART = "executable-validation";
        private static final String EXECUTABLE_TYPE_QNAME_LOCAL_PART = "executable-type";
        private static final QName ENABLED_QNAME = new QName("enabled");
        private Boolean enabled;
        private EnumSet<ExecutableType> executableTypes;

        private ExecutableValidationStaxBuilder() {
            this.executableTypes = EnumSet.noneOf(ExecutableType.class);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
        public String getAcceptableQName() {
            return EXECUTABLE_VALIDATION_QNAME_LOCAL_PART;
        }

        @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
        protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
            Optional<String> enabledAttribute = readAttribute(xmlEvent.asStartElement(), ENABLED_QNAME);
            if (enabledAttribute.isPresent()) {
                this.enabled = Boolean.valueOf(Boolean.parseBoolean(enabledAttribute.get()));
            }
            while (true) {
                if (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(EXECUTABLE_VALIDATION_QNAME_LOCAL_PART)) {
                    XMLEvent currentEvent = xmlEventReader.nextEvent();
                    xmlEvent = currentEvent;
                    if (currentEvent.isStartElement() && currentEvent.asStartElement().getName().getLocalPart().equals(EXECUTABLE_TYPE_QNAME_LOCAL_PART)) {
                        this.executableTypes.add(ExecutableType.valueOf(readSingleElement(xmlEventReader)));
                    }
                } else {
                    return;
                }
            }
        }

        public boolean isEnabled() {
            if (this.enabled == null) {
                return true;
            }
            return this.enabled.booleanValue();
        }

        public EnumSet<ExecutableType> build() {
            if (this.executableTypes.isEmpty()) {
                return null;
            }
            return this.executableTypes;
        }
    }
}