package org.springframework.cglib.core;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/core/SpringNamingPolicy.class */
public class SpringNamingPolicy extends DefaultNamingPolicy {
    public static final SpringNamingPolicy INSTANCE = new SpringNamingPolicy();

    @Override // org.springframework.cglib.core.DefaultNamingPolicy
    protected String getTag() {
        return "BySpringCGLIB";
    }
}