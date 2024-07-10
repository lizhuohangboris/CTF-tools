package org.hibernate.validator.internal.metadata;

import java.util.EnumSet;
import java.util.List;
import org.hibernate.validator.internal.engine.MethodValidationConfiguration;
import org.hibernate.validator.internal.engine.groups.ValidationOrderGenerator;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.BeanMetaData;
import org.hibernate.validator.internal.metadata.aggregated.BeanMetaDataImpl;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptions;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.provider.AnnotationMetaDataProvider;
import org.hibernate.validator.internal.metadata.provider.MetaDataProvider;
import org.hibernate.validator.internal.metadata.raw.BeanConfiguration;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.ConcurrentReferenceHashMap;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.ExecutableHelper;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.classhierarchy.ClassHierarchyHelper;
import org.hibernate.validator.internal.util.classhierarchy.Filter;
import org.hibernate.validator.internal.util.logging.Messages;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/BeanMetaDataManager.class */
public class BeanMetaDataManager {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    private final List<MetaDataProvider> metaDataProviders = CollectionHelper.newArrayList();
    private final ConstraintHelper constraintHelper;
    private final TypeResolutionHelper typeResolutionHelper;
    private final ValueExtractorManager valueExtractorManager;
    private final ExecutableParameterNameProvider parameterNameProvider;
    private final ConcurrentReferenceHashMap<Class<?>, BeanMetaData<?>> beanMetaDataCache;
    private final ExecutableHelper executableHelper;
    private final ValidationOrderGenerator validationOrderGenerator;
    private final MethodValidationConfiguration methodValidationConfiguration;

    public BeanMetaDataManager(ConstraintHelper constraintHelper, ExecutableHelper executableHelper, TypeResolutionHelper typeResolutionHelper, ExecutableParameterNameProvider parameterNameProvider, ValueExtractorManager valueExtractorManager, ValidationOrderGenerator validationOrderGenerator, List<MetaDataProvider> optionalMetaDataProviders, MethodValidationConfiguration methodValidationConfiguration) {
        this.constraintHelper = constraintHelper;
        this.executableHelper = executableHelper;
        this.typeResolutionHelper = typeResolutionHelper;
        this.valueExtractorManager = valueExtractorManager;
        this.parameterNameProvider = parameterNameProvider;
        this.validationOrderGenerator = validationOrderGenerator;
        this.metaDataProviders.addAll(optionalMetaDataProviders);
        this.methodValidationConfiguration = methodValidationConfiguration;
        this.beanMetaDataCache = new ConcurrentReferenceHashMap<>(16, DEFAULT_LOAD_FACTOR, 16, ConcurrentReferenceHashMap.ReferenceType.SOFT, ConcurrentReferenceHashMap.ReferenceType.SOFT, EnumSet.of(ConcurrentReferenceHashMap.Option.IDENTITY_COMPARISONS));
        AnnotationProcessingOptions annotationProcessingOptions = getAnnotationProcessingOptionsFromNonDefaultProviders();
        AnnotationMetaDataProvider defaultProvider = new AnnotationMetaDataProvider(constraintHelper, typeResolutionHelper, valueExtractorManager, annotationProcessingOptions);
        this.metaDataProviders.add(defaultProvider);
    }

    public <T> BeanMetaData<T> getBeanMetaData(Class<T> beanClass) {
        Contracts.assertNotNull(beanClass, Messages.MESSAGES.beanTypeCannotBeNull());
        BeanMetaData<T> beanMetaData = (BeanMetaData<T>) this.beanMetaDataCache.computeIfAbsent(beanClass, bc -> {
            return createBeanMetaData(bc);
        });
        return beanMetaData;
    }

    public void clear() {
        this.beanMetaDataCache.clear();
    }

    public int numberOfCachedBeanMetaDataInstances() {
        return this.beanMetaDataCache.size();
    }

    private <T> BeanMetaDataImpl<T> createBeanMetaData(Class<T> clazz) {
        BeanMetaDataImpl.BeanMetaDataBuilder<T> builder = BeanMetaDataImpl.BeanMetaDataBuilder.getInstance(this.constraintHelper, this.executableHelper, this.typeResolutionHelper, this.valueExtractorManager, this.parameterNameProvider, this.validationOrderGenerator, clazz, this.methodValidationConfiguration);
        for (MetaDataProvider provider : this.metaDataProviders) {
            for (BeanConfiguration<? super T> beanConfiguration : getBeanConfigurationForHierarchy(provider, clazz)) {
                builder.add(beanConfiguration);
            }
        }
        return builder.build();
    }

    private AnnotationProcessingOptions getAnnotationProcessingOptionsFromNonDefaultProviders() {
        AnnotationProcessingOptions options = new AnnotationProcessingOptionsImpl();
        for (MetaDataProvider metaDataProvider : this.metaDataProviders) {
            options.merge(metaDataProvider.getAnnotationProcessingOptions());
        }
        return options;
    }

    private <T> List<BeanConfiguration<? super T>> getBeanConfigurationForHierarchy(MetaDataProvider provider, Class<T> beanClass) {
        List<BeanConfiguration<? super T>> configurations = CollectionHelper.newArrayList();
        for (Class<T> cls : ClassHierarchyHelper.getHierarchy(beanClass, new Filter[0])) {
            BeanConfiguration<? super T> configuration = provider.getBeanConfiguration(cls);
            if (configuration != null) {
                configurations.add(configuration);
            }
        }
        return configurations;
    }
}