package org.springframework.context.support;

import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.context.support.BeanDefinitionDsl;

/* compiled from: BeanDefinitionDsl.kt */
@Metadata(mv = {1, 1, 11}, bv = {1, 0, 2}, k = 3, d1 = {"��\u0014\n��\n\u0002\u0010\u0002\n��\n\u0002\u0010��\n��\n\u0002\u0018\u0002\n��\u0010��\u001a\u00020\u0001\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\n¢\u0006\u0002\b\u0006"}, d2 = {"<anonymous>", "", "T", "", "bd", "Lorg/springframework/beans/factory/config/BeanDefinition;", "customize"})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/support/BeanDefinitionDsl$bean$customizer$1.class */
public final class BeanDefinitionDsl$bean$customizer$1 implements BeanDefinitionCustomizer {
    final /* synthetic */ BeanDefinitionDsl.Scope $scope;
    final /* synthetic */ Boolean $isLazyInit;
    final /* synthetic */ Boolean $isPrimary;
    final /* synthetic */ Boolean $isAutowireCandidate;
    final /* synthetic */ String $initMethodName;
    final /* synthetic */ String $destroyMethodName;
    final /* synthetic */ String $description;
    final /* synthetic */ BeanDefinitionDsl.Role $role;

    public BeanDefinitionDsl$bean$customizer$1(BeanDefinitionDsl.Scope scope, Boolean bool, Boolean bool2, Boolean bool3, String str, String str2, String str3, BeanDefinitionDsl.Role role) {
        this.$scope = scope;
        this.$isLazyInit = bool;
        this.$isPrimary = bool2;
        this.$isAutowireCandidate = bool3;
        this.$initMethodName = str;
        this.$destroyMethodName = str2;
        this.$description = str3;
        this.$role = role;
    }

    @Override // org.springframework.beans.factory.config.BeanDefinitionCustomizer
    public final void customize(@NotNull BeanDefinition bd) {
        Intrinsics.checkParameterIsNotNull(bd, "bd");
        if (this.$scope != null) {
            String name = this.$scope.name();
            if (name == null) {
                throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
            }
            String lowerCase = name.toLowerCase();
            Intrinsics.checkExpressionValueIsNotNull(lowerCase, "(this as java.lang.String).toLowerCase()");
            bd.setScope(lowerCase);
        }
        Boolean bool = this.$isLazyInit;
        if (bool != null) {
            bool.booleanValue();
            bd.setLazyInit(this.$isLazyInit.booleanValue());
        }
        Boolean bool2 = this.$isPrimary;
        if (bool2 != null) {
            bool2.booleanValue();
            bd.setPrimary(this.$isPrimary.booleanValue());
        }
        Boolean bool3 = this.$isAutowireCandidate;
        if (bool3 != null) {
            bool3.booleanValue();
            bd.setAutowireCandidate(this.$isAutowireCandidate.booleanValue());
        }
        if (this.$initMethodName != null) {
            bd.setInitMethodName(this.$initMethodName);
        }
        if (this.$destroyMethodName != null) {
            bd.setDestroyMethodName(this.$destroyMethodName);
        }
        if (this.$description != null) {
            bd.setDescription(this.$description);
        }
        if (this.$role != null) {
            bd.setRole(this.$role.ordinal());
        }
    }
}