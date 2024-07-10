package org.springframework.context.annotation;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.index.CandidateComponentsIndex;
import org.springframework.context.index.CandidateComponentsIndexLoader;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/ClassPathScanningCandidateComponentProvider.class */
public class ClassPathScanningCandidateComponentProvider implements EnvironmentCapable, ResourceLoaderAware {
    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    protected final Log logger;
    private String resourcePattern;
    private final List<TypeFilter> includeFilters;
    private final List<TypeFilter> excludeFilters;
    @Nullable
    private Environment environment;
    @Nullable
    private ConditionEvaluator conditionEvaluator;
    @Nullable
    private ResourcePatternResolver resourcePatternResolver;
    @Nullable
    private MetadataReaderFactory metadataReaderFactory;
    @Nullable
    private CandidateComponentsIndex componentsIndex;

    public ClassPathScanningCandidateComponentProvider() {
        this.logger = LogFactory.getLog(getClass());
        this.resourcePattern = DEFAULT_RESOURCE_PATTERN;
        this.includeFilters = new LinkedList();
        this.excludeFilters = new LinkedList();
    }

    public ClassPathScanningCandidateComponentProvider(boolean useDefaultFilters) {
        this(useDefaultFilters, new StandardEnvironment());
    }

    public ClassPathScanningCandidateComponentProvider(boolean useDefaultFilters, Environment environment) {
        this.logger = LogFactory.getLog(getClass());
        this.resourcePattern = DEFAULT_RESOURCE_PATTERN;
        this.includeFilters = new LinkedList();
        this.excludeFilters = new LinkedList();
        if (useDefaultFilters) {
            registerDefaultFilters();
        }
        setEnvironment(environment);
        setResourceLoader(null);
    }

    public void setResourcePattern(String resourcePattern) {
        Assert.notNull(resourcePattern, "'resourcePattern' must not be null");
        this.resourcePattern = resourcePattern;
    }

    public void addIncludeFilter(TypeFilter includeFilter) {
        this.includeFilters.add(includeFilter);
    }

    public void addExcludeFilter(TypeFilter excludeFilter) {
        this.excludeFilters.add(0, excludeFilter);
    }

    public void resetFilters(boolean useDefaultFilters) {
        this.includeFilters.clear();
        this.excludeFilters.clear();
        if (useDefaultFilters) {
            registerDefaultFilters();
        }
    }

    public void registerDefaultFilters() {
        this.includeFilters.add(new AnnotationTypeFilter(Component.class));
        ClassLoader cl = ClassPathScanningCandidateComponentProvider.class.getClassLoader();
        try {
            this.includeFilters.add(new AnnotationTypeFilter(ClassUtils.forName("javax.annotation.ManagedBean", cl), false));
            this.logger.trace("JSR-250 'javax.annotation.ManagedBean' found and supported for component scanning");
        } catch (ClassNotFoundException e) {
        }
        try {
            this.includeFilters.add(new AnnotationTypeFilter(ClassUtils.forName("javax.inject.Named", cl), false));
            this.logger.trace("JSR-330 'javax.inject.Named' annotation found and supported for component scanning");
        } catch (ClassNotFoundException e2) {
        }
    }

    public void setEnvironment(Environment environment) {
        Assert.notNull(environment, "Environment must not be null");
        this.environment = environment;
        this.conditionEvaluator = null;
    }

    @Override // org.springframework.core.env.EnvironmentCapable
    public final Environment getEnvironment() {
        if (this.environment == null) {
            this.environment = new StandardEnvironment();
        }
        return this.environment;
    }

    @Nullable
    protected BeanDefinitionRegistry getRegistry() {
        return null;
    }

    @Override // org.springframework.context.ResourceLoaderAware
    public void setResourceLoader(@Nullable ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
        this.componentsIndex = CandidateComponentsIndexLoader.loadIndex(this.resourcePatternResolver.getClassLoader());
    }

    public final ResourceLoader getResourceLoader() {
        return getResourcePatternResolver();
    }

    private ResourcePatternResolver getResourcePatternResolver() {
        if (this.resourcePatternResolver == null) {
            this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
        }
        return this.resourcePatternResolver;
    }

    public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
        this.metadataReaderFactory = metadataReaderFactory;
    }

    public final MetadataReaderFactory getMetadataReaderFactory() {
        if (this.metadataReaderFactory == null) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory();
        }
        return this.metadataReaderFactory;
    }

    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        if (this.componentsIndex != null && indexSupportsIncludeFilters()) {
            return addCandidateComponentsFromIndex(this.componentsIndex, basePackage);
        }
        return scanCandidateComponents(basePackage);
    }

    private boolean indexSupportsIncludeFilters() {
        for (TypeFilter includeFilter : this.includeFilters) {
            if (!indexSupportsIncludeFilter(includeFilter)) {
                return false;
            }
        }
        return true;
    }

    private boolean indexSupportsIncludeFilter(TypeFilter filter) {
        if (filter instanceof AnnotationTypeFilter) {
            Class<? extends Annotation> annotation = ((AnnotationTypeFilter) filter).getAnnotationType();
            return AnnotationUtils.isAnnotationDeclaredLocally(Indexed.class, annotation) || annotation.getName().startsWith("javax.");
        } else if (filter instanceof AssignableTypeFilter) {
            Class<?> target = ((AssignableTypeFilter) filter).getTargetType();
            return AnnotationUtils.isAnnotationDeclaredLocally(Indexed.class, target);
        } else {
            return false;
        }
    }

    @Nullable
    private String extractStereotype(TypeFilter filter) {
        if (filter instanceof AnnotationTypeFilter) {
            return ((AnnotationTypeFilter) filter).getAnnotationType().getName();
        }
        if (filter instanceof AssignableTypeFilter) {
            return ((AssignableTypeFilter) filter).getTargetType().getName();
        }
        return null;
    }

    private Set<BeanDefinition> addCandidateComponentsFromIndex(CandidateComponentsIndex index, String basePackage) {
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        try {
            Set<String> types = new HashSet<>();
            for (TypeFilter filter : this.includeFilters) {
                String stereotype = extractStereotype(filter);
                if (stereotype == null) {
                    throw new IllegalArgumentException("Failed to extract stereotype from " + filter);
                }
                types.addAll(index.getCandidateTypes(basePackage, stereotype));
            }
            boolean traceEnabled = this.logger.isTraceEnabled();
            boolean debugEnabled = this.logger.isDebugEnabled();
            for (String type : types) {
                MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(type);
                if (isCandidateComponent(metadataReader)) {
                    AnnotatedGenericBeanDefinition sbd = new AnnotatedGenericBeanDefinition(metadataReader.getAnnotationMetadata());
                    if (isCandidateComponent(sbd)) {
                        if (debugEnabled) {
                            this.logger.debug("Using candidate component class from index: " + type);
                        }
                        candidates.add(sbd);
                    } else if (debugEnabled) {
                        this.logger.debug("Ignored because not a concrete top-level class: " + type);
                    }
                } else if (traceEnabled) {
                    this.logger.trace("Ignored because matching an exclude filter: " + type);
                }
            }
            return candidates;
        } catch (IOException ex) {
            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
        }
    }

    private Set<BeanDefinition> scanCandidateComponents(String basePackage) {
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(basePackage) + '/' + this.resourcePattern;
            Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);
            boolean traceEnabled = this.logger.isTraceEnabled();
            boolean debugEnabled = this.logger.isDebugEnabled();
            for (Resource resource : resources) {
                if (traceEnabled) {
                    this.logger.trace("Scanning " + resource);
                }
                if (resource.isReadable()) {
                    MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
                    if (isCandidateComponent(metadataReader)) {
                        ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                        sbd.setResource(resource);
                        sbd.setSource(resource);
                        if (isCandidateComponent(sbd)) {
                            if (debugEnabled) {
                                this.logger.debug("Identified candidate component class: " + resource);
                            }
                            candidates.add(sbd);
                        } else if (debugEnabled) {
                            this.logger.debug("Ignored because not a concrete top-level class: " + resource);
                        }
                    } else if (traceEnabled) {
                        this.logger.trace("Ignored because not matching any filter: " + resource);
                    }
                } else if (traceEnabled) {
                    this.logger.trace("Ignored because not readable: " + resource);
                }
            }
            return candidates;
        } catch (IOException ex) {
            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
        }
    }

    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(getEnvironment().resolveRequiredPlaceholders(basePackage));
    }

    protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
        for (TypeFilter tf : this.excludeFilters) {
            if (tf.match(metadataReader, getMetadataReaderFactory())) {
                return false;
            }
        }
        for (TypeFilter tf2 : this.includeFilters) {
            if (tf2.match(metadataReader, getMetadataReaderFactory())) {
                return isConditionMatch(metadataReader);
            }
        }
        return false;
    }

    private boolean isConditionMatch(MetadataReader metadataReader) {
        if (this.conditionEvaluator == null) {
            this.conditionEvaluator = new ConditionEvaluator(getRegistry(), this.environment, this.resourcePatternResolver);
        }
        return !this.conditionEvaluator.shouldSkip(metadataReader.getAnnotationMetadata());
    }

    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return metadata.isIndependent() && (metadata.isConcrete() || (metadata.isAbstract() && metadata.hasAnnotatedMethods(Lookup.class.getName())));
    }

    public void clearCache() {
        if (this.metadataReaderFactory instanceof CachingMetadataReaderFactory) {
            ((CachingMetadataReaderFactory) this.metadataReaderFactory).clearCache();
        }
    }
}