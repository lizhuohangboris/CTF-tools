package org.hibernate.validator.internal.xml.mapping;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.Payload;
import javax.validation.ValidationException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.core.MetaConstraints;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.annotation.AnnotationDescriptor;
import org.hibernate.validator.internal.util.annotation.ConstraintAnnotationDescriptor;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetMethod;
import org.hibernate.validator.internal.xml.AbstractStaxBuilder;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/ConstraintTypeStaxBuilder.class */
public class ConstraintTypeStaxBuilder extends AbstractStaxBuilder {
    private static final String CONSTRAINT_QNAME_LOCAL_PART = "constraint";
    private final ClassLoadingHelper classLoadingHelper;
    private final ConstraintHelper constraintHelper;
    private final TypeResolutionHelper typeResolutionHelper;
    private final ValueExtractorManager valueExtractorManager;
    private final DefaultPackageStaxBuilder defaultPackageStaxBuilder;
    private final GroupsStaxBuilder groupsStaxBuilder;
    private final PayloadStaxBuilder payloadStaxBuilder;
    private final ConstraintParameterStaxBuilder constrainParameterStaxBuilder;
    private final MessageStaxBuilder messageStaxBuilder = new MessageStaxBuilder();
    private final List<AbstractStaxBuilder> builders;
    private String constraintAnnotation;
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final Pattern IS_ONLY_WHITESPACE = Pattern.compile("\\s*");
    private static final QName CONSTRAINT_ANNOTATION_QNAME = new QName("annotation");

    public ConstraintTypeStaxBuilder(ClassLoadingHelper classLoadingHelper, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, DefaultPackageStaxBuilder defaultPackageStaxBuilder) {
        this.classLoadingHelper = classLoadingHelper;
        this.defaultPackageStaxBuilder = defaultPackageStaxBuilder;
        this.constraintHelper = constraintHelper;
        this.typeResolutionHelper = typeResolutionHelper;
        this.valueExtractorManager = valueExtractorManager;
        this.groupsStaxBuilder = new GroupsStaxBuilder(classLoadingHelper, defaultPackageStaxBuilder);
        this.payloadStaxBuilder = new PayloadStaxBuilder(classLoadingHelper, defaultPackageStaxBuilder);
        this.constrainParameterStaxBuilder = new ConstraintParameterStaxBuilder(classLoadingHelper, defaultPackageStaxBuilder);
        this.builders = (List) Stream.of((Object[]) new AbstractStaxBuilder[]{this.groupsStaxBuilder, this.payloadStaxBuilder, this.constrainParameterStaxBuilder, this.messageStaxBuilder}).collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    public String getAcceptableQName() {
        return CONSTRAINT_QNAME_LOCAL_PART;
    }

    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
        StartElement startElement = xmlEvent.asStartElement();
        this.constraintAnnotation = readAttribute(startElement, CONSTRAINT_ANNOTATION_QNAME).get();
        while (true) {
            if (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(CONSTRAINT_QNAME_LOCAL_PART)) {
                XMLEvent currentEvent = xmlEvent;
                this.builders.forEach(builder -> {
                    builder.process(xmlEventReader, currentEvent);
                });
                xmlEvent = xmlEventReader.nextEvent();
            } else {
                return;
            }
        }
    }

    public <A extends Annotation> MetaConstraint<A> build(ConstraintLocation constraintLocation, ElementType type, ConstraintDescriptorImpl.ConstraintType constraintType) {
        String defaultPackage = this.defaultPackageStaxBuilder.build().orElse("");
        try {
            Class<?> loadClass = this.classLoadingHelper.loadClass(this.constraintAnnotation, defaultPackage);
            ConstraintAnnotationDescriptor.Builder<A> annotationDescriptorBuilder = new ConstraintAnnotationDescriptor.Builder<>((Class<A>) loadClass);
            Optional<String> message = this.messageStaxBuilder.build();
            if (message.isPresent()) {
                annotationDescriptorBuilder.setMessage(message.get());
            }
            annotationDescriptorBuilder.setGroups(this.groupsStaxBuilder.build()).setPayload(this.payloadStaxBuilder.build());
            Map<String, Object> parameters = this.constrainParameterStaxBuilder.build(loadClass);
            for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
                annotationDescriptorBuilder.setAttribute(parameter.getKey(), parameter.getValue());
            }
            try {
                ConstraintAnnotationDescriptor<A> annotationDescriptor = annotationDescriptorBuilder.build();
                ConstraintDescriptorImpl<A> constraintDescriptor = new ConstraintDescriptorImpl<>(this.constraintHelper, constraintLocation.getMember(), annotationDescriptor, type, constraintType);
                return MetaConstraints.create(this.typeResolutionHelper, this.valueExtractorManager, constraintDescriptor, constraintLocation);
            } catch (RuntimeException e) {
                throw LOG.getUnableToCreateAnnotationForConfiguredConstraintException(e);
            }
        } catch (ValidationException e2) {
            throw LOG.getUnableToLoadConstraintAnnotationClassException(this.constraintAnnotation, e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/ConstraintTypeStaxBuilder$MessageStaxBuilder.class */
    public static class MessageStaxBuilder extends AbstractOneLineStringStaxBuilder {
        private static final String MESSAGE_PACKAGE_QNAME = "message";

        private MessageStaxBuilder() {
        }

        @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
        public String getAcceptableQName() {
            return "message";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/ConstraintTypeStaxBuilder$ConstraintParameterStaxBuilder.class */
    public static class ConstraintParameterStaxBuilder extends AnnotationParameterStaxBuilder {
        private static final String ELEMENT_QNAME_LOCAL_PART = "element";
        private static final QName NAME_QNAME = new QName("name");

        public ConstraintParameterStaxBuilder(ClassLoadingHelper classLoadingHelper, DefaultPackageStaxBuilder defaultPackageStaxBuilder) {
            super(classLoadingHelper, defaultPackageStaxBuilder);
        }

        @Override // org.hibernate.validator.internal.xml.mapping.ConstraintTypeStaxBuilder.AnnotationParameterStaxBuilder, org.hibernate.validator.internal.xml.AbstractStaxBuilder
        public String getAcceptableQName() {
            return ELEMENT_QNAME_LOCAL_PART;
        }

        @Override // org.hibernate.validator.internal.xml.mapping.ConstraintTypeStaxBuilder.AnnotationParameterStaxBuilder, org.hibernate.validator.internal.xml.AbstractStaxBuilder
        protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
            String name = readAttribute(xmlEvent.asStartElement(), NAME_QNAME).get();
            while (true) {
                if (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(ELEMENT_QNAME_LOCAL_PART)) {
                    xmlEvent = xmlEventReader.nextEvent();
                    readElement(xmlEventReader, xmlEvent, name);
                } else {
                    return;
                }
            }
        }

        @Override // org.hibernate.validator.internal.xml.mapping.ConstraintTypeStaxBuilder.AnnotationParameterStaxBuilder
        protected void checkNameIsValid(String name) {
            if (ConstraintHelper.MESSAGE.equals(name) || ConstraintHelper.GROUPS.equals(name) || ConstraintHelper.PAYLOAD.equals(name)) {
                throw ConstraintTypeStaxBuilder.LOG.getReservedParameterNamesException(ConstraintHelper.MESSAGE, ConstraintHelper.GROUPS, ConstraintHelper.PAYLOAD);
            }
        }

        public <A extends Annotation> Map<String, Object> build(Class<A> annotationClass) {
            String defaultPackage = this.defaultPackageStaxBuilder.build().orElse("");
            Map<String, Object> builtParameters = new HashMap<>();
            for (Map.Entry<String, List<String>> parameter : this.parameters.entrySet()) {
                builtParameters.put(parameter.getKey(), getElementValue(parameter.getValue(), annotationClass, parameter.getKey(), defaultPackage));
            }
            for (Map.Entry<String, List<AnnotationParameterStaxBuilder>> parameter2 : this.annotationParameters.entrySet()) {
                builtParameters.put(parameter2.getKey(), getAnnotationElementValue(parameter2.getValue(), annotationClass, parameter2.getKey(), defaultPackage));
            }
            return builtParameters;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/ConstraintTypeStaxBuilder$AnnotationParameterStaxBuilder.class */
    public static class AnnotationParameterStaxBuilder extends AbstractStaxBuilder {
        private static final String ANNOTATION_QNAME_LOCAL_PART = "annotation";
        private static final String ELEMENT_QNAME_LOCAL_PART = "element";
        private static final String VALUE_QNAME_LOCAL_PART = "value";
        private static final QName NAME_QNAME = new QName("name");
        private final ClassLoadingHelper classLoadingHelper;
        protected final DefaultPackageStaxBuilder defaultPackageStaxBuilder;
        protected Map<String, List<String>> parameters = new HashMap();
        protected Map<String, List<AnnotationParameterStaxBuilder>> annotationParameters = new HashMap();

        public AnnotationParameterStaxBuilder(ClassLoadingHelper classLoadingHelper, DefaultPackageStaxBuilder defaultPackageStaxBuilder) {
            this.classLoadingHelper = classLoadingHelper;
            this.defaultPackageStaxBuilder = defaultPackageStaxBuilder;
        }

        @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
        public String getAcceptableQName() {
            return ANNOTATION_QNAME_LOCAL_PART;
        }

        @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
        protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
            while (true) {
                if (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(ANNOTATION_QNAME_LOCAL_PART)) {
                    xmlEvent = xmlEventReader.nextEvent();
                    if (xmlEvent.isStartElement()) {
                        StartElement startElement = xmlEvent.asStartElement();
                        if (startElement.getName().getLocalPart().equals(ELEMENT_QNAME_LOCAL_PART)) {
                            String name = readAttribute(xmlEvent.asStartElement(), NAME_QNAME).get();
                            this.parameters.put(name, Collections.emptyList());
                            while (true) {
                                if (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(ELEMENT_QNAME_LOCAL_PART)) {
                                    readElement(xmlEventReader, xmlEvent, name);
                                    xmlEvent = xmlEventReader.nextEvent();
                                }
                            }
                        }
                    }
                } else {
                    return;
                }
            }
        }

        protected void readElement(XMLEventReader xmlEventReader, XMLEvent xmlEvent, String name) throws XMLStreamException {
            if (xmlEvent.isCharacters() && !xmlEvent.asCharacters().getData().trim().isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder(xmlEvent.asCharacters().getData());
                while (xmlEventReader.peek().isCharacters()) {
                    stringBuilder.append(xmlEventReader.nextEvent().asCharacters().getData());
                }
                addParameterValue(name, stringBuilder.toString().trim());
            } else if (xmlEvent.isStartElement()) {
                StartElement startElement = xmlEvent.asStartElement();
                if (startElement.getName().getLocalPart().equals("value")) {
                    addParameterValue(name, readSingleElement(xmlEventReader));
                } else if (startElement.getName().getLocalPart().equals(ANNOTATION_QNAME_LOCAL_PART)) {
                    addAnnotationParameterValue(name, xmlEventReader, xmlEvent);
                }
            }
        }

        protected void addAnnotationParameterValue(String name, XMLEventReader xmlEventReader, XMLEvent xmlEvent) {
            checkNameIsValid(name);
            AnnotationParameterStaxBuilder annotationParameterStaxBuilder = new AnnotationParameterStaxBuilder(this.classLoadingHelper, this.defaultPackageStaxBuilder);
            annotationParameterStaxBuilder.process(xmlEventReader, xmlEvent);
            this.annotationParameters.merge(name, Collections.singletonList(annotationParameterStaxBuilder), v1, v2 -> {
                return (List) Stream.concat(v1.stream(), v2.stream()).collect(Collectors.toList());
            });
        }

        protected void addParameterValue(String name, String value) {
            checkNameIsValid(name);
            this.parameters.merge(name, Collections.singletonList(value), v1, v2 -> {
                return (List) Stream.concat(v1.stream(), v2.stream()).collect(Collectors.toList());
            });
        }

        protected void checkNameIsValid(String name) {
        }

        public <A extends Annotation> Annotation build(Class<A> annotationClass, String defaultPackage) {
            AnnotationDescriptor.Builder<A> annotationDescriptorBuilder = new AnnotationDescriptor.Builder<>(annotationClass);
            for (Map.Entry<String, List<String>> parameter : this.parameters.entrySet()) {
                annotationDescriptorBuilder.setAttribute(parameter.getKey(), getElementValue(parameter.getValue(), annotationClass, parameter.getKey(), defaultPackage));
            }
            for (Map.Entry<String, List<AnnotationParameterStaxBuilder>> parameter2 : this.annotationParameters.entrySet()) {
                annotationDescriptorBuilder.setAttribute(parameter2.getKey(), getAnnotationElementValue(parameter2.getValue(), annotationClass, parameter2.getKey(), defaultPackage));
            }
            return annotationDescriptorBuilder.build().getAnnotation();
        }

        protected <A extends Annotation> Object getElementValue(List<String> parsedParameters, Class<A> annotationClass, String name, String defaultPackage) {
            List<String> parameters = removeEmptyContentElements(parsedParameters);
            Class<?> returnType = getAnnotationParameterType(annotationClass, name);
            boolean isArray = returnType.isArray();
            if (!isArray) {
                if (parameters.size() == 0) {
                    return "";
                }
                if (parameters.size() > 1) {
                    throw ConstraintTypeStaxBuilder.LOG.getAttemptToSpecifyAnArrayWhereSingleValueIsExpectedException();
                }
                return convertStringToReturnType(parameters.get(0), returnType, defaultPackage);
            }
            return parameters.stream().map(value -> {
                return convertStringToReturnType(value, returnType.getComponentType(), defaultPackage);
            }).toArray(size -> {
                return (Object[]) Array.newInstance(returnType.getComponentType(), size);
            });
        }

        protected <A extends Annotation> Object getAnnotationElementValue(List<AnnotationParameterStaxBuilder> parameters, Class<A> annotationClass, String name, String defaultPackage) {
            Class<?> returnType = getAnnotationParameterType(annotationClass, name);
            boolean isArray = returnType.isArray();
            if (!isArray) {
                if (parameters.size() == 0) {
                    throw ConstraintTypeStaxBuilder.LOG.getEmptyElementOnlySupportedWhenCharSequenceIsExpectedExpection();
                }
                if (parameters.size() > 1) {
                    throw ConstraintTypeStaxBuilder.LOG.getAttemptToSpecifyAnArrayWhereSingleValueIsExpectedException();
                }
                return parameters.get(0).build(returnType, defaultPackage);
            }
            return parameters.stream().map(value -> {
                return value.build(returnType.getComponentType(), defaultPackage);
            }).toArray(size -> {
                return (Object[]) Array.newInstance(returnType.getComponentType(), size);
            });
        }

        private static List<String> removeEmptyContentElements(List<String> params) {
            return (List) params.stream().filter(content -> {
                return !ConstraintTypeStaxBuilder.IS_ONLY_WHITESPACE.matcher(content).matches();
            }).collect(Collectors.toList());
        }

        private static <A extends Annotation> Class<?> getAnnotationParameterType(Class<A> annotationClass, String name) {
            Method m = (Method) run(GetMethod.action(annotationClass, name));
            if (m == null) {
                throw ConstraintTypeStaxBuilder.LOG.getAnnotationDoesNotContainAParameterException(annotationClass, name);
            }
            return m.getReturnType();
        }

        private Object convertStringToReturnType(String value, Class<?> returnType, String defaultPackage) {
            Object returnValue;
            if (returnType == Byte.TYPE) {
                try {
                    returnValue = Byte.valueOf(Byte.parseByte(value));
                } catch (NumberFormatException e) {
                    throw ConstraintTypeStaxBuilder.LOG.getInvalidNumberFormatException("byte", e);
                }
            } else if (returnType == Short.TYPE) {
                try {
                    returnValue = Short.valueOf(Short.parseShort(value));
                } catch (NumberFormatException e2) {
                    throw ConstraintTypeStaxBuilder.LOG.getInvalidNumberFormatException("short", e2);
                }
            } else if (returnType == Integer.TYPE) {
                try {
                    returnValue = Integer.valueOf(Integer.parseInt(value));
                } catch (NumberFormatException e3) {
                    throw ConstraintTypeStaxBuilder.LOG.getInvalidNumberFormatException("int", e3);
                }
            } else if (returnType == Long.TYPE) {
                try {
                    returnValue = Long.valueOf(Long.parseLong(value));
                } catch (NumberFormatException e4) {
                    throw ConstraintTypeStaxBuilder.LOG.getInvalidNumberFormatException("long", e4);
                }
            } else if (returnType == Float.TYPE) {
                try {
                    returnValue = Float.valueOf(Float.parseFloat(value));
                } catch (NumberFormatException e5) {
                    throw ConstraintTypeStaxBuilder.LOG.getInvalidNumberFormatException("float", e5);
                }
            } else if (returnType == Double.TYPE) {
                try {
                    returnValue = Double.valueOf(Double.parseDouble(value));
                } catch (NumberFormatException e6) {
                    throw ConstraintTypeStaxBuilder.LOG.getInvalidNumberFormatException("double", e6);
                }
            } else if (returnType == Boolean.TYPE) {
                returnValue = Boolean.valueOf(Boolean.parseBoolean(value));
            } else if (returnType == Character.TYPE) {
                if (value.length() != 1) {
                    throw ConstraintTypeStaxBuilder.LOG.getInvalidCharValueException(value);
                }
                returnValue = Character.valueOf(value.charAt(0));
            } else if (returnType == String.class) {
                returnValue = value;
            } else if (returnType == Class.class) {
                returnValue = this.classLoadingHelper.loadClass(value, defaultPackage);
            } else {
                try {
                    returnValue = Enum.valueOf(returnType, value);
                } catch (ClassCastException e7) {
                    throw ConstraintTypeStaxBuilder.LOG.getInvalidReturnTypeException(returnType, e7);
                }
            }
            return returnValue;
        }

        private static <T> T run(PrivilegedAction<T> action) {
            return System.getSecurityManager() != null ? (T) AccessController.doPrivileged(action) : action.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/ConstraintTypeStaxBuilder$GroupsStaxBuilder.class */
    public static class GroupsStaxBuilder extends AbstractMultiValuedElementStaxBuilder {
        private static final String GROUPS_QNAME_LOCAL_PART = "groups";

        private GroupsStaxBuilder(ClassLoadingHelper classLoadingHelper, DefaultPackageStaxBuilder defaultPackageStaxBuilder) {
            super(classLoadingHelper, defaultPackageStaxBuilder);
        }

        @Override // org.hibernate.validator.internal.xml.mapping.AbstractMultiValuedElementStaxBuilder
        public void verifyClass(Class<?> clazz) {
        }

        @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
        public String getAcceptableQName() {
            return "groups";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/ConstraintTypeStaxBuilder$PayloadStaxBuilder.class */
    public static class PayloadStaxBuilder extends AbstractMultiValuedElementStaxBuilder {
        private static final String PAYLOAD_QNAME_LOCAL_PART = "payload";

        private PayloadStaxBuilder(ClassLoadingHelper classLoadingHelper, DefaultPackageStaxBuilder defaultPackageStaxBuilder) {
            super(classLoadingHelper, defaultPackageStaxBuilder);
        }

        @Override // org.hibernate.validator.internal.xml.mapping.AbstractMultiValuedElementStaxBuilder
        public void verifyClass(Class<?> payload) {
            if (!Payload.class.isAssignableFrom(payload)) {
                throw ConstraintTypeStaxBuilder.LOG.getWrongPayloadClassException(payload);
            }
        }

        @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
        public String getAcceptableQName() {
            return "payload";
        }
    }
}