package org.springframework.context.annotation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.naming.factory.Constants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.Location;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.NestedIOException;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.thymeleaf.engine.XMLDeclaration;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/ConfigurationClassParser.class */
public class ConfigurationClassParser {
    private static final PropertySourceFactory DEFAULT_PROPERTY_SOURCE_FACTORY = new DefaultPropertySourceFactory();
    private static final Comparator<DeferredImportSelectorHolder> DEFERRED_IMPORT_COMPARATOR = o1, o2 -> {
        return AnnotationAwareOrderComparator.INSTANCE.compare(o1.getImportSelector(), o2.getImportSelector());
    };
    private final MetadataReaderFactory metadataReaderFactory;
    private final ProblemReporter problemReporter;
    private final Environment environment;
    private final ResourceLoader resourceLoader;
    private final BeanDefinitionRegistry registry;
    private final ComponentScanAnnotationParser componentScanParser;
    private final ConditionEvaluator conditionEvaluator;
    private final Log logger = LogFactory.getLog(getClass());
    private final Map<ConfigurationClass, ConfigurationClass> configurationClasses = new LinkedHashMap();
    private final Map<String, ConfigurationClass> knownSuperclasses = new HashMap();
    private final List<String> propertySourceNames = new ArrayList();
    private final ImportStack importStack = new ImportStack();
    private final DeferredImportSelectorHandler deferredImportSelectorHandler = new DeferredImportSelectorHandler();

    public ConfigurationClassParser(MetadataReaderFactory metadataReaderFactory, ProblemReporter problemReporter, Environment environment, ResourceLoader resourceLoader, BeanNameGenerator componentScanBeanNameGenerator, BeanDefinitionRegistry registry) {
        this.metadataReaderFactory = metadataReaderFactory;
        this.problemReporter = problemReporter;
        this.environment = environment;
        this.resourceLoader = resourceLoader;
        this.registry = registry;
        this.componentScanParser = new ComponentScanAnnotationParser(environment, resourceLoader, componentScanBeanNameGenerator, registry);
        this.conditionEvaluator = new ConditionEvaluator(registry, environment, resourceLoader);
    }

    public void parse(Set<BeanDefinitionHolder> configCandidates) {
        for (BeanDefinitionHolder holder : configCandidates) {
            BeanDefinition bd = holder.getBeanDefinition();
            try {
                if (bd instanceof AnnotatedBeanDefinition) {
                    parse(((AnnotatedBeanDefinition) bd).getMetadata(), holder.getBeanName());
                } else if ((bd instanceof AbstractBeanDefinition) && ((AbstractBeanDefinition) bd).hasBeanClass()) {
                    parse(((AbstractBeanDefinition) bd).getBeanClass(), holder.getBeanName());
                } else {
                    parse(bd.getBeanClassName(), holder.getBeanName());
                }
            } catch (BeanDefinitionStoreException ex) {
                throw ex;
            } catch (Throwable ex2) {
                throw new BeanDefinitionStoreException("Failed to parse configuration class [" + bd.getBeanClassName() + "]", ex2);
            }
        }
        this.deferredImportSelectorHandler.process();
    }

    protected final void parse(@Nullable String className, String beanName) throws IOException {
        Assert.notNull(className, "No bean class name for configuration class bean definition");
        MetadataReader reader = this.metadataReaderFactory.getMetadataReader(className);
        processConfigurationClass(new ConfigurationClass(reader, beanName));
    }

    protected final void parse(Class<?> clazz, String beanName) throws IOException {
        processConfigurationClass(new ConfigurationClass(clazz, beanName));
    }

    protected final void parse(AnnotationMetadata metadata, String beanName) throws IOException {
        processConfigurationClass(new ConfigurationClass(metadata, beanName));
    }

    public void validate() {
        for (ConfigurationClass configClass : this.configurationClasses.keySet()) {
            configClass.validate(this.problemReporter);
        }
    }

    public Set<ConfigurationClass> getConfigurationClasses() {
        return this.configurationClasses.keySet();
    }

    protected void processConfigurationClass(ConfigurationClass configClass) throws IOException {
        if (this.conditionEvaluator.shouldSkip(configClass.getMetadata(), ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION)) {
            return;
        }
        ConfigurationClass existingClass = this.configurationClasses.get(configClass);
        if (existingClass != null) {
            if (configClass.isImported()) {
                if (existingClass.isImported()) {
                    existingClass.mergeImportedBy(configClass);
                    return;
                }
                return;
            }
            this.configurationClasses.remove(configClass);
            Collection<ConfigurationClass> values = this.knownSuperclasses.values();
            configClass.getClass();
            values.removeIf((v1) -> {
                return r1.equals(v1);
            });
        }
        SourceClass sourceClass = asSourceClass(configClass);
        do {
            sourceClass = doProcessConfigurationClass(configClass, sourceClass);
        } while (sourceClass != null);
        this.configurationClasses.put(configClass, configClass);
    }

    @Nullable
    protected final SourceClass doProcessConfigurationClass(ConfigurationClass configClass, SourceClass sourceClass) throws IOException {
        String superclass;
        if (configClass.getMetadata().isAnnotated(Component.class.getName())) {
            processMemberClasses(configClass, sourceClass);
        }
        for (AnnotationAttributes propertySource : AnnotationConfigUtils.attributesForRepeatable(sourceClass.getMetadata(), PropertySources.class, PropertySource.class)) {
            if (this.environment instanceof ConfigurableEnvironment) {
                processPropertySource(propertySource);
            } else {
                this.logger.info("Ignoring @PropertySource annotation on [" + sourceClass.getMetadata().getClassName() + "]. Reason: Environment must implement ConfigurableEnvironment");
            }
        }
        Set<AnnotationAttributes> componentScans = AnnotationConfigUtils.attributesForRepeatable(sourceClass.getMetadata(), ComponentScans.class, ComponentScan.class);
        if (!componentScans.isEmpty() && !this.conditionEvaluator.shouldSkip(sourceClass.getMetadata(), ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN)) {
            for (AnnotationAttributes componentScan : componentScans) {
                Set<BeanDefinitionHolder> scannedBeanDefinitions = this.componentScanParser.parse(componentScan, sourceClass.getMetadata().getClassName());
                for (BeanDefinitionHolder holder : scannedBeanDefinitions) {
                    BeanDefinition bdCand = holder.getBeanDefinition().getOriginatingBeanDefinition();
                    if (bdCand == null) {
                        bdCand = holder.getBeanDefinition();
                    }
                    if (ConfigurationClassUtils.checkConfigurationClassCandidate(bdCand, this.metadataReaderFactory)) {
                        parse(bdCand.getBeanClassName(), holder.getBeanName());
                    }
                }
            }
        }
        processImports(configClass, sourceClass, getImports(sourceClass), true);
        AnnotationAttributes importResource = AnnotationConfigUtils.attributesFor(sourceClass.getMetadata(), ImportResource.class);
        if (importResource != null) {
            String[] resources = importResource.getStringArray("locations");
            Class<? extends BeanDefinitionReader> readerClass = importResource.getClass("reader");
            for (String resource : resources) {
                String resolvedResource = this.environment.resolveRequiredPlaceholders(resource);
                configClass.addImportedResource(resolvedResource, readerClass);
            }
        }
        Set<MethodMetadata> beanMethods = retrieveBeanMethodMetadata(sourceClass);
        for (MethodMetadata methodMetadata : beanMethods) {
            configClass.addBeanMethod(new BeanMethod(methodMetadata, configClass));
        }
        processInterfaces(configClass, sourceClass);
        if (sourceClass.getMetadata().hasSuperClass() && (superclass = sourceClass.getMetadata().getSuperClassName()) != null && !superclass.startsWith("java") && !this.knownSuperclasses.containsKey(superclass)) {
            this.knownSuperclasses.put(superclass, configClass);
            return sourceClass.getSuperClass();
        }
        return null;
    }

    private void processMemberClasses(ConfigurationClass configClass, SourceClass sourceClass) throws IOException {
        Collection<SourceClass> memberClasses = sourceClass.getMemberClasses();
        if (!memberClasses.isEmpty()) {
            List<SourceClass> candidates = new ArrayList<>(memberClasses.size());
            for (SourceClass memberClass : memberClasses) {
                if (ConfigurationClassUtils.isConfigurationCandidate(memberClass.getMetadata()) && !memberClass.getMetadata().getClassName().equals(configClass.getMetadata().getClassName())) {
                    candidates.add(memberClass);
                }
            }
            OrderComparator.sort(candidates);
            for (SourceClass candidate : candidates) {
                if (this.importStack.contains(configClass)) {
                    this.problemReporter.error(new CircularImportProblem(configClass, this.importStack));
                } else {
                    this.importStack.push(configClass);
                    try {
                        processConfigurationClass(candidate.asConfigClass(configClass));
                        this.importStack.pop();
                    } catch (Throwable th) {
                        this.importStack.pop();
                        throw th;
                    }
                }
            }
        }
    }

    private void processInterfaces(ConfigurationClass configClass, SourceClass sourceClass) throws IOException {
        for (SourceClass ifc : sourceClass.getInterfaces()) {
            Set<MethodMetadata> beanMethods = retrieveBeanMethodMetadata(ifc);
            for (MethodMetadata methodMetadata : beanMethods) {
                if (!methodMetadata.isAbstract()) {
                    configClass.addBeanMethod(new BeanMethod(methodMetadata, configClass));
                }
            }
            processInterfaces(configClass, ifc);
        }
    }

    private Set<MethodMetadata> retrieveBeanMethodMetadata(SourceClass sourceClass) {
        AnnotationMetadata original = sourceClass.getMetadata();
        Set<MethodMetadata> beanMethods = original.getAnnotatedMethods(Bean.class.getName());
        if (beanMethods.size() > 1 && (original instanceof StandardAnnotationMetadata)) {
            try {
                AnnotationMetadata asm = this.metadataReaderFactory.getMetadataReader(original.getClassName()).getAnnotationMetadata();
                Set<MethodMetadata> asmMethods = asm.getAnnotatedMethods(Bean.class.getName());
                if (asmMethods.size() >= beanMethods.size()) {
                    Set<MethodMetadata> linkedHashSet = new LinkedHashSet<>(asmMethods.size());
                    for (MethodMetadata asmMethod : asmMethods) {
                        Iterator<MethodMetadata> it = beanMethods.iterator();
                        while (true) {
                            if (it.hasNext()) {
                                MethodMetadata beanMethod = it.next();
                                if (beanMethod.getMethodName().equals(asmMethod.getMethodName())) {
                                    linkedHashSet.add(beanMethod);
                                    break;
                                }
                            }
                        }
                    }
                    if (linkedHashSet.size() == beanMethods.size()) {
                        beanMethods = linkedHashSet;
                    }
                }
            } catch (IOException ex) {
                this.logger.debug("Failed to read class file via ASM for determining @Bean method order", ex);
            }
        }
        return beanMethods;
    }

    private void processPropertySource(AnnotationAttributes propertySource) throws IOException {
        String name = propertySource.getString("name");
        if (!StringUtils.hasLength(name)) {
            name = null;
        }
        String encoding = propertySource.getString(XMLDeclaration.ATTRIBUTE_NAME_ENCODING);
        if (!StringUtils.hasLength(encoding)) {
            encoding = null;
        }
        String[] locations = propertySource.getStringArray("value");
        Assert.isTrue(locations.length > 0, "At least one @PropertySource(value) location is required");
        boolean ignoreResourceNotFound = propertySource.getBoolean("ignoreResourceNotFound");
        Class<? extends PropertySourceFactory> factoryClass = propertySource.getClass(Constants.FACTORY);
        PropertySourceFactory factory = factoryClass == PropertySourceFactory.class ? DEFAULT_PROPERTY_SOURCE_FACTORY : (PropertySourceFactory) BeanUtils.instantiateClass(factoryClass);
        for (String location : locations) {
            try {
                String resolvedLocation = this.environment.resolveRequiredPlaceholders(location);
                Resource resource = this.resourceLoader.getResource(resolvedLocation);
                addPropertySource(factory.createPropertySource(name, new EncodedResource(resource, encoding)));
            } catch (FileNotFoundException | IllegalArgumentException | UnknownHostException ex) {
                if (ignoreResourceNotFound) {
                    if (this.logger.isInfoEnabled()) {
                        this.logger.info("Properties location [" + location + "] not resolvable: " + ex.getMessage());
                    }
                } else {
                    throw ex;
                }
            }
        }
    }

    private void addPropertySource(org.springframework.core.env.PropertySource<?> propertySource) {
        String name = propertySource.getName();
        MutablePropertySources propertySources = ((ConfigurableEnvironment) this.environment).getPropertySources();
        if (this.propertySourceNames.contains(name)) {
            org.springframework.core.env.PropertySource<?> existing = propertySources.get(name);
            if (existing != null) {
                org.springframework.core.env.PropertySource<?> newSource = propertySource instanceof ResourcePropertySource ? ((ResourcePropertySource) propertySource).withResourceName() : propertySource;
                if (existing instanceof CompositePropertySource) {
                    ((CompositePropertySource) existing).addFirstPropertySource(newSource);
                    return;
                }
                if (existing instanceof ResourcePropertySource) {
                    existing = ((ResourcePropertySource) existing).withResourceName();
                }
                CompositePropertySource composite = new CompositePropertySource(name);
                composite.addPropertySource(newSource);
                composite.addPropertySource(existing);
                propertySources.replace(name, composite);
                return;
            }
        }
        if (this.propertySourceNames.isEmpty()) {
            propertySources.addLast(propertySource);
        } else {
            String firstProcessed = this.propertySourceNames.get(this.propertySourceNames.size() - 1);
            propertySources.addBefore(firstProcessed, propertySource);
        }
        this.propertySourceNames.add(name);
    }

    private Set<SourceClass> getImports(SourceClass sourceClass) throws IOException {
        Set<SourceClass> imports = new LinkedHashSet<>();
        Set<SourceClass> visited = new LinkedHashSet<>();
        collectImports(sourceClass, imports, visited);
        return imports;
    }

    private void collectImports(SourceClass sourceClass, Set<SourceClass> imports, Set<SourceClass> visited) throws IOException {
        if (visited.add(sourceClass)) {
            for (SourceClass annotation : sourceClass.getAnnotations()) {
                String annName = annotation.getMetadata().getClassName();
                if (!annName.startsWith("java") && !annName.equals(Import.class.getName())) {
                    collectImports(annotation, imports, visited);
                }
            }
            imports.addAll(sourceClass.getAnnotationAttributes(Import.class.getName(), "value"));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processImports(ConfigurationClass configClass, SourceClass currentSourceClass, Collection<SourceClass> importCandidates, boolean checkForCircularImports) {
        if (importCandidates.isEmpty()) {
            return;
        }
        if (checkForCircularImports && isChainedImportOnStack(configClass)) {
            this.problemReporter.error(new CircularImportProblem(configClass, this.importStack));
            return;
        }
        this.importStack.push(configClass);
        try {
            try {
                for (SourceClass candidate : importCandidates) {
                    if (candidate.isAssignable(ImportSelector.class)) {
                        Class<?> candidateClass = candidate.loadClass();
                        ImportSelector selector = (ImportSelector) BeanUtils.instantiateClass(candidateClass, ImportSelector.class);
                        ParserStrategyUtils.invokeAwareMethods(selector, this.environment, this.resourceLoader, this.registry);
                        if (selector instanceof DeferredImportSelector) {
                            this.deferredImportSelectorHandler.handle(configClass, (DeferredImportSelector) selector);
                        } else {
                            String[] importClassNames = selector.selectImports(currentSourceClass.getMetadata());
                            Collection<SourceClass> importSourceClasses = asSourceClasses(importClassNames);
                            processImports(configClass, currentSourceClass, importSourceClasses, false);
                        }
                    } else if (candidate.isAssignable(ImportBeanDefinitionRegistrar.class)) {
                        Class<?> candidateClass2 = candidate.loadClass();
                        ImportBeanDefinitionRegistrar registrar = (ImportBeanDefinitionRegistrar) BeanUtils.instantiateClass(candidateClass2, ImportBeanDefinitionRegistrar.class);
                        ParserStrategyUtils.invokeAwareMethods(registrar, this.environment, this.resourceLoader, this.registry);
                        configClass.addImportBeanDefinitionRegistrar(registrar, currentSourceClass.getMetadata());
                    } else {
                        this.importStack.registerImport(currentSourceClass.getMetadata(), candidate.getMetadata().getClassName());
                        processConfigurationClass(candidate.asConfigClass(configClass));
                    }
                }
            } finally {
                this.importStack.pop();
            }
        } catch (BeanDefinitionStoreException ex) {
            throw ex;
        }
    }

    private boolean isChainedImportOnStack(ConfigurationClass configClass) {
        if (this.importStack.contains(configClass)) {
            String configClassName = configClass.getMetadata().getClassName();
            AnnotationMetadata importingClassFor = this.importStack.getImportingClassFor(configClassName);
            while (true) {
                AnnotationMetadata importingClass = importingClassFor;
                if (importingClass != null) {
                    if (configClassName.equals(importingClass.getClassName())) {
                        return true;
                    }
                    importingClassFor = this.importStack.getImportingClassFor(importingClass.getClassName());
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ImportRegistry getImportRegistry() {
        return this.importStack;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SourceClass asSourceClass(ConfigurationClass configurationClass) throws IOException {
        AnnotationMetadata metadata = configurationClass.getMetadata();
        if (metadata instanceof StandardAnnotationMetadata) {
            return asSourceClass(((StandardAnnotationMetadata) metadata).getIntrospectedClass());
        }
        return asSourceClass(metadata.getClassName());
    }

    SourceClass asSourceClass(@Nullable Class<?> classType) throws IOException {
        Annotation[] annotations;
        if (classType == null) {
            return new SourceClass(Object.class);
        }
        try {
            for (Annotation ann : classType.getAnnotations()) {
                AnnotationUtils.validateAnnotation(ann);
            }
            return new SourceClass(classType);
        } catch (Throwable th) {
            return asSourceClass(classType.getName());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Collection<SourceClass> asSourceClasses(String... classNames) throws IOException {
        List<SourceClass> annotatedClasses = new ArrayList<>(classNames.length);
        for (String className : classNames) {
            annotatedClasses.add(asSourceClass(className));
        }
        return annotatedClasses;
    }

    SourceClass asSourceClass(@Nullable String className) throws IOException {
        if (className == null) {
            return new SourceClass(Object.class);
        }
        if (className.startsWith("java")) {
            try {
                return new SourceClass(ClassUtils.forName(className, this.resourceLoader.getClassLoader()));
            } catch (ClassNotFoundException ex) {
                throw new NestedIOException("Failed to load class [" + className + "]", ex);
            }
        }
        return new SourceClass(this.metadataReaderFactory.getMetadataReader(className));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/ConfigurationClassParser$ImportStack.class */
    public static class ImportStack extends ArrayDeque<ConfigurationClass> implements ImportRegistry {
        private final MultiValueMap<String, AnnotationMetadata> imports;

        private ImportStack() {
            this.imports = new LinkedMultiValueMap();
        }

        public void registerImport(AnnotationMetadata importingClass, String importedClass) {
            this.imports.add(importedClass, importingClass);
        }

        @Override // org.springframework.context.annotation.ImportRegistry
        @Nullable
        public AnnotationMetadata getImportingClassFor(String importedClass) {
            return (AnnotationMetadata) CollectionUtils.lastElement((List) this.imports.get(importedClass));
        }

        @Override // org.springframework.context.annotation.ImportRegistry
        public void removeImportingClass(String importingClass) {
            Iterator<AnnotationMetadata> it = this.imports.values().iterator();
            while (it.hasNext()) {
                List<AnnotationMetadata> list = (List) it.next();
                Iterator<AnnotationMetadata> iterator = list.iterator();
                while (true) {
                    if (!iterator.hasNext()) {
                        break;
                    } else if (iterator.next().getClassName().equals(importingClass)) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }

        @Override // java.util.AbstractCollection
        public String toString() {
            StringBuilder builder = new StringBuilder(PropertyAccessor.PROPERTY_KEY_PREFIX);
            Iterator<ConfigurationClass> iterator = iterator();
            while (iterator.hasNext()) {
                builder.append(iterator.next().getSimpleName());
                if (iterator.hasNext()) {
                    builder.append("->");
                }
            }
            return builder.append(']').toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/ConfigurationClassParser$DeferredImportSelectorHandler.class */
    public class DeferredImportSelectorHandler {
        @Nullable
        private List<DeferredImportSelectorHolder> deferredImportSelectors;

        private DeferredImportSelectorHandler() {
            this.deferredImportSelectors = new ArrayList();
        }

        public void handle(ConfigurationClass configClass, DeferredImportSelector importSelector) {
            DeferredImportSelectorHolder holder = new DeferredImportSelectorHolder(configClass, importSelector);
            if (this.deferredImportSelectors == null) {
                DeferredImportSelectorGroupingHandler handler = new DeferredImportSelectorGroupingHandler();
                handler.register(holder);
                handler.processGroupImports();
                return;
            }
            this.deferredImportSelectors.add(holder);
        }

        public void process() {
            List<DeferredImportSelectorHolder> deferredImports = this.deferredImportSelectors;
            this.deferredImportSelectors = null;
            if (deferredImports != null) {
                try {
                    DeferredImportSelectorGroupingHandler handler = new DeferredImportSelectorGroupingHandler();
                    deferredImports.sort(ConfigurationClassParser.DEFERRED_IMPORT_COMPARATOR);
                    handler.getClass();
                    deferredImports.forEach(this::register);
                    handler.processGroupImports();
                } finally {
                    this.deferredImportSelectors = new ArrayList();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/ConfigurationClassParser$DeferredImportSelectorGroupingHandler.class */
    public class DeferredImportSelectorGroupingHandler {
        private final Map<Object, DeferredImportSelectorGrouping> groupings;
        private final Map<AnnotationMetadata, ConfigurationClass> configurationClasses;

        private DeferredImportSelectorGroupingHandler() {
            this.groupings = new LinkedHashMap();
            this.configurationClasses = new HashMap();
        }

        public void register(DeferredImportSelectorHolder deferredImport) {
            Class<? extends DeferredImportSelector.Group> group = deferredImport.getImportSelector().getImportGroup();
            DeferredImportSelectorGrouping grouping = this.groupings.computeIfAbsent(group != null ? group : deferredImport, key -> {
                return new DeferredImportSelectorGrouping(createGroup(group));
            });
            grouping.add(deferredImport);
            this.configurationClasses.put(deferredImport.getConfigurationClass().getMetadata(), deferredImport.getConfigurationClass());
        }

        public void processGroupImports() {
            for (DeferredImportSelectorGrouping grouping : this.groupings.values()) {
                grouping.getImports().forEach(entry -> {
                    ConfigurationClass configurationClass = this.configurationClasses.get(entry.getMetadata());
                    try {
                        ConfigurationClassParser.this.processImports(configurationClass, ConfigurationClassParser.this.asSourceClass(configurationClass), ConfigurationClassParser.this.asSourceClasses(entry.getImportClassName()), false);
                    } catch (BeanDefinitionStoreException ex) {
                        throw ex;
                    } catch (Throwable ex2) {
                        throw new BeanDefinitionStoreException("Failed to process import candidates for configuration class [" + configurationClass.getMetadata().getClassName() + "]", ex2);
                    }
                });
            }
        }

        private DeferredImportSelector.Group createGroup(@Nullable Class<? extends DeferredImportSelector.Group> type) {
            Class<? extends DeferredImportSelector.Group> effectiveType = type != null ? type : DefaultDeferredImportSelectorGroup.class;
            DeferredImportSelector.Group group = (DeferredImportSelector.Group) BeanUtils.instantiateClass(effectiveType);
            ParserStrategyUtils.invokeAwareMethods(group, ConfigurationClassParser.this.environment, ConfigurationClassParser.this.resourceLoader, ConfigurationClassParser.this.registry);
            return group;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/ConfigurationClassParser$DeferredImportSelectorHolder.class */
    public static class DeferredImportSelectorHolder {
        private final ConfigurationClass configurationClass;
        private final DeferredImportSelector importSelector;

        public DeferredImportSelectorHolder(ConfigurationClass configClass, DeferredImportSelector selector) {
            this.configurationClass = configClass;
            this.importSelector = selector;
        }

        public ConfigurationClass getConfigurationClass() {
            return this.configurationClass;
        }

        public DeferredImportSelector getImportSelector() {
            return this.importSelector;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/ConfigurationClassParser$DeferredImportSelectorGrouping.class */
    public static class DeferredImportSelectorGrouping {
        private final DeferredImportSelector.Group group;
        private final List<DeferredImportSelectorHolder> deferredImports = new ArrayList();

        DeferredImportSelectorGrouping(DeferredImportSelector.Group group) {
            this.group = group;
        }

        public void add(DeferredImportSelectorHolder deferredImport) {
            this.deferredImports.add(deferredImport);
        }

        public Iterable<DeferredImportSelector.Group.Entry> getImports() {
            for (DeferredImportSelectorHolder deferredImport : this.deferredImports) {
                this.group.process(deferredImport.getConfigurationClass().getMetadata(), deferredImport.getImportSelector());
            }
            return this.group.selectImports();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/ConfigurationClassParser$DefaultDeferredImportSelectorGroup.class */
    private static class DefaultDeferredImportSelectorGroup implements DeferredImportSelector.Group {
        private final List<DeferredImportSelector.Group.Entry> imports = new ArrayList();

        private DefaultDeferredImportSelectorGroup() {
        }

        @Override // org.springframework.context.annotation.DeferredImportSelector.Group
        public void process(AnnotationMetadata metadata, DeferredImportSelector selector) {
            String[] selectImports;
            for (String importClassName : selector.selectImports(metadata)) {
                this.imports.add(new DeferredImportSelector.Group.Entry(metadata, importClassName));
            }
        }

        @Override // org.springframework.context.annotation.DeferredImportSelector.Group
        public Iterable<DeferredImportSelector.Group.Entry> selectImports() {
            return this.imports;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/ConfigurationClassParser$SourceClass.class */
    public class SourceClass implements Ordered {
        private final Object source;
        private final AnnotationMetadata metadata;

        public SourceClass(Object source) {
            this.source = source;
            if (source instanceof Class) {
                this.metadata = new StandardAnnotationMetadata((Class) source, true);
            } else {
                this.metadata = ((MetadataReader) source).getAnnotationMetadata();
            }
        }

        public final AnnotationMetadata getMetadata() {
            return this.metadata;
        }

        @Override // org.springframework.core.Ordered
        public int getOrder() {
            Integer order = ConfigurationClassUtils.getOrder(this.metadata);
            if (order != null) {
                return order.intValue();
            }
            return Integer.MAX_VALUE;
        }

        public Class<?> loadClass() throws ClassNotFoundException {
            if (this.source instanceof Class) {
                return (Class) this.source;
            }
            String className = ((MetadataReader) this.source).getClassMetadata().getClassName();
            return ClassUtils.forName(className, ConfigurationClassParser.this.resourceLoader.getClassLoader());
        }

        public boolean isAssignable(Class<?> clazz) throws IOException {
            if (!(this.source instanceof Class)) {
                return new AssignableTypeFilter(clazz).match((MetadataReader) this.source, ConfigurationClassParser.this.metadataReaderFactory);
            }
            return clazz.isAssignableFrom((Class) this.source);
        }

        public ConfigurationClass asConfigClass(ConfigurationClass importedBy) throws IOException {
            if (this.source instanceof Class) {
                return new ConfigurationClass((Class) this.source, importedBy);
            }
            return new ConfigurationClass((MetadataReader) this.source, importedBy);
        }

        public Collection<SourceClass> getMemberClasses() throws IOException {
            Object sourceToProcess = this.source;
            if (sourceToProcess instanceof Class) {
                Class<?> sourceClass = (Class) sourceToProcess;
                try {
                    Class<?>[] declaredClasses = sourceClass.getDeclaredClasses();
                    List<SourceClass> members = new ArrayList<>(declaredClasses.length);
                    for (Class<?> declaredClass : declaredClasses) {
                        members.add(ConfigurationClassParser.this.asSourceClass(declaredClass));
                    }
                    return members;
                } catch (NoClassDefFoundError e) {
                    sourceToProcess = ConfigurationClassParser.this.metadataReaderFactory.getMetadataReader(sourceClass.getName());
                }
            }
            MetadataReader sourceReader = (MetadataReader) sourceToProcess;
            String[] memberClassNames = sourceReader.getClassMetadata().getMemberClassNames();
            List<SourceClass> members2 = new ArrayList<>(memberClassNames.length);
            for (String memberClassName : memberClassNames) {
                try {
                    members2.add(ConfigurationClassParser.this.asSourceClass(memberClassName));
                } catch (IOException e2) {
                    if (ConfigurationClassParser.this.logger.isDebugEnabled()) {
                        ConfigurationClassParser.this.logger.debug("Failed to resolve member class [" + memberClassName + "] - not considering it as a configuration class candidate");
                    }
                }
            }
            return members2;
        }

        public SourceClass getSuperClass() throws IOException {
            if (this.source instanceof Class) {
                return ConfigurationClassParser.this.asSourceClass(((Class) this.source).getSuperclass());
            }
            return ConfigurationClassParser.this.asSourceClass(((MetadataReader) this.source).getClassMetadata().getSuperClassName());
        }

        public Set<SourceClass> getInterfaces() throws IOException {
            String[] interfaceNames;
            Class<?>[] interfaces;
            Set<SourceClass> result = new LinkedHashSet<>();
            if (this.source instanceof Class) {
                Class<?> sourceClass = (Class) this.source;
                for (Class<?> ifcClass : sourceClass.getInterfaces()) {
                    result.add(ConfigurationClassParser.this.asSourceClass(ifcClass));
                }
            } else {
                for (String className : this.metadata.getInterfaceNames()) {
                    result.add(ConfigurationClassParser.this.asSourceClass(className));
                }
            }
            return result;
        }

        public Set<SourceClass> getAnnotations() throws IOException {
            Set<SourceClass> result = new LinkedHashSet<>();
            for (String className : this.metadata.getAnnotationTypes()) {
                try {
                    result.add(getRelated(className));
                } catch (Throwable th) {
                }
            }
            return result;
        }

        public Collection<SourceClass> getAnnotationAttributes(String annType, String attribute) throws IOException {
            Map<String, Object> annotationAttributes = this.metadata.getAnnotationAttributes(annType, true);
            if (annotationAttributes == null || !annotationAttributes.containsKey(attribute)) {
                return Collections.emptySet();
            }
            String[] classNames = (String[]) annotationAttributes.get(attribute);
            Set<SourceClass> result = new LinkedHashSet<>();
            for (String className : classNames) {
                result.add(getRelated(className));
            }
            return result;
        }

        private SourceClass getRelated(String className) throws IOException {
            if (this.source instanceof Class) {
                try {
                    Class<?> clazz = ClassUtils.forName(className, ((Class) this.source).getClassLoader());
                    return ConfigurationClassParser.this.asSourceClass(clazz);
                } catch (ClassNotFoundException ex) {
                    if (className.startsWith("java")) {
                        throw new NestedIOException("Failed to load class [" + className + "]", ex);
                    }
                    return new SourceClass(ConfigurationClassParser.this.metadataReaderFactory.getMetadataReader(className));
                }
            }
            return ConfigurationClassParser.this.asSourceClass(className);
        }

        public boolean equals(Object other) {
            return this == other || ((other instanceof SourceClass) && this.metadata.getClassName().equals(((SourceClass) other).metadata.getClassName()));
        }

        public int hashCode() {
            return this.metadata.getClassName().hashCode();
        }

        public String toString() {
            return this.metadata.getClassName();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/ConfigurationClassParser$CircularImportProblem.class */
    public static class CircularImportProblem extends Problem {
        public CircularImportProblem(ConfigurationClass attemptedImport, Deque<ConfigurationClass> importStack) {
            super(String.format("A circular @Import has been detected: Illegal attempt by @Configuration class '%s' to import class '%s' as '%s' is already present in the current import stack %s", importStack.element().getSimpleName(), attemptedImport.getSimpleName(), attemptedImport.getSimpleName(), importStack), new Location(importStack.element().getResource(), attemptedImport.getMetadata()));
        }
    }
}