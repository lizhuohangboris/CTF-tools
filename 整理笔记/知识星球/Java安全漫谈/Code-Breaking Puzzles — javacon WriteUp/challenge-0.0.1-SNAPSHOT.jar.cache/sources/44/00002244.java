package org.springframework.objenesis.instantiator.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.security.ProtectionDomain;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.strategy.PlatformDescription;
import sun.misc.Unsafe;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/objenesis/instantiator/util/DefineClassHelper.class */
public final class DefineClassHelper {
    private static final Helper privileged;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/objenesis/instantiator/util/DefineClassHelper$Helper.class */
    private static abstract class Helper {
        abstract Class<?> defineClass(String str, byte[] bArr, int i, int i2, Class<?> cls, ClassLoader classLoader, ProtectionDomain protectionDomain);

        private Helper() {
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/objenesis/instantiator/util/DefineClassHelper$Java8.class */
    private static class Java8 extends Helper {
        private final MethodHandle defineClass;

        private Java8() {
            super();
            this.defineClass = defineClass();
        }

        private MethodHandle defineClass() {
            MethodType mt = MethodType.methodType(Class.class, String.class, byte[].class, Integer.TYPE, Integer.TYPE, ClassLoader.class, ProtectionDomain.class);
            try {
                MethodHandle m = MethodHandles.publicLookup().findVirtual(Unsafe.class, "defineClass", mt);
                Unsafe unsafe = UnsafeUtils.getUnsafe();
                return m.bindTo(unsafe);
            } catch (IllegalAccessException | NoSuchMethodException e) {
                throw new ObjenesisException(e);
            }
        }

        @Override // org.springframework.objenesis.instantiator.util.DefineClassHelper.Helper
        Class<?> defineClass(String className, byte[] b, int off, int len, Class<?> neighbor, ClassLoader loader, ProtectionDomain protectionDomain) {
            try {
                return (Class) this.defineClass.invokeExact(className, b, off, len, loader, protectionDomain);
            } catch (Throwable e) {
                if (e instanceof Error) {
                    throw ((Error) e);
                }
                if (e instanceof RuntimeException) {
                    throw ((RuntimeException) e);
                }
                throw new ObjenesisException(e);
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/objenesis/instantiator/util/DefineClassHelper$Java11.class */
    private static class Java11 extends Helper {
        private final Class<?> module;
        private final MethodHandles.Lookup lookup;
        private final MethodHandle getModule;
        private final MethodHandle addReads;
        private final MethodHandle privateLookupIn;
        private final MethodHandle defineClass;

        private Java11() {
            super();
            this.module = module();
            this.lookup = MethodHandles.lookup();
            this.getModule = getModule();
            this.addReads = addReads();
            this.privateLookupIn = privateLookupIn();
            this.defineClass = defineClass();
        }

        private Class<?> module() {
            try {
                return Class.forName("java.lang.Module");
            } catch (ClassNotFoundException e) {
                throw new ObjenesisException(e);
            }
        }

        private MethodHandle getModule() {
            try {
                return this.lookup.findVirtual(Class.class, "getModule", MethodType.methodType(this.module));
            } catch (IllegalAccessException | NoSuchMethodException e) {
                throw new ObjenesisException(e);
            }
        }

        private MethodHandle addReads() {
            try {
                return this.lookup.findVirtual(this.module, "addReads", MethodType.methodType(this.module, this.module));
            } catch (IllegalAccessException | NoSuchMethodException e) {
                throw new ObjenesisException(e);
            }
        }

        private MethodHandle privateLookupIn() {
            try {
                return this.lookup.findStatic(MethodHandles.class, "privateLookupIn", MethodType.methodType(MethodHandles.Lookup.class, Class.class, MethodHandles.Lookup.class));
            } catch (IllegalAccessException | NoSuchMethodException e) {
                throw new ObjenesisException(e);
            }
        }

        private MethodHandle defineClass() {
            try {
                return this.lookup.findVirtual(MethodHandles.Lookup.class, "defineClass", MethodType.methodType(Class.class, byte[].class));
            } catch (IllegalAccessException | NoSuchMethodException e) {
                throw new ObjenesisException(e);
            }
        }

        @Override // org.springframework.objenesis.instantiator.util.DefineClassHelper.Helper
        Class<?> defineClass(String className, byte[] b, int off, int len, Class<?> neighbor, ClassLoader loader, ProtectionDomain protectionDomain) {
            try {
                Object module = this.getModule.invokeWithArguments(DefineClassHelper.class);
                Object neighborModule = this.getModule.invokeWithArguments(neighbor);
                this.addReads.invokeWithArguments(module, neighborModule);
                MethodHandles.Lookup prvlookup = (MethodHandles.Lookup) this.privateLookupIn.invokeExact(neighbor, this.lookup);
                return (Class) this.defineClass.invokeExact(prvlookup, b);
            } catch (Throwable e) {
                throw new ObjenesisException(neighbor.getName() + " has no permission to define the class", e);
            }
        }
    }

    static {
        privileged = PlatformDescription.isAfterJava11() ? new Java11() : new Java8();
    }

    public static Class<?> defineClass(String name, byte[] b, int off, int len, Class<?> neighbor, ClassLoader loader, ProtectionDomain protectionDomain) {
        return privileged.defineClass(name, b, off, len, neighbor, loader, protectionDomain);
    }

    private DefineClassHelper() {
    }
}