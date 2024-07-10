package org.springframework.cglib.proxy;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/proxy/LazyLoader.class */
public interface LazyLoader extends Callback {
    Object loadObject() throws Exception;
}