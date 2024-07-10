package org.springframework.cglib.core;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.asm.Attribute;
import org.springframework.asm.Type;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/core/ReflectUtils.class */
public class ReflectUtils {
    private static final Method privateLookupInMethod;
    private static final Method lookupDefineClassMethod;
    private static final Method classLoaderDefineClassMethod;
    private static final ProtectionDomain PROTECTION_DOMAIN;
    private static final Throwable THROWABLE;
    private static final String[] CGLIB_PACKAGES;
    private static final Map primitives = new HashMap(8);
    private static final Map transforms = new HashMap(8);
    private static final ClassLoader defaultLoader = ReflectUtils.class.getClassLoader();
    private static final List<Method> OBJECT_METHODS = new ArrayList();

    private ReflectUtils() {
    }

    static {
        Method privateLookupIn;
        Method lookupDefineClass;
        Method classLoaderDefineClass;
        ProtectionDomain protectionDomain;
        Throwable throwable = null;
        try {
            privateLookupIn = (Method) AccessController.doPrivileged(new PrivilegedExceptionAction() { // from class: org.springframework.cglib.core.ReflectUtils.1
                @Override // java.security.PrivilegedExceptionAction
                public Object run() throws Exception {
                    try {
                        return MethodHandles.class.getMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
                    } catch (NoSuchMethodException e) {
                        return null;
                    }
                }
            });
            lookupDefineClass = (Method) AccessController.doPrivileged(new PrivilegedExceptionAction() { // from class: org.springframework.cglib.core.ReflectUtils.2
                @Override // java.security.PrivilegedExceptionAction
                public Object run() throws Exception {
                    try {
                        return MethodHandles.Lookup.class.getMethod("defineClass", byte[].class);
                    } catch (NoSuchMethodException e) {
                        return null;
                    }
                }
            });
            classLoaderDefineClass = (Method) AccessController.doPrivileged(new PrivilegedExceptionAction() { // from class: org.springframework.cglib.core.ReflectUtils.3
                @Override // java.security.PrivilegedExceptionAction
                public Object run() throws Exception {
                    return ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE, ProtectionDomain.class);
                }
            });
            protectionDomain = getProtectionDomain(ReflectUtils.class);
            AccessController.doPrivileged(new PrivilegedExceptionAction() { // from class: org.springframework.cglib.core.ReflectUtils.4
                @Override // java.security.PrivilegedExceptionAction
                public Object run() throws Exception {
                    Method[] methods = Object.class.getDeclaredMethods();
                    for (Method method : methods) {
                        if (!"finalize".equals(method.getName()) && (method.getModifiers() & 24) <= 0) {
                            ReflectUtils.OBJECT_METHODS.add(method);
                        }
                    }
                    return null;
                }
            });
        } catch (Throwable t) {
            privateLookupIn = null;
            lookupDefineClass = null;
            classLoaderDefineClass = null;
            protectionDomain = null;
            throwable = t;
        }
        privateLookupInMethod = privateLookupIn;
        lookupDefineClassMethod = lookupDefineClass;
        classLoaderDefineClassMethod = classLoaderDefineClass;
        PROTECTION_DOMAIN = protectionDomain;
        THROWABLE = throwable;
        CGLIB_PACKAGES = new String[]{"java.lang"};
        primitives.put("byte", Byte.TYPE);
        primitives.put("char", Character.TYPE);
        primitives.put("double", Double.TYPE);
        primitives.put("float", Float.TYPE);
        primitives.put("int", Integer.TYPE);
        primitives.put("long", Long.TYPE);
        primitives.put("short", Short.TYPE);
        primitives.put("boolean", Boolean.TYPE);
        transforms.put("byte", "B");
        transforms.put("char", "C");
        transforms.put("double", "D");
        transforms.put("float", "F");
        transforms.put("int", "I");
        transforms.put("long", "J");
        transforms.put("short", "S");
        transforms.put("boolean", "Z");
    }

    public static ProtectionDomain getProtectionDomain(final Class source) {
        if (source == null) {
            return null;
        }
        return (ProtectionDomain) AccessController.doPrivileged(new PrivilegedAction() { // from class: org.springframework.cglib.core.ReflectUtils.5
            @Override // java.security.PrivilegedAction
            public Object run() {
                return source.getProtectionDomain();
            }
        });
    }

    public static Type[] getExceptionTypes(Member member) {
        if (member instanceof Method) {
            return TypeUtils.getTypes(((Method) member).getExceptionTypes());
        }
        if (member instanceof Constructor) {
            return TypeUtils.getTypes(((Constructor) member).getExceptionTypes());
        }
        throw new IllegalArgumentException("Cannot get exception types of a field");
    }

    public static Signature getSignature(Member member) {
        if (member instanceof Method) {
            return new Signature(member.getName(), Type.getMethodDescriptor((Method) member));
        }
        if (member instanceof Constructor) {
            Type[] types = TypeUtils.getTypes(((Constructor) member).getParameterTypes());
            return new Signature(Constants.CONSTRUCTOR_NAME, Type.getMethodDescriptor(Type.VOID_TYPE, types));
        }
        throw new IllegalArgumentException("Cannot get signature of a field");
    }

    public static Constructor findConstructor(String desc) {
        return findConstructor(desc, defaultLoader);
    }

    public static Constructor findConstructor(String desc, ClassLoader loader) {
        try {
            int lparen = desc.indexOf(40);
            String className = desc.substring(0, lparen).trim();
            return getClass(className, loader).getConstructor(parseTypes(desc, loader));
        } catch (ClassNotFoundException | NoSuchMethodException ex) {
            throw new CodeGenerationException(ex);
        }
    }

    public static Method findMethod(String desc) {
        return findMethod(desc, defaultLoader);
    }

    public static Method findMethod(String desc, ClassLoader loader) {
        try {
            int lparen = desc.indexOf(40);
            int dot = desc.lastIndexOf(46, lparen);
            String className = desc.substring(0, dot).trim();
            String methodName = desc.substring(dot + 1, lparen).trim();
            return getClass(className, loader).getDeclaredMethod(methodName, parseTypes(desc, loader));
        } catch (ClassNotFoundException | NoSuchMethodException ex) {
            throw new CodeGenerationException(ex);
        }
    }

    private static Class[] parseTypes(String desc, ClassLoader loader) throws ClassNotFoundException {
        int start;
        int lparen = desc.indexOf(40);
        int rparen = desc.indexOf(41, lparen);
        List params = new ArrayList();
        int i = lparen;
        while (true) {
            start = i + 1;
            int comma = desc.indexOf(44, start);
            if (comma < 0) {
                break;
            }
            params.add(desc.substring(start, comma).trim());
            i = comma;
        }
        if (start < rparen) {
            params.add(desc.substring(start, rparen).trim());
        }
        Class[] types = new Class[params.size()];
        for (int i2 = 0; i2 < types.length; i2++) {
            types[i2] = getClass((String) params.get(i2), loader);
        }
        return types;
    }

    private static Class getClass(String className, ClassLoader loader) throws ClassNotFoundException {
        return getClass(className, loader, CGLIB_PACKAGES);
    }

    private static Class getClass(String className, ClassLoader loader, String[] packages) throws ClassNotFoundException {
        int dimensions = 0;
        int index = 0;
        while (true) {
            int indexOf = className.indexOf(ClassUtils.ARRAY_SUFFIX, index) + 1;
            index = indexOf;
            if (indexOf <= 0) {
                break;
            }
            dimensions++;
        }
        StringBuffer brackets = new StringBuffer(className.length() - dimensions);
        for (int i = 0; i < dimensions; i++) {
            brackets.append('[');
        }
        String className2 = className.substring(0, className.length() - (2 * dimensions));
        String prefix = dimensions > 0 ? ((Object) brackets) + "L" : "";
        String suffix = dimensions > 0 ? ";" : "";
        try {
            return Class.forName(prefix + className2 + suffix, false, loader);
        } catch (ClassNotFoundException e) {
            for (int i2 = 0; i2 < packages.length; i2++) {
                try {
                    return Class.forName(prefix + packages[i2] + '.' + className2 + suffix, false, loader);
                } catch (ClassNotFoundException e2) {
                }
            }
            if (dimensions == 0) {
                Class c = (Class) primitives.get(className2);
                if (c != null) {
                    return c;
                }
            } else {
                String transform = (String) transforms.get(className2);
                if (transform != null) {
                    try {
                        return Class.forName(((Object) brackets) + transform, false, loader);
                    } catch (ClassNotFoundException e3) {
                        throw new ClassNotFoundException(className);
                    }
                }
            }
            throw new ClassNotFoundException(className);
        }
    }

    public static Object newInstance(Class type) {
        return newInstance(type, Constants.EMPTY_CLASS_ARRAY, null);
    }

    public static Object newInstance(Class type, Class[] parameterTypes, Object[] args) {
        return newInstance(getConstructor(type, parameterTypes), args);
    }

    public static Object newInstance(Constructor cstruct, Object[] args) {
        boolean flag = cstruct.isAccessible();
        try {
            if (!flag) {
                try {
                    try {
                        try {
                            cstruct.setAccessible(true);
                        } catch (InvocationTargetException e) {
                            throw new CodeGenerationException(e.getTargetException());
                        }
                    } catch (IllegalAccessException e2) {
                        throw new CodeGenerationException(e2);
                    }
                } catch (InstantiationException e3) {
                    throw new CodeGenerationException(e3);
                }
            }
            Object result = cstruct.newInstance(args);
            if (!flag) {
                cstruct.setAccessible(flag);
            }
            return result;
        } catch (Throwable th) {
            if (!flag) {
                cstruct.setAccessible(flag);
            }
            throw th;
        }
    }

    public static Constructor getConstructor(Class type, Class[] parameterTypes) {
        try {
            Constructor constructor = type.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException e) {
            throw new CodeGenerationException(e);
        }
    }

    public static String[] getNames(Class[] classes) {
        if (classes == null) {
            return null;
        }
        String[] names = new String[classes.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = classes[i].getName();
        }
        return names;
    }

    public static Class[] getClasses(Object[] objects) {
        Class[] classes = new Class[objects.length];
        for (int i = 0; i < objects.length; i++) {
            classes[i] = objects[i].getClass();
        }
        return classes;
    }

    public static Method findNewInstance(Class iface) {
        Method m = findInterfaceMethod(iface);
        if (!m.getName().equals("newInstance")) {
            throw new IllegalArgumentException(iface + " missing newInstance method");
        }
        return m;
    }

    public static Method[] getPropertyMethods(PropertyDescriptor[] properties, boolean read, boolean write) {
        Set methods = new HashSet();
        for (PropertyDescriptor pd : properties) {
            if (read) {
                methods.add(pd.getReadMethod());
            }
            if (write) {
                methods.add(pd.getWriteMethod());
            }
        }
        methods.remove(null);
        return (Method[]) methods.toArray(new Method[methods.size()]);
    }

    public static PropertyDescriptor[] getBeanProperties(Class type) {
        return getPropertiesHelper(type, true, true);
    }

    public static PropertyDescriptor[] getBeanGetters(Class type) {
        return getPropertiesHelper(type, true, false);
    }

    public static PropertyDescriptor[] getBeanSetters(Class type) {
        return getPropertiesHelper(type, false, true);
    }

    private static PropertyDescriptor[] getPropertiesHelper(Class type, boolean read, boolean write) {
        try {
            BeanInfo info = Introspector.getBeanInfo(type, Object.class);
            PropertyDescriptor[] all = info.getPropertyDescriptors();
            if (read && write) {
                return all;
            }
            List properties = new ArrayList(all.length);
            for (PropertyDescriptor pd : all) {
                if ((read && pd.getReadMethod() != null) || (write && pd.getWriteMethod() != null)) {
                    properties.add(pd);
                }
            }
            return (PropertyDescriptor[]) properties.toArray(new PropertyDescriptor[properties.size()]);
        } catch (IntrospectionException e) {
            throw new CodeGenerationException(e);
        }
    }

    public static Method findDeclaredMethod(Class type, String methodName, Class[] parameterTypes) throws NoSuchMethodException {
        Class cls = type;
        while (true) {
            Class cl = cls;
            if (cl != null) {
                try {
                    return cl.getDeclaredMethod(methodName, parameterTypes);
                } catch (NoSuchMethodException e) {
                    cls = cl.getSuperclass();
                }
            } else {
                throw new NoSuchMethodException(methodName);
            }
        }
    }

    public static List addAllMethods(Class type, List list) {
        if (type == Object.class) {
            list.addAll(OBJECT_METHODS);
        } else {
            list.addAll(Arrays.asList(type.getDeclaredMethods()));
        }
        Class superclass = type.getSuperclass();
        if (superclass != null) {
            addAllMethods(superclass, list);
        }
        Class[] interfaces = type.getInterfaces();
        for (Class cls : interfaces) {
            addAllMethods(cls, list);
        }
        return list;
    }

    public static List addAllInterfaces(Class type, List list) {
        Class superclass = type.getSuperclass();
        if (superclass != null) {
            list.addAll(Arrays.asList(type.getInterfaces()));
            addAllInterfaces(superclass, list);
        }
        return list;
    }

    public static Method findInterfaceMethod(Class iface) {
        if (!iface.isInterface()) {
            throw new IllegalArgumentException(iface + " is not an interface");
        }
        Method[] methods = iface.getDeclaredMethods();
        if (methods.length != 1) {
            throw new IllegalArgumentException("expecting exactly 1 method in " + iface);
        }
        return methods[0];
    }

    public static Class defineClass(String className, byte[] b, ClassLoader loader) throws Exception {
        return defineClass(className, b, loader, null, null);
    }

    public static Class defineClass(String className, byte[] b, ClassLoader loader, ProtectionDomain protectionDomain) throws Exception {
        return defineClass(className, b, loader, protectionDomain, null);
    }

    public static Class defineClass(String className, byte[] b, ClassLoader loader, ProtectionDomain protectionDomain, Class<?> contextClass) throws Exception {
        Class c = null;
        if (contextClass != null && privateLookupInMethod != null && lookupDefineClassMethod != null) {
            try {
                MethodHandles.Lookup lookup = (MethodHandles.Lookup) privateLookupInMethod.invoke(null, contextClass, MethodHandles.lookup());
                c = (Class) lookupDefineClassMethod.invoke(lookup, b);
            } catch (InvocationTargetException ex) {
                Throwable target = ex.getTargetException();
                if (target.getClass() != LinkageError.class && target.getClass() != IllegalArgumentException.class) {
                    throw new CodeGenerationException(target);
                }
            } catch (Throwable ex2) {
                throw new CodeGenerationException(ex2);
            }
        }
        if (protectionDomain == null) {
            protectionDomain = PROTECTION_DOMAIN;
        }
        if (c == null) {
            if (classLoaderDefineClassMethod != null) {
                Object[] args = {className, b, 0, Integer.valueOf(b.length), protectionDomain};
                try {
                    if (!classLoaderDefineClassMethod.isAccessible()) {
                        classLoaderDefineClassMethod.setAccessible(true);
                    }
                    c = (Class) classLoaderDefineClassMethod.invoke(loader, args);
                } catch (InvocationTargetException ex3) {
                    throw new CodeGenerationException(ex3.getTargetException());
                } catch (Throwable ex4) {
                    throw new CodeGenerationException(ex4);
                }
            } else {
                throw new CodeGenerationException(THROWABLE);
            }
        }
        Class.forName(className, true, loader);
        return c;
    }

    public static int findPackageProtected(Class[] classes) {
        for (int i = 0; i < classes.length; i++) {
            if (!Modifier.isPublic(classes[i].getModifiers())) {
                return i;
            }
        }
        return 0;
    }

    public static MethodInfo getMethodInfo(final Member member, final int modifiers) {
        final Signature sig = getSignature(member);
        return new MethodInfo() { // from class: org.springframework.cglib.core.ReflectUtils.6
            private ClassInfo ci;

            @Override // org.springframework.cglib.core.MethodInfo
            public ClassInfo getClassInfo() {
                if (this.ci == null) {
                    this.ci = ReflectUtils.getClassInfo(member.getDeclaringClass());
                }
                return this.ci;
            }

            @Override // org.springframework.cglib.core.MethodInfo
            public int getModifiers() {
                return modifiers;
            }

            @Override // org.springframework.cglib.core.MethodInfo
            public Signature getSignature() {
                return sig;
            }

            @Override // org.springframework.cglib.core.MethodInfo
            public Type[] getExceptionTypes() {
                return ReflectUtils.getExceptionTypes(member);
            }

            public Attribute getAttribute() {
                return null;
            }
        };
    }

    public static MethodInfo getMethodInfo(Member member) {
        return getMethodInfo(member, member.getModifiers());
    }

    public static ClassInfo getClassInfo(final Class clazz) {
        final Type type = Type.getType(clazz);
        final Type sc = clazz.getSuperclass() == null ? null : Type.getType(clazz.getSuperclass());
        return new ClassInfo() { // from class: org.springframework.cglib.core.ReflectUtils.7
            @Override // org.springframework.cglib.core.ClassInfo
            public Type getType() {
                return Type.this;
            }

            @Override // org.springframework.cglib.core.ClassInfo
            public Type getSuperType() {
                return sc;
            }

            @Override // org.springframework.cglib.core.ClassInfo
            public Type[] getInterfaces() {
                return TypeUtils.getTypes(clazz.getInterfaces());
            }

            @Override // org.springframework.cglib.core.ClassInfo
            public int getModifiers() {
                return clazz.getModifiers();
            }
        };
    }

    public static Method[] findMethods(String[] namesAndDescriptors, Method[] methods) {
        Map map = new HashMap();
        for (Method method : methods) {
            map.put(method.getName() + Type.getMethodDescriptor(method), method);
        }
        Method[] result = new Method[namesAndDescriptors.length / 2];
        for (int i = 0; i < result.length; i++) {
            result[i] = (Method) map.get(namesAndDescriptors[i * 2] + namesAndDescriptors[(i * 2) + 1]);
            if (result[i] == null) {
            }
        }
        return result;
    }
}