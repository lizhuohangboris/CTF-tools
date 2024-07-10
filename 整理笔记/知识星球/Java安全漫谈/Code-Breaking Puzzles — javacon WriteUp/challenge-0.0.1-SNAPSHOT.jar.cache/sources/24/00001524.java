package org.springframework.boot.autoconfigure;

import ch.qos.logback.classic.spi.CallerData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/AutoConfigurationImportSelector.class */
public class AutoConfigurationImportSelector implements DeferredImportSelector, BeanClassLoaderAware, ResourceLoaderAware, BeanFactoryAware, EnvironmentAware, Ordered {
    private static final AutoConfigurationEntry EMPTY_ENTRY = new AutoConfigurationEntry();
    private static final String[] NO_IMPORTS = new String[0];
    private static final Log logger = LogFactory.getLog(AutoConfigurationImportSelector.class);
    private static final String PROPERTY_NAME_AUTOCONFIGURE_EXCLUDE = "spring.autoconfigure.exclude";
    private ConfigurableListableBeanFactory beanFactory;
    private Environment environment;
    private ClassLoader beanClassLoader;
    private ResourceLoader resourceLoader;

    @Override // org.springframework.context.annotation.ImportSelector
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        if (!isEnabled(annotationMetadata)) {
            return NO_IMPORTS;
        }
        AutoConfigurationMetadata autoConfigurationMetadata = AutoConfigurationMetadataLoader.loadMetadata(this.beanClassLoader);
        AutoConfigurationEntry autoConfigurationEntry = getAutoConfigurationEntry(autoConfigurationMetadata, annotationMetadata);
        return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
    }

    protected AutoConfigurationEntry getAutoConfigurationEntry(AutoConfigurationMetadata autoConfigurationMetadata, AnnotationMetadata annotationMetadata) {
        if (!isEnabled(annotationMetadata)) {
            return EMPTY_ENTRY;
        }
        AnnotationAttributes attributes = getAttributes(annotationMetadata);
        List<String> configurations = removeDuplicates(getCandidateConfigurations(annotationMetadata, attributes));
        Set<String> exclusions = getExclusions(annotationMetadata, attributes);
        checkExcludedClasses(configurations, exclusions);
        configurations.removeAll(exclusions);
        List<String> configurations2 = filter(configurations, autoConfigurationMetadata);
        fireAutoConfigurationImportEvents(configurations2, exclusions);
        return new AutoConfigurationEntry(configurations2, exclusions);
    }

    @Override // org.springframework.context.annotation.DeferredImportSelector
    public Class<? extends DeferredImportSelector.Group> getImportGroup() {
        return AutoConfigurationGroup.class;
    }

    protected boolean isEnabled(AnnotationMetadata metadata) {
        if (getClass() == AutoConfigurationImportSelector.class) {
            return ((Boolean) getEnvironment().getProperty(EnableAutoConfiguration.ENABLED_OVERRIDE_PROPERTY, Boolean.class, true)).booleanValue();
        }
        return true;
    }

    protected AnnotationAttributes getAttributes(AnnotationMetadata metadata) {
        String name = getAnnotationClass().getName();
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(name, true));
        Assert.notNull(attributes, () -> {
            return "No auto-configuration attributes found. Is " + metadata.getClassName() + " annotated with " + ClassUtils.getShortName(name) + CallerData.NA;
        });
        return attributes;
    }

    protected Class<?> getAnnotationClass() {
        return EnableAutoConfiguration.class;
    }

    protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
        List<String> configurations = SpringFactoriesLoader.loadFactoryNames(getSpringFactoriesLoaderFactoryClass(), getBeanClassLoader());
        Assert.notEmpty(configurations, "No auto configuration classes found in META-INF/spring.factories. If you are using a custom packaging, make sure that file is correct.");
        return configurations;
    }

    protected Class<?> getSpringFactoriesLoaderFactoryClass() {
        return EnableAutoConfiguration.class;
    }

    private void checkExcludedClasses(List<String> configurations, Set<String> exclusions) {
        List<String> invalidExcludes = new ArrayList<>(exclusions.size());
        for (String exclusion : exclusions) {
            if (ClassUtils.isPresent(exclusion, getClass().getClassLoader()) && !configurations.contains(exclusion)) {
                invalidExcludes.add(exclusion);
            }
        }
        if (!invalidExcludes.isEmpty()) {
            handleInvalidExcludes(invalidExcludes);
        }
    }

    protected void handleInvalidExcludes(List<String> invalidExcludes) {
        StringBuilder message = new StringBuilder();
        for (String exclude : invalidExcludes) {
            message.append("\t- ").append(exclude).append(String.format("%n", new Object[0]));
        }
        throw new IllegalStateException(String.format("The following classes could not be excluded because they are not auto-configuration classes:%n%s", message));
    }

    protected Set<String> getExclusions(AnnotationMetadata metadata, AnnotationAttributes attributes) {
        Set<String> excluded = new LinkedHashSet<>();
        excluded.addAll(asList(attributes, "exclude"));
        excluded.addAll(Arrays.asList(attributes.getStringArray("excludeName")));
        excluded.addAll(getExcludeAutoConfigurationsProperty());
        return excluded;
    }

    private List<String> getExcludeAutoConfigurationsProperty() {
        if (getEnvironment() instanceof ConfigurableEnvironment) {
            Binder binder = Binder.get(getEnvironment());
            return (List) binder.bind(PROPERTY_NAME_AUTOCONFIGURE_EXCLUDE, String[].class).map((v0) -> {
                return Arrays.asList(v0);
            }).orElse(Collections.emptyList());
        }
        String[] excludes = (String[]) getEnvironment().getProperty(PROPERTY_NAME_AUTOCONFIGURE_EXCLUDE, String[].class);
        return excludes != null ? Arrays.asList(excludes) : Collections.emptyList();
    }

    private List<String> filter(List<String> configurations, AutoConfigurationMetadata autoConfigurationMetadata) {
        long startTime = System.nanoTime();
        String[] candidates = StringUtils.toStringArray(configurations);
        boolean[] skip = new boolean[candidates.length];
        boolean skipped = false;
        for (AutoConfigurationImportFilter filter : getAutoConfigurationImportFilters()) {
            invokeAwareMethods(filter);
            boolean[] match = filter.match(candidates, autoConfigurationMetadata);
            for (int i = 0; i < match.length; i++) {
                if (!match[i]) {
                    skip[i] = true;
                    candidates[i] = null;
                    skipped = true;
                }
            }
        }
        if (!skipped) {
            return configurations;
        }
        List<String> result = new ArrayList<>(candidates.length);
        for (int i2 = 0; i2 < candidates.length; i2++) {
            if (!skip[i2]) {
                result.add(candidates[i2]);
            }
        }
        if (logger.isTraceEnabled()) {
            int numberFiltered = configurations.size() - result.size();
            logger.trace("Filtered " + numberFiltered + " auto configuration class in " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + " ms");
        }
        return new ArrayList(result);
    }

    protected List<AutoConfigurationImportFilter> getAutoConfigurationImportFilters() {
        return SpringFactoriesLoader.loadFactories(AutoConfigurationImportFilter.class, this.beanClassLoader);
    }

    protected final <T> List<T> removeDuplicates(List<T> list) {
        return new ArrayList(new LinkedHashSet(list));
    }

    protected final List<String> asList(AnnotationAttributes attributes, String name) {
        String[] value = attributes.getStringArray(name);
        return Arrays.asList(value != null ? value : new String[0]);
    }

    private void fireAutoConfigurationImportEvents(List<String> configurations, Set<String> exclusions) {
        List<AutoConfigurationImportListener> listeners = getAutoConfigurationImportListeners();
        if (!listeners.isEmpty()) {
            AutoConfigurationImportEvent event = new AutoConfigurationImportEvent(this, configurations, exclusions);
            for (AutoConfigurationImportListener listener : listeners) {
                invokeAwareMethods(listener);
                listener.onAutoConfigurationImportEvent(event);
            }
        }
    }

    protected List<AutoConfigurationImportListener> getAutoConfigurationImportListeners() {
        return SpringFactoriesLoader.loadFactories(AutoConfigurationImportListener.class, this.beanClassLoader);
    }

    private void invokeAwareMethods(Object instance) {
        if (instance instanceof Aware) {
            if (instance instanceof BeanClassLoaderAware) {
                ((BeanClassLoaderAware) instance).setBeanClassLoader(this.beanClassLoader);
            }
            if (instance instanceof BeanFactoryAware) {
                ((BeanFactoryAware) instance).setBeanFactory(this.beanFactory);
            }
            if (instance instanceof EnvironmentAware) {
                ((EnvironmentAware) instance).setEnvironment(this.environment);
            }
            if (instance instanceof ResourceLoaderAware) {
                ((ResourceLoaderAware) instance).setResourceLoader(this.resourceLoader);
            }
        }
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        Assert.isInstanceOf(ConfigurableListableBeanFactory.class, beanFactory);
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    protected final ConfigurableListableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    protected ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

    @Override // org.springframework.context.EnvironmentAware
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    protected final Environment getEnvironment() {
        return this.environment;
    }

    @Override // org.springframework.context.ResourceLoaderAware
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    protected final ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 2147483646;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/AutoConfigurationImportSelector$AutoConfigurationGroup.class */
    private static class AutoConfigurationGroup implements DeferredImportSelector.Group, BeanClassLoaderAware, BeanFactoryAware, ResourceLoaderAware {
        private final Map<String, AnnotationMetadata> entries = new LinkedHashMap();
        private final List<AutoConfigurationEntry> autoConfigurationEntries = new ArrayList();
        private ClassLoader beanClassLoader;
        private BeanFactory beanFactory;
        private ResourceLoader resourceLoader;
        private AutoConfigurationMetadata autoConfigurationMetadata;

        private AutoConfigurationGroup() {
        }

        @Override // org.springframework.beans.factory.BeanClassLoaderAware
        public void setBeanClassLoader(ClassLoader classLoader) {
            this.beanClassLoader = classLoader;
        }

        @Override // org.springframework.beans.factory.BeanFactoryAware
        public void setBeanFactory(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Override // org.springframework.context.ResourceLoaderAware
        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }

        @Override // org.springframework.context.annotation.DeferredImportSelector.Group
        public void process(AnnotationMetadata annotationMetadata, DeferredImportSelector deferredImportSelector) {
            Assert.state(deferredImportSelector instanceof AutoConfigurationImportSelector, () -> {
                return String.format("Only %s implementations are supported, got %s", AutoConfigurationImportSelector.class.getSimpleName(), deferredImportSelector.getClass().getName());
            });
            AutoConfigurationEntry autoConfigurationEntry = ((AutoConfigurationImportSelector) deferredImportSelector).getAutoConfigurationEntry(getAutoConfigurationMetadata(), annotationMetadata);
            this.autoConfigurationEntries.add(autoConfigurationEntry);
            for (String importClassName : autoConfigurationEntry.getConfigurations()) {
                this.entries.putIfAbsent(importClassName, annotationMetadata);
            }
        }

        @Override // org.springframework.context.annotation.DeferredImportSelector.Group
        public Iterable<DeferredImportSelector.Group.Entry> selectImports() {
            if (this.autoConfigurationEntries.isEmpty()) {
                return Collections.emptyList();
            }
            Set<String> allExclusions = (Set) this.autoConfigurationEntries.stream().map((v0) -> {
                return v0.getExclusions();
            }).flatMap((v0) -> {
                return v0.stream();
            }).collect(Collectors.toSet());
            Set<String> processedConfigurations = (Set) this.autoConfigurationEntries.stream().map((v0) -> {
                return v0.getConfigurations();
            }).flatMap((v0) -> {
                return v0.stream();
            }).collect(Collectors.toCollection(LinkedHashSet::new));
            processedConfigurations.removeAll(allExclusions);
            return (Iterable) sortAutoConfigurations(processedConfigurations, getAutoConfigurationMetadata()).stream().map(importClassName -> {
                return new DeferredImportSelector.Group.Entry(this.entries.get(importClassName), importClassName);
            }).collect(Collectors.toList());
        }

        private AutoConfigurationMetadata getAutoConfigurationMetadata() {
            if (this.autoConfigurationMetadata == null) {
                this.autoConfigurationMetadata = AutoConfigurationMetadataLoader.loadMetadata(this.beanClassLoader);
            }
            return this.autoConfigurationMetadata;
        }

        private List<String> sortAutoConfigurations(Set<String> configurations, AutoConfigurationMetadata autoConfigurationMetadata) {
            return new AutoConfigurationSorter(getMetadataReaderFactory(), autoConfigurationMetadata).getInPriorityOrder(configurations);
        }

        private MetadataReaderFactory getMetadataReaderFactory() {
            try {
                return (MetadataReaderFactory) this.beanFactory.getBean(SharedMetadataReaderFactoryContextInitializer.BEAN_NAME, MetadataReaderFactory.class);
            } catch (NoSuchBeanDefinitionException e) {
                return new CachingMetadataReaderFactory(this.resourceLoader);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/AutoConfigurationImportSelector$AutoConfigurationEntry.class */
    public static class AutoConfigurationEntry {
        private final List<String> configurations;
        private final Set<String> exclusions;

        private AutoConfigurationEntry() {
            this.configurations = Collections.emptyList();
            this.exclusions = Collections.emptySet();
        }

        AutoConfigurationEntry(Collection<String> configurations, Collection<String> exclusions) {
            this.configurations = new ArrayList(configurations);
            this.exclusions = new HashSet(exclusions);
        }

        public List<String> getConfigurations() {
            return this.configurations;
        }

        public Set<String> getExclusions() {
            return this.exclusions;
        }
    }
}