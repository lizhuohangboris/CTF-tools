package org.springframework.core.io.support;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.UrlResource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/support/SpringFactoriesLoader.class */
public final class SpringFactoriesLoader {
    public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";
    private static final Log logger = LogFactory.getLog(SpringFactoriesLoader.class);
    private static final Map<ClassLoader, MultiValueMap<String, String>> cache = new ConcurrentReferenceHashMap();

    private SpringFactoriesLoader() {
    }

    public static <T> List<T> loadFactories(Class<T> factoryClass, @Nullable ClassLoader classLoader) {
        Assert.notNull(factoryClass, "'factoryClass' must not be null");
        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = SpringFactoriesLoader.class.getClassLoader();
        }
        List<String> factoryNames = loadFactoryNames(factoryClass, classLoaderToUse);
        if (logger.isTraceEnabled()) {
            logger.trace("Loaded [" + factoryClass.getName() + "] names: " + factoryNames);
        }
        ArrayList arrayList = new ArrayList(factoryNames.size());
        for (String factoryName : factoryNames) {
            arrayList.add(instantiateFactory(factoryName, factoryClass, classLoaderToUse));
        }
        AnnotationAwareOrderComparator.sort(arrayList);
        return arrayList;
    }

    public static List<String> loadFactoryNames(Class<?> factoryClass, @Nullable ClassLoader classLoader) {
        String factoryClassName = factoryClass.getName();
        return loadSpringFactories(classLoader).getOrDefault(factoryClassName, Collections.emptyList());
    }

    private static Map<String, List<String>> loadSpringFactories(@Nullable ClassLoader classLoader) {
        Enumeration<URL> systemResources;
        String[] commaDelimitedListToStringArray;
        MultiValueMap<String, String> result = cache.get(classLoader);
        if (result != null) {
            return result;
        }
        try {
            if (classLoader != null) {
                systemResources = classLoader.getResources(FACTORIES_RESOURCE_LOCATION);
            } else {
                systemResources = ClassLoader.getSystemResources(FACTORIES_RESOURCE_LOCATION);
            }
            Enumeration<URL> urls = systemResources;
            MultiValueMap<String, String> result2 = new LinkedMultiValueMap<>();
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                UrlResource resource = new UrlResource(url);
                Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                for (Map.Entry<?, ?> entry : properties.entrySet()) {
                    String factoryClassName = ((String) entry.getKey()).trim();
                    for (String factoryName : StringUtils.commaDelimitedListToStringArray((String) entry.getValue())) {
                        result2.add(factoryClassName, factoryName.trim());
                    }
                }
            }
            cache.put(classLoader, result2);
            return result2;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to load factories from location [META-INF/spring.factories]", ex);
        }
    }

    private static <T> T instantiateFactory(String instanceClassName, Class<T> factoryClass, ClassLoader classLoader) {
        try {
            Class<?> instanceClass = ClassUtils.forName(instanceClassName, classLoader);
            if (!factoryClass.isAssignableFrom(instanceClass)) {
                throw new IllegalArgumentException("Class [" + instanceClassName + "] is not assignable to [" + factoryClass.getName() + "]");
            }
            return (T) ReflectionUtils.accessibleConstructor(instanceClass, new Class[0]).newInstance(new Object[0]);
        } catch (Throwable ex) {
            throw new IllegalArgumentException("Unable to instantiate factory class: " + factoryClass.getName(), ex);
        }
    }
}