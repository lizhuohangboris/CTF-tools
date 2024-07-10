package org.springframework.context.support;

import ch.qos.logback.core.CoreConstants;
import java.util.ArrayList;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.PublishedApi;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;

/* compiled from: BeanDefinitionDsl.kt */
@Metadata(mv = {1, 1, 11}, bv = {1, 0, 2}, k = 1, d1 = {"��`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010��\n��\n\u0002\u0010\u000e\n��\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0016\u0018��2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u000256B4\u0012\u0017\u0010\u0003\u001a\u0013\u0012\u0004\u0012\u00020��\u0012\u0004\u0012\u00020\u00050\u0004¢\u0006\u0002\b\u0006\u0012\u0014\b\u0002\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0004¢\u0006\u0002\u0010\nJ\u0086\u0001\u0010\u001b\u001a\u00020\u0005\"\n\b��\u0010\u001c\u0018\u0001*\u00020\u001d2\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\n\b\u0002\u0010 \u001a\u0004\u0018\u00010!2\n\b\u0002\u0010\"\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010#\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010$\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010%\u001a\u0004\u0018\u00010\u001f2\n\b\u0002\u0010&\u001a\u0004\u0018\u00010\u001f2\n\b\u0002\u0010'\u001a\u0004\u0018\u00010\u001f2\n\b\u0002\u0010(\u001a\u0004\u0018\u00010)H\u0086\b¢\u0006\u0002\u0010*J\u0096\u0001\u0010\u001b\u001a\u00020\u0005\"\n\b��\u0010\u001c\u0018\u0001*\u00020\u001d2\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\n\b\u0002\u0010 \u001a\u0004\u0018\u00010!2\n\b\u0002\u0010\"\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010#\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010$\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010%\u001a\u0004\u0018\u00010\u001f2\n\b\u0002\u0010&\u001a\u0004\u0018\u00010\u001f2\n\b\u0002\u0010'\u001a\u0004\u0018\u00010\u001f2\n\b\u0002\u0010(\u001a\u0004\u0018\u00010)2\u000e\b\u0004\u0010+\u001a\b\u0012\u0004\u0012\u0002H\u001c0,H\u0086\b¢\u0006\u0002\u0010-J8\u0010.\u001a\u00020\u00052\u0017\u0010\u0007\u001a\u0013\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0004¢\u0006\u0002\b\u00062\u0017\u0010\u0003\u001a\u0013\u0012\u0004\u0012\u00020��\u0012\u0004\u0012\u00020\u00050\u0004¢\u0006\u0002\b\u0006J\u0010\u0010/\u001a\u00020\u00052\u0006\u0010\u0012\u001a\u00020\u0002H\u0016J'\u00100\u001a\u00020\u00052\u0006\u00100\u001a\u00020\u001f2\u0017\u0010\u0003\u001a\u0013\u0012\u0004\u0012\u00020��\u0012\u0004\u0012\u00020\u00050\u0004¢\u0006\u0002\b\u0006J\u001b\u00101\u001a\b\u0012\u0004\u0012\u0002H\u001c02\"\n\b��\u0010\u001c\u0018\u0001*\u00020\u001dH\u0086\bJ&\u00103\u001a\u0002H\u001c\"\n\b��\u0010\u001c\u0018\u0001*\u00020\u001d2\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0086\b¢\u0006\u0002\u00104R,\u0010\u000b\u001a\u0012\u0012\u0004\u0012\u00020��0\fj\b\u0012\u0004\u0012\u00020��`\r8��X\u0081\u0004¢\u0006\u000e\n��\u0012\u0004\b\u000e\u0010\u000f\u001a\u0004\b\u0010\u0010\u0011R\u001a\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0004X\u0082\u0004¢\u0006\u0002\n��R$\u0010\u0012\u001a\u00020\u00028��@��X\u0081.¢\u0006\u0014\n��\u0012\u0004\b\u0013\u0010\u000f\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0018\u001a\u00020\b8F¢\u0006\u0006\u001a\u0004\b\u0019\u0010\u001aR\u001f\u0010\u0003\u001a\u0013\u0012\u0004\u0012\u00020��\u0012\u0004\u0012\u00020\u00050\u0004¢\u0006\u0002\b\u0006X\u0082\u0004¢\u0006\u0002\n��¨\u00067"}, d2 = {"Lorg/springframework/context/support/BeanDefinitionDsl;", "Lorg/springframework/context/ApplicationContextInitializer;", "Lorg/springframework/context/support/GenericApplicationContext;", "init", "Lkotlin/Function1;", "", "Lkotlin/ExtensionFunctionType;", "condition", "Lorg/springframework/core/env/ConfigurableEnvironment;", "", "(Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)V", "children", "Ljava/util/ArrayList;", "Lkotlin/collections/ArrayList;", "children$annotations", "()V", "getChildren", "()Ljava/util/ArrayList;", CoreConstants.CONTEXT_SCOPE_VALUE, "context$annotations", "getContext", "()Lorg/springframework/context/support/GenericApplicationContext;", "setContext", "(Lorg/springframework/context/support/GenericApplicationContext;)V", "env", "getEnv", "()Lorg/springframework/core/env/ConfigurableEnvironment;", "bean", "T", "", "name", "", "scope", "Lorg/springframework/context/support/BeanDefinitionDsl$Scope;", "isLazyInit", "isPrimary", "isAutowireCandidate", "initMethodName", "destroyMethodName", "description", "role", "Lorg/springframework/context/support/BeanDefinitionDsl$Role;", "(Ljava/lang/String;Lorg/springframework/context/support/BeanDefinitionDsl$Scope;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lorg/springframework/context/support/BeanDefinitionDsl$Role;)V", "function", "Lkotlin/Function0;", "(Ljava/lang/String;Lorg/springframework/context/support/BeanDefinitionDsl$Scope;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lorg/springframework/context/support/BeanDefinitionDsl$Role;Lkotlin/jvm/functions/Function0;)V", "environment", "initialize", DefaultBeanDefinitionDocumentReader.PROFILE_ATTRIBUTE, "provider", "Lorg/springframework/beans/factory/ObjectProvider;", "ref", "(Ljava/lang/String;)Ljava/lang/Object;", "Role", "Scope", "spring-context"})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/support/BeanDefinitionDsl.class */
public class BeanDefinitionDsl implements ApplicationContextInitializer<GenericApplicationContext> {
    @NotNull
    private final ArrayList<BeanDefinitionDsl> children;
    @NotNull
    public GenericApplicationContext context;
    private final Function1<BeanDefinitionDsl, Unit> init;
    private final Function1<ConfigurableEnvironment, Boolean> condition;

    /* compiled from: BeanDefinitionDsl.kt */
    @Metadata(mv = {1, 1, 11}, bv = {1, 0, 2}, k = 1, d1 = {"��\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0001\u0018��2\b\u0012\u0004\u0012\u00020��0\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005¨\u0006\u0006"}, d2 = {"Lorg/springframework/context/support/BeanDefinitionDsl$Role;", "", "(Ljava/lang/String;I)V", "APPLICATION", "SUPPORT", "INFRASTRUCTURE", "spring-context"})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/support/BeanDefinitionDsl$Role.class */
    public enum Role {
        APPLICATION,
        SUPPORT,
        INFRASTRUCTURE
    }

    /* compiled from: BeanDefinitionDsl.kt */
    @Metadata(mv = {1, 1, 11}, bv = {1, 0, 2}, k = 1, d1 = {"��\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0004\b\u0086\u0001\u0018��2\b\u0012\u0004\u0012\u00020��0\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004¨\u0006\u0005"}, d2 = {"Lorg/springframework/context/support/BeanDefinitionDsl$Scope;", "", "(Ljava/lang/String;I)V", "SINGLETON", "PROTOTYPE", "spring-context"})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/support/BeanDefinitionDsl$Scope.class */
    public enum Scope {
        SINGLETON,
        PROTOTYPE
    }

    @PublishedApi
    public static /* synthetic */ void children$annotations() {
    }

    @PublishedApi
    public static /* synthetic */ void context$annotations() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BeanDefinitionDsl(@NotNull Function1<? super BeanDefinitionDsl, Unit> function1, @NotNull Function1<? super ConfigurableEnvironment, Boolean> function12) {
        Intrinsics.checkParameterIsNotNull(function1, "init");
        Intrinsics.checkParameterIsNotNull(function12, "condition");
        this.init = function1;
        this.condition = function12;
        this.children = new ArrayList<>();
    }

    public /* synthetic */ BeanDefinitionDsl(Function1 function1, Function1 function12, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(function1, (i & 2) != 0 ? new Function1<ConfigurableEnvironment, Boolean>() { // from class: org.springframework.context.support.BeanDefinitionDsl.1
            public /* bridge */ /* synthetic */ Object invoke(Object obj) {
                return Boolean.valueOf(invoke((ConfigurableEnvironment) obj));
            }

            public final boolean invoke(@NotNull ConfigurableEnvironment it) {
                Intrinsics.checkParameterIsNotNull(it, "it");
                return true;
            }
        } : function12);
    }

    @NotNull
    public final ArrayList<BeanDefinitionDsl> getChildren() {
        return this.children;
    }

    @NotNull
    public final GenericApplicationContext getContext() {
        GenericApplicationContext genericApplicationContext = this.context;
        if (genericApplicationContext == null) {
            Intrinsics.throwUninitializedPropertyAccessException(CoreConstants.CONTEXT_SCOPE_VALUE);
        }
        return genericApplicationContext;
    }

    public final void setContext(@NotNull GenericApplicationContext genericApplicationContext) {
        Intrinsics.checkParameterIsNotNull(genericApplicationContext, "<set-?>");
        this.context = genericApplicationContext;
    }

    @NotNull
    public final ConfigurableEnvironment getEnv() {
        GenericApplicationContext genericApplicationContext = this.context;
        if (genericApplicationContext == null) {
            Intrinsics.throwUninitializedPropertyAccessException(CoreConstants.CONTEXT_SCOPE_VALUE);
        }
        ConfigurableEnvironment environment = genericApplicationContext.getEnvironment();
        Intrinsics.checkExpressionValueIsNotNull(environment, "context.environment");
        return environment;
    }

    static /* bridge */ /* synthetic */ void bean$default(BeanDefinitionDsl beanDefinitionDsl, String name, Scope scope, Boolean isLazyInit, Boolean isPrimary, Boolean isAutowireCandidate, String initMethodName, String destroyMethodName, String description, Role role, int i, Object obj) {
        if (obj != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: bean");
        }
        if ((i & 1) != 0) {
            name = null;
        }
        if ((i & 2) != 0) {
            scope = null;
        }
        if ((i & 4) != 0) {
            isLazyInit = null;
        }
        if ((i & 8) != 0) {
            isPrimary = null;
        }
        if ((i & 16) != 0) {
            isAutowireCandidate = null;
        }
        if ((i & 32) != 0) {
            initMethodName = null;
        }
        if ((i & 64) != 0) {
            destroyMethodName = null;
        }
        if ((i & 128) != 0) {
            description = null;
        }
        if ((i & 256) != 0) {
            role = null;
        }
        BeanDefinitionCustomizer customizer = new BeanDefinitionDsl$bean$customizer$1(scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role);
        String str = name;
        if (str == null) {
            Intrinsics.reifiedOperationMarker(4, "T");
            str = BeanDefinitionReaderUtils.uniqueBeanName(Object.class.getName(), beanDefinitionDsl.getContext());
            Intrinsics.checkExpressionValueIsNotNull(str, "BeanDefinitionReaderUtil…class.java.name, context)");
        }
        String beanName = str;
        GenericApplicationContext context = beanDefinitionDsl.getContext();
        Intrinsics.reifiedOperationMarker(4, "T");
        context.registerBean(beanName, Object.class, customizer);
    }

    private final <T> void bean(String name, Scope scope, Boolean isLazyInit, Boolean isPrimary, Boolean isAutowireCandidate, String initMethodName, String destroyMethodName, String description, Role role) {
        BeanDefinitionCustomizer customizer = new BeanDefinitionDsl$bean$customizer$1(scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role);
        String str = name;
        if (str == null) {
            Intrinsics.reifiedOperationMarker(4, "T");
            str = BeanDefinitionReaderUtils.uniqueBeanName(Object.class.getName(), getContext());
            Intrinsics.checkExpressionValueIsNotNull(str, "BeanDefinitionReaderUtil…class.java.name, context)");
        }
        String beanName = str;
        GenericApplicationContext context = getContext();
        Intrinsics.reifiedOperationMarker(4, "T");
        context.registerBean(beanName, Object.class, customizer);
    }

    static /* bridge */ /* synthetic */ void bean$default(BeanDefinitionDsl beanDefinitionDsl, String name, Scope scope, Boolean isLazyInit, Boolean isPrimary, Boolean isAutowireCandidate, String initMethodName, String destroyMethodName, String description, Role role, Function0 function, int i, Object obj) {
        if (obj != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: bean");
        }
        if ((i & 1) != 0) {
            name = null;
        }
        if ((i & 2) != 0) {
            scope = null;
        }
        if ((i & 4) != 0) {
            isLazyInit = null;
        }
        if ((i & 8) != 0) {
            isPrimary = null;
        }
        if ((i & 16) != 0) {
            isAutowireCandidate = null;
        }
        if ((i & 32) != 0) {
            initMethodName = null;
        }
        if ((i & 64) != 0) {
            destroyMethodName = null;
        }
        if ((i & 128) != 0) {
            description = null;
        }
        if ((i & 256) != 0) {
            role = null;
        }
        BeanDefinitionCustomizer customizer = new BeanDefinitionDsl$bean$customizer$2(scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role);
        String str = name;
        if (str == null) {
            Intrinsics.reifiedOperationMarker(4, "T");
            str = BeanDefinitionReaderUtils.uniqueBeanName(Object.class.getName(), beanDefinitionDsl.getContext());
            Intrinsics.checkExpressionValueIsNotNull(str, "BeanDefinitionReaderUtil…class.java.name, context)");
        }
        String beanName = str;
        GenericApplicationContext context = beanDefinitionDsl.getContext();
        Intrinsics.reifiedOperationMarker(4, "T");
        context.registerBean(beanName, Object.class, new BeanDefinitionDsl$bean$1(function), customizer);
    }

    private final <T> void bean(String name, Scope scope, Boolean isLazyInit, Boolean isPrimary, Boolean isAutowireCandidate, String initMethodName, String destroyMethodName, String description, Role role, Function0<? extends T> function0) {
        BeanDefinitionCustomizer customizer = new BeanDefinitionDsl$bean$customizer$2(scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role);
        String str = name;
        if (str == null) {
            Intrinsics.reifiedOperationMarker(4, "T");
            str = BeanDefinitionReaderUtils.uniqueBeanName(Object.class.getName(), getContext());
            Intrinsics.checkExpressionValueIsNotNull(str, "BeanDefinitionReaderUtil…class.java.name, context)");
        }
        String beanName = str;
        GenericApplicationContext context = getContext();
        Intrinsics.reifiedOperationMarker(4, "T");
        context.registerBean(beanName, Object.class, new BeanDefinitionDsl$bean$1(function0), customizer);
    }

    private final <T> T ref(String name) {
        if (name == null) {
            GenericApplicationContext context = getContext();
            Intrinsics.reifiedOperationMarker(4, "T");
            T t = (T) context.getBean(Object.class);
            Intrinsics.checkExpressionValueIsNotNull(t, "context.getBean(T::class.java)");
            return t;
        }
        GenericApplicationContext context2 = getContext();
        Intrinsics.reifiedOperationMarker(4, "T");
        T t2 = (T) context2.getBean(name, Object.class);
        Intrinsics.checkExpressionValueIsNotNull(t2, "context.getBean(name, T::class.java)");
        return t2;
    }

    static /* bridge */ /* synthetic */ Object ref$default(BeanDefinitionDsl beanDefinitionDsl, String name, int i, Object obj) {
        if (obj != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: ref");
        }
        if ((i & 1) != 0) {
            name = null;
        }
        if (name == null) {
            GenericApplicationContext context = beanDefinitionDsl.getContext();
            Intrinsics.reifiedOperationMarker(4, "T");
            Object bean = context.getBean(Object.class);
            Intrinsics.checkExpressionValueIsNotNull(bean, "context.getBean(T::class.java)");
            return bean;
        }
        Intrinsics.reifiedOperationMarker(4, "T");
        Object bean2 = beanDefinitionDsl.getContext().getBean(name, Object.class);
        Intrinsics.checkExpressionValueIsNotNull(bean2, "context.getBean(name, T::class.java)");
        return bean2;
    }

    private final <T> ObjectProvider<T> provider() {
        BeanFactory $receiver$iv = getContext();
        Intrinsics.needClassReification();
        ObjectProvider<T> beanProvider = $receiver$iv.getBeanProvider(ResolvableType.forType(new ParameterizedTypeReference<T>() { // from class: org.springframework.context.support.BeanDefinitionDsl$provider$$inlined$getBeanProvider$1
        }.getType()));
        Intrinsics.checkExpressionValueIsNotNull(beanProvider, "getBeanProvider(Resolvab…Reference<T>() {}).type))");
        return beanProvider;
    }

    public final void profile(@NotNull final String profile, @NotNull Function1<? super BeanDefinitionDsl, Unit> function1) {
        Intrinsics.checkParameterIsNotNull(profile, DefaultBeanDefinitionDocumentReader.PROFILE_ATTRIBUTE);
        Intrinsics.checkParameterIsNotNull(function1, "init");
        BeanDefinitionDsl beans = new BeanDefinitionDsl(function1, new Function1<ConfigurableEnvironment, Boolean>() { // from class: org.springframework.context.support.BeanDefinitionDsl$profile$beans$1
            /* JADX INFO: Access modifiers changed from: package-private */
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            public /* bridge */ /* synthetic */ Object invoke(Object obj) {
                return Boolean.valueOf(invoke((ConfigurableEnvironment) obj));
            }

            public final boolean invoke(@NotNull ConfigurableEnvironment it) {
                Intrinsics.checkParameterIsNotNull(it, "it");
                return ArraysKt.contains(it.getActiveProfiles(), profile);
            }
        });
        this.children.add(beans);
    }

    public final void environment(@NotNull Function1<? super ConfigurableEnvironment, Boolean> function1, @NotNull Function1<? super BeanDefinitionDsl, Unit> function12) {
        Intrinsics.checkParameterIsNotNull(function1, "condition");
        Intrinsics.checkParameterIsNotNull(function12, "init");
        BeanDefinitionDsl beans = new BeanDefinitionDsl(function12, new BeanDefinitionDsl$environment$beans$1(function1));
        this.children.add(beans);
    }

    @Override // org.springframework.context.ApplicationContextInitializer
    public void initialize(@NotNull GenericApplicationContext context) {
        Intrinsics.checkParameterIsNotNull(context, CoreConstants.CONTEXT_SCOPE_VALUE);
        this.context = context;
        this.init.invoke(this);
        Iterator<BeanDefinitionDsl> it = this.children.iterator();
        while (it.hasNext()) {
            BeanDefinitionDsl child = it.next();
            Function1<ConfigurableEnvironment, Boolean> function1 = child.condition;
            ConfigurableEnvironment environment = context.getEnvironment();
            Intrinsics.checkExpressionValueIsNotNull(environment, "context.environment");
            if (((Boolean) function1.invoke(environment)).booleanValue()) {
                child.initialize(context);
            }
        }
    }
}