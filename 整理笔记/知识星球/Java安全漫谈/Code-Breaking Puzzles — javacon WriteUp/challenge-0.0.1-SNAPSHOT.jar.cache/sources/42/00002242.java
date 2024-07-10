package org.springframework.objenesis.instantiator.util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.ProtectionDomain;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/objenesis/instantiator/util/ClassDefinitionUtils.class */
public final class ClassDefinitionUtils {
    public static final byte OPS_aload_0 = 42;
    public static final byte OPS_invokespecial = -73;
    public static final byte OPS_return = -79;
    public static final byte OPS_new = -69;
    public static final byte OPS_dup = 89;
    public static final byte OPS_areturn = -80;
    public static final int CONSTANT_Utf8 = 1;
    public static final int CONSTANT_Integer = 3;
    public static final int CONSTANT_Float = 4;
    public static final int CONSTANT_Long = 5;
    public static final int CONSTANT_Double = 6;
    public static final int CONSTANT_Class = 7;
    public static final int CONSTANT_String = 8;
    public static final int CONSTANT_Fieldref = 9;
    public static final int CONSTANT_Methodref = 10;
    public static final int CONSTANT_InterfaceMethodref = 11;
    public static final int CONSTANT_NameAndType = 12;
    public static final int CONSTANT_MethodHandle = 15;
    public static final int CONSTANT_MethodType = 16;
    public static final int CONSTANT_InvokeDynamic = 18;
    public static final int ACC_PUBLIC = 1;
    public static final int ACC_FINAL = 16;
    public static final int ACC_SUPER = 32;
    public static final int ACC_INTERFACE = 512;
    public static final int ACC_ABSTRACT = 1024;
    public static final int ACC_SYNTHETIC = 4096;
    public static final int ACC_ANNOTATION = 8192;
    public static final int ACC_ENUM = 16384;
    public static final byte[] MAGIC = {-54, -2, -70, -66};
    public static final byte[] VERSION = {0, 0, 0, 49};
    private static final ProtectionDomain PROTECTION_DOMAIN;

    static {
        ClassDefinitionUtils.class.getClass();
        PROTECTION_DOMAIN = (ProtectionDomain) AccessController.doPrivileged(this::getProtectionDomain);
    }

    private ClassDefinitionUtils() {
    }

    public static <T> Class<T> defineClass(String className, byte[] b, Class<?> neighbor, ClassLoader loader) throws Exception {
        Class<T> c = (Class<T>) DefineClassHelper.defineClass(className, b, 0, b.length, neighbor, loader, PROTECTION_DOMAIN);
        Class.forName(className, true, loader);
        return c;
    }

    public static byte[] readClass(String className) throws IOException {
        byte[] b = new byte[2500];
        InputStream in = ClassDefinitionUtils.class.getClassLoader().getResourceAsStream(ClassUtils.classNameToResource(className));
        Throwable th = null;
        try {
            int length = in.read(b);
            if (in != null) {
                if (0 != 0) {
                    try {
                        in.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                } else {
                    in.close();
                }
            }
            if (length >= 2500) {
                throw new IllegalArgumentException("The class is longer that 2500 bytes which is currently unsupported");
            }
            byte[] copy = new byte[length];
            System.arraycopy(b, 0, copy, 0, length);
            return copy;
        } finally {
        }
    }

    public static void writeClass(String fileName, byte[] bytes) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName));
        Throwable th = null;
        try {
            out.write(bytes);
            if (out != null) {
                if (0 != 0) {
                    try {
                        out.close();
                        return;
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                        return;
                    }
                }
                out.close();
            }
        } catch (Throwable th3) {
            try {
                throw th3;
            } catch (Throwable th4) {
                if (out != null) {
                    if (th3 != null) {
                        try {
                            out.close();
                        } catch (Throwable th5) {
                            th3.addSuppressed(th5);
                        }
                    } else {
                        out.close();
                    }
                }
                throw th4;
            }
        }
    }
}