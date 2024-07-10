package org.springframework.boot;

import groovy.lang.Closure;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.GroovyWebApplicationContext;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/BeanDefinitionLoader.class */
public class BeanDefinitionLoader {
    private final Object[] sources;
    private final AnnotatedBeanDefinitionReader annotatedReader;
    private final XmlBeanDefinitionReader xmlReader;
    private BeanDefinitionReader groovyReader;
    private final ClassPathBeanDefinitionScanner scanner;
    private ResourceLoader resourceLoader;

    /* JADX INFO: Access modifiers changed from: protected */
    @FunctionalInterface
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/BeanDefinitionLoader$GroovyBeanDefinitionSource.class */
    public interface GroovyBeanDefinitionSource {
        Closure<?> getBeans();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BeanDefinitionLoader(BeanDefinitionRegistry registry, Object... sources) {
        Assert.notNull(registry, "Registry must not be null");
        Assert.notEmpty(sources, "Sources must not be empty");
        this.sources = sources;
        this.annotatedReader = new AnnotatedBeanDefinitionReader(registry);
        this.xmlReader = new XmlBeanDefinitionReader(registry);
        if (isGroovyPresent()) {
            this.groovyReader = new GroovyBeanDefinitionReader(registry);
        }
        this.scanner = new ClassPathBeanDefinitionScanner(registry);
        this.scanner.addExcludeFilter(new ClassExcludeFilter(sources));
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.annotatedReader.setBeanNameGenerator(beanNameGenerator);
        this.xmlReader.setBeanNameGenerator(beanNameGenerator);
        this.scanner.setBeanNameGenerator(beanNameGenerator);
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.xmlReader.setResourceLoader(resourceLoader);
        this.scanner.setResourceLoader(resourceLoader);
    }

    public void setEnvironment(ConfigurableEnvironment environment) {
        this.annotatedReader.setEnvironment(environment);
        this.xmlReader.setEnvironment(environment);
        this.scanner.setEnvironment(environment);
    }

    public int load() {
        Object[] objArr;
        int count = 0;
        for (Object source : this.sources) {
            count += load(source);
        }
        return count;
    }

    private int load(Object source) {
        Assert.notNull(source, "Source must not be null");
        if (source instanceof Class) {
            return load((Class) source);
        }
        if (source instanceof Resource) {
            return load((Resource) source);
        }
        if (source instanceof Package) {
            return load((Package) source);
        }
        if (source instanceof CharSequence) {
            return load((CharSequence) source);
        }
        throw new IllegalArgumentException("Invalid source type " + source.getClass());
    }

    private int load(Class<?> source) {
        if (isGroovyPresent() && GroovyBeanDefinitionSource.class.isAssignableFrom(source)) {
            GroovyBeanDefinitionSource loader = (GroovyBeanDefinitionSource) BeanUtils.instantiateClass(source, GroovyBeanDefinitionSource.class);
            load(loader);
        }
        if (isComponent(source)) {
            this.annotatedReader.register(source);
            return 1;
        }
        return 0;
    }

    private int load(GroovyBeanDefinitionSource source) {
        int before = this.xmlReader.getRegistry().getBeanDefinitionCount();
        ((GroovyBeanDefinitionReader) this.groovyReader).beans(source.getBeans());
        int after = this.xmlReader.getRegistry().getBeanDefinitionCount();
        return after - before;
    }

    private int load(Resource source) {
        if (source.getFilename().endsWith(GroovyWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX)) {
            if (this.groovyReader == null) {
                throw new BeanDefinitionStoreException("Cannot load Groovy beans without Groovy on classpath");
            }
            return this.groovyReader.loadBeanDefinitions(source);
        }
        return this.xmlReader.loadBeanDefinitions(source);
    }

    private int load(Package source) {
        return this.scanner.scan(source.getName());
    }

    private int load(CharSequence source) {
        String resolvedSource = this.xmlReader.getEnvironment().resolvePlaceholders(source.toString());
        try {
            return load(ClassUtils.forName(resolvedSource, null));
        } catch (ClassNotFoundException | IllegalArgumentException e) {
            Resource[] resources = findResources(resolvedSource);
            int loadCount = 0;
            boolean atLeastOneResourceExists = false;
            for (Resource resource : resources) {
                if (isLoadCandidate(resource)) {
                    atLeastOneResourceExists = true;
                    loadCount += load(resource);
                }
            }
            if (atLeastOneResourceExists) {
                return loadCount;
            }
            Package packageResource = findPackage(resolvedSource);
            if (packageResource != null) {
                return load(packageResource);
            }
            throw new IllegalArgumentException("Invalid source '" + resolvedSource + "'");
        }
    }

    private boolean isGroovyPresent() {
        return ClassUtils.isPresent("groovy.lang.MetaClass", null);
    }

    private Resource[] findResources(String source) {
        ResourceLoader loader = this.resourceLoader != null ? this.resourceLoader : new PathMatchingResourcePatternResolver();
        try {
            return loader instanceof ResourcePatternResolver ? ((ResourcePatternResolver) loader).getResources(source) : new Resource[]{loader.getResource(source)};
        } catch (IOException e) {
            throw new IllegalStateException("Error reading source '" + source + "'");
        }
    }

    private boolean isLoadCandidate(Resource resource) {
        if (resource == null || !resource.exists()) {
            return false;
        }
        if (resource instanceof ClassPathResource) {
            String path = ((ClassPathResource) resource).getPath();
            if (path.indexOf(46) == -1) {
                try {
                    return Package.getPackage(path) == null;
                } catch (Exception e) {
                    return true;
                }
            }
            return true;
        }
        return true;
    }

    private Package findPackage(CharSequence source) {
        Package pkg = Package.getPackage(source.toString());
        if (pkg != null) {
            return pkg;
        }
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(getClass().getClassLoader());
            Resource[] resources = resolver.getResources(ClassUtils.convertClassNameToResourcePath(source.toString()) + "/*.class");
            if (0 < resources.length) {
                Resource resource = resources[0];
                String className = StringUtils.stripFilenameExtension(resource.getFilename());
                load(Class.forName(source.toString() + "." + className));
            }
        } catch (Exception e) {
        }
        return Package.getPackage(source.toString());
    }

    private boolean isComponent(Class<?> type) {
        if (AnnotationUtils.findAnnotation(type, (Class<Annotation>) Component.class) != null) {
            return true;
        }
        if (type.getName().matches(".*\\$_.*closure.*") || type.isAnonymousClass() || type.getConstructors() == null || type.getConstructors().length == 0) {
            return false;
        }
        return true;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/BeanDefinitionLoader$ClassExcludeFilter.class */
    private static class ClassExcludeFilter extends AbstractTypeHierarchyTraversingFilter {
        private final Set<String> classNames;

        ClassExcludeFilter(Object... sources) {
            super(false, false);
            this.classNames = new HashSet();
            for (Object source : sources) {
                if (source instanceof Class) {
                    this.classNames.add(((Class) source).getName());
                }
            }
        }

        @Override // org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter
        protected boolean matchClassName(String className) {
            return this.classNames.contains(className);
        }
    }
}