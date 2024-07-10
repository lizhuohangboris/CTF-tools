package org.hibernate.validator.internal.metadata.provider;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptions;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.raw.BeanConfiguration;
import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.xml.mapping.MappingXmlParser;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/provider/XmlMetaDataProvider.class */
public class XmlMetaDataProvider implements MetaDataProvider {
    private final Map<String, BeanConfiguration<?>> configuredBeans;
    private final AnnotationProcessingOptions annotationProcessingOptions;

    public XmlMetaDataProvider(ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, Set<InputStream> mappingStreams, ClassLoader externalClassLoader) {
        MappingXmlParser mappingParser = new MappingXmlParser(constraintHelper, typeResolutionHelper, valueExtractorManager, externalClassLoader);
        mappingParser.parse(mappingStreams);
        this.configuredBeans = CollectionHelper.toImmutableMap(createBeanConfigurations(mappingParser));
        this.annotationProcessingOptions = mappingParser.getAnnotationProcessingOptions();
    }

    private static Map<String, BeanConfiguration<?>> createBeanConfigurations(MappingXmlParser mappingParser) {
        Map<String, BeanConfiguration<?>> configuredBeans = new HashMap<>();
        for (Class<?> clazz : mappingParser.getXmlConfiguredClasses()) {
            Set<ConstrainedElement> constrainedElements = mappingParser.getConstrainedElementsForClass(clazz);
            BeanConfiguration<?> beanConfiguration = new BeanConfiguration<>(ConfigurationSource.XML, clazz, constrainedElements, mappingParser.getDefaultSequenceForClass(clazz), null);
            configuredBeans.put(clazz.getName(), beanConfiguration);
        }
        return configuredBeans;
    }

    @Override // org.hibernate.validator.internal.metadata.provider.MetaDataProvider
    public <T> BeanConfiguration<T> getBeanConfiguration(Class<T> beanClass) {
        return (BeanConfiguration<T>) this.configuredBeans.get(beanClass.getName());
    }

    @Override // org.hibernate.validator.internal.metadata.provider.MetaDataProvider
    public AnnotationProcessingOptions getAnnotationProcessingOptions() {
        return this.annotationProcessingOptions;
    }
}