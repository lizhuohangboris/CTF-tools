package org.hibernate.validator.internal.util;

import com.fasterxml.classmate.Filter;
import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.RawMethod;
import com.fasterxml.classmate.members.ResolvedMethod;
import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetResolvedMemberMethods;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/ExecutableHelper.class */
public final class ExecutableHelper {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final TypeResolver typeResolver;

    public ExecutableHelper(TypeResolutionHelper typeResolutionHelper) {
        this.typeResolver = typeResolutionHelper.getTypeResolver();
    }

    public boolean overrides(Method subTypeMethod, Method superTypeMethod) {
        Contracts.assertValueNotNull(subTypeMethod, "subTypeMethod");
        Contracts.assertValueNotNull(superTypeMethod, "superTypeMethod");
        if (subTypeMethod.equals(superTypeMethod) || !subTypeMethod.getName().equals(superTypeMethod.getName()) || subTypeMethod.getParameterTypes().length != superTypeMethod.getParameterTypes().length || !superTypeMethod.getDeclaringClass().isAssignableFrom(subTypeMethod.getDeclaringClass()) || Modifier.isStatic(superTypeMethod.getModifiers()) || Modifier.isStatic(subTypeMethod.getModifiers()) || subTypeMethod.isBridge() || Modifier.isPrivate(superTypeMethod.getModifiers())) {
            return false;
        }
        if (!Modifier.isPublic(superTypeMethod.getModifiers()) && !Modifier.isProtected(superTypeMethod.getModifiers()) && !superTypeMethod.getDeclaringClass().getPackage().equals(subTypeMethod.getDeclaringClass().getPackage())) {
            return false;
        }
        return instanceMethodParametersResolveToSameTypes(subTypeMethod, superTypeMethod);
    }

    public static String getSimpleName(Executable executable) {
        return executable instanceof Constructor ? executable.getDeclaringClass().getSimpleName() : executable.getName();
    }

    public static String getSignature(Executable executable) {
        return getSignature(getSimpleName(executable), executable.getParameterTypes());
    }

    public static String getSignature(String name, Class<?>[] parameterTypes) {
        return (String) Stream.of((Object[]) parameterTypes).map(t -> {
            return t.getName();
        }).collect(Collectors.joining(",", name + "(", ")"));
    }

    public static String getExecutableAsString(String name, Class<?>... parameterTypes) {
        return (String) Stream.of((Object[]) parameterTypes).map(t -> {
            return t.getSimpleName();
        }).collect(Collectors.joining(", ", name + "(", ")"));
    }

    public static ElementType getElementType(Executable executable) {
        return executable instanceof Constructor ? ElementType.CONSTRUCTOR : ElementType.METHOD;
    }

    private boolean instanceMethodParametersResolveToSameTypes(Method subTypeMethod, Method superTypeMethod) {
        if (subTypeMethod.getParameterTypes().length == 0) {
            return true;
        }
        ResolvedType resolvedSubType = this.typeResolver.resolve(subTypeMethod.getDeclaringClass(), new Type[0]);
        MemberResolver memberResolver = new MemberResolver(this.typeResolver);
        memberResolver.setMethodFilter(new SimpleMethodFilter(subTypeMethod, superTypeMethod));
        ResolvedTypeWithMembers typeWithMembers = memberResolver.resolve(resolvedSubType, null, null);
        ResolvedMethod[] resolvedMethods = (ResolvedMethod[]) run(GetResolvedMemberMethods.action(typeWithMembers));
        if (resolvedMethods.length == 1) {
            return true;
        }
        for (int i = 0; i < resolvedMethods[0].getArgumentCount(); i++) {
            try {
                if (!resolvedMethods[0].getArgumentType(i).equals(resolvedMethods[1].getArgumentType(i))) {
                    return false;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                LOG.debug("Error in ExecutableHelper#instanceMethodParametersResolveToSameTypes comparing " + subTypeMethod + " with " + superTypeMethod);
                return true;
            }
        }
        return true;
    }

    private <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? (T) AccessController.doPrivileged(action) : action.run();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/ExecutableHelper$SimpleMethodFilter.class */
    public static class SimpleMethodFilter implements Filter<RawMethod> {
        private final Method method1;
        private final Method method2;

        private SimpleMethodFilter(Method method1, Method method2) {
            this.method1 = method1;
            this.method2 = method2;
        }

        @Override // com.fasterxml.classmate.Filter
        public boolean include(RawMethod element) {
            return element.getRawMember().equals(this.method1) || element.getRawMember().equals(this.method2);
        }
    }
}