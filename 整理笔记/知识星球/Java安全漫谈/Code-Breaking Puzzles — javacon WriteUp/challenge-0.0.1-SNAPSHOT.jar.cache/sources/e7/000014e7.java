package org.springframework.beans.support;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/support/PropertyComparator.class */
public class PropertyComparator<T> implements Comparator<T> {
    private final SortDefinition sortDefinition;
    protected final Log logger = LogFactory.getLog(getClass());
    private final BeanWrapperImpl beanWrapper = new BeanWrapperImpl(false);

    public PropertyComparator(SortDefinition sortDefinition) {
        this.sortDefinition = sortDefinition;
    }

    public PropertyComparator(String property, boolean ignoreCase, boolean ascending) {
        this.sortDefinition = new MutableSortDefinition(property, ignoreCase, ascending);
    }

    public final SortDefinition getSortDefinition() {
        return this.sortDefinition;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Comparator
    public int compare(T o1, T o2) {
        int result;
        int compareTo;
        Object v1 = getPropertyValue(o1);
        Object v2 = getPropertyValue(o2);
        Object v12 = v1;
        Object v22 = v2;
        if (this.sortDefinition.isIgnoreCase()) {
            boolean z = v1 instanceof String;
            v12 = v1;
            v22 = v2;
            if (z) {
                boolean z2 = v2 instanceof String;
                v12 = v1;
                v22 = v2;
                if (z2) {
                    v12 = ((String) v1).toLowerCase();
                    v22 = ((String) v2).toLowerCase();
                }
            }
        }
        if (v12 != null) {
            if (v22 != null) {
                try {
                    compareTo = ((Comparable) v12).compareTo(v22);
                } catch (RuntimeException ex) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Could not sort objects [" + o1 + "] and [" + o2 + "]", ex);
                        return 0;
                    }
                    return 0;
                }
            } else {
                compareTo = -1;
            }
            result = compareTo;
        } else {
            result = v22 != null ? 1 : 0;
        }
        return this.sortDefinition.isAscending() ? result : -result;
    }

    @Nullable
    private Object getPropertyValue(Object obj) {
        try {
            this.beanWrapper.setWrappedInstance(obj);
            return this.beanWrapper.getPropertyValue(this.sortDefinition.getProperty());
        } catch (BeansException ex) {
            this.logger.debug("PropertyComparator could not access property - treating as null for sorting", ex);
            return null;
        }
    }

    public static void sort(List<?> source, SortDefinition sortDefinition) throws BeansException {
        if (StringUtils.hasText(sortDefinition.getProperty())) {
            source.sort(new PropertyComparator(sortDefinition));
        }
    }

    public static void sort(Object[] source, SortDefinition sortDefinition) throws BeansException {
        if (StringUtils.hasText(sortDefinition.getProperty())) {
            Arrays.sort(source, new PropertyComparator(sortDefinition));
        }
    }
}