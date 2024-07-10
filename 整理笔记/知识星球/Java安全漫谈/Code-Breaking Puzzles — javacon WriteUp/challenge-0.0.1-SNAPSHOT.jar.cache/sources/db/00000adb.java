package org.apache.el.util;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import javax.el.ELException;
import org.apache.el.lang.ELSupport;
import org.apache.el.lang.EvaluationContext;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/util/ReflectionUtil.class */
public class ReflectionUtil {
    protected static final String[] PRIMITIVE_NAMES = {"boolean", "byte", "char", "double", "float", "int", "long", "short", "void"};
    protected static final Class<?>[] PRIMITIVES = {Boolean.TYPE, Byte.TYPE, Character.TYPE, Double.TYPE, Float.TYPE, Integer.TYPE, Long.TYPE, Short.TYPE, Void.TYPE};

    private ReflectionUtil() {
    }

    public static Class<?> forName(String name) throws ClassNotFoundException {
        if (null == name || "".equals(name)) {
            return null;
        }
        Class<?> c = forNamePrimitive(name);
        if (c == null) {
            if (name.endsWith(ClassUtils.ARRAY_SUFFIX)) {
                String nc = name.substring(0, name.length() - 2);
                c = Array.newInstance(Class.forName(nc, true, getContextClassLoader()), 0).getClass();
            } else {
                c = Class.forName(name, true, getContextClassLoader());
            }
        }
        return c;
    }

    protected static Class<?> forNamePrimitive(String name) {
        int p;
        if (name.length() <= 8 && (p = Arrays.binarySearch(PRIMITIVE_NAMES, name)) >= 0) {
            return PRIMITIVES[p];
        }
        return null;
    }

    public static Class<?>[] toTypeArray(String[] s) throws ClassNotFoundException {
        if (s == null) {
            return null;
        }
        Class<?>[] c = new Class[s.length];
        for (int i = 0; i < s.length; i++) {
            c[i] = forName(s[i]);
        }
        return c;
    }

    public static String[] toTypeNameArray(Class<?>[] c) {
        if (c == null) {
            return null;
        }
        String[] s = new String[c.length];
        for (int i = 0; i < c.length; i++) {
            s[i] = c[i].getName();
        }
        return s;
    }

    /* JADX WARN: Code restructure failed: missing block: B:247:0x0155, code lost:
        r25 = r25 + 1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.lang.reflect.Method getMethod(org.apache.el.lang.EvaluationContext r9, java.lang.Object r10, java.lang.Object r11, java.lang.Class<?>[] r12, java.lang.Object[] r13) throws javax.el.MethodNotFoundException {
        /*
            Method dump skipped, instructions count: 829
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.el.util.ReflectionUtil.getMethod(org.apache.el.lang.EvaluationContext, java.lang.Object, java.lang.Object, java.lang.Class[], java.lang.Object[]):java.lang.reflect.Method");
    }

    private static Method resolveAmbiguousMethod(Set<Method> candidates, Class<?>[] paramTypes) {
        Method m = candidates.iterator().next();
        int nonMatchIndex = 0;
        Class<?> nonMatchClass = null;
        int i = 0;
        while (true) {
            if (i < paramTypes.length) {
                if (m.getParameterTypes()[i] == paramTypes[i]) {
                    i++;
                } else {
                    nonMatchIndex = i;
                    nonMatchClass = paramTypes[i];
                    break;
                }
            } else {
                break;
            }
        }
        if (nonMatchClass == null) {
            return null;
        }
        for (Method c : candidates) {
            if (c.getParameterTypes()[nonMatchIndex] == paramTypes[nonMatchIndex]) {
                return null;
            }
        }
        Class<?> superclass = nonMatchClass.getSuperclass();
        while (true) {
            Class<?> superClass = superclass;
            if (superClass != null) {
                for (Method c2 : candidates) {
                    if (c2.getParameterTypes()[nonMatchIndex].equals(superClass)) {
                        return c2;
                    }
                }
                superclass = superClass.getSuperclass();
            } else {
                Method match = null;
                if (Number.class.isAssignableFrom(nonMatchClass)) {
                    Iterator<Method> it = candidates.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        Method c3 = it.next();
                        Class<?> candidateType = c3.getParameterTypes()[nonMatchIndex];
                        if (Number.class.isAssignableFrom(candidateType) || candidateType.isPrimitive()) {
                            if (match == null) {
                                match = c3;
                            } else {
                                match = null;
                                break;
                            }
                        }
                    }
                }
                return match;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r5v3 */
    /* JADX WARN: Type inference failed for: r5v4 */
    /* JADX WARN: Type inference failed for: r5v5 */
    /* JADX WARN: Type inference failed for: r5v6 */
    /* JADX WARN: Type inference failed for: r5v7 */
    /* JADX WARN: Type inference failed for: r5v8 */
    /* JADX WARN: Type inference failed for: r5v9 */
    private static boolean isAssignableFrom(Class<?> src, Class<?> target) {
        Class<?> targetClass;
        if (src == null) {
            return true;
        }
        if (target.isPrimitive()) {
            if (target == Boolean.TYPE) {
                targetClass = Boolean.class;
            } else if (target == Character.TYPE) {
                targetClass = Character.class;
            } else if (target == Byte.TYPE) {
                targetClass = Byte.class;
            } else if (target == Short.TYPE) {
                targetClass = Short.class;
            } else if (target == Integer.TYPE) {
                targetClass = Integer.class;
            } else if (target == Long.TYPE) {
                targetClass = Long.class;
            } else if (target == Float.TYPE) {
                targetClass = Float.class;
            } else {
                targetClass = Double.class;
            }
        } else {
            targetClass = target;
        }
        return targetClass.isAssignableFrom(src);
    }

    private static boolean isCoercibleFrom(EvaluationContext ctx, Object src, Class<?> target) {
        try {
            ELSupport.coerceToType(ctx, src, target);
            return true;
        } catch (ELException e) {
            return false;
        }
    }

    private static Method getMethod(Class<?> type, Method m) {
        Method mp;
        if (m == null || Modifier.isPublic(type.getModifiers())) {
            return m;
        }
        Class<?>[] inf = type.getInterfaces();
        for (Class<?> cls : inf) {
            try {
                Method mp2 = cls.getMethod(m.getName(), m.getParameterTypes());
                mp = getMethod(mp2.getDeclaringClass(), mp2);
            } catch (NoSuchMethodException e) {
            }
            if (mp != null) {
                return mp;
            }
        }
        Class<?> sup = type.getSuperclass();
        if (sup != null) {
            try {
                Method mp3 = sup.getMethod(m.getName(), m.getParameterTypes());
                Method mp4 = getMethod(mp3.getDeclaringClass(), mp3);
                if (mp4 != null) {
                    return mp4;
                }
                return null;
            } catch (NoSuchMethodException e2) {
                return null;
            }
        }
        return null;
    }

    private static final String paramString(Class<?>[] types) {
        if (types != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < types.length; i++) {
                if (types[i] == null) {
                    sb.append("null, ");
                } else {
                    sb.append(types[i].getName()).append(", ");
                }
            }
            if (sb.length() > 2) {
                sb.setLength(sb.length() - 2);
            }
            return sb.toString();
        }
        return null;
    }

    private static ClassLoader getContextClassLoader() {
        ClassLoader tccl;
        if (System.getSecurityManager() != null) {
            PrivilegedAction<ClassLoader> pa = new PrivilegedGetTccl();
            tccl = (ClassLoader) AccessController.doPrivileged(pa);
        } else {
            tccl = Thread.currentThread().getContextClassLoader();
        }
        return tccl;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/util/ReflectionUtil$PrivilegedGetTccl.class */
    public static class PrivilegedGetTccl implements PrivilegedAction<ClassLoader> {
        private PrivilegedGetTccl() {
        }

        @Override // java.security.PrivilegedAction
        public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/util/ReflectionUtil$MatchResult.class */
    public static class MatchResult implements Comparable<MatchResult> {
        private final int exact;
        private final int assignable;
        private final int coercible;
        private final boolean bridge;

        public MatchResult(int exact, int assignable, int coercible, boolean bridge) {
            this.exact = exact;
            this.assignable = assignable;
            this.coercible = coercible;
            this.bridge = bridge;
        }

        public int getExact() {
            return this.exact;
        }

        public int getAssignable() {
            return this.assignable;
        }

        public int getCoercible() {
            return this.coercible;
        }

        public boolean isBridge() {
            return this.bridge;
        }

        @Override // java.lang.Comparable
        public int compareTo(MatchResult o) {
            int cmp = Integer.compare(getExact(), o.getExact());
            if (cmp == 0) {
                cmp = Integer.compare(getAssignable(), o.getAssignable());
                if (cmp == 0) {
                    cmp = Integer.compare(getCoercible(), o.getCoercible());
                    if (cmp == 0) {
                        cmp = Boolean.compare(o.isBridge(), isBridge());
                    }
                }
            }
            return cmp;
        }

        public boolean equals(Object o) {
            return o == this || (null != o && getClass().equals(o.getClass()) && ((MatchResult) o).getExact() == getExact() && ((MatchResult) o).getAssignable() == getAssignable() && ((MatchResult) o).getCoercible() == getCoercible() && ((MatchResult) o).isBridge() == isBridge());
        }

        public int hashCode() {
            return (((isBridge() ? 16777216 : 0) ^ (getExact() << 16)) ^ (getAssignable() << 8)) ^ getCoercible();
        }
    }
}