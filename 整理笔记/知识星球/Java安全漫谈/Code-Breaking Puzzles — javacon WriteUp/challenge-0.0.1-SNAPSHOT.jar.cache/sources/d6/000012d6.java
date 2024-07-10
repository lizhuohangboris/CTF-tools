package org.springframework.aop.framework;

import org.aopalliance.intercept.Interceptor;
import org.springframework.aop.TargetSource;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/ProxyFactory.class */
public class ProxyFactory extends ProxyCreatorSupport {
    public ProxyFactory() {
    }

    public ProxyFactory(Object target) {
        setTarget(target);
        setInterfaces(ClassUtils.getAllInterfaces(target));
    }

    public ProxyFactory(Class<?>... proxyInterfaces) {
        setInterfaces(proxyInterfaces);
    }

    public ProxyFactory(Class<?> proxyInterface, Interceptor interceptor) {
        addInterface(proxyInterface);
        addAdvice(interceptor);
    }

    public ProxyFactory(Class<?> proxyInterface, TargetSource targetSource) {
        addInterface(proxyInterface);
        setTargetSource(targetSource);
    }

    public Object getProxy() {
        return createAopProxy().getProxy();
    }

    public Object getProxy(@Nullable ClassLoader classLoader) {
        return createAopProxy().getProxy(classLoader);
    }

    public static <T> T getProxy(Class<T> proxyInterface, Interceptor interceptor) {
        return (T) new ProxyFactory((Class<?>) proxyInterface, interceptor).getProxy();
    }

    public static <T> T getProxy(Class<T> proxyInterface, TargetSource targetSource) {
        return (T) new ProxyFactory((Class<?>) proxyInterface, targetSource).getProxy();
    }

    public static Object getProxy(TargetSource targetSource) {
        if (targetSource.getTargetClass() == null) {
            throw new IllegalArgumentException("Cannot create class proxy for TargetSource with null target class");
        }
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetSource(targetSource);
        proxyFactory.setProxyTargetClass(true);
        return proxyFactory.getProxy();
    }
}