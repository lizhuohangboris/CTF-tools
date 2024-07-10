package org.springframework.beans.factory;

import java.lang.annotation.Annotation;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ListableBeanFactoryExtensions.kt */
@Metadata(mv = {1, 1, 11}, bv = {1, 0, 2}, k = 2, d1 = {"��2\n��\n\u0002\u0010\u001b\n��\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000e\n��\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\b\u0002\u001a#\u0010��\u001a\u0004\u0018\u00010\u0001\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0086\b\u001a&\u0010\u0006\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00050\u0007\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0001*\u00020\u0003H\u0086\b¢\u0006\u0002\u0010\b\u001a:\u0010\t\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00050\u0007\"\n\b��\u0010\u0002\u0018\u0001*\u00020\n*\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\fH\u0086\b¢\u0006\u0002\u0010\u000e\u001a9\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u0002H\u00020\u0010\"\n\b��\u0010\u0002\u0018\u0001*\u00020\n*\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\fH\u0086\b\u001a%\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\n0\u0010\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0001*\u00020\u0003H\u0086\b¨\u0006\u0012"}, d2 = {"findAnnotationOnBean", "", "T", "Lorg/springframework/beans/factory/ListableBeanFactory;", "beanName", "", "getBeanNamesForAnnotation", "", "(Lorg/springframework/beans/factory/ListableBeanFactory;)[Ljava/lang/String;", "getBeanNamesForType", "", "includeNonSingletons", "", "allowEagerInit", "(Lorg/springframework/beans/factory/ListableBeanFactory;ZZ)[Ljava/lang/String;", "getBeansOfType", "", "getBeansWithAnnotation", "spring-beans"})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/ListableBeanFactoryExtensionsKt.class */
public final class ListableBeanFactoryExtensionsKt {
    static /* bridge */ /* synthetic */ String[] getBeanNamesForType$default(ListableBeanFactory $receiver, boolean includeNonSingletons, boolean allowEagerInit, int i, Object obj) {
        if ((i & 1) != 0) {
            includeNonSingletons = true;
        }
        if ((i & 2) != 0) {
            allowEagerInit = true;
        }
        Intrinsics.reifiedOperationMarker(4, "T");
        String[] beanNamesForType = $receiver.getBeanNamesForType(Object.class, includeNonSingletons, allowEagerInit);
        Intrinsics.checkExpressionValueIsNotNull(beanNamesForType, "getBeanNamesForType(T::c…ngletons, allowEagerInit)");
        return beanNamesForType;
    }

    private static final <T> String[] getBeanNamesForType(@NotNull ListableBeanFactory $receiver, boolean includeNonSingletons, boolean allowEagerInit) {
        Intrinsics.reifiedOperationMarker(4, "T");
        String[] beanNamesForType = $receiver.getBeanNamesForType(Object.class, includeNonSingletons, allowEagerInit);
        Intrinsics.checkExpressionValueIsNotNull(beanNamesForType, "getBeanNamesForType(T::c…ngletons, allowEagerInit)");
        return beanNamesForType;
    }

    static /* bridge */ /* synthetic */ Map getBeansOfType$default(ListableBeanFactory $receiver, boolean includeNonSingletons, boolean allowEagerInit, int i, Object obj) {
        if ((i & 1) != 0) {
            includeNonSingletons = true;
        }
        if ((i & 2) != 0) {
            allowEagerInit = true;
        }
        Intrinsics.reifiedOperationMarker(4, "T");
        Map beansOfType = $receiver.getBeansOfType(Object.class, includeNonSingletons, allowEagerInit);
        Intrinsics.checkExpressionValueIsNotNull(beansOfType, "getBeansOfType(T::class.…ngletons, allowEagerInit)");
        return beansOfType;
    }

    private static final <T> Map<String, T> getBeansOfType(@NotNull ListableBeanFactory $receiver, boolean includeNonSingletons, boolean allowEagerInit) {
        Intrinsics.reifiedOperationMarker(4, "T");
        Map<String, T> beansOfType = $receiver.getBeansOfType(Object.class, includeNonSingletons, allowEagerInit);
        Intrinsics.checkExpressionValueIsNotNull(beansOfType, "getBeansOfType(T::class.…ngletons, allowEagerInit)");
        return beansOfType;
    }

    private static final <T extends Annotation> String[] getBeanNamesForAnnotation(@NotNull ListableBeanFactory $receiver) {
        Intrinsics.reifiedOperationMarker(4, "T");
        String[] beanNamesForAnnotation = $receiver.getBeanNamesForAnnotation(Annotation.class);
        Intrinsics.checkExpressionValueIsNotNull(beanNamesForAnnotation, "getBeanNamesForAnnotation(T::class.java)");
        return beanNamesForAnnotation;
    }

    private static final <T extends Annotation> Map<String, Object> getBeansWithAnnotation(@NotNull ListableBeanFactory $receiver) {
        Intrinsics.reifiedOperationMarker(4, "T");
        Map<String, Object> beansWithAnnotation = $receiver.getBeansWithAnnotation(Annotation.class);
        Intrinsics.checkExpressionValueIsNotNull(beansWithAnnotation, "getBeansWithAnnotation(T::class.java)");
        return beansWithAnnotation;
    }

    private static final <T extends Annotation> Annotation findAnnotationOnBean(@NotNull ListableBeanFactory $receiver, String beanName) {
        Intrinsics.reifiedOperationMarker(4, "T");
        return $receiver.findAnnotationOnBean(beanName, Annotation.class);
    }
}