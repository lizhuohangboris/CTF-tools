package org.springframework.aop.support;

import java.io.Serializable;
import org.springframework.aop.ClassFilter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/support/RootClassFilter.class */
public class RootClassFilter implements ClassFilter, Serializable {
    private Class<?> clazz;

    public RootClassFilter(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override // org.springframework.aop.ClassFilter
    public boolean matches(Class<?> candidate) {
        return this.clazz.isAssignableFrom(candidate);
    }
}