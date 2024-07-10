package org.springframework.context.support;

import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionReference;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KDeclarationContainer;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.env.ConfigurableEnvironment;

/* compiled from: BeanDefinitionDsl.kt */
@Metadata(mv = {1, 1, 11}, bv = {1, 0, 2}, k = 3, d1 = {"��\u0014\n��\n\u0002\u0010\u000b\n��\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010��\u001a\u00020\u00012\u0015\u0010\u0002\u001a\u00110\u0003¢\u0006\f\b\u0004\u0012\b\b\u0005\u0012\u0004\b\b(\u0002¢\u0006\u0002\b\u0006"}, d2 = {"<anonymous>", "", "p1", "Lorg/springframework/core/env/ConfigurableEnvironment;", "Lkotlin/ParameterName;", "name", "invoke"})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/support/BeanDefinitionDsl$environment$beans$1.class */
final class BeanDefinitionDsl$environment$beans$1 extends FunctionReference implements Function1<ConfigurableEnvironment, Boolean> {
    public final KDeclarationContainer getOwner() {
        return Reflection.getOrCreateKotlinClass(Function1.class);
    }

    public final String getName() {
        return "invoke";
    }

    public final String getSignature() {
        return "invoke(Ljava/lang/Object;)Ljava/lang/Object;";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BeanDefinitionDsl$environment$beans$1(Function1 function1) {
        super(1, function1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        return Boolean.valueOf(invoke((ConfigurableEnvironment) obj));
    }

    public final boolean invoke(@NotNull ConfigurableEnvironment p1) {
        Intrinsics.checkParameterIsNotNull(p1, "p1");
        return ((Boolean) ((Function1) this.receiver).invoke(p1)).booleanValue();
    }
}