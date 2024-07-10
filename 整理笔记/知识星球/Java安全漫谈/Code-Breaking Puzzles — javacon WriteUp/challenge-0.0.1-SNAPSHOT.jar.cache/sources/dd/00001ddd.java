package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/annotation/OrderUtils.class */
public abstract class OrderUtils {
    private static final Object NOT_ANNOTATED = new Object();
    @Nullable
    private static Class<? extends Annotation> priorityAnnotationType;
    private static final Map<Class<?>, Object> orderCache;
    private static final Map<Class<?>, Object> priorityCache;

    static {
        try {
            priorityAnnotationType = ClassUtils.forName("javax.annotation.Priority", OrderUtils.class.getClassLoader());
        } catch (Throwable th) {
            priorityAnnotationType = null;
        }
        orderCache = new ConcurrentReferenceHashMap(64);
        priorityCache = new ConcurrentReferenceHashMap();
    }

    public static int getOrder(Class<?> type, int defaultOrder) {
        Integer order = getOrder(type);
        return order != null ? order.intValue() : defaultOrder;
    }

    @Nullable
    public static Integer getOrder(Class<?> type, @Nullable Integer defaultOrder) {
        Integer order = getOrder(type);
        return order != null ? order : defaultOrder;
    }

    @Nullable
    public static Integer getOrder(Class<?> type) {
        Integer result;
        Object cached = orderCache.get(type);
        if (cached != null) {
            if (cached instanceof Integer) {
                return (Integer) cached;
            }
            return null;
        }
        Order order = (Order) AnnotationUtils.findAnnotation(type, (Class<Annotation>) Order.class);
        if (order != null) {
            result = Integer.valueOf(order.value());
        } else {
            result = getPriority(type);
        }
        orderCache.put(type, result != null ? result : NOT_ANNOTATED);
        return result;
    }

    @Nullable
    public static Integer getPriority(Class<?> type) {
        if (priorityAnnotationType == null) {
            return null;
        }
        Object cached = priorityCache.get(type);
        if (cached != null) {
            if (cached instanceof Integer) {
                return (Integer) cached;
            }
            return null;
        }
        Annotation priority = AnnotationUtils.findAnnotation(type, (Class<Annotation>) priorityAnnotationType);
        Integer result = null;
        if (priority != null) {
            result = (Integer) AnnotationUtils.getValue(priority);
        }
        priorityCache.put(type, result != null ? result : NOT_ANNOTATED);
        return result;
    }
}