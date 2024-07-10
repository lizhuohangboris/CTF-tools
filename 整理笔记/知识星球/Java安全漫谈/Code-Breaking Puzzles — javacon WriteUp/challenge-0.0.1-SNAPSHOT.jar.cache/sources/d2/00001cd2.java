package org.springframework.context.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.parsing.FailFastProblemReporter;
import org.springframework.beans.factory.parsing.PassThroughSourceExtractor;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ConfigurationClassEnhancer;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/ConfigurationClassPostProcessor.class */
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered, ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware {
    private static final String IMPORT_REGISTRY_BEAN_NAME = ConfigurationClassPostProcessor.class.getName() + ".importRegistry";
    @Nullable
    private Environment environment;
    @Nullable
    private ConfigurationClassBeanDefinitionReader reader;
    private final Log logger = LogFactory.getLog(getClass());
    private SourceExtractor sourceExtractor = new PassThroughSourceExtractor();
    private ProblemReporter problemReporter = new FailFastProblemReporter();
    private ResourceLoader resourceLoader = new DefaultResourceLoader();
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();
    private boolean setMetadataReaderFactoryCalled = false;
    private final Set<Integer> registriesPostProcessed = new HashSet();
    private final Set<Integer> factoriesPostProcessed = new HashSet();
    private boolean localBeanNameGeneratorSet = false;
    private BeanNameGenerator componentScanBeanNameGenerator = new AnnotationBeanNameGenerator();
    private BeanNameGenerator importBeanNameGenerator = new AnnotationBeanNameGenerator() { // from class: org.springframework.context.annotation.ConfigurationClassPostProcessor.1
        @Override // org.springframework.context.annotation.AnnotationBeanNameGenerator
        protected String buildDefaultBeanName(BeanDefinition definition) {
            String beanClassName = definition.getBeanClassName();
            Assert.state(beanClassName != null, "No bean class name set");
            return beanClassName;
        }
    };

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    public void setSourceExtractor(@Nullable SourceExtractor sourceExtractor) {
        this.sourceExtractor = sourceExtractor != null ? sourceExtractor : new PassThroughSourceExtractor();
    }

    public void setProblemReporter(@Nullable ProblemReporter problemReporter) {
        this.problemReporter = problemReporter != null ? problemReporter : new FailFastProblemReporter();
    }

    public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
        Assert.notNull(metadataReaderFactory, "MetadataReaderFactory must not be null");
        this.metadataReaderFactory = metadataReaderFactory;
        this.setMetadataReaderFactoryCalled = true;
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        Assert.notNull(beanNameGenerator, "BeanNameGenerator must not be null");
        this.localBeanNameGeneratorSet = true;
        this.componentScanBeanNameGenerator = beanNameGenerator;
        this.importBeanNameGenerator = beanNameGenerator;
    }

    @Override // org.springframework.context.EnvironmentAware
    public void setEnvironment(Environment environment) {
        Assert.notNull(environment, "Environment must not be null");
        this.environment = environment;
    }

    @Override // org.springframework.context.ResourceLoaderAware
    public void setResourceLoader(ResourceLoader resourceLoader) {
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        this.resourceLoader = resourceLoader;
        if (!this.setMetadataReaderFactoryCalled) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
        }
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
        if (!this.setMetadataReaderFactoryCalled) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory(beanClassLoader);
        }
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        int registryId = System.identityHashCode(registry);
        if (this.registriesPostProcessed.contains(Integer.valueOf(registryId))) {
            throw new IllegalStateException("postProcessBeanDefinitionRegistry already called on this post-processor against " + registry);
        }
        if (this.factoriesPostProcessed.contains(Integer.valueOf(registryId))) {
            throw new IllegalStateException("postProcessBeanFactory already called on this post-processor against " + registry);
        }
        this.registriesPostProcessed.add(Integer.valueOf(registryId));
        processConfigBeanDefinitions(registry);
    }

    @Override // org.springframework.beans.factory.config.BeanFactoryPostProcessor
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        int factoryId = System.identityHashCode(beanFactory);
        if (this.factoriesPostProcessed.contains(Integer.valueOf(factoryId))) {
            throw new IllegalStateException("postProcessBeanFactory already called on this post-processor against " + beanFactory);
        }
        this.factoriesPostProcessed.add(Integer.valueOf(factoryId));
        if (!this.registriesPostProcessed.contains(Integer.valueOf(factoryId))) {
            processConfigBeanDefinitions((BeanDefinitionRegistry) beanFactory);
        }
        enhanceConfigurationClasses(beanFactory);
        beanFactory.addBeanPostProcessor(new ImportAwareBeanPostProcessor(beanFactory));
    }

    public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
        BeanNameGenerator generator;
        List<BeanDefinitionHolder> configCandidates = new ArrayList<>();
        String[] candidateNames = registry.getBeanDefinitionNames();
        for (String beanName : candidateNames) {
            BeanDefinition beanDef = registry.getBeanDefinition(beanName);
            if (ConfigurationClassUtils.isFullConfigurationClass(beanDef) || ConfigurationClassUtils.isLiteConfigurationClass(beanDef)) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Bean definition has already been processed as a configuration class: " + beanDef);
                }
            } else if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, this.metadataReaderFactory)) {
                configCandidates.add(new BeanDefinitionHolder(beanDef, beanName));
            }
        }
        if (configCandidates.isEmpty()) {
            return;
        }
        configCandidates.sort(bd1, bd2 -> {
            int i1 = ConfigurationClassUtils.getOrder(bd1.getBeanDefinition());
            int i2 = ConfigurationClassUtils.getOrder(bd2.getBeanDefinition());
            return Integer.compare(i1, i2);
        });
        SingletonBeanRegistry sbr = null;
        if (registry instanceof SingletonBeanRegistry) {
            sbr = (SingletonBeanRegistry) registry;
            if (!this.localBeanNameGeneratorSet && (generator = (BeanNameGenerator) sbr.getSingleton(AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR)) != null) {
                this.componentScanBeanNameGenerator = generator;
                this.importBeanNameGenerator = generator;
            }
        }
        if (this.environment == null) {
            this.environment = new StandardEnvironment();
        }
        ConfigurationClassParser parser = new ConfigurationClassParser(this.metadataReaderFactory, this.problemReporter, this.environment, this.resourceLoader, this.componentScanBeanNameGenerator, registry);
        Set<BeanDefinitionHolder> candidates = new LinkedHashSet<>(configCandidates);
        Set<ConfigurationClass> alreadyParsed = new HashSet<>(configCandidates.size());
        do {
            parser.parse(candidates);
            parser.validate();
            Set<ConfigurationClass> configClasses = new LinkedHashSet<>(parser.getConfigurationClasses());
            configClasses.removeAll(alreadyParsed);
            if (this.reader == null) {
                this.reader = new ConfigurationClassBeanDefinitionReader(registry, this.sourceExtractor, this.resourceLoader, this.environment, this.importBeanNameGenerator, parser.getImportRegistry());
            }
            this.reader.loadBeanDefinitions(configClasses);
            alreadyParsed.addAll(configClasses);
            candidates.clear();
            if (registry.getBeanDefinitionCount() > candidateNames.length) {
                String[] newCandidateNames = registry.getBeanDefinitionNames();
                Set<String> oldCandidateNames = new HashSet<>(Arrays.asList(candidateNames));
                Set<String> alreadyParsedClasses = new HashSet<>();
                for (ConfigurationClass configurationClass : alreadyParsed) {
                    alreadyParsedClasses.add(configurationClass.getMetadata().getClassName());
                }
                for (String candidateName : newCandidateNames) {
                    if (!oldCandidateNames.contains(candidateName)) {
                        BeanDefinition bd = registry.getBeanDefinition(candidateName);
                        if (ConfigurationClassUtils.checkConfigurationClassCandidate(bd, this.metadataReaderFactory) && !alreadyParsedClasses.contains(bd.getBeanClassName())) {
                            candidates.add(new BeanDefinitionHolder(bd, candidateName));
                        }
                    }
                }
                candidateNames = newCandidateNames;
            }
        } while (!candidates.isEmpty());
        if (sbr != null && !sbr.containsSingleton(IMPORT_REGISTRY_BEAN_NAME)) {
            sbr.registerSingleton(IMPORT_REGISTRY_BEAN_NAME, parser.getImportRegistry());
        }
        if (this.metadataReaderFactory instanceof CachingMetadataReaderFactory) {
            ((CachingMetadataReaderFactory) this.metadataReaderFactory).clearCache();
        }
    }

    public void enhanceConfigurationClasses(ConfigurableListableBeanFactory beanFactory) {
        String[] beanDefinitionNames;
        Class<?> enhancedClass;
        Map<String, AbstractBeanDefinition> configBeanDefs = new LinkedHashMap<>();
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDef = beanFactory.getBeanDefinition(beanName);
            if (ConfigurationClassUtils.isFullConfigurationClass(beanDef)) {
                if (!(beanDef instanceof AbstractBeanDefinition)) {
                    throw new BeanDefinitionStoreException("Cannot enhance @Configuration bean definition '" + beanName + "' since it is not stored in an AbstractBeanDefinition subclass");
                }
                if (this.logger.isInfoEnabled() && beanFactory.containsSingleton(beanName)) {
                    this.logger.info("Cannot enhance @Configuration bean definition '" + beanName + "' since its singleton instance has been created too early. The typical cause is a non-static @Bean method with a BeanDefinitionRegistryPostProcessor return type: Consider declaring such methods as 'static'.");
                }
                configBeanDefs.put(beanName, (AbstractBeanDefinition) beanDef);
            }
        }
        if (configBeanDefs.isEmpty()) {
            return;
        }
        ConfigurationClassEnhancer enhancer = new ConfigurationClassEnhancer();
        for (Map.Entry<String, AbstractBeanDefinition> entry : configBeanDefs.entrySet()) {
            AbstractBeanDefinition beanDef2 = entry.getValue();
            beanDef2.setAttribute(AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
            try {
                Class<?> configClass = beanDef2.resolveBeanClass(this.beanClassLoader);
                if (configClass != null && configClass != (enhancedClass = enhancer.enhance(configClass, this.beanClassLoader))) {
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace(String.format("Replacing bean definition '%s' existing class '%s' with enhanced class '%s'", entry.getKey(), configClass.getName(), enhancedClass.getName()));
                    }
                    beanDef2.setBeanClass(enhancedClass);
                }
            } catch (Throwable ex) {
                throw new IllegalStateException("Cannot load configuration class: " + beanDef2.getBeanClassName(), ex);
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/ConfigurationClassPostProcessor$ImportAwareBeanPostProcessor.class */
    private static class ImportAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter {
        private final BeanFactory beanFactory;

        public ImportAwareBeanPostProcessor(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Override // org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter, org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor
        public PropertyValues postProcessProperties(@Nullable PropertyValues pvs, Object bean, String beanName) {
            if (bean instanceof ConfigurationClassEnhancer.EnhancedConfiguration) {
                ((ConfigurationClassEnhancer.EnhancedConfiguration) bean).setBeanFactory(this.beanFactory);
            }
            return pvs;
        }

        @Override // org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter, org.springframework.beans.factory.config.BeanPostProcessor
        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            if (bean instanceof ImportAware) {
                ImportRegistry ir = (ImportRegistry) this.beanFactory.getBean(ConfigurationClassPostProcessor.IMPORT_REGISTRY_BEAN_NAME, ImportRegistry.class);
                AnnotationMetadata importingClass = ir.getImportingClassFor(bean.getClass().getSuperclass().getName());
                if (importingClass != null) {
                    ((ImportAware) bean).setImportMetadata(importingClass);
                }
            }
            return bean;
        }
    }
}