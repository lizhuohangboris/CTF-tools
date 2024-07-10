package org.hibernate.validator.internal.metadata.provider;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.hibernate.validator.internal.cfg.context.DefaultConstraintMapping;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptions;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.raw.BeanConfiguration;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/provider/ProgrammaticMetaDataProvider.class */
public class ProgrammaticMetaDataProvider implements MetaDataProvider {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Map<String, BeanConfiguration<?>> configuredBeans;
    private final AnnotationProcessingOptions annotationProcessingOptions;

    public ProgrammaticMetaDataProvider(ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, Set<DefaultConstraintMapping> constraintMappings) {
        Contracts.assertNotNull(constraintMappings);
        this.configuredBeans = CollectionHelper.toImmutableMap(createBeanConfigurations(constraintMappings, constraintHelper, typeResolutionHelper, valueExtractorManager));
        assertUniquenessOfConfiguredTypes(constraintMappings);
        this.annotationProcessingOptions = mergeAnnotationProcessingOptions(constraintMappings);
    }

    private static void assertUniquenessOfConfiguredTypes(Set<DefaultConstraintMapping> mappings) {
        Set<Class<?>> allConfiguredTypes = CollectionHelper.newHashSet();
        for (DefaultConstraintMapping constraintMapping : mappings) {
            for (Class<?> configuredType : constraintMapping.getConfiguredTypes()) {
                if (allConfiguredTypes.contains(configuredType)) {
                    throw LOG.getBeanClassHasAlreadyBeConfiguredViaProgrammaticApiException(configuredType);
                }
            }
            allConfiguredTypes.addAll(constraintMapping.getConfiguredTypes());
        }
    }

    private static Map<String, BeanConfiguration<?>> createBeanConfigurations(Set<DefaultConstraintMapping> mappings, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
        Map<String, BeanConfiguration<?>> configuredBeans = new HashMap<>();
        for (DefaultConstraintMapping mapping : mappings) {
            Set<BeanConfiguration<?>> beanConfigurations = mapping.getBeanConfigurations(constraintHelper, typeResolutionHelper, valueExtractorManager);
            for (BeanConfiguration<?> beanConfiguration : beanConfigurations) {
                configuredBeans.put(beanConfiguration.getBeanClass().getName(), beanConfiguration);
            }
        }
        return configuredBeans;
    }

    private static AnnotationProcessingOptions mergeAnnotationProcessingOptions(Set<DefaultConstraintMapping> mappings) {
        if (mappings.size() == 1) {
            return mappings.iterator().next().getAnnotationProcessingOptions();
        }
        AnnotationProcessingOptions options = new AnnotationProcessingOptionsImpl();
        for (DefaultConstraintMapping mapping : mappings) {
            options.merge(mapping.getAnnotationProcessingOptions());
        }
        return options;
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