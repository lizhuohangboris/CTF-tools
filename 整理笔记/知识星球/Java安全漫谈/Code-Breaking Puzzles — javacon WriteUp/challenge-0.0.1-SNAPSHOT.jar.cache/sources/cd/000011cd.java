package org.hibernate.validator.internal.xml.mapping;

import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.validator.internal.engine.valueextraction.ArrayElement;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.TypeHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.xml.AbstractStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.ContainerElementTypeConfigurationBuilder;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/ContainerElementTypeStaxBuilder.class */
public class ContainerElementTypeStaxBuilder extends AbstractStaxBuilder {
    private static final String CONTAINER_ELEMENT_TYPE_QNAME_LOCAL_PART = "container-element-type";
    private final ClassLoadingHelper classLoadingHelper;
    private final ConstraintHelper constraintHelper;
    private final TypeResolutionHelper typeResolutionHelper;
    private final ValueExtractorManager valueExtractorManager;
    private final DefaultPackageStaxBuilder defaultPackageStaxBuilder;
    private Integer typeArgumentIndex;
    private final GroupConversionStaxBuilder groupConversionBuilder;
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final QName TYPE_ARGUMENT_INDEX_QNAME = new QName("type-argument-index");
    private final ValidStaxBuilder validStaxBuilder = new ValidStaxBuilder();
    private final List<ConstraintTypeStaxBuilder> constraintTypeStaxBuilders = new ArrayList();
    private final List<ContainerElementTypeStaxBuilder> containerElementTypeConfigurationStaxBuilders = new ArrayList();

    /* JADX INFO: Access modifiers changed from: package-private */
    public ContainerElementTypeStaxBuilder(ClassLoadingHelper classLoadingHelper, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, DefaultPackageStaxBuilder defaultPackageStaxBuilder) {
        this.classLoadingHelper = classLoadingHelper;
        this.defaultPackageStaxBuilder = defaultPackageStaxBuilder;
        this.constraintHelper = constraintHelper;
        this.typeResolutionHelper = typeResolutionHelper;
        this.valueExtractorManager = valueExtractorManager;
        this.groupConversionBuilder = new GroupConversionStaxBuilder(classLoadingHelper, defaultPackageStaxBuilder);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    public String getAcceptableQName() {
        return CONTAINER_ELEMENT_TYPE_QNAME_LOCAL_PART;
    }

    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
        Optional<String> typeArgumentIndex = readAttribute(xmlEvent.asStartElement(), TYPE_ARGUMENT_INDEX_QNAME);
        if (typeArgumentIndex.isPresent()) {
            this.typeArgumentIndex = Integer.valueOf(Integer.parseInt(typeArgumentIndex.get()));
        }
        ConstraintTypeStaxBuilder constraintTypeStaxBuilder = getNewConstraintTypeStaxBuilder();
        ContainerElementTypeStaxBuilder containerElementTypeConfigurationStaxBuilder = getNewContainerElementTypeConfigurationStaxBuilder();
        while (true) {
            if (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(getAcceptableQName())) {
                xmlEvent = xmlEventReader.nextEvent();
                this.validStaxBuilder.process(xmlEventReader, xmlEvent);
                this.groupConversionBuilder.process(xmlEventReader, xmlEvent);
                if (constraintTypeStaxBuilder.process(xmlEventReader, xmlEvent)) {
                    this.constraintTypeStaxBuilders.add(constraintTypeStaxBuilder);
                    constraintTypeStaxBuilder = getNewConstraintTypeStaxBuilder();
                }
                if (containerElementTypeConfigurationStaxBuilder.process(xmlEventReader, xmlEvent)) {
                    this.containerElementTypeConfigurationStaxBuilders.add(containerElementTypeConfigurationStaxBuilder);
                    containerElementTypeConfigurationStaxBuilder = getNewContainerElementTypeConfigurationStaxBuilder();
                }
            } else {
                return;
            }
        }
    }

    private ConstraintTypeStaxBuilder getNewConstraintTypeStaxBuilder() {
        return new ConstraintTypeStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder);
    }

    private ContainerElementTypeStaxBuilder getNewContainerElementTypeConfigurationStaxBuilder() {
        return new ContainerElementTypeStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder);
    }

    public ContainerElementTypeConfigurationBuilder.ContainerElementTypeConfiguration build(Set<ContainerElementTypePath> configuredPaths, ContainerElementTypePath parentConstraintElementTypePath, ConstraintLocation parentConstraintLocation, Type enclosingType) {
        if (TypeHelper.isArray(enclosingType)) {
            throw LOG.getContainerElementConstraintsAndCascadedValidationNotSupportedOnArraysException(enclosingType);
        }
        if (!(enclosingType instanceof ParameterizedType) && !TypeHelper.isArray(enclosingType)) {
            throw LOG.getTypeIsNotAParameterizedNorArrayTypeException(enclosingType);
        }
        Map<TypeVariable<?>, CascadingMetaDataBuilder> containerElementTypesCascadingMetaDataBuilder = CollectionHelper.newHashMap(this.containerElementTypeConfigurationStaxBuilders.size());
        boolean isArray = TypeHelper.isArray(enclosingType);
        TypeVariable<?>[] typeParameters = isArray ? new TypeVariable[0] : ReflectionHelper.getClassFromType(enclosingType).getTypeParameters();
        Integer typeArgumentIndex = getTypeArgumentIndex(typeParameters, isArray, enclosingType);
        ContainerElementTypePath constraintElementTypePath = ContainerElementTypePath.of(parentConstraintElementTypePath, typeArgumentIndex);
        boolean configuredBefore = !configuredPaths.add(constraintElementTypePath);
        if (configuredBefore) {
            throw LOG.getContainerElementTypeHasAlreadyBeenConfiguredViaXmlMappingConfigurationException(parentConstraintLocation, constraintElementTypePath);
        }
        TypeVariable<?> typeParameter = getTypeParameter(typeParameters, typeArgumentIndex, isArray, enclosingType);
        Type containerElementType = getContainerElementType(enclosingType, typeArgumentIndex, isArray);
        ConstraintLocation containerElementTypeConstraintLocation = ConstraintLocation.forTypeArgument(parentConstraintLocation, typeParameter, containerElementType);
        ContainerElementTypeConfigurationBuilder.ContainerElementTypeConfiguration nestedContainerElementTypeConfiguration = (ContainerElementTypeConfigurationBuilder.ContainerElementTypeConfiguration) this.containerElementTypeConfigurationStaxBuilders.stream().map(nested -> {
            return nested.build(configuredPaths, constraintElementTypePath, containerElementTypeConstraintLocation, containerElementType);
        }).reduce(ContainerElementTypeConfigurationBuilder.ContainerElementTypeConfiguration.EMPTY_CONFIGURATION, ContainerElementTypeConfigurationBuilder.ContainerElementTypeConfiguration::merge);
        boolean isCascaded = this.validStaxBuilder.build();
        containerElementTypesCascadingMetaDataBuilder.put(typeParameter, new CascadingMetaDataBuilder(enclosingType, typeParameter, isCascaded, nestedContainerElementTypeConfiguration.getTypeParametersCascadingMetaData(), this.groupConversionBuilder.build()));
        return new ContainerElementTypeConfigurationBuilder.ContainerElementTypeConfiguration((Set) Stream.concat(this.constraintTypeStaxBuilders.stream().map(builder -> {
            return builder.build(containerElementTypeConstraintLocation, ElementType.TYPE_USE, null);
        }), nestedContainerElementTypeConfiguration.getMetaConstraints().stream()).collect(Collectors.toSet()), containerElementTypesCascadingMetaDataBuilder);
    }

    private Integer getTypeArgumentIndex(TypeVariable<?>[] typeParameters, boolean isArray, Type enclosingType) {
        if (isArray) {
            return null;
        }
        if (this.typeArgumentIndex == null) {
            if (typeParameters.length > 1) {
                throw LOG.getNoTypeArgumentIndexIsGivenForTypeWithMultipleTypeArgumentsException(enclosingType);
            }
            return 0;
        }
        return this.typeArgumentIndex;
    }

    private TypeVariable<?> getTypeParameter(TypeVariable<?>[] typeParameters, Integer typeArgumentIndex, boolean isArray, Type enclosingType) {
        TypeVariable<?> typeParameter;
        if (!isArray) {
            if (typeArgumentIndex.intValue() > typeParameters.length - 1) {
                throw LOG.getInvalidTypeArgumentIndexException(enclosingType, typeArgumentIndex.intValue());
            }
            typeParameter = typeParameters[typeArgumentIndex.intValue()];
        } else {
            typeParameter = new ArrayElement(enclosingType);
        }
        return typeParameter;
    }

    private Type getContainerElementType(Type enclosingType, Integer typeArgumentIndex, boolean isArray) {
        Type containerElementType;
        if (!isArray) {
            containerElementType = ((ParameterizedType) enclosingType).getActualTypeArguments()[typeArgumentIndex.intValue()];
        } else {
            containerElementType = TypeHelper.getComponentType(enclosingType);
        }
        return containerElementType;
    }

    public Integer getTypeArgumentIndex() {
        return null;
    }
}