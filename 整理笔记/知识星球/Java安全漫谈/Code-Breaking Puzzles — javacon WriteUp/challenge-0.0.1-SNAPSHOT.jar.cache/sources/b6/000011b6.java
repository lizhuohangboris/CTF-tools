package org.hibernate.validator.internal.xml.mapping;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.xml.AbstractStaxBuilder;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/BeanStaxBuilder.class */
public class BeanStaxBuilder extends AbstractStaxBuilder {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final QName IGNORE_ANNOTATIONS_QNAME = new QName("ignore-annotations");
    private static final QName CLASS_QNAME = new QName("class");
    private static final String BEAN_QNAME_LOCAL_PART = "bean";
    private final ClassLoadingHelper classLoadingHelper;
    private final ConstraintHelper constraintHelper;
    private final TypeResolutionHelper typeResolutionHelper;
    private final ValueExtractorManager valueExtractorManager;
    private final DefaultPackageStaxBuilder defaultPackageStaxBuilder;
    private final AnnotationProcessingOptionsImpl annotationProcessingOptions;
    private final Map<Class<?>, List<Class<?>>> defaultSequences;
    protected String className;
    protected Optional<Boolean> ignoreAnnotations;
    private ClassConstraintTypeStaxBuilder classConstraintTypeStaxBuilder;
    private final List<ConstrainedFieldStaxBuilder> constrainedFieldStaxBuilders = new ArrayList();
    private final List<ConstrainedGetterStaxBuilder> constrainedGetterStaxBuilders = new ArrayList();
    private final List<ConstrainedMethodStaxBuilder> constrainedMethodStaxBuilders = new ArrayList();
    private final List<ConstrainedConstructorStaxBuilder> constrainedConstructorStaxBuilders = new ArrayList();

    /* JADX INFO: Access modifiers changed from: package-private */
    public BeanStaxBuilder(ClassLoadingHelper classLoadingHelper, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, DefaultPackageStaxBuilder defaultPackageStaxBuilder, AnnotationProcessingOptionsImpl annotationProcessingOptions, Map<Class<?>, List<Class<?>>> defaultSequences) {
        this.classLoadingHelper = classLoadingHelper;
        this.defaultPackageStaxBuilder = defaultPackageStaxBuilder;
        this.constraintHelper = constraintHelper;
        this.typeResolutionHelper = typeResolutionHelper;
        this.valueExtractorManager = valueExtractorManager;
        this.annotationProcessingOptions = annotationProcessingOptions;
        this.defaultSequences = defaultSequences;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    public String getAcceptableQName() {
        return "bean";
    }

    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
        this.className = readAttribute(xmlEvent.asStartElement(), CLASS_QNAME).get();
        this.ignoreAnnotations = readAttribute(xmlEvent.asStartElement(), IGNORE_ANNOTATIONS_QNAME).map(Boolean::parseBoolean);
        ConstrainedFieldStaxBuilder fieldStaxBuilder = getNewConstrainedFieldStaxBuilder();
        ConstrainedGetterStaxBuilder getterStaxBuilder = getNewConstrainedGetterStaxBuilder();
        ConstrainedMethodStaxBuilder methodStaxBuilder = getNewConstrainedMethodStaxBuilder();
        ConstrainedConstructorStaxBuilder constructorStaxBuilder = getNewConstrainedConstructorStaxBuilder();
        ClassConstraintTypeStaxBuilder localClassConstraintTypeStaxBuilder = new ClassConstraintTypeStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder, this.annotationProcessingOptions, this.defaultSequences);
        while (true) {
            if (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(getAcceptableQName())) {
                xmlEvent = xmlEventReader.nextEvent();
                if (fieldStaxBuilder.process(xmlEventReader, xmlEvent)) {
                    this.constrainedFieldStaxBuilders.add(fieldStaxBuilder);
                    fieldStaxBuilder = getNewConstrainedFieldStaxBuilder();
                } else if (getterStaxBuilder.process(xmlEventReader, xmlEvent)) {
                    this.constrainedGetterStaxBuilders.add(getterStaxBuilder);
                    getterStaxBuilder = getNewConstrainedGetterStaxBuilder();
                } else if (methodStaxBuilder.process(xmlEventReader, xmlEvent)) {
                    this.constrainedMethodStaxBuilders.add(methodStaxBuilder);
                    methodStaxBuilder = getNewConstrainedMethodStaxBuilder();
                } else if (constructorStaxBuilder.process(xmlEventReader, xmlEvent)) {
                    this.constrainedConstructorStaxBuilders.add(constructorStaxBuilder);
                    constructorStaxBuilder = getNewConstrainedConstructorStaxBuilder();
                } else if (localClassConstraintTypeStaxBuilder.process(xmlEventReader, xmlEvent)) {
                    this.classConstraintTypeStaxBuilder = localClassConstraintTypeStaxBuilder;
                }
            } else {
                return;
            }
        }
    }

    private ConstrainedFieldStaxBuilder getNewConstrainedFieldStaxBuilder() {
        return new ConstrainedFieldStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder, this.annotationProcessingOptions);
    }

    private ConstrainedGetterStaxBuilder getNewConstrainedGetterStaxBuilder() {
        return new ConstrainedGetterStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder, this.annotationProcessingOptions);
    }

    private ConstrainedMethodStaxBuilder getNewConstrainedMethodStaxBuilder() {
        return new ConstrainedMethodStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder, this.annotationProcessingOptions);
    }

    private ConstrainedConstructorStaxBuilder getNewConstrainedConstructorStaxBuilder() {
        return new ConstrainedConstructorStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.defaultPackageStaxBuilder, this.annotationProcessingOptions);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void build(Set<Class<?>> processedClasses, Map<Class<?>, Set<ConstrainedElement>> constrainedElementsByType) {
        Class<?> beanClass = this.classLoadingHelper.loadClass(this.className, this.defaultPackageStaxBuilder.build().orElse(""));
        checkClassHasNotBeenProcessed(processedClasses, beanClass);
        this.annotationProcessingOptions.ignoreAnnotationConstraintForClass(beanClass, this.ignoreAnnotations.orElse(true));
        if (this.classConstraintTypeStaxBuilder != null) {
            addConstrainedElements(constrainedElementsByType, beanClass, Collections.singleton(this.classConstraintTypeStaxBuilder.build(beanClass)));
        }
        List<String> alreadyProcessedFieldNames = new ArrayList<>(this.constrainedFieldStaxBuilders.size());
        addConstrainedElements(constrainedElementsByType, beanClass, (Collection) this.constrainedFieldStaxBuilders.stream().map(builder -> {
            return builder.build(beanClass, alreadyProcessedFieldNames);
        }).collect(Collectors.toList()));
        List<String> alreadyProcessedGetterNames = new ArrayList<>(this.constrainedGetterStaxBuilders.size());
        addConstrainedElements(constrainedElementsByType, beanClass, (Collection) this.constrainedGetterStaxBuilders.stream().map(builder2 -> {
            return builder2.build(beanClass, alreadyProcessedGetterNames);
        }).collect(Collectors.toList()));
        List<Method> alreadyProcessedMethods = new ArrayList<>(this.constrainedMethodStaxBuilders.size());
        addConstrainedElements(constrainedElementsByType, beanClass, (Collection) this.constrainedMethodStaxBuilders.stream().map(builder3 -> {
            return builder3.build(beanClass, alreadyProcessedMethods);
        }).collect(Collectors.toList()));
        List<Constructor<?>> alreadyProcessedConstructors = new ArrayList<>(this.constrainedConstructorStaxBuilders.size());
        addConstrainedElements(constrainedElementsByType, beanClass, (Collection) this.constrainedConstructorStaxBuilders.stream().map(builder4 -> {
            return builder4.build(beanClass, alreadyProcessedConstructors);
        }).collect(Collectors.toList()));
    }

    private void addConstrainedElements(Map<Class<?>, Set<ConstrainedElement>> constrainedElementsbyType, Class<?> beanClass, Collection<? extends ConstrainedElement> newConstrainedElements) {
        if (constrainedElementsbyType.containsKey(beanClass)) {
            Set<ConstrainedElement> existingConstrainedElements = constrainedElementsbyType.get(beanClass);
            for (ConstrainedElement constrainedElement : newConstrainedElements) {
                if (existingConstrainedElements.contains(constrainedElement)) {
                    throw LOG.getConstrainedElementConfiguredMultipleTimesException(constrainedElement.toString());
                }
            }
            existingConstrainedElements.addAll(newConstrainedElements);
            return;
        }
        Set<ConstrainedElement> tmpSet = CollectionHelper.newHashSet();
        tmpSet.addAll(newConstrainedElements);
        constrainedElementsbyType.put(beanClass, tmpSet);
    }

    private void checkClassHasNotBeenProcessed(Set<Class<?>> processedClasses, Class<?> beanClass) {
        if (processedClasses.contains(beanClass)) {
            throw LOG.getBeanClassHasAlreadyBeenConfiguredInXmlException(beanClass);
        }
        processedClasses.add(beanClass);
    }
}