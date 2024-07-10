package org.springframework.beans.factory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/BeanFactoryUtils.class */
public abstract class BeanFactoryUtils {
    public static final String GENERATED_BEAN_NAME_SEPARATOR = "#";
    private static final Map<String, String> transformedBeanNameCache = new ConcurrentHashMap();

    public static boolean isFactoryDereference(@Nullable String name) {
        return name != null && name.startsWith(BeanFactory.FACTORY_BEAN_PREFIX);
    }

    public static String transformedBeanName(String name) {
        Assert.notNull(name, "'name' must not be null");
        if (!name.startsWith(BeanFactory.FACTORY_BEAN_PREFIX)) {
            return name;
        }
        return transformedBeanNameCache.computeIfAbsent(name, beanName -> {
            do {
                beanName = beanName.substring(BeanFactory.FACTORY_BEAN_PREFIX.length());
            } while (beanName.startsWith(BeanFactory.FACTORY_BEAN_PREFIX));
            return beanName;
        });
    }

    public static boolean isGeneratedBeanName(@Nullable String name) {
        return name != null && name.contains("#");
    }

    public static String originalBeanName(String name) {
        Assert.notNull(name, "'name' must not be null");
        int separatorIndex = name.indexOf("#");
        return separatorIndex != -1 ? name.substring(0, separatorIndex) : name;
    }

    public static int countBeansIncludingAncestors(ListableBeanFactory lbf) {
        return beanNamesIncludingAncestors(lbf).length;
    }

    public static String[] beanNamesIncludingAncestors(ListableBeanFactory lbf) {
        return beanNamesForTypeIncludingAncestors(lbf, Object.class);
    }

    public static String[] beanNamesForTypeIncludingAncestors(ListableBeanFactory lbf, ResolvableType type) {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        String[] result = lbf.getBeanNamesForType(type);
        if (lbf instanceof HierarchicalBeanFactory) {
            HierarchicalBeanFactory hbf = (HierarchicalBeanFactory) lbf;
            if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
                String[] parentResult = beanNamesForTypeIncludingAncestors((ListableBeanFactory) hbf.getParentBeanFactory(), type);
                result = mergeNamesWithParent(result, parentResult, hbf);
            }
        }
        return result;
    }

    public static String[] beanNamesForTypeIncludingAncestors(ListableBeanFactory lbf, Class<?> type) {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        String[] result = lbf.getBeanNamesForType(type);
        if (lbf instanceof HierarchicalBeanFactory) {
            HierarchicalBeanFactory hbf = (HierarchicalBeanFactory) lbf;
            if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
                String[] parentResult = beanNamesForTypeIncludingAncestors((ListableBeanFactory) hbf.getParentBeanFactory(), type);
                result = mergeNamesWithParent(result, parentResult, hbf);
            }
        }
        return result;
    }

    public static String[] beanNamesForTypeIncludingAncestors(ListableBeanFactory lbf, Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        String[] result = lbf.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
        if (lbf instanceof HierarchicalBeanFactory) {
            HierarchicalBeanFactory hbf = (HierarchicalBeanFactory) lbf;
            if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
                String[] parentResult = beanNamesForTypeIncludingAncestors((ListableBeanFactory) hbf.getParentBeanFactory(), type, includeNonSingletons, allowEagerInit);
                result = mergeNamesWithParent(result, parentResult, hbf);
            }
        }
        return result;
    }

    public static String[] beanNamesForAnnotationIncludingAncestors(ListableBeanFactory lbf, Class<? extends Annotation> annotationType) {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        String[] result = lbf.getBeanNamesForAnnotation(annotationType);
        if (lbf instanceof HierarchicalBeanFactory) {
            HierarchicalBeanFactory hbf = (HierarchicalBeanFactory) lbf;
            if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
                String[] parentResult = beanNamesForAnnotationIncludingAncestors((ListableBeanFactory) hbf.getParentBeanFactory(), annotationType);
                result = mergeNamesWithParent(result, parentResult, hbf);
            }
        }
        return result;
    }

    public static <T> Map<String, T> beansOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> type) throws BeansException {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        Map<String, T> result = new LinkedHashMap<>(4);
        result.putAll(lbf.getBeansOfType(type));
        if (lbf instanceof HierarchicalBeanFactory) {
            HierarchicalBeanFactory hbf = (HierarchicalBeanFactory) lbf;
            if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
                Map<String, T> parentResult = beansOfTypeIncludingAncestors((ListableBeanFactory) hbf.getParentBeanFactory(), type);
                parentResult.forEach(beanName, beanInstance -> {
                    if (!result.containsKey(beanName) && !hbf.containsLocalBean(beanName)) {
                        result.put(beanName, beanInstance);
                    }
                });
            }
        }
        return result;
    }

    public static <T> Map<String, T> beansOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        Map<String, T> result = new LinkedHashMap<>(4);
        result.putAll(lbf.getBeansOfType(type, includeNonSingletons, allowEagerInit));
        if (lbf instanceof HierarchicalBeanFactory) {
            HierarchicalBeanFactory hbf = (HierarchicalBeanFactory) lbf;
            if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
                Map<String, T> parentResult = beansOfTypeIncludingAncestors((ListableBeanFactory) hbf.getParentBeanFactory(), type, includeNonSingletons, allowEagerInit);
                parentResult.forEach(beanName, beanInstance -> {
                    if (!result.containsKey(beanName) && !hbf.containsLocalBean(beanName)) {
                        result.put(beanName, beanInstance);
                    }
                });
            }
        }
        return result;
    }

    public static <T> T beanOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> type) throws BeansException {
        Map<String, T> beansOfType = beansOfTypeIncludingAncestors(lbf, type);
        return (T) uniqueBean(type, beansOfType);
    }

    public static <T> T beanOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        Map<String, T> beansOfType = beansOfTypeIncludingAncestors(lbf, type, includeNonSingletons, allowEagerInit);
        return (T) uniqueBean(type, beansOfType);
    }

    public static <T> T beanOfType(ListableBeanFactory lbf, Class<T> type) throws BeansException {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        Map<String, T> beansOfType = lbf.getBeansOfType(type);
        return (T) uniqueBean(type, beansOfType);
    }

    public static <T> T beanOfType(ListableBeanFactory lbf, Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        Map<String, T> beansOfType = lbf.getBeansOfType(type, includeNonSingletons, allowEagerInit);
        return (T) uniqueBean(type, beansOfType);
    }

    private static String[] mergeNamesWithParent(String[] result, String[] parentResult, HierarchicalBeanFactory hbf) {
        if (parentResult.length == 0) {
            return result;
        }
        List<String> merged = new ArrayList<>(result.length + parentResult.length);
        merged.addAll(Arrays.asList(result));
        for (String beanName : parentResult) {
            if (!merged.contains(beanName) && !hbf.containsLocalBean(beanName)) {
                merged.add(beanName);
            }
        }
        return StringUtils.toStringArray(merged);
    }

    private static <T> T uniqueBean(Class<T> type, Map<String, T> matchingBeans) {
        int count = matchingBeans.size();
        if (count == 1) {
            return matchingBeans.values().iterator().next();
        }
        if (count > 1) {
            throw new NoUniqueBeanDefinitionException((Class<?>) type, (Collection<String>) matchingBeans.keySet());
        }
        throw new NoSuchBeanDefinitionException((Class<?>) type);
    }
}