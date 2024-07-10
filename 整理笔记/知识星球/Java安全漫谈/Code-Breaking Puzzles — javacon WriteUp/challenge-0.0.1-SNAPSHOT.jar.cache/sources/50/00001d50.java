package org.springframework.context.support;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;

/* compiled from: BeanDefinitionDsl.kt */
@Metadata(mv = {1, 1, 11}, bv = {1, 0, 2}, k = 2, d1 = {"��\u0016\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n��\u001a\u001f\u0010��\u001a\u00020\u00012\u0017\u0010\u0002\u001a\u0013\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0002\b\u0005¨\u0006\u0006"}, d2 = {DefaultBeanDefinitionDocumentReader.NESTED_BEANS_ELEMENT, "Lorg/springframework/context/support/BeanDefinitionDsl;", "init", "Lkotlin/Function1;", "", "Lkotlin/ExtensionFunctionType;", "spring-context"})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/support/BeanDefinitionDslKt.class */
public final class BeanDefinitionDslKt {
    @NotNull
    public static final BeanDefinitionDsl beans(@NotNull Function1<? super BeanDefinitionDsl, Unit> function1) {
        Intrinsics.checkParameterIsNotNull(function1, "init");
        return new BeanDefinitionDsl(function1, null, 2, null);
    }
}