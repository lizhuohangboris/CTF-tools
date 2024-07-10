package org.springframework.aop.framework;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.AopInvocationException;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.RawTargetAccess;
import org.springframework.aop.TargetSource;
import org.springframework.aop.support.AopUtils;
import org.springframework.cglib.core.ClassGenerator;
import org.springframework.cglib.core.CodeGenerationException;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.Dispatcher;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.Factory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.cglib.transform.impl.UndeclaredThrowableStrategy;
import org.springframework.core.SmartClassLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/CglibAopProxy.class */
public class CglibAopProxy implements AopProxy, Serializable {
    private static final int AOP_PROXY = 0;
    private static final int INVOKE_TARGET = 1;
    private static final int NO_OVERRIDE = 2;
    private static final int DISPATCH_TARGET = 3;
    private static final int DISPATCH_ADVISED = 4;
    private static final int INVOKE_EQUALS = 5;
    private static final int INVOKE_HASHCODE = 6;
    protected static final Log logger = LogFactory.getLog(CglibAopProxy.class);
    private static final Map<Class<?>, Boolean> validatedClasses = new WeakHashMap();
    protected final AdvisedSupport advised;
    @Nullable
    protected Object[] constructorArgs;
    @Nullable
    protected Class<?>[] constructorArgTypes;
    private final transient AdvisedDispatcher advisedDispatcher;
    private transient Map<String, Integer> fixedInterceptorMap = Collections.emptyMap();
    private transient int fixedInterceptorOffset;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/CglibAopProxy$SerializableNoOp.class */
    public static class SerializableNoOp implements NoOp, Serializable {
    }

    public CglibAopProxy(AdvisedSupport config) throws AopConfigException {
        Assert.notNull(config, "AdvisedSupport must not be null");
        if (config.getAdvisors().length == 0 && config.getTargetSource() == AdvisedSupport.EMPTY_TARGET_SOURCE) {
            throw new AopConfigException("No advisors and no TargetSource specified");
        }
        this.advised = config;
        this.advisedDispatcher = new AdvisedDispatcher(this.advised);
    }

    public void setConstructorArguments(@Nullable Object[] constructorArgs, @Nullable Class<?>[] constructorArgTypes) {
        if (constructorArgs == null || constructorArgTypes == null) {
            throw new IllegalArgumentException("Both 'constructorArgs' and 'constructorArgTypes' need to be specified");
        }
        if (constructorArgs.length != constructorArgTypes.length) {
            throw new IllegalArgumentException("Number of 'constructorArgs' (" + constructorArgs.length + ") must match number of 'constructorArgTypes' (" + constructorArgTypes.length + ")");
        }
        this.constructorArgs = constructorArgs;
        this.constructorArgTypes = constructorArgTypes;
    }

    @Override // org.springframework.aop.framework.AopProxy
    public Object getProxy() {
        return getProxy(null);
    }

    @Override // org.springframework.aop.framework.AopProxy
    public Object getProxy(@Nullable ClassLoader classLoader) {
        if (logger.isTraceEnabled()) {
            logger.trace("Creating CGLIB proxy: " + this.advised.getTargetSource());
        }
        try {
            Class<?> rootClass = this.advised.getTargetClass();
            Assert.state(rootClass != null, "Target class must be available for creating a CGLIB proxy");
            Class<?> proxySuperClass = rootClass;
            if (ClassUtils.isCglibProxyClass(rootClass)) {
                proxySuperClass = rootClass.getSuperclass();
                Class<?>[] additionalInterfaces = rootClass.getInterfaces();
                for (Class<?> additionalInterface : additionalInterfaces) {
                    this.advised.addInterface(additionalInterface);
                }
            }
            validateClassIfNecessary(proxySuperClass, classLoader);
            Enhancer enhancer = createEnhancer();
            if (classLoader != null) {
                enhancer.setClassLoader(classLoader);
                if ((classLoader instanceof SmartClassLoader) && ((SmartClassLoader) classLoader).isClassReloadable(proxySuperClass)) {
                    enhancer.setUseCache(false);
                }
            }
            enhancer.setSuperclass(proxySuperClass);
            enhancer.setInterfaces(AopProxyUtils.completeProxiedInterfaces(this.advised));
            enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
            enhancer.setStrategy(new ClassLoaderAwareUndeclaredThrowableStrategy(classLoader));
            Callback[] callbacks = getCallbacks(rootClass);
            Class<?>[] types = new Class[callbacks.length];
            for (int x = 0; x < types.length; x++) {
                types[x] = callbacks[x].getClass();
            }
            enhancer.setCallbackFilter(new ProxyCallbackFilter(this.advised.getConfigurationOnlyCopy(), this.fixedInterceptorMap, this.fixedInterceptorOffset));
            enhancer.setCallbackTypes(types);
            return createProxyClassAndInstance(enhancer, callbacks);
        } catch (IllegalArgumentException | CodeGenerationException ex) {
            throw new AopConfigException("Could not generate CGLIB subclass of " + this.advised.getTargetClass() + ": Common causes of this problem include using a final class or a non-visible class", ex);
        } catch (Throwable ex2) {
            throw new AopConfigException("Unexpected AOP exception", ex2);
        }
    }

    protected Object createProxyClassAndInstance(Enhancer enhancer, Callback[] callbacks) {
        enhancer.setInterceptDuringConstruction(false);
        enhancer.setCallbacks(callbacks);
        if (this.constructorArgs != null && this.constructorArgTypes != null) {
            return enhancer.create(this.constructorArgTypes, this.constructorArgs);
        }
        return enhancer.create();
    }

    protected Enhancer createEnhancer() {
        return new Enhancer();
    }

    private void validateClassIfNecessary(Class<?> proxySuperClass, @Nullable ClassLoader proxyClassLoader) {
        if (logger.isWarnEnabled()) {
            synchronized (validatedClasses) {
                if (!validatedClasses.containsKey(proxySuperClass)) {
                    doValidateClass(proxySuperClass, proxyClassLoader, ClassUtils.getAllInterfacesForClassAsSet(proxySuperClass));
                    validatedClasses.put(proxySuperClass, Boolean.TRUE);
                }
            }
        }
    }

    private void doValidateClass(Class<?> proxySuperClass, @Nullable ClassLoader proxyClassLoader, Set<Class<?>> ifcs) {
        if (proxySuperClass != Object.class) {
            Method[] methods = proxySuperClass.getDeclaredMethods();
            for (Method method : methods) {
                int mod = method.getModifiers();
                if (!Modifier.isStatic(mod) && !Modifier.isPrivate(mod)) {
                    if (Modifier.isFinal(mod)) {
                        if (implementsInterface(method, ifcs)) {
                            logger.info("Unable to proxy interface-implementing method [" + method + "] because it is marked as final: Consider using interface-based JDK proxies instead!");
                        }
                        logger.debug("Final method [" + method + "] cannot get proxied via CGLIB: Calls to this method will NOT be routed to the target instance and might lead to NPEs against uninitialized fields in the proxy instance.");
                    } else if (!Modifier.isPublic(mod) && !Modifier.isProtected(mod) && proxyClassLoader != null && proxySuperClass.getClassLoader() != proxyClassLoader) {
                        logger.debug("Method [" + method + "] is package-visible across different ClassLoaders and cannot get proxied via CGLIB: Declare this method as public or protected if you need to support invocations through the proxy.");
                    }
                }
            }
            doValidateClass(proxySuperClass.getSuperclass(), proxyClassLoader, ifcs);
        }
    }

    private Callback[] getCallbacks(Class<?> rootClass) throws Exception {
        Callback dynamicUnadvisedInterceptor;
        Callback targetInterceptor;
        Callback[] callbacks;
        Callback dynamicUnadvisedExposedInterceptor;
        boolean exposeProxy = this.advised.isExposeProxy();
        boolean isFrozen = this.advised.isFrozen();
        boolean isStatic = this.advised.getTargetSource().isStatic();
        Callback aopInterceptor = new DynamicAdvisedInterceptor(this.advised);
        if (exposeProxy) {
            if (isStatic) {
                dynamicUnadvisedExposedInterceptor = new StaticUnadvisedExposedInterceptor(this.advised.getTargetSource().getTarget());
            } else {
                dynamicUnadvisedExposedInterceptor = new DynamicUnadvisedExposedInterceptor(this.advised.getTargetSource());
            }
            targetInterceptor = dynamicUnadvisedExposedInterceptor;
        } else {
            if (isStatic) {
                dynamicUnadvisedInterceptor = new StaticUnadvisedInterceptor(this.advised.getTargetSource().getTarget());
            } else {
                dynamicUnadvisedInterceptor = new DynamicUnadvisedInterceptor(this.advised.getTargetSource());
            }
            targetInterceptor = dynamicUnadvisedInterceptor;
        }
        Callback targetDispatcher = isStatic ? new StaticDispatcher(this.advised.getTargetSource().getTarget()) : new SerializableNoOp();
        Callback[] mainCallbacks = {aopInterceptor, targetInterceptor, new SerializableNoOp(), targetDispatcher, this.advisedDispatcher, new EqualsInterceptor(this.advised), new HashCodeInterceptor(this.advised)};
        if (isStatic && isFrozen) {
            Method[] methods = rootClass.getMethods();
            Callback[] fixedCallbacks = new Callback[methods.length];
            this.fixedInterceptorMap = new HashMap(methods.length);
            for (int x = 0; x < methods.length; x++) {
                List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(methods[x], rootClass);
                fixedCallbacks[x] = new FixedChainStaticTargetInterceptor(chain, this.advised.getTargetSource().getTarget(), this.advised.getTargetClass());
                this.fixedInterceptorMap.put(methods[x].toString(), Integer.valueOf(x));
            }
            callbacks = new Callback[mainCallbacks.length + fixedCallbacks.length];
            System.arraycopy(mainCallbacks, 0, callbacks, 0, mainCallbacks.length);
            System.arraycopy(fixedCallbacks, 0, callbacks, mainCallbacks.length, fixedCallbacks.length);
            this.fixedInterceptorOffset = mainCallbacks.length;
        } else {
            callbacks = mainCallbacks;
        }
        return callbacks;
    }

    public boolean equals(Object other) {
        return this == other || ((other instanceof CglibAopProxy) && AopProxyUtils.equalsInProxy(this.advised, ((CglibAopProxy) other).advised));
    }

    public int hashCode() {
        return (CglibAopProxy.class.hashCode() * 13) + this.advised.getTargetSource().hashCode();
    }

    private static boolean implementsInterface(Method method, Set<Class<?>> ifcs) {
        for (Class<?> ifc : ifcs) {
            if (ClassUtils.hasMethod(ifc, method.getName(), method.getParameterTypes())) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Nullable
    public static Object processReturnType(Object proxy, @Nullable Object target, Method method, @Nullable Object returnValue) {
        if (returnValue != null && returnValue == target && !RawTargetAccess.class.isAssignableFrom(method.getDeclaringClass())) {
            returnValue = proxy;
        }
        Class<?> returnType = method.getReturnType();
        if (returnValue == null && returnType != Void.TYPE && returnType.isPrimitive()) {
            throw new AopInvocationException("Null return value from advice does not match primitive return type for: " + method);
        }
        return returnValue;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/CglibAopProxy$StaticUnadvisedInterceptor.class */
    public static class StaticUnadvisedInterceptor implements MethodInterceptor, Serializable {
        @Nullable
        private final Object target;

        public StaticUnadvisedInterceptor(@Nullable Object target) {
            this.target = target;
        }

        @Override // org.springframework.cglib.proxy.MethodInterceptor
        @Nullable
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object retVal = methodProxy.invoke(this.target, args);
            return CglibAopProxy.processReturnType(proxy, this.target, method, retVal);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/CglibAopProxy$StaticUnadvisedExposedInterceptor.class */
    public static class StaticUnadvisedExposedInterceptor implements MethodInterceptor, Serializable {
        @Nullable
        private final Object target;

        public StaticUnadvisedExposedInterceptor(@Nullable Object target) {
            this.target = target;
        }

        @Override // org.springframework.cglib.proxy.MethodInterceptor
        @Nullable
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object oldProxy = null;
            try {
                oldProxy = AopContext.setCurrentProxy(proxy);
                Object retVal = methodProxy.invoke(this.target, args);
                Object processReturnType = CglibAopProxy.processReturnType(proxy, this.target, method, retVal);
                AopContext.setCurrentProxy(oldProxy);
                return processReturnType;
            } catch (Throwable th) {
                AopContext.setCurrentProxy(oldProxy);
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/CglibAopProxy$DynamicUnadvisedInterceptor.class */
    public static class DynamicUnadvisedInterceptor implements MethodInterceptor, Serializable {
        private final TargetSource targetSource;

        public DynamicUnadvisedInterceptor(TargetSource targetSource) {
            this.targetSource = targetSource;
        }

        @Override // org.springframework.cglib.proxy.MethodInterceptor
        @Nullable
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object target = this.targetSource.getTarget();
            try {
                Object retVal = methodProxy.invoke(target, args);
                Object processReturnType = CglibAopProxy.processReturnType(proxy, target, method, retVal);
                if (target != null) {
                    this.targetSource.releaseTarget(target);
                }
                return processReturnType;
            } catch (Throwable th) {
                if (target != null) {
                    this.targetSource.releaseTarget(target);
                }
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/CglibAopProxy$DynamicUnadvisedExposedInterceptor.class */
    public static class DynamicUnadvisedExposedInterceptor implements MethodInterceptor, Serializable {
        private final TargetSource targetSource;

        public DynamicUnadvisedExposedInterceptor(TargetSource targetSource) {
            this.targetSource = targetSource;
        }

        @Override // org.springframework.cglib.proxy.MethodInterceptor
        @Nullable
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object oldProxy = null;
            Object target = this.targetSource.getTarget();
            try {
                oldProxy = AopContext.setCurrentProxy(proxy);
                Object retVal = methodProxy.invoke(target, args);
                Object processReturnType = CglibAopProxy.processReturnType(proxy, target, method, retVal);
                AopContext.setCurrentProxy(oldProxy);
                if (target != null) {
                    this.targetSource.releaseTarget(target);
                }
                return processReturnType;
            } catch (Throwable th) {
                AopContext.setCurrentProxy(oldProxy);
                if (target != null) {
                    this.targetSource.releaseTarget(target);
                }
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/CglibAopProxy$StaticDispatcher.class */
    public static class StaticDispatcher implements Dispatcher, Serializable {
        @Nullable
        private Object target;

        public StaticDispatcher(@Nullable Object target) {
            this.target = target;
        }

        @Override // org.springframework.cglib.proxy.Dispatcher
        @Nullable
        public Object loadObject() {
            return this.target;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/CglibAopProxy$AdvisedDispatcher.class */
    public static class AdvisedDispatcher implements Dispatcher, Serializable {
        private final AdvisedSupport advised;

        public AdvisedDispatcher(AdvisedSupport advised) {
            this.advised = advised;
        }

        @Override // org.springframework.cglib.proxy.Dispatcher
        public Object loadObject() throws Exception {
            return this.advised;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/CglibAopProxy$EqualsInterceptor.class */
    public static class EqualsInterceptor implements MethodInterceptor, Serializable {
        private final AdvisedSupport advised;

        public EqualsInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        @Override // org.springframework.cglib.proxy.MethodInterceptor
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) {
            Object other = args[0];
            if (proxy == other) {
                return true;
            }
            if (other instanceof Factory) {
                Callback callback = ((Factory) other).getCallback(5);
                if (!(callback instanceof EqualsInterceptor)) {
                    return false;
                }
                AdvisedSupport otherAdvised = ((EqualsInterceptor) callback).advised;
                return Boolean.valueOf(AopProxyUtils.equalsInProxy(this.advised, otherAdvised));
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/CglibAopProxy$HashCodeInterceptor.class */
    public static class HashCodeInterceptor implements MethodInterceptor, Serializable {
        private final AdvisedSupport advised;

        public HashCodeInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        @Override // org.springframework.cglib.proxy.MethodInterceptor
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) {
            return Integer.valueOf((CglibAopProxy.class.hashCode() * 13) + this.advised.getTargetSource().hashCode());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/CglibAopProxy$FixedChainStaticTargetInterceptor.class */
    public static class FixedChainStaticTargetInterceptor implements MethodInterceptor, Serializable {
        private final List<Object> adviceChain;
        @Nullable
        private final Object target;
        @Nullable
        private final Class<?> targetClass;

        public FixedChainStaticTargetInterceptor(List<Object> adviceChain, @Nullable Object target, @Nullable Class<?> targetClass) {
            this.adviceChain = adviceChain;
            this.target = target;
            this.targetClass = targetClass;
        }

        @Override // org.springframework.cglib.proxy.MethodInterceptor
        @Nullable
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            MethodInvocation invocation = new CglibMethodInvocation(proxy, this.target, method, args, this.targetClass, this.adviceChain, methodProxy);
            Object retVal = invocation.proceed();
            return CglibAopProxy.processReturnType(proxy, this.target, method, retVal);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/CglibAopProxy$DynamicAdvisedInterceptor.class */
    public static class DynamicAdvisedInterceptor implements MethodInterceptor, Serializable {
        private final AdvisedSupport advised;

        public DynamicAdvisedInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        @Override // org.springframework.cglib.proxy.MethodInterceptor
        @Nullable
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object retVal;
            Object oldProxy = null;
            boolean setProxyContext = false;
            Object target = null;
            TargetSource targetSource = this.advised.getTargetSource();
            try {
                if (this.advised.exposeProxy) {
                    oldProxy = AopContext.setCurrentProxy(proxy);
                    setProxyContext = true;
                }
                target = targetSource.getTarget();
                Class<?> targetClass = target != null ? target.getClass() : null;
                List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
                if (chain.isEmpty() && Modifier.isPublic(method.getModifiers())) {
                    Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args);
                    retVal = methodProxy.invoke(target, argsToUse);
                } else {
                    retVal = new CglibMethodInvocation(proxy, target, method, args, targetClass, chain, methodProxy).proceed();
                }
                Object retVal2 = CglibAopProxy.processReturnType(proxy, target, method, retVal);
                if (target != null && !targetSource.isStatic()) {
                    targetSource.releaseTarget(target);
                }
                if (setProxyContext) {
                    AopContext.setCurrentProxy(oldProxy);
                }
                return retVal2;
            } catch (Throwable th) {
                if (target != null && !targetSource.isStatic()) {
                    targetSource.releaseTarget(target);
                }
                if (setProxyContext) {
                    AopContext.setCurrentProxy(oldProxy);
                }
                throw th;
            }
        }

        public boolean equals(Object other) {
            return this == other || ((other instanceof DynamicAdvisedInterceptor) && this.advised.equals(((DynamicAdvisedInterceptor) other).advised));
        }

        public int hashCode() {
            return this.advised.hashCode();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/CglibAopProxy$CglibMethodInvocation.class */
    private static class CglibMethodInvocation extends ReflectiveMethodInvocation {
        private final MethodProxy methodProxy;
        private final boolean publicMethod;

        public CglibMethodInvocation(Object proxy, @Nullable Object target, Method method, Object[] arguments, @Nullable Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers, MethodProxy methodProxy) {
            super(proxy, target, method, arguments, targetClass, interceptorsAndDynamicMethodMatchers);
            this.methodProxy = methodProxy;
            this.publicMethod = Modifier.isPublic(method.getModifiers());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.aop.framework.ReflectiveMethodInvocation
        public Object invokeJoinpoint() throws Throwable {
            if (this.publicMethod && getMethod().getDeclaringClass() != Object.class) {
                return this.methodProxy.invoke(this.target, this.arguments);
            }
            return super.invokeJoinpoint();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/CglibAopProxy$ProxyCallbackFilter.class */
    public static class ProxyCallbackFilter implements CallbackFilter {
        private final AdvisedSupport advised;
        private final Map<String, Integer> fixedInterceptorMap;
        private final int fixedInterceptorOffset;

        public ProxyCallbackFilter(AdvisedSupport advised, Map<String, Integer> fixedInterceptorMap, int fixedInterceptorOffset) {
            this.advised = advised;
            this.fixedInterceptorMap = fixedInterceptorMap;
            this.fixedInterceptorOffset = fixedInterceptorOffset;
        }

        @Override // org.springframework.cglib.proxy.CallbackFilter
        public int accept(Method method) {
            if (AopUtils.isFinalizeMethod(method)) {
                CglibAopProxy.logger.trace("Found finalize() method - using NO_OVERRIDE");
                return 2;
            } else if (!this.advised.isOpaque() && method.getDeclaringClass().isInterface() && method.getDeclaringClass().isAssignableFrom(Advised.class)) {
                if (CglibAopProxy.logger.isTraceEnabled()) {
                    CglibAopProxy.logger.trace("Method is declared on Advised interface: " + method);
                    return 4;
                }
                return 4;
            } else if (AopUtils.isEqualsMethod(method)) {
                if (CglibAopProxy.logger.isTraceEnabled()) {
                    CglibAopProxy.logger.trace("Found 'equals' method: " + method);
                    return 5;
                }
                return 5;
            } else if (AopUtils.isHashCodeMethod(method)) {
                if (CglibAopProxy.logger.isTraceEnabled()) {
                    CglibAopProxy.logger.trace("Found 'hashCode' method: " + method);
                    return 6;
                }
                return 6;
            } else {
                Class<?> targetClass = this.advised.getTargetClass();
                List<?> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
                boolean haveAdvice = !chain.isEmpty();
                boolean exposeProxy = this.advised.isExposeProxy();
                boolean isStatic = this.advised.getTargetSource().isStatic();
                boolean isFrozen = this.advised.isFrozen();
                if (haveAdvice || !isFrozen) {
                    if (exposeProxy) {
                        if (CglibAopProxy.logger.isTraceEnabled()) {
                            CglibAopProxy.logger.trace("Must expose proxy on advised method: " + method);
                            return 0;
                        }
                        return 0;
                    }
                    String key = method.toString();
                    if (isStatic && isFrozen && this.fixedInterceptorMap.containsKey(key)) {
                        if (CglibAopProxy.logger.isTraceEnabled()) {
                            CglibAopProxy.logger.trace("Method has advice and optimizations are enabled: " + method);
                        }
                        int index = this.fixedInterceptorMap.get(key).intValue();
                        return index + this.fixedInterceptorOffset;
                    } else if (CglibAopProxy.logger.isTraceEnabled()) {
                        CglibAopProxy.logger.trace("Unable to apply any optimizations to advised method: " + method);
                        return 0;
                    } else {
                        return 0;
                    }
                } else if (exposeProxy || !isStatic) {
                    return 1;
                } else {
                    Class<?> returnType = method.getReturnType();
                    if (targetClass != null && returnType.isAssignableFrom(targetClass)) {
                        if (CglibAopProxy.logger.isTraceEnabled()) {
                            CglibAopProxy.logger.trace("Method return type is assignable from target type and may therefore return 'this' - using INVOKE_TARGET: " + method);
                            return 1;
                        }
                        return 1;
                    } else if (CglibAopProxy.logger.isTraceEnabled()) {
                        CglibAopProxy.logger.trace("Method return type ensures 'this' cannot be returned - using DISPATCH_TARGET: " + method);
                        return 3;
                    } else {
                        return 3;
                    }
                }
            }
        }

        @Override // org.springframework.cglib.proxy.CallbackFilter
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ProxyCallbackFilter)) {
                return false;
            }
            ProxyCallbackFilter otherCallbackFilter = (ProxyCallbackFilter) other;
            AdvisedSupport otherAdvised = otherCallbackFilter.advised;
            if (this.advised.isFrozen() != otherAdvised.isFrozen() || this.advised.isExposeProxy() != otherAdvised.isExposeProxy() || this.advised.getTargetSource().isStatic() != otherAdvised.getTargetSource().isStatic() || !AopProxyUtils.equalsProxiedInterfaces(this.advised, otherAdvised)) {
                return false;
            }
            Advisor[] thisAdvisors = this.advised.getAdvisors();
            Advisor[] thatAdvisors = otherAdvised.getAdvisors();
            if (thisAdvisors.length != thatAdvisors.length) {
                return false;
            }
            for (int i = 0; i < thisAdvisors.length; i++) {
                Advisor thisAdvisor = thisAdvisors[i];
                Advisor thatAdvisor = thatAdvisors[i];
                if (!equalsAdviceClasses(thisAdvisor, thatAdvisor) || !equalsPointcuts(thisAdvisor, thatAdvisor)) {
                    return false;
                }
            }
            return true;
        }

        private boolean equalsAdviceClasses(Advisor a, Advisor b) {
            return a.getAdvice().getClass() == b.getAdvice().getClass();
        }

        private boolean equalsPointcuts(Advisor a, Advisor b) {
            return !(a instanceof PointcutAdvisor) || ((b instanceof PointcutAdvisor) && ObjectUtils.nullSafeEquals(((PointcutAdvisor) a).getPointcut(), ((PointcutAdvisor) b).getPointcut()));
        }

        public int hashCode() {
            int hashCode = 0;
            Advisor[] advisors = this.advised.getAdvisors();
            for (Advisor advisor : advisors) {
                Advice advice = advisor.getAdvice();
                hashCode = (13 * hashCode) + advice.getClass().hashCode();
            }
            return (13 * ((13 * ((13 * ((13 * hashCode) + (this.advised.isFrozen() ? 1 : 0))) + (this.advised.isExposeProxy() ? 1 : 0))) + (this.advised.isOptimize() ? 1 : 0))) + (this.advised.isOpaque() ? 1 : 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/CglibAopProxy$ClassLoaderAwareUndeclaredThrowableStrategy.class */
    public static class ClassLoaderAwareUndeclaredThrowableStrategy extends UndeclaredThrowableStrategy {
        @Nullable
        private final ClassLoader classLoader;

        public ClassLoaderAwareUndeclaredThrowableStrategy(@Nullable ClassLoader classLoader) {
            super(UndeclaredThrowableException.class);
            this.classLoader = classLoader;
        }

        @Override // org.springframework.cglib.core.DefaultGeneratorStrategy, org.springframework.cglib.core.GeneratorStrategy
        public byte[] generate(ClassGenerator cg) throws Exception {
            if (this.classLoader == null) {
                return super.generate(cg);
            }
            Thread currentThread = Thread.currentThread();
            try {
                ClassLoader threadContextClassLoader = currentThread.getContextClassLoader();
                boolean overrideClassLoader = !this.classLoader.equals(threadContextClassLoader);
                if (overrideClassLoader) {
                    currentThread.setContextClassLoader(this.classLoader);
                }
                try {
                    byte[] generate = super.generate(cg);
                    if (overrideClassLoader) {
                        currentThread.setContextClassLoader(threadContextClassLoader);
                    }
                    return generate;
                } catch (Throwable th) {
                    if (overrideClassLoader) {
                        currentThread.setContextClassLoader(threadContextClassLoader);
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                return super.generate(cg);
            }
        }
    }
}