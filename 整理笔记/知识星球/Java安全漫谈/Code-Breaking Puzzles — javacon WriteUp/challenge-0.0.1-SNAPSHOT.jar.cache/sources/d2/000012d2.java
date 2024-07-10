package org.springframework.aop.framework;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.AopInvocationException;
import org.springframework.aop.RawTargetAccess;
import org.springframework.aop.TargetSource;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.DecoratingProxy;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/JdkDynamicAopProxy.class */
final class JdkDynamicAopProxy implements AopProxy, InvocationHandler, Serializable {
    private static final long serialVersionUID = 5531744639992436476L;
    private static final Log logger = LogFactory.getLog(JdkDynamicAopProxy.class);
    private final AdvisedSupport advised;
    private boolean equalsDefined;
    private boolean hashCodeDefined;

    public JdkDynamicAopProxy(AdvisedSupport config) throws AopConfigException {
        Assert.notNull(config, "AdvisedSupport must not be null");
        if (config.getAdvisors().length == 0 && config.getTargetSource() == AdvisedSupport.EMPTY_TARGET_SOURCE) {
            throw new AopConfigException("No advisors and no TargetSource specified");
        }
        this.advised = config;
    }

    @Override // org.springframework.aop.framework.AopProxy
    public Object getProxy() {
        return getProxy(ClassUtils.getDefaultClassLoader());
    }

    @Override // org.springframework.aop.framework.AopProxy
    public Object getProxy(@Nullable ClassLoader classLoader) {
        if (logger.isTraceEnabled()) {
            logger.trace("Creating JDK dynamic proxy: " + this.advised.getTargetSource());
        }
        Class<?>[] proxiedInterfaces = AopProxyUtils.completeProxiedInterfaces(this.advised, true);
        findDefinedEqualsAndHashCodeMethods(proxiedInterfaces);
        return Proxy.newProxyInstance(classLoader, proxiedInterfaces, this);
    }

    private void findDefinedEqualsAndHashCodeMethods(Class<?>[] proxiedInterfaces) {
        for (Class<?> proxiedInterface : proxiedInterfaces) {
            Method[] methods = proxiedInterface.getDeclaredMethods();
            for (Method method : methods) {
                if (AopUtils.isEqualsMethod(method)) {
                    this.equalsDefined = true;
                }
                if (AopUtils.isHashCodeMethod(method)) {
                    this.hashCodeDefined = true;
                }
                if (this.equalsDefined && this.hashCodeDefined) {
                    return;
                }
            }
        }
    }

    @Override // java.lang.reflect.InvocationHandler
    @Nullable
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object retVal;
        Object oldProxy = null;
        boolean setProxyContext = false;
        TargetSource targetSource = this.advised.targetSource;
        try {
            if (!this.equalsDefined && AopUtils.isEqualsMethod(method)) {
                Boolean valueOf = Boolean.valueOf(equals(args[0]));
                if (0 != 0 && !targetSource.isStatic()) {
                    targetSource.releaseTarget(null);
                }
                if (0 != 0) {
                    AopContext.setCurrentProxy(null);
                }
                return valueOf;
            } else if (!this.hashCodeDefined && AopUtils.isHashCodeMethod(method)) {
                Integer valueOf2 = Integer.valueOf(hashCode());
                if (0 != 0 && !targetSource.isStatic()) {
                    targetSource.releaseTarget(null);
                }
                if (0 != 0) {
                    AopContext.setCurrentProxy(null);
                }
                return valueOf2;
            } else if (method.getDeclaringClass() == DecoratingProxy.class) {
                Class<?> ultimateTargetClass = AopProxyUtils.ultimateTargetClass(this.advised);
                if (0 != 0 && !targetSource.isStatic()) {
                    targetSource.releaseTarget(null);
                }
                if (0 != 0) {
                    AopContext.setCurrentProxy(null);
                }
                return ultimateTargetClass;
            } else if (!this.advised.opaque && method.getDeclaringClass().isInterface() && method.getDeclaringClass().isAssignableFrom(Advised.class)) {
                Object invokeJoinpointUsingReflection = AopUtils.invokeJoinpointUsingReflection(this.advised, method, args);
                if (0 != 0 && !targetSource.isStatic()) {
                    targetSource.releaseTarget(null);
                }
                if (0 != 0) {
                    AopContext.setCurrentProxy(null);
                }
                return invokeJoinpointUsingReflection;
            } else {
                if (this.advised.exposeProxy) {
                    oldProxy = AopContext.setCurrentProxy(proxy);
                    setProxyContext = true;
                }
                Object target = targetSource.getTarget();
                Class<?> targetClass = target != null ? target.getClass() : null;
                List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
                if (chain.isEmpty()) {
                    Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args);
                    retVal = AopUtils.invokeJoinpointUsingReflection(target, method, argsToUse);
                } else {
                    MethodInvocation invocation = new ReflectiveMethodInvocation(proxy, target, method, args, targetClass, chain);
                    retVal = invocation.proceed();
                }
                Class<?> returnType = method.getReturnType();
                if (retVal != null && retVal == target && returnType != Object.class && returnType.isInstance(proxy) && !RawTargetAccess.class.isAssignableFrom(method.getDeclaringClass())) {
                    retVal = proxy;
                } else if (retVal == null && returnType != Void.TYPE && returnType.isPrimitive()) {
                    throw new AopInvocationException("Null return value from advice does not match primitive return type for: " + method);
                }
                Object obj = retVal;
                if (target != null && !targetSource.isStatic()) {
                    targetSource.releaseTarget(target);
                }
                if (setProxyContext) {
                    AopContext.setCurrentProxy(oldProxy);
                }
                return obj;
            }
        } catch (Throwable th) {
            if (0 != 0 && !targetSource.isStatic()) {
                targetSource.releaseTarget(null);
            }
            if (0 != 0) {
                AopContext.setCurrentProxy(null);
            }
            throw th;
        }
    }

    public boolean equals(@Nullable Object other) {
        JdkDynamicAopProxy otherProxy;
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (other instanceof JdkDynamicAopProxy) {
            otherProxy = (JdkDynamicAopProxy) other;
        } else if (Proxy.isProxyClass(other.getClass())) {
            InvocationHandler ih = Proxy.getInvocationHandler(other);
            if (!(ih instanceof JdkDynamicAopProxy)) {
                return false;
            }
            otherProxy = (JdkDynamicAopProxy) ih;
        } else {
            return false;
        }
        return AopProxyUtils.equalsInProxy(this.advised, otherProxy.advised);
    }

    public int hashCode() {
        return (JdkDynamicAopProxy.class.hashCode() * 13) + this.advised.getTargetSource().hashCode();
    }
}