package org.springframework.cglib.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/core/MethodInfoTransformer.class */
public class MethodInfoTransformer implements Transformer {
    private static final MethodInfoTransformer INSTANCE = new MethodInfoTransformer();

    public static MethodInfoTransformer getInstance() {
        return INSTANCE;
    }

    @Override // org.springframework.cglib.core.Transformer
    public Object transform(Object value) {
        if (value instanceof Method) {
            return ReflectUtils.getMethodInfo((Method) value);
        }
        if (value instanceof Constructor) {
            return ReflectUtils.getMethodInfo((Constructor) value);
        }
        throw new IllegalArgumentException("cannot get method info for " + value);
    }
}