package org.springframework.expression;

import java.lang.reflect.Method;
import java.util.List;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/MethodFilter.class */
public interface MethodFilter {
    List<Method> filter(List<Method> list);
}