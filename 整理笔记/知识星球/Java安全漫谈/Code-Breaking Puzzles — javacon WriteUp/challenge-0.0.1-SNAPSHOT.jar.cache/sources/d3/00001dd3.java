package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.springframework.core.DecoratingProxy;
import org.springframework.core.OrderComparator;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/annotation/AnnotationAwareOrderComparator.class */
public class AnnotationAwareOrderComparator extends OrderComparator {
    public static final AnnotationAwareOrderComparator INSTANCE = new AnnotationAwareOrderComparator();

    @Override // org.springframework.core.OrderComparator
    @Nullable
    public Integer findOrder(Object obj) {
        Integer order = super.findOrder(obj);
        if (order != null) {
            return order;
        }
        if (obj instanceof Class) {
            return OrderUtils.getOrder((Class) obj);
        }
        if (obj instanceof Method) {
            Order ann = (Order) AnnotationUtils.findAnnotation((Method) obj, (Class<Annotation>) Order.class);
            if (ann != null) {
                return Integer.valueOf(ann.value());
            }
        } else if (obj instanceof AnnotatedElement) {
            Order ann2 = (Order) AnnotationUtils.getAnnotation((AnnotatedElement) obj, Order.class);
            if (ann2 != null) {
                return Integer.valueOf(ann2.value());
            }
        } else {
            order = OrderUtils.getOrder(obj.getClass());
            if (order == null && (obj instanceof DecoratingProxy)) {
                order = OrderUtils.getOrder(((DecoratingProxy) obj).getDecoratedClass());
            }
        }
        return order;
    }

    @Override // org.springframework.core.OrderComparator
    @Nullable
    public Integer getPriority(Object obj) {
        if (obj instanceof Class) {
            return OrderUtils.getPriority((Class) obj);
        }
        Integer priority = OrderUtils.getPriority(obj.getClass());
        if (priority == null && (obj instanceof DecoratingProxy)) {
            priority = OrderUtils.getPriority(((DecoratingProxy) obj).getDecoratedClass());
        }
        return priority;
    }

    public static void sort(List<?> list) {
        if (list.size() > 1) {
            list.sort(INSTANCE);
        }
    }

    public static void sort(Object[] array) {
        if (array.length > 1) {
            Arrays.sort(array, INSTANCE);
        }
    }

    public static void sortIfNecessary(Object value) {
        if (value instanceof Object[]) {
            sort((Object[]) value);
        } else if (value instanceof List) {
            sort((List) value);
        }
    }
}