package org.springframework.cglib.core;

import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/core/DefaultNamingPolicy.class */
public class DefaultNamingPolicy implements NamingPolicy {
    public static final DefaultNamingPolicy INSTANCE = new DefaultNamingPolicy();
    private static final boolean STRESS_HASH_CODE = Boolean.getBoolean("org.springframework.cglib.test.stressHashCodes");

    @Override // org.springframework.cglib.core.NamingPolicy
    public String getClassName(String prefix, String source, Object key, Predicate names) {
        if (prefix == null) {
            prefix = "org.springframework.cglib.empty.Object";
        } else if (prefix.startsWith("java")) {
            prefix = PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX + prefix;
        }
        String base = prefix + ClassUtils.CGLIB_CLASS_SEPARATOR + source.substring(source.lastIndexOf(46) + 1) + getTag() + ClassUtils.CGLIB_CLASS_SEPARATOR + Integer.toHexString(STRESS_HASH_CODE ? 0 : key.hashCode());
        String attempt = base;
        int index = 2;
        while (names.evaluate(attempt)) {
            int i = index;
            index++;
            attempt = base + "_" + i;
        }
        return attempt;
    }

    protected String getTag() {
        return "ByCGLIB";
    }

    public int hashCode() {
        return getTag().hashCode();
    }

    @Override // org.springframework.cglib.core.NamingPolicy
    public boolean equals(Object o) {
        return (o instanceof DefaultNamingPolicy) && ((DefaultNamingPolicy) o).getTag().equals(getTag());
    }
}