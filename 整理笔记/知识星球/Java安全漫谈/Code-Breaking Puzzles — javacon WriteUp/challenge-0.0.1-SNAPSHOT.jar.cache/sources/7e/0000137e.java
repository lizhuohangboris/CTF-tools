package org.springframework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.SpringProperties;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/CachedIntrospectionResults.class */
public final class CachedIntrospectionResults {
    public static final String IGNORE_BEANINFO_PROPERTY_NAME = "spring.beaninfo.ignore";
    private static final boolean shouldIntrospectorIgnoreBeaninfoClasses = SpringProperties.getFlag(IGNORE_BEANINFO_PROPERTY_NAME);
    private static List<BeanInfoFactory> beanInfoFactories = SpringFactoriesLoader.loadFactories(BeanInfoFactory.class, CachedIntrospectionResults.class.getClassLoader());
    private static final Log logger = LogFactory.getLog(CachedIntrospectionResults.class);
    static final Set<ClassLoader> acceptedClassLoaders = Collections.newSetFromMap(new ConcurrentHashMap(16));
    static final ConcurrentMap<Class<?>, CachedIntrospectionResults> strongClassCache = new ConcurrentHashMap(64);
    static final ConcurrentMap<Class<?>, CachedIntrospectionResults> softClassCache = new ConcurrentReferenceHashMap(64);
    private final BeanInfo beanInfo;
    private final Map<String, PropertyDescriptor> propertyDescriptorCache;
    private final ConcurrentMap<PropertyDescriptor, TypeDescriptor> typeDescriptorCache;

    public static void acceptClassLoader(@Nullable ClassLoader classLoader) {
        if (classLoader != null) {
            acceptedClassLoaders.add(classLoader);
        }
    }

    public static void clearClassLoader(@Nullable ClassLoader classLoader) {
        acceptedClassLoaders.removeIf(registeredLoader -> {
            return isUnderneathClassLoader(registeredLoader, classLoader);
        });
        strongClassCache.keySet().removeIf(beanClass -> {
            return isUnderneathClassLoader(beanClass.getClassLoader(), classLoader);
        });
        softClassCache.keySet().removeIf(beanClass2 -> {
            return isUnderneathClassLoader(beanClass2.getClassLoader(), classLoader);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CachedIntrospectionResults forClass(Class<?> beanClass) throws BeansException {
        ConcurrentMap<Class<?>, CachedIntrospectionResults> classCacheToUse;
        CachedIntrospectionResults results = strongClassCache.get(beanClass);
        if (results != null) {
            return results;
        }
        CachedIntrospectionResults results2 = softClassCache.get(beanClass);
        if (results2 != null) {
            return results2;
        }
        CachedIntrospectionResults results3 = new CachedIntrospectionResults(beanClass);
        if (ClassUtils.isCacheSafe(beanClass, CachedIntrospectionResults.class.getClassLoader()) || isClassLoaderAccepted(beanClass.getClassLoader())) {
            classCacheToUse = strongClassCache;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Not strongly caching class [" + beanClass.getName() + "] because it is not cache-safe");
            }
            classCacheToUse = softClassCache;
        }
        CachedIntrospectionResults existing = classCacheToUse.putIfAbsent(beanClass, results3);
        return existing != null ? existing : results3;
    }

    private static boolean isClassLoaderAccepted(ClassLoader classLoader) {
        for (ClassLoader acceptedLoader : acceptedClassLoaders) {
            if (isUnderneathClassLoader(classLoader, acceptedLoader)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isUnderneathClassLoader(@Nullable ClassLoader candidate, @Nullable ClassLoader parent) {
        if (candidate == parent) {
            return true;
        }
        if (candidate == null) {
            return false;
        }
        ClassLoader classLoaderToCheck = candidate;
        while (classLoaderToCheck != null) {
            classLoaderToCheck = classLoaderToCheck.getParent();
            if (classLoaderToCheck == parent) {
                return true;
            }
        }
        return false;
    }

    private static BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException {
        for (BeanInfoFactory beanInfoFactory : beanInfoFactories) {
            BeanInfo beanInfo = beanInfoFactory.getBeanInfo(beanClass);
            if (beanInfo != null) {
                return beanInfo;
            }
        }
        if (shouldIntrospectorIgnoreBeaninfoClasses) {
            return Introspector.getBeanInfo(beanClass, 3);
        }
        return Introspector.getBeanInfo(beanClass);
    }

    private CachedIntrospectionResults(Class<?> beanClass) throws BeansException {
        try {
            if (logger.isTraceEnabled()) {
                logger.trace("Getting BeanInfo for class [" + beanClass.getName() + "]");
            }
            this.beanInfo = getBeanInfo(beanClass);
            if (logger.isTraceEnabled()) {
                logger.trace("Caching PropertyDescriptors for class [" + beanClass.getName() + "]");
            }
            this.propertyDescriptorCache = new LinkedHashMap();
            PropertyDescriptor[] pds = this.beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                if (Class.class != beanClass || (!"classLoader".equals(pd.getName()) && !"protectionDomain".equals(pd.getName()))) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Found bean property '" + pd.getName() + "'" + (pd.getPropertyType() != null ? " of type [" + pd.getPropertyType().getName() + "]" : "") + (pd.getPropertyEditorClass() != null ? "; editor [" + pd.getPropertyEditorClass().getName() + "]" : ""));
                    }
                    PropertyDescriptor pd2 = buildGenericTypeAwarePropertyDescriptor(beanClass, pd);
                    this.propertyDescriptorCache.put(pd2.getName(), pd2);
                }
            }
            for (Class<?> currClass = beanClass; currClass != null && currClass != Object.class; currClass = currClass.getSuperclass()) {
                introspectInterfaces(beanClass, currClass);
            }
            this.typeDescriptorCache = new ConcurrentReferenceHashMap();
        } catch (IntrospectionException ex) {
            throw new FatalBeanException("Failed to obtain BeanInfo for class [" + beanClass.getName() + "]", ex);
        }
    }

    private void introspectInterfaces(Class<?> beanClass, Class<?> currClass) throws IntrospectionException {
        Class<?>[] interfaces;
        PropertyDescriptor[] propertyDescriptors;
        for (Class<?> ifc : currClass.getInterfaces()) {
            if (!ClassUtils.isJavaLanguageInterface(ifc)) {
                for (PropertyDescriptor pd : getBeanInfo(ifc).getPropertyDescriptors()) {
                    PropertyDescriptor existingPd = this.propertyDescriptorCache.get(pd.getName());
                    if (existingPd == null || (existingPd.getReadMethod() == null && pd.getReadMethod() != null)) {
                        PropertyDescriptor pd2 = buildGenericTypeAwarePropertyDescriptor(beanClass, pd);
                        this.propertyDescriptorCache.put(pd2.getName(), pd2);
                    }
                }
                introspectInterfaces(ifc, ifc);
            }
        }
    }

    BeanInfo getBeanInfo() {
        return this.beanInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Class<?> getBeanClass() {
        return this.beanInfo.getBeanDescriptor().getBeanClass();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public PropertyDescriptor getPropertyDescriptor(String name) {
        PropertyDescriptor pd = this.propertyDescriptorCache.get(name);
        if (pd == null && StringUtils.hasLength(name)) {
            pd = this.propertyDescriptorCache.get(StringUtils.uncapitalize(name));
            if (pd == null) {
                pd = this.propertyDescriptorCache.get(StringUtils.capitalize(name));
            }
        }
        return (pd == null || (pd instanceof GenericTypeAwarePropertyDescriptor)) ? pd : buildGenericTypeAwarePropertyDescriptor(getBeanClass(), pd);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] pds = new PropertyDescriptor[this.propertyDescriptorCache.size()];
        int i = 0;
        for (PropertyDescriptor pd : this.propertyDescriptorCache.values()) {
            pds[i] = pd instanceof GenericTypeAwarePropertyDescriptor ? pd : buildGenericTypeAwarePropertyDescriptor(getBeanClass(), pd);
            i++;
        }
        return pds;
    }

    private PropertyDescriptor buildGenericTypeAwarePropertyDescriptor(Class<?> beanClass, PropertyDescriptor pd) {
        try {
            return new GenericTypeAwarePropertyDescriptor(beanClass, pd.getName(), pd.getReadMethod(), pd.getWriteMethod(), pd.getPropertyEditorClass());
        } catch (IntrospectionException ex) {
            throw new FatalBeanException("Failed to re-introspect class [" + beanClass.getName() + "]", ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TypeDescriptor addTypeDescriptor(PropertyDescriptor pd, TypeDescriptor td) {
        TypeDescriptor existing = this.typeDescriptorCache.putIfAbsent(pd, td);
        return existing != null ? existing : td;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public TypeDescriptor getTypeDescriptor(PropertyDescriptor pd) {
        return this.typeDescriptorCache.get(pd);
    }
}