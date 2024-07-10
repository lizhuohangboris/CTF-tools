package org.springframework.context.annotation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AspectJTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/ComponentScanAnnotationParser.class */
class ComponentScanAnnotationParser {
    private final Environment environment;
    private final ResourceLoader resourceLoader;
    private final BeanNameGenerator beanNameGenerator;
    private final BeanDefinitionRegistry registry;

    public ComponentScanAnnotationParser(Environment environment, ResourceLoader resourceLoader, BeanNameGenerator beanNameGenerator, BeanDefinitionRegistry registry) {
        this.environment = environment;
        this.resourceLoader = resourceLoader;
        this.beanNameGenerator = beanNameGenerator;
        this.registry = registry;
    }

    public Set<BeanDefinitionHolder> parse(AnnotationAttributes componentScan, final String declaringClass) {
        AnnotationAttributes[] annotationArray;
        AnnotationAttributes[] annotationArray2;
        Class<?>[] classArray;
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(this.registry, componentScan.getBoolean("useDefaultFilters"), this.environment, this.resourceLoader);
        Class<? extends BeanNameGenerator> generatorClass = componentScan.getClass("nameGenerator");
        boolean useInheritedGenerator = BeanNameGenerator.class == generatorClass;
        scanner.setBeanNameGenerator(useInheritedGenerator ? this.beanNameGenerator : (BeanNameGenerator) BeanUtils.instantiateClass(generatorClass));
        ScopedProxyMode scopedProxyMode = (ScopedProxyMode) componentScan.getEnum("scopedProxy");
        if (scopedProxyMode != ScopedProxyMode.DEFAULT) {
            scanner.setScopedProxyMode(scopedProxyMode);
        } else {
            Class<? extends ScopeMetadataResolver> resolverClass = componentScan.getClass("scopeResolver");
            scanner.setScopeMetadataResolver((ScopeMetadataResolver) BeanUtils.instantiateClass(resolverClass));
        }
        scanner.setResourcePattern(componentScan.getString("resourcePattern"));
        for (AnnotationAttributes filter : componentScan.getAnnotationArray("includeFilters")) {
            for (TypeFilter typeFilter : typeFiltersFor(filter)) {
                scanner.addIncludeFilter(typeFilter);
            }
        }
        for (AnnotationAttributes filter2 : componentScan.getAnnotationArray("excludeFilters")) {
            for (TypeFilter typeFilter2 : typeFiltersFor(filter2)) {
                scanner.addExcludeFilter(typeFilter2);
            }
        }
        boolean lazyInit = componentScan.getBoolean("lazyInit");
        if (lazyInit) {
            scanner.getBeanDefinitionDefaults().setLazyInit(true);
        }
        Set<String> basePackages = new LinkedHashSet<>();
        String[] basePackagesArray = componentScan.getStringArray("basePackages");
        for (String pkg : basePackagesArray) {
            String[] tokenized = StringUtils.tokenizeToStringArray(this.environment.resolvePlaceholders(pkg), ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
            Collections.addAll(basePackages, tokenized);
        }
        for (Class<?> clazz : componentScan.getClassArray("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(declaringClass));
        }
        scanner.addExcludeFilter(new AbstractTypeHierarchyTraversingFilter(false, false) { // from class: org.springframework.context.annotation.ComponentScanAnnotationParser.1
            @Override // org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter
            protected boolean matchClassName(String className) {
                return declaringClass.equals(className);
            }
        });
        return scanner.doScan(StringUtils.toStringArray(basePackages));
    }

    private List<TypeFilter> typeFiltersFor(AnnotationAttributes filterAttributes) {
        Class<?>[] classArray;
        String[] stringArray;
        List<TypeFilter> typeFilters = new ArrayList<>();
        FilterType filterType = (FilterType) filterAttributes.getEnum("type");
        for (Class<?> filterClass : filterAttributes.getClassArray("classes")) {
            switch (filterType) {
                case ANNOTATION:
                    Assert.isAssignable(Annotation.class, filterClass, "@ComponentScan ANNOTATION type filter requires an annotation type");
                    typeFilters.add(new AnnotationTypeFilter(filterClass));
                    break;
                case ASSIGNABLE_TYPE:
                    typeFilters.add(new AssignableTypeFilter(filterClass));
                    break;
                case CUSTOM:
                    Assert.isAssignable(TypeFilter.class, filterClass, "@ComponentScan CUSTOM type filter requires a TypeFilter implementation");
                    TypeFilter filter = (TypeFilter) BeanUtils.instantiateClass(filterClass, TypeFilter.class);
                    ParserStrategyUtils.invokeAwareMethods(filter, this.environment, this.resourceLoader, this.registry);
                    typeFilters.add(filter);
                    break;
                default:
                    throw new IllegalArgumentException("Filter type not supported with Class value: " + filterType);
            }
        }
        for (String expression : filterAttributes.getStringArray("pattern")) {
            switch (filterType) {
                case ASPECTJ:
                    typeFilters.add(new AspectJTypeFilter(expression, this.resourceLoader.getClassLoader()));
                    break;
                case REGEX:
                    typeFilters.add(new RegexPatternTypeFilter(Pattern.compile(expression)));
                    break;
                default:
                    throw new IllegalArgumentException("Filter type not supported with String pattern: " + filterType);
            }
        }
        return typeFilters;
    }
}