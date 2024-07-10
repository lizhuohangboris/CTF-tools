package org.springframework.beans.factory;

import java.util.Arrays;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;

/* compiled from: BeanFactoryExtensions.kt */
@Metadata(mv = {1, 1, 11}, bv = {1, 0, 2}, k = 2, d1 = {"��&\n\u0002\b\u0002\n\u0002\u0010��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\u001a\u001e\u0010��\u001a\u0002H\u0001\"\n\b��\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u0003H\u0086\b¢\u0006\u0002\u0010\u0004\u001a2\u0010��\u001a\u0002H\u0001\"\n\b��\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u00032\u0012\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00020\u0006\"\u00020\u0002H\u0086\b¢\u0006\u0002\u0010\u0007\u001a&\u0010��\u001a\u0002H\u0001\"\n\b��\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u0086\b¢\u0006\u0002\u0010\n\u001a\u001f\u0010\u000b\u001a\b\u0012\u0004\u0012\u0002H\u00010\f\"\n\b��\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u0003H\u0086\b¨\u0006\r"}, d2 = {"getBean", "T", "", "Lorg/springframework/beans/factory/BeanFactory;", "(Lorg/springframework/beans/factory/BeanFactory;)Ljava/lang/Object;", "args", "", "(Lorg/springframework/beans/factory/BeanFactory;[Ljava/lang/Object;)Ljava/lang/Object;", "name", "", "(Lorg/springframework/beans/factory/BeanFactory;Ljava/lang/String;)Ljava/lang/Object;", "getBeanProvider", "Lorg/springframework/beans/factory/ObjectProvider;", "spring-beans"})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/BeanFactoryExtensionsKt.class */
public final class BeanFactoryExtensionsKt {
    private static final <T> T getBean(@NotNull BeanFactory $receiver) {
        Intrinsics.reifiedOperationMarker(4, "T");
        T t = (T) $receiver.getBean(Object.class);
        Intrinsics.checkExpressionValueIsNotNull(t, "getBean(T::class.java)");
        return t;
    }

    private static final <T> T getBean(@NotNull BeanFactory $receiver, String name) {
        Intrinsics.reifiedOperationMarker(4, "T");
        T t = (T) $receiver.getBean(name, Object.class);
        Intrinsics.checkExpressionValueIsNotNull(t, "getBean(name, T::class.java)");
        return t;
    }

    private static final <T> T getBean(@NotNull BeanFactory $receiver, Object... args) {
        Intrinsics.reifiedOperationMarker(4, "T");
        T t = (T) $receiver.getBean(Object.class, Arrays.copyOf(args, args.length));
        Intrinsics.checkExpressionValueIsNotNull(t, "getBean(T::class.java, *args)");
        return t;
    }

    private static final <T> ObjectProvider<T> getBeanProvider(@NotNull BeanFactory $receiver) {
        Intrinsics.needClassReification();
        ObjectProvider<T> beanProvider = $receiver.getBeanProvider(ResolvableType.forType(new ParameterizedTypeReference<T>() { // from class: org.springframework.beans.factory.BeanFactoryExtensionsKt$getBeanProvider$1
        }.getType()));
        Intrinsics.checkExpressionValueIsNotNull(beanProvider, "getBeanProvider(Resolvab…Reference<T>() {}).type))");
        return beanProvider;
    }
}