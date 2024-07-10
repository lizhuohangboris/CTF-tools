package org.springframework.beans.factory.support;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import javax.inject.Provider;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.core.OrderComparator;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CompositeIterator;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/DefaultListableBeanFactory.class */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements ConfigurableListableBeanFactory, BeanDefinitionRegistry, Serializable {
    @Nullable
    private static Class<?> javaxInjectProviderClass;
    private static final Map<String, Reference<DefaultListableBeanFactory>> serializableFactories;
    @Nullable
    private String serializationId;
    private boolean allowBeanDefinitionOverriding;
    private boolean allowEagerClassLoading;
    @Nullable
    private Comparator<Object> dependencyComparator;
    private AutowireCandidateResolver autowireCandidateResolver;
    private final Map<Class<?>, Object> resolvableDependencies;
    private final Map<String, BeanDefinition> beanDefinitionMap;
    private final Map<Class<?>, String[]> allBeanNamesByType;
    private final Map<Class<?>, String[]> singletonBeanNamesByType;
    private volatile List<String> beanDefinitionNames;
    private volatile Set<String> manualSingletonNames;
    @Nullable
    private volatile String[] frozenBeanDefinitionNames;
    private volatile boolean configurationFrozen;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/DefaultListableBeanFactory$BeanObjectProvider.class */
    private interface BeanObjectProvider<T> extends ObjectProvider<T>, Serializable {
    }

    static {
        try {
            javaxInjectProviderClass = ClassUtils.forName("javax.inject.Provider", DefaultListableBeanFactory.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            javaxInjectProviderClass = null;
        }
        serializableFactories = new ConcurrentHashMap(8);
    }

    public DefaultListableBeanFactory() {
        this.allowBeanDefinitionOverriding = true;
        this.allowEagerClassLoading = true;
        this.autowireCandidateResolver = new SimpleAutowireCandidateResolver();
        this.resolvableDependencies = new ConcurrentHashMap(16);
        this.beanDefinitionMap = new ConcurrentHashMap(256);
        this.allBeanNamesByType = new ConcurrentHashMap(64);
        this.singletonBeanNamesByType = new ConcurrentHashMap(64);
        this.beanDefinitionNames = new ArrayList(256);
        this.manualSingletonNames = new LinkedHashSet(16);
        this.configurationFrozen = false;
    }

    public DefaultListableBeanFactory(@Nullable BeanFactory parentBeanFactory) {
        super(parentBeanFactory);
        this.allowBeanDefinitionOverriding = true;
        this.allowEagerClassLoading = true;
        this.autowireCandidateResolver = new SimpleAutowireCandidateResolver();
        this.resolvableDependencies = new ConcurrentHashMap(16);
        this.beanDefinitionMap = new ConcurrentHashMap(256);
        this.allBeanNamesByType = new ConcurrentHashMap(64);
        this.singletonBeanNamesByType = new ConcurrentHashMap(64);
        this.beanDefinitionNames = new ArrayList(256);
        this.manualSingletonNames = new LinkedHashSet(16);
        this.configurationFrozen = false;
    }

    public void setSerializationId(@Nullable String serializationId) {
        if (serializationId != null) {
            serializableFactories.put(serializationId, new WeakReference(this));
        } else if (this.serializationId != null) {
            serializableFactories.remove(this.serializationId);
        }
        this.serializationId = serializationId;
    }

    @Nullable
    public String getSerializationId() {
        return this.serializationId;
    }

    public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
        this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
    }

    public boolean isAllowBeanDefinitionOverriding() {
        return this.allowBeanDefinitionOverriding;
    }

    public void setAllowEagerClassLoading(boolean allowEagerClassLoading) {
        this.allowEagerClassLoading = allowEagerClassLoading;
    }

    public boolean isAllowEagerClassLoading() {
        return this.allowEagerClassLoading;
    }

    public void setDependencyComparator(@Nullable Comparator<Object> dependencyComparator) {
        this.dependencyComparator = dependencyComparator;
    }

    @Nullable
    public Comparator<Object> getDependencyComparator() {
        return this.dependencyComparator;
    }

    public void setAutowireCandidateResolver(AutowireCandidateResolver autowireCandidateResolver) {
        Assert.notNull(autowireCandidateResolver, "AutowireCandidateResolver must not be null");
        if (autowireCandidateResolver instanceof BeanFactoryAware) {
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged(() -> {
                    ((BeanFactoryAware) autowireCandidateResolver).setBeanFactory(this);
                    return null;
                }, getAccessControlContext());
            } else {
                ((BeanFactoryAware) autowireCandidateResolver).setBeanFactory(this);
            }
        }
        this.autowireCandidateResolver = autowireCandidateResolver;
    }

    public AutowireCandidateResolver getAutowireCandidateResolver() {
        return this.autowireCandidateResolver;
    }

    @Override // org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory, org.springframework.beans.factory.support.AbstractBeanFactory, org.springframework.beans.factory.config.ConfigurableBeanFactory
    public void copyConfigurationFrom(ConfigurableBeanFactory otherFactory) {
        super.copyConfigurationFrom(otherFactory);
        if (otherFactory instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory otherListableFactory = (DefaultListableBeanFactory) otherFactory;
            this.allowBeanDefinitionOverriding = otherListableFactory.allowBeanDefinitionOverriding;
            this.allowEagerClassLoading = otherListableFactory.allowEagerClassLoading;
            this.dependencyComparator = otherListableFactory.dependencyComparator;
            setAutowireCandidateResolver((AutowireCandidateResolver) BeanUtils.instantiateClass(getAutowireCandidateResolver().getClass()));
            this.resolvableDependencies.putAll(otherListableFactory.resolvableDependencies);
        }
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return (T) getBean(requiredType, (Object[]) null);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(Class<T> requiredType, @Nullable Object... args) throws BeansException {
        T t = (T) resolveBean(ResolvableType.forRawClass(requiredType), args, false);
        if (t == null) {
            throw new NoSuchBeanDefinitionException((Class<?>) requiredType);
        }
        return t;
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) throws BeansException {
        return getBeanProvider(ResolvableType.forRawClass(requiredType));
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> ObjectProvider<T> getBeanProvider(final ResolvableType requiredType) {
        return new BeanObjectProvider<T>() { // from class: org.springframework.beans.factory.support.DefaultListableBeanFactory.1
            {
                DefaultListableBeanFactory.this = this;
            }

            @Override // org.springframework.beans.factory.ObjectFactory
            public T getObject() throws BeansException {
                T resolved = (T) DefaultListableBeanFactory.this.resolveBean(requiredType, null, false);
                if (resolved == null) {
                    throw new NoSuchBeanDefinitionException(requiredType);
                }
                return resolved;
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            public T getObject(Object... args) throws BeansException {
                T resolved = (T) DefaultListableBeanFactory.this.resolveBean(requiredType, args, false);
                if (resolved == null) {
                    throw new NoSuchBeanDefinitionException(requiredType);
                }
                return resolved;
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            @Nullable
            public T getIfAvailable() throws BeansException {
                return (T) DefaultListableBeanFactory.this.resolveBean(requiredType, null, false);
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            @Nullable
            public T getIfUnique() throws BeansException {
                return (T) DefaultListableBeanFactory.this.resolveBean(requiredType, null, true);
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            public Stream<T> stream() {
                return Arrays.stream(DefaultListableBeanFactory.this.getBeanNamesForTypedStream(requiredType)).map(name -> {
                    return DefaultListableBeanFactory.this.getBean(name);
                }).filter(bean -> {
                    return !(bean instanceof NullBean);
                });
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            public Stream<T> orderedStream() {
                String[] beanNames = DefaultListableBeanFactory.this.getBeanNamesForTypedStream(requiredType);
                LinkedHashMap linkedHashMap = new LinkedHashMap(beanNames.length);
                for (String beanName : beanNames) {
                    Object beanInstance = DefaultListableBeanFactory.this.getBean(beanName);
                    if (!(beanInstance instanceof NullBean)) {
                        linkedHashMap.put(beanName, beanInstance);
                    }
                }
                Stream<T> stream = linkedHashMap.values().stream();
                return stream.sorted(DefaultListableBeanFactory.this.adaptOrderComparator(linkedHashMap));
            }
        };
    }

    @Nullable
    public <T> T resolveBean(ResolvableType requiredType, @Nullable Object[] args, boolean nonUniqueAsNull) {
        NamedBeanHolder<T> namedBean = resolveNamedBean(requiredType, args, nonUniqueAsNull);
        if (namedBean != null) {
            return namedBean.getBeanInstance();
        }
        BeanFactory parent = getParentBeanFactory();
        if (parent instanceof DefaultListableBeanFactory) {
            return (T) ((DefaultListableBeanFactory) parent).resolveBean(requiredType, args, nonUniqueAsNull);
        }
        if (parent != null) {
            ObjectProvider<T> parentProvider = parent.getBeanProvider(requiredType);
            if (args != null) {
                return parentProvider.getObject(args);
            }
            return nonUniqueAsNull ? parentProvider.getIfUnique() : parentProvider.getIfAvailable();
        }
        return null;
    }

    public String[] getBeanNamesForTypedStream(ResolvableType requiredType) {
        return BeanFactoryUtils.beanNamesForTypeIncludingAncestors(this, requiredType);
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanFactory, org.springframework.beans.factory.ListableBeanFactory, org.springframework.beans.factory.support.BeanDefinitionRegistry
    public boolean containsBeanDefinition(String beanName) {
        Assert.notNull(beanName, "Bean name must not be null");
        return this.beanDefinitionMap.containsKey(beanName);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory, org.springframework.beans.factory.support.BeanDefinitionRegistry
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory, org.springframework.beans.factory.support.BeanDefinitionRegistry
    public String[] getBeanDefinitionNames() {
        String[] frozenNames = this.frozenBeanDefinitionNames;
        if (frozenNames != null) {
            return (String[]) frozenNames.clone();
        }
        return StringUtils.toStringArray(this.beanDefinitionNames);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForType(ResolvableType type) {
        Class<?> resolved = type.resolve();
        if (resolved != null && !type.hasGenerics()) {
            return getBeanNamesForType(resolved, true, true);
        }
        return doGetBeanNamesForType(type, true, true);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForType(@Nullable Class<?> type) {
        return getBeanNamesForType(type, true, true);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        if (!isConfigurationFrozen() || type == null || !allowEagerInit) {
            return doGetBeanNamesForType(ResolvableType.forRawClass(type), includeNonSingletons, allowEagerInit);
        }
        Map<Class<?>, String[]> cache = includeNonSingletons ? this.allBeanNamesByType : this.singletonBeanNamesByType;
        String[] resolvedBeanNames = cache.get(type);
        if (resolvedBeanNames != null) {
            return resolvedBeanNames;
        }
        String[] resolvedBeanNames2 = doGetBeanNamesForType(ResolvableType.forRawClass(type), includeNonSingletons, true);
        if (ClassUtils.isCacheSafe(type, getBeanClassLoader())) {
            cache.put(type, resolvedBeanNames2);
        }
        return resolvedBeanNames2;
    }

    /* JADX WARN: Code restructure failed: missing block: B:165:0x00b4, code lost:
        if (isSingleton(r10) != false) goto L57;
     */
    /* JADX WARN: Removed duplicated region for block: B:185:0x010a A[Catch: CannotLoadBeanClassException -> 0x0117, BeanDefinitionStoreException -> 0x0159, TryCatch #3 {BeanDefinitionStoreException -> 0x0159, CannotLoadBeanClassException -> 0x0117, blocks: (B:135:0x0033, B:139:0x0047, B:141:0x004f, B:143:0x0057, B:145:0x005e, B:147:0x006a, B:153:0x0089, B:155:0x0091, B:174:0x00d2, B:176:0x00ec, B:178:0x00f4, B:185:0x010a, B:161:0x00a3, B:164:0x00ae, B:166:0x00b7), top: B:233:0x0033 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private java.lang.String[] doGetBeanNamesForType(org.springframework.core.ResolvableType r5, boolean r6, boolean r7) {
        /*
            Method dump skipped, instructions count: 593
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.beans.factory.support.DefaultListableBeanFactory.doGetBeanNamesForType(org.springframework.core.ResolvableType, boolean, boolean):java.lang.String[]");
    }

    private boolean requiresEagerInitForType(@Nullable String factoryBeanName) {
        return (factoryBeanName == null || !isFactoryBean(factoryBeanName) || containsSingleton(factoryBeanName)) ? false : true;
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException {
        return getBeansOfType(type, true, true);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        String[] beanNames = getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
        LinkedHashMap linkedHashMap = new LinkedHashMap(beanNames.length);
        for (String beanName : beanNames) {
            try {
                Object beanInstance = getBean(beanName);
                if (!(beanInstance instanceof NullBean)) {
                    linkedHashMap.put(beanName, beanInstance);
                }
            } catch (BeanCreationException ex) {
                Throwable rootCause = ex.getMostSpecificCause();
                if (rootCause instanceof BeanCurrentlyInCreationException) {
                    BeanCreationException bce = (BeanCreationException) rootCause;
                    String exBeanName = bce.getBeanName();
                    if (exBeanName != null && isCurrentlyInCreation(exBeanName)) {
                        if (this.logger.isTraceEnabled()) {
                            this.logger.trace("Ignoring match to currently created bean '" + exBeanName + "': " + ex.getMessage());
                        }
                        onSuppressedException(ex);
                    }
                }
                throw ex;
            }
        }
        return linkedHashMap;
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        List<String> result = new ArrayList<>();
        for (String beanName : this.beanDefinitionNames) {
            BeanDefinition beanDefinition = getBeanDefinition(beanName);
            if (!beanDefinition.isAbstract() && findAnnotationOnBean(beanName, annotationType) != null) {
                result.add(beanName);
            }
        }
        for (String beanName2 : this.manualSingletonNames) {
            if (!result.contains(beanName2) && findAnnotationOnBean(beanName2, annotationType) != null) {
                result.add(beanName2);
            }
        }
        return StringUtils.toStringArray(result);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        String[] beanNames = getBeanNamesForAnnotation(annotationType);
        Map<String, Object> result = new LinkedHashMap<>(beanNames.length);
        for (String beanName : beanNames) {
            Object beanInstance = getBean(beanName);
            if (!(beanInstance instanceof NullBean)) {
                result.put(beanName, beanInstance);
            }
        }
        return result;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v18, types: [java.lang.annotation.Annotation] */
    /* JADX WARN: Type inference failed for: r0v20, types: [java.lang.annotation.Annotation] */
    @Override // org.springframework.beans.factory.ListableBeanFactory
    @Nullable
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException {
        A ann = null;
        Class<?> beanType = getType(beanName);
        if (beanType != null) {
            ann = AnnotationUtils.findAnnotation(beanType, (Class) annotationType);
        }
        if (ann == null && containsBeanDefinition(beanName)) {
            BeanDefinition bd = getMergedBeanDefinition(beanName);
            if (bd instanceof AbstractBeanDefinition) {
                AbstractBeanDefinition abd = (AbstractBeanDefinition) bd;
                if (abd.hasBeanClass()) {
                    ann = AnnotationUtils.findAnnotation(abd.getBeanClass(), (Class) annotationType);
                }
            }
        }
        return ann;
    }

    @Override // org.springframework.beans.factory.config.ConfigurableListableBeanFactory
    public void registerResolvableDependency(Class<?> dependencyType, @Nullable Object autowiredValue) {
        Assert.notNull(dependencyType, "Dependency type must not be null");
        if (autowiredValue != null) {
            if (!(autowiredValue instanceof ObjectFactory) && !dependencyType.isInstance(autowiredValue)) {
                throw new IllegalArgumentException("Value [" + autowiredValue + "] does not implement specified dependency type [" + dependencyType.getName() + "]");
            }
            this.resolvableDependencies.put(dependencyType, autowiredValue);
        }
    }

    @Override // org.springframework.beans.factory.config.ConfigurableListableBeanFactory
    public boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor) throws NoSuchBeanDefinitionException {
        return isAutowireCandidate(beanName, descriptor, getAutowireCandidateResolver());
    }

    protected boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor, AutowireCandidateResolver resolver) throws NoSuchBeanDefinitionException {
        String beanDefinitionName = BeanFactoryUtils.transformedBeanName(beanName);
        if (containsBeanDefinition(beanDefinitionName)) {
            return isAutowireCandidate(beanName, getMergedLocalBeanDefinition(beanDefinitionName), descriptor, resolver);
        }
        if (containsSingleton(beanName)) {
            return isAutowireCandidate(beanName, new RootBeanDefinition(getType(beanName)), descriptor, resolver);
        }
        BeanFactory parent = getParentBeanFactory();
        if (parent instanceof DefaultListableBeanFactory) {
            return ((DefaultListableBeanFactory) parent).isAutowireCandidate(beanName, descriptor, resolver);
        }
        if (parent instanceof ConfigurableListableBeanFactory) {
            return ((ConfigurableListableBeanFactory) parent).isAutowireCandidate(beanName, descriptor);
        }
        return true;
    }

    protected boolean isAutowireCandidate(String beanName, RootBeanDefinition mbd, DependencyDescriptor descriptor, AutowireCandidateResolver resolver) {
        String beanDefinitionName = BeanFactoryUtils.transformedBeanName(beanName);
        resolveBeanClass(mbd, beanDefinitionName, new Class[0]);
        if (mbd.isFactoryMethodUnique && mbd.factoryMethodToIntrospect == null) {
            new ConstructorResolver(this).resolveFactoryMethodIfPossible(mbd);
        }
        return resolver.isAutowireCandidate(new BeanDefinitionHolder(mbd, beanName, getAliases(beanDefinitionName)), descriptor);
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanFactory, org.springframework.beans.factory.config.ConfigurableListableBeanFactory, org.springframework.beans.factory.support.BeanDefinitionRegistry
    public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        BeanDefinition bd = this.beanDefinitionMap.get(beanName);
        if (bd == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No bean named '" + beanName + "' found in " + this);
            }
            throw new NoSuchBeanDefinitionException(beanName);
        }
        return bd;
    }

    @Override // org.springframework.beans.factory.config.ConfigurableListableBeanFactory
    public Iterator<String> getBeanNamesIterator() {
        CompositeIterator<String> iterator = new CompositeIterator<>();
        iterator.add(this.beanDefinitionNames.iterator());
        iterator.add(this.manualSingletonNames.iterator());
        return iterator;
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanFactory, org.springframework.beans.factory.config.ConfigurableListableBeanFactory
    public void clearMetadataCache() {
        super.clearMetadataCache();
        clearByTypeCache();
    }

    @Override // org.springframework.beans.factory.config.ConfigurableListableBeanFactory
    public void freezeConfiguration() {
        this.configurationFrozen = true;
        this.frozenBeanDefinitionNames = StringUtils.toStringArray(this.beanDefinitionNames);
    }

    @Override // org.springframework.beans.factory.config.ConfigurableListableBeanFactory
    public boolean isConfigurationFrozen() {
        return this.configurationFrozen;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.beans.factory.support.AbstractBeanFactory
    public boolean isBeanEligibleForMetadataCaching(String beanName) {
        return this.configurationFrozen || super.isBeanEligibleForMetadataCaching(beanName);
    }

    @Override // org.springframework.beans.factory.config.ConfigurableListableBeanFactory
    public void preInstantiateSingletons() throws BeansException {
        boolean isEagerInit;
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Pre-instantiating singletons in " + this);
        }
        List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);
        for (String beanName : beanNames) {
            RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
            if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
                if (isFactoryBean(beanName)) {
                    Object bean = getBean(BeanFactory.FACTORY_BEAN_PREFIX + beanName);
                    if (bean instanceof FactoryBean) {
                        FactoryBean<?> factory = (FactoryBean) bean;
                        if (System.getSecurityManager() != null && (factory instanceof SmartFactoryBean)) {
                            SmartFactoryBean smartFactoryBean = (SmartFactoryBean) factory;
                            smartFactoryBean.getClass();
                            isEagerInit = ((Boolean) AccessController.doPrivileged(this::isEagerInit, getAccessControlContext())).booleanValue();
                        } else {
                            isEagerInit = (factory instanceof SmartFactoryBean) && ((SmartFactoryBean) factory).isEagerInit();
                        }
                        if (isEagerInit) {
                            getBean(beanName);
                        }
                    }
                } else {
                    getBean(beanName);
                }
            }
        }
        for (String beanName2 : beanNames) {
            Object singletonInstance = getSingleton(beanName2);
            if (singletonInstance instanceof SmartInitializingSingleton) {
                SmartInitializingSingleton smartSingleton = (SmartInitializingSingleton) singletonInstance;
                if (System.getSecurityManager() != null) {
                    AccessController.doPrivileged(() -> {
                        smartSingleton.afterSingletonsInstantiated();
                        return null;
                    }, getAccessControlContext());
                } else {
                    smartSingleton.afterSingletonsInstantiated();
                }
            }
        }
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionRegistry
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        Assert.hasText(beanName, "Bean name must not be empty");
        Assert.notNull(beanDefinition, "BeanDefinition must not be null");
        if (beanDefinition instanceof AbstractBeanDefinition) {
            try {
                ((AbstractBeanDefinition) beanDefinition).validate();
            } catch (BeanDefinitionValidationException ex) {
                throw new BeanDefinitionStoreException(beanDefinition.getResourceDescription(), beanName, "Validation of bean definition failed", ex);
            }
        }
        BeanDefinition existingDefinition = this.beanDefinitionMap.get(beanName);
        if (existingDefinition != null) {
            if (!isAllowBeanDefinitionOverriding()) {
                throw new BeanDefinitionOverrideException(beanName, beanDefinition, existingDefinition);
            }
            if (existingDefinition.getRole() < beanDefinition.getRole()) {
                if (this.logger.isInfoEnabled()) {
                    this.logger.info("Overriding user-defined bean definition for bean '" + beanName + "' with a framework-generated bean definition: replacing [" + existingDefinition + "] with [" + beanDefinition + "]");
                }
            } else if (!beanDefinition.equals(existingDefinition)) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Overriding bean definition for bean '" + beanName + "' with a different definition: replacing [" + existingDefinition + "] with [" + beanDefinition + "]");
                }
            } else if (this.logger.isTraceEnabled()) {
                this.logger.trace("Overriding bean definition for bean '" + beanName + "' with an equivalent definition: replacing [" + existingDefinition + "] with [" + beanDefinition + "]");
            }
            this.beanDefinitionMap.put(beanName, beanDefinition);
        } else {
            if (hasBeanCreationStarted()) {
                synchronized (this.beanDefinitionMap) {
                    this.beanDefinitionMap.put(beanName, beanDefinition);
                    List<String> updatedDefinitions = new ArrayList<>(this.beanDefinitionNames.size() + 1);
                    updatedDefinitions.addAll(this.beanDefinitionNames);
                    updatedDefinitions.add(beanName);
                    this.beanDefinitionNames = updatedDefinitions;
                    if (this.manualSingletonNames.contains(beanName)) {
                        Set<String> updatedSingletons = new LinkedHashSet<>(this.manualSingletonNames);
                        updatedSingletons.remove(beanName);
                        this.manualSingletonNames = updatedSingletons;
                    }
                }
            } else {
                this.beanDefinitionMap.put(beanName, beanDefinition);
                this.beanDefinitionNames.add(beanName);
                this.manualSingletonNames.remove(beanName);
            }
            this.frozenBeanDefinitionNames = null;
        }
        if (existingDefinition != null || containsSingleton(beanName)) {
            resetBeanDefinition(beanName);
        }
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionRegistry
    public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        Assert.hasText(beanName, "'beanName' must not be empty");
        BeanDefinition bd = this.beanDefinitionMap.remove(beanName);
        if (bd == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No bean named '" + beanName + "' found in " + this);
            }
            throw new NoSuchBeanDefinitionException(beanName);
        }
        if (hasBeanCreationStarted()) {
            synchronized (this.beanDefinitionMap) {
                List<String> updatedDefinitions = new ArrayList<>(this.beanDefinitionNames);
                updatedDefinitions.remove(beanName);
                this.beanDefinitionNames = updatedDefinitions;
            }
        } else {
            this.beanDefinitionNames.remove(beanName);
        }
        this.frozenBeanDefinitionNames = null;
        resetBeanDefinition(beanName);
    }

    protected void resetBeanDefinition(String beanName) {
        clearMergedBeanDefinition(beanName);
        destroySingleton(beanName);
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            if (processor instanceof MergedBeanDefinitionPostProcessor) {
                ((MergedBeanDefinitionPostProcessor) processor).resetBeanDefinition(beanName);
            }
        }
        for (String bdName : this.beanDefinitionNames) {
            if (!beanName.equals(bdName)) {
                BeanDefinition bd = this.beanDefinitionMap.get(bdName);
                if (beanName.equals(bd.getParentName())) {
                    resetBeanDefinition(bdName);
                }
            }
        }
    }

    @Override // org.springframework.core.SimpleAliasRegistry
    protected boolean allowAliasOverriding() {
        return isAllowBeanDefinitionOverriding();
    }

    @Override // org.springframework.beans.factory.support.DefaultSingletonBeanRegistry, org.springframework.beans.factory.config.SingletonBeanRegistry
    public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
        super.registerSingleton(beanName, singletonObject);
        if (hasBeanCreationStarted()) {
            synchronized (this.beanDefinitionMap) {
                if (!this.beanDefinitionMap.containsKey(beanName)) {
                    Set<String> updatedSingletons = new LinkedHashSet<>(this.manualSingletonNames.size() + 1);
                    updatedSingletons.addAll(this.manualSingletonNames);
                    updatedSingletons.add(beanName);
                    this.manualSingletonNames = updatedSingletons;
                }
            }
        } else if (!this.beanDefinitionMap.containsKey(beanName)) {
            this.manualSingletonNames.add(beanName);
        }
        clearByTypeCache();
    }

    @Override // org.springframework.beans.factory.support.DefaultSingletonBeanRegistry
    public void destroySingleton(String beanName) {
        super.destroySingleton(beanName);
        this.manualSingletonNames.remove(beanName);
        clearByTypeCache();
    }

    @Override // org.springframework.beans.factory.support.DefaultSingletonBeanRegistry, org.springframework.beans.factory.config.ConfigurableBeanFactory
    public void destroySingletons() {
        super.destroySingletons();
        this.manualSingletonNames.clear();
        clearByTypeCache();
    }

    private void clearByTypeCache() {
        this.allBeanNamesByType.clear();
        this.singletonBeanNamesByType.clear();
    }

    @Override // org.springframework.beans.factory.config.AutowireCapableBeanFactory
    public <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException {
        NamedBeanHolder<T> namedBean = resolveNamedBean(ResolvableType.forRawClass(requiredType), null, false);
        if (namedBean != null) {
            return namedBean;
        }
        BeanFactory parent = getParentBeanFactory();
        if (parent instanceof AutowireCapableBeanFactory) {
            return ((AutowireCapableBeanFactory) parent).resolveNamedBean(requiredType);
        }
        throw new NoSuchBeanDefinitionException((Class<?>) requiredType);
    }

    @Nullable
    private <T> NamedBeanHolder<T> resolveNamedBean(ResolvableType requiredType, @Nullable Object[] args, boolean nonUniqueAsNull) throws BeansException {
        String[] strArr;
        Assert.notNull(requiredType, "Required type must not be null");
        Class<?> clazz = requiredType.getRawClass();
        Assert.notNull(clazz, "Required type must have a raw Class");
        String[] candidateNames = getBeanNamesForType(requiredType);
        if (candidateNames.length > 1) {
            List<String> autowireCandidates = new ArrayList<>(candidateNames.length);
            for (String beanName : candidateNames) {
                if (!containsBeanDefinition(beanName) || getBeanDefinition(beanName).isAutowireCandidate()) {
                    autowireCandidates.add(beanName);
                }
            }
            if (!autowireCandidates.isEmpty()) {
                candidateNames = StringUtils.toStringArray(autowireCandidates);
            }
        }
        if (candidateNames.length == 1) {
            String beanName2 = candidateNames[0];
            return new NamedBeanHolder<>(beanName2, getBean(beanName2, clazz, args));
        } else if (candidateNames.length > 1) {
            Map<String, Object> candidates = new LinkedHashMap<>(candidateNames.length);
            for (String beanName3 : candidateNames) {
                if (containsSingleton(beanName3) && args == null) {
                    Object beanInstance = getBean(beanName3);
                    candidates.put(beanName3, beanInstance instanceof NullBean ? null : beanInstance);
                } else {
                    candidates.put(beanName3, getType(beanName3));
                }
            }
            String candidateName = determinePrimaryCandidate(candidates, clazz);
            if (candidateName == null) {
                candidateName = determineHighestPriorityCandidate(candidates, clazz);
            }
            if (candidateName != null) {
                Object beanInstance2 = candidates.get(candidateName);
                if (beanInstance2 == null || (beanInstance2 instanceof Class)) {
                    beanInstance2 = getBean(candidateName, clazz, args);
                }
                return new NamedBeanHolder<>(candidateName, beanInstance2);
            } else if (!nonUniqueAsNull) {
                throw new NoUniqueBeanDefinitionException(requiredType, candidates.keySet());
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override // org.springframework.beans.factory.config.AutowireCapableBeanFactory
    @Nullable
    public Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName, @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {
        descriptor.initParameterNameDiscovery(getParameterNameDiscoverer());
        if (Optional.class == descriptor.getDependencyType()) {
            return createOptionalDependency(descriptor, requestingBeanName, new Object[0]);
        }
        if (ObjectFactory.class == descriptor.getDependencyType() || ObjectProvider.class == descriptor.getDependencyType()) {
            return new DependencyObjectProvider(descriptor, requestingBeanName);
        }
        if (javaxInjectProviderClass == descriptor.getDependencyType()) {
            return new Jsr330Factory().createDependencyProvider(descriptor, requestingBeanName);
        }
        Object result = getAutowireCandidateResolver().getLazyResolutionProxyIfNecessary(descriptor, requestingBeanName);
        if (result == null) {
            result = doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);
        }
        return result;
    }

    @Nullable
    public Object doResolveDependency(DependencyDescriptor descriptor, @Nullable String beanName, @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {
        String autowiredBeanName;
        Object instanceCandidate;
        InjectionPoint previousInjectionPoint = ConstructorResolver.setCurrentInjectionPoint(descriptor);
        try {
            Object shortcut = descriptor.resolveShortcut(this);
            if (shortcut != null) {
                return shortcut;
            }
            Class<?> type = descriptor.getDependencyType();
            Object value = getAutowireCandidateResolver().getSuggestedValue(descriptor);
            if (value != null) {
                if (value instanceof String) {
                    String strVal = resolveEmbeddedValue((String) value);
                    BeanDefinition bd = (beanName == null || !containsBean(beanName)) ? null : getMergedBeanDefinition(beanName);
                    value = evaluateBeanDefinitionString(strVal, bd);
                }
                TypeConverter converter = typeConverter != null ? typeConverter : getTypeConverter();
                Object convertIfNecessary = descriptor.getField() != null ? converter.convertIfNecessary(value, type, descriptor.getField()) : converter.convertIfNecessary(value, type, descriptor.getMethodParameter());
                ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
                return convertIfNecessary;
            }
            Object multipleBeans = resolveMultipleBeans(descriptor, beanName, autowiredBeanNames, typeConverter);
            if (multipleBeans != null) {
                ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
                return multipleBeans;
            }
            Map<String, Object> matchingBeans = findAutowireCandidates(beanName, type, descriptor);
            if (matchingBeans.isEmpty()) {
                if (isRequired(descriptor)) {
                    raiseNoMatchingBeanFound(type, descriptor.getResolvableType(), descriptor);
                }
                ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
                return null;
            }
            if (matchingBeans.size() > 1) {
                autowiredBeanName = determineAutowireCandidate(matchingBeans, descriptor);
                if (autowiredBeanName == null) {
                    if (!isRequired(descriptor) && indicatesMultipleBeans(type)) {
                        ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
                        return null;
                    }
                    Object resolveNotUnique = descriptor.resolveNotUnique(descriptor.getResolvableType(), matchingBeans);
                    ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
                    return resolveNotUnique;
                }
                instanceCandidate = matchingBeans.get(autowiredBeanName);
            } else {
                Map.Entry<String, Object> entry = matchingBeans.entrySet().iterator().next();
                autowiredBeanName = entry.getKey();
                instanceCandidate = entry.getValue();
            }
            if (autowiredBeanNames != null) {
                autowiredBeanNames.add(autowiredBeanName);
            }
            if (instanceCandidate instanceof Class) {
                instanceCandidate = descriptor.resolveCandidate(autowiredBeanName, type, this);
            }
            Object result = instanceCandidate;
            if (result instanceof NullBean) {
                if (isRequired(descriptor)) {
                    raiseNoMatchingBeanFound(type, descriptor.getResolvableType(), descriptor);
                }
                result = null;
            }
            if (ClassUtils.isAssignableValue(type, result)) {
                Object obj = result;
                ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
                return obj;
            }
            throw new BeanNotOfRequiredTypeException(autowiredBeanName, type, instanceCandidate.getClass());
        } finally {
            ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
        }
    }

    @Nullable
    private Object resolveMultipleBeans(DependencyDescriptor descriptor, @Nullable String beanName, @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) {
        Class<?> valueType;
        Comparator<Object> comparator;
        Comparator<Object> comparator2;
        Class<?> type = descriptor.getDependencyType();
        if (descriptor instanceof StreamDependencyDescriptor) {
            Map<String, Object> matchingBeans = findAutowireCandidates(beanName, type, descriptor);
            if (autowiredBeanNames != null) {
                autowiredBeanNames.addAll(matchingBeans.keySet());
            }
            Stream<Object> stream = matchingBeans.keySet().stream().map(name -> {
                return descriptor.resolveCandidate(name, type, this);
            }).filter(bean -> {
                return !(bean instanceof NullBean);
            });
            if (((StreamDependencyDescriptor) descriptor).isOrdered()) {
                stream = stream.sorted(adaptOrderComparator(matchingBeans));
            }
            return stream;
        } else if (type.isArray()) {
            Class<?> componentType = type.getComponentType();
            ResolvableType resolvableType = descriptor.getResolvableType();
            Class<?> resolvedArrayType = resolvableType.resolve(type);
            if (resolvedArrayType != type) {
                componentType = resolvableType.getComponentType().resolve();
            }
            if (componentType == null) {
                return null;
            }
            Map<String, Object> matchingBeans2 = findAutowireCandidates(beanName, componentType, new MultiElementDescriptor(descriptor));
            if (matchingBeans2.isEmpty()) {
                return null;
            }
            if (autowiredBeanNames != null) {
                autowiredBeanNames.addAll(matchingBeans2.keySet());
            }
            TypeConverter converter = typeConverter != null ? typeConverter : getTypeConverter();
            Object result = converter.convertIfNecessary(matchingBeans2.values(), resolvedArrayType);
            if ((result instanceof Object[]) && (comparator2 = adaptDependencyComparator(matchingBeans2)) != null) {
                Arrays.sort((Object[]) result, comparator2);
            }
            return result;
        } else if (Collection.class.isAssignableFrom(type) && type.isInterface()) {
            Class<?> elementType = descriptor.getResolvableType().asCollection().resolveGeneric(new int[0]);
            if (elementType == null) {
                return null;
            }
            Map<String, Object> matchingBeans3 = findAutowireCandidates(beanName, elementType, new MultiElementDescriptor(descriptor));
            if (matchingBeans3.isEmpty()) {
                return null;
            }
            if (autowiredBeanNames != null) {
                autowiredBeanNames.addAll(matchingBeans3.keySet());
            }
            TypeConverter converter2 = typeConverter != null ? typeConverter : getTypeConverter();
            Object result2 = converter2.convertIfNecessary(matchingBeans3.values(), type);
            if ((result2 instanceof List) && (comparator = adaptDependencyComparator(matchingBeans3)) != null) {
                ((List) result2).sort(comparator);
            }
            return result2;
        } else if (Map.class == type) {
            ResolvableType mapType = descriptor.getResolvableType().asMap();
            Class<?> keyType = mapType.resolveGeneric(0);
            if (String.class != keyType || (valueType = mapType.resolveGeneric(1)) == null) {
                return null;
            }
            Map<String, Object> matchingBeans4 = findAutowireCandidates(beanName, valueType, new MultiElementDescriptor(descriptor));
            if (matchingBeans4.isEmpty()) {
                return null;
            }
            if (autowiredBeanNames != null) {
                autowiredBeanNames.addAll(matchingBeans4.keySet());
            }
            return matchingBeans4;
        } else {
            return null;
        }
    }

    private boolean isRequired(DependencyDescriptor descriptor) {
        return getAutowireCandidateResolver().isRequired(descriptor);
    }

    private boolean indicatesMultipleBeans(Class<?> type) {
        return type.isArray() || (type.isInterface() && (Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type)));
    }

    @Nullable
    private Comparator<Object> adaptDependencyComparator(Map<String, ?> matchingBeans) {
        Comparator<Object> comparator = getDependencyComparator();
        if (comparator instanceof OrderComparator) {
            return ((OrderComparator) comparator).withSourceProvider(createFactoryAwareOrderSourceProvider(matchingBeans));
        }
        return comparator;
    }

    public Comparator<Object> adaptOrderComparator(Map<String, ?> matchingBeans) {
        Comparator<Object> dependencyComparator = getDependencyComparator();
        OrderComparator comparator = dependencyComparator instanceof OrderComparator ? (OrderComparator) dependencyComparator : OrderComparator.INSTANCE;
        return comparator.withSourceProvider(createFactoryAwareOrderSourceProvider(matchingBeans));
    }

    private OrderComparator.OrderSourceProvider createFactoryAwareOrderSourceProvider(Map<String, ?> beans) {
        IdentityHashMap<Object, String> instancesToBeanNames = new IdentityHashMap<>();
        beans.forEach(beanName, instance -> {
            String str = (String) instancesToBeanNames.put(instance, beanName);
        });
        return new FactoryAwareOrderSourceProvider(instancesToBeanNames);
    }

    protected Map<String, Object> findAutowireCandidates(@Nullable String beanName, Class<?> requiredType, DependencyDescriptor descriptor) {
        String[] candidateNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(this, requiredType, true, descriptor.isEager());
        Map<String, Object> result = new LinkedHashMap<>(candidateNames.length);
        Iterator<Map.Entry<Class<?>, Object>> it = this.resolvableDependencies.entrySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Map.Entry<Class<?>, Object> classObjectEntry = it.next();
            Class<?> autowiringType = classObjectEntry.getKey();
            if (autowiringType.isAssignableFrom(requiredType)) {
                Object autowiringValue = AutowireUtils.resolveAutowiringValue(classObjectEntry.getValue(), requiredType);
                if (requiredType.isInstance(autowiringValue)) {
                    result.put(ObjectUtils.identityToString(autowiringValue), autowiringValue);
                    break;
                }
            }
        }
        for (String candidate : candidateNames) {
            if (!isSelfReference(beanName, candidate) && isAutowireCandidate(candidate, descriptor)) {
                addCandidateEntry(result, candidate, descriptor, requiredType);
            }
        }
        if (result.isEmpty()) {
            boolean multiple = indicatesMultipleBeans(requiredType);
            DependencyDescriptor fallbackDescriptor = descriptor.forFallbackMatch();
            for (String candidate2 : candidateNames) {
                if (!isSelfReference(beanName, candidate2) && isAutowireCandidate(candidate2, fallbackDescriptor) && (!multiple || getAutowireCandidateResolver().hasQualifier(descriptor))) {
                    addCandidateEntry(result, candidate2, descriptor, requiredType);
                }
            }
            if (result.isEmpty() && !multiple) {
                for (String candidate3 : candidateNames) {
                    if (isSelfReference(beanName, candidate3) && ((!(descriptor instanceof MultiElementDescriptor) || !beanName.equals(candidate3)) && isAutowireCandidate(candidate3, fallbackDescriptor))) {
                        addCandidateEntry(result, candidate3, descriptor, requiredType);
                    }
                }
            }
        }
        return result;
    }

    private void addCandidateEntry(Map<String, Object> candidates, String candidateName, DependencyDescriptor descriptor, Class<?> requiredType) {
        if (descriptor instanceof MultiElementDescriptor) {
            Object beanInstance = descriptor.resolveCandidate(candidateName, requiredType, this);
            if (!(beanInstance instanceof NullBean)) {
                candidates.put(candidateName, beanInstance);
            }
        } else if (containsSingleton(candidateName) || ((descriptor instanceof StreamDependencyDescriptor) && ((StreamDependencyDescriptor) descriptor).isOrdered())) {
            Object beanInstance2 = descriptor.resolveCandidate(candidateName, requiredType, this);
            candidates.put(candidateName, beanInstance2 instanceof NullBean ? null : beanInstance2);
        } else {
            candidates.put(candidateName, getType(candidateName));
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:40:0x003c  */
    @org.springframework.lang.Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected java.lang.String determineAutowireCandidate(java.util.Map<java.lang.String, java.lang.Object> r5, org.springframework.beans.factory.config.DependencyDescriptor r6) {
        /*
            r4 = this;
            r0 = r6
            java.lang.Class r0 = r0.getDependencyType()
            r7 = r0
            r0 = r4
            r1 = r5
            r2 = r7
            java.lang.String r0 = r0.determinePrimaryCandidate(r1, r2)
            r8 = r0
            r0 = r8
            if (r0 == 0) goto L15
            r0 = r8
            return r0
        L15:
            r0 = r4
            r1 = r5
            r2 = r7
            java.lang.String r0 = r0.determineHighestPriorityCandidate(r1, r2)
            r9 = r0
            r0 = r9
            if (r0 == 0) goto L25
            r0 = r9
            return r0
        L25:
            r0 = r5
            java.util.Set r0 = r0.entrySet()
            java.util.Iterator r0 = r0.iterator()
            r10 = r0
        L32:
            r0 = r10
            boolean r0 = r0.hasNext()
            if (r0 == 0) goto L83
            r0 = r10
            java.lang.Object r0 = r0.next()
            java.util.Map$Entry r0 = (java.util.Map.Entry) r0
            r11 = r0
            r0 = r11
            java.lang.Object r0 = r0.getKey()
            java.lang.String r0 = (java.lang.String) r0
            r12 = r0
            r0 = r11
            java.lang.Object r0 = r0.getValue()
            r13 = r0
            r0 = r13
            if (r0 == 0) goto L70
            r0 = r4
            java.util.Map<java.lang.Class<?>, java.lang.Object> r0 = r0.resolvableDependencies
            r1 = r13
            boolean r0 = r0.containsValue(r1)
            if (r0 != 0) goto L7d
        L70:
            r0 = r4
            r1 = r12
            r2 = r6
            java.lang.String r2 = r2.getDependencyName()
            boolean r0 = r0.matchesBeanName(r1, r2)
            if (r0 == 0) goto L80
        L7d:
            r0 = r12
            return r0
        L80:
            goto L32
        L83:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.beans.factory.support.DefaultListableBeanFactory.determineAutowireCandidate(java.util.Map, org.springframework.beans.factory.config.DependencyDescriptor):java.lang.String");
    }

    @Nullable
    protected String determinePrimaryCandidate(Map<String, Object> candidates, Class<?> requiredType) {
        String primaryBeanName = null;
        for (Map.Entry<String, Object> entry : candidates.entrySet()) {
            String candidateBeanName = entry.getKey();
            Object beanInstance = entry.getValue();
            if (isPrimary(candidateBeanName, beanInstance)) {
                if (primaryBeanName != null) {
                    boolean candidateLocal = containsBeanDefinition(candidateBeanName);
                    boolean primaryLocal = containsBeanDefinition(primaryBeanName);
                    if (candidateLocal && primaryLocal) {
                        throw new NoUniqueBeanDefinitionException(requiredType, candidates.size(), "more than one 'primary' bean found among candidates: " + candidates.keySet());
                    }
                    if (candidateLocal) {
                        primaryBeanName = candidateBeanName;
                    }
                } else {
                    primaryBeanName = candidateBeanName;
                }
            }
        }
        return primaryBeanName;
    }

    @Nullable
    protected String determineHighestPriorityCandidate(Map<String, Object> candidates, Class<?> requiredType) {
        Integer candidatePriority;
        String highestPriorityBeanName = null;
        Integer highestPriority = null;
        for (Map.Entry<String, Object> entry : candidates.entrySet()) {
            String candidateBeanName = entry.getKey();
            Object beanInstance = entry.getValue();
            if (beanInstance != null && (candidatePriority = getPriority(beanInstance)) != null) {
                if (highestPriorityBeanName != null) {
                    if (candidatePriority.equals(highestPriority)) {
                        throw new NoUniqueBeanDefinitionException(requiredType, candidates.size(), "Multiple beans found with the same priority ('" + highestPriority + "') among candidates: " + candidates.keySet());
                    }
                    if (candidatePriority.intValue() < highestPriority.intValue()) {
                        highestPriorityBeanName = candidateBeanName;
                        highestPriority = candidatePriority;
                    }
                } else {
                    highestPriorityBeanName = candidateBeanName;
                    highestPriority = candidatePriority;
                }
            }
        }
        return highestPriorityBeanName;
    }

    protected boolean isPrimary(String beanName, Object beanInstance) {
        if (containsBeanDefinition(beanName)) {
            return getMergedLocalBeanDefinition(beanName).isPrimary();
        }
        BeanFactory parent = getParentBeanFactory();
        return (parent instanceof DefaultListableBeanFactory) && ((DefaultListableBeanFactory) parent).isPrimary(beanName, beanInstance);
    }

    @Nullable
    protected Integer getPriority(Object beanInstance) {
        Comparator<Object> comparator = getDependencyComparator();
        if (comparator instanceof OrderComparator) {
            return ((OrderComparator) comparator).getPriority(beanInstance);
        }
        return null;
    }

    protected boolean matchesBeanName(String beanName, @Nullable String candidateName) {
        return candidateName != null && (candidateName.equals(beanName) || ObjectUtils.containsElement(getAliases(beanName), candidateName));
    }

    private boolean isSelfReference(@Nullable String beanName, @Nullable String candidateName) {
        return (beanName == null || candidateName == null || (!beanName.equals(candidateName) && (!containsBeanDefinition(candidateName) || !beanName.equals(getMergedLocalBeanDefinition(candidateName).getFactoryBeanName())))) ? false : true;
    }

    private void raiseNoMatchingBeanFound(Class<?> type, ResolvableType resolvableType, DependencyDescriptor descriptor) throws BeansException {
        checkBeanNotOfRequiredType(type, descriptor);
        throw new NoSuchBeanDefinitionException(resolvableType, "expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: " + ObjectUtils.nullSafeToString((Object[]) descriptor.getAnnotations()));
    }

    private void checkBeanNotOfRequiredType(Class<?> type, DependencyDescriptor descriptor) {
        for (String beanName : this.beanDefinitionNames) {
            RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
            Class<?> targetType = mbd.getTargetType();
            if (targetType != null && type.isAssignableFrom(targetType) && isAutowireCandidate(beanName, mbd, descriptor, getAutowireCandidateResolver())) {
                Object beanInstance = getSingleton(beanName, false);
                Class<?> beanType = (beanInstance == null || beanInstance.getClass() == NullBean.class) ? predictBeanType(beanName, mbd, new Class[0]) : beanInstance.getClass();
                if (beanType != null && !type.isAssignableFrom(beanType)) {
                    throw new BeanNotOfRequiredTypeException(beanName, type, beanType);
                }
            }
        }
        BeanFactory parent = getParentBeanFactory();
        if (parent instanceof DefaultListableBeanFactory) {
            ((DefaultListableBeanFactory) parent).checkBeanNotOfRequiredType(type, descriptor);
        }
    }

    public Optional<?> createOptionalDependency(DependencyDescriptor descriptor, @Nullable String beanName, final Object... args) {
        DependencyDescriptor descriptorToUse = new NestedDependencyDescriptor(descriptor) { // from class: org.springframework.beans.factory.support.DefaultListableBeanFactory.2
            {
                DefaultListableBeanFactory.this = this;
            }

            @Override // org.springframework.beans.factory.config.DependencyDescriptor
            public boolean isRequired() {
                return false;
            }

            @Override // org.springframework.beans.factory.config.DependencyDescriptor
            public Object resolveCandidate(String beanName2, Class<?> requiredType, BeanFactory beanFactory) {
                return !ObjectUtils.isEmpty(args) ? beanFactory.getBean(beanName2, args) : super.resolveCandidate(beanName2, requiredType, beanFactory);
            }
        };
        return Optional.ofNullable(doResolveDependency(descriptorToUse, beanName, null, null));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(ObjectUtils.identityToString(this));
        sb.append(": defining beans [");
        sb.append(StringUtils.collectionToCommaDelimitedString(this.beanDefinitionNames));
        sb.append("]; ");
        BeanFactory parent = getParentBeanFactory();
        if (parent == null) {
            sb.append("root of factory hierarchy");
        } else {
            sb.append("parent: ").append(ObjectUtils.identityToString(parent));
        }
        return sb.toString();
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        throw new NotSerializableException("DefaultListableBeanFactory itself is not deserializable - just a SerializedBeanFactoryReference is");
    }

    protected Object writeReplace() throws ObjectStreamException {
        if (this.serializationId != null) {
            return new SerializedBeanFactoryReference(this.serializationId);
        }
        throw new NotSerializableException("DefaultListableBeanFactory has no serialization id");
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/DefaultListableBeanFactory$SerializedBeanFactoryReference.class */
    private static class SerializedBeanFactoryReference implements Serializable {
        private final String id;

        public SerializedBeanFactoryReference(String id) {
            this.id = id;
        }

        private Object readResolve() {
            Object result;
            Reference<?> ref = (Reference) DefaultListableBeanFactory.serializableFactories.get(this.id);
            if (ref != null && (result = ref.get()) != null) {
                return result;
            }
            return new DefaultListableBeanFactory();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/DefaultListableBeanFactory$NestedDependencyDescriptor.class */
    public static class NestedDependencyDescriptor extends DependencyDescriptor {
        public NestedDependencyDescriptor(DependencyDescriptor original) {
            super(original);
            increaseNestingLevel();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/DefaultListableBeanFactory$MultiElementDescriptor.class */
    public static class MultiElementDescriptor extends NestedDependencyDescriptor {
        public MultiElementDescriptor(DependencyDescriptor original) {
            super(original);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/DefaultListableBeanFactory$StreamDependencyDescriptor.class */
    public static class StreamDependencyDescriptor extends DependencyDescriptor {
        private final boolean ordered;

        public StreamDependencyDescriptor(DependencyDescriptor original, boolean ordered) {
            super(original);
            this.ordered = ordered;
        }

        public boolean isOrdered() {
            return this.ordered;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/DefaultListableBeanFactory$DependencyObjectProvider.class */
    public class DependencyObjectProvider implements BeanObjectProvider<Object> {
        private final DependencyDescriptor descriptor;
        private final boolean optional;
        @Nullable
        private final String beanName;

        public DependencyObjectProvider(DependencyDescriptor descriptor, @Nullable String beanName) {
            DefaultListableBeanFactory.this = r6;
            this.descriptor = new NestedDependencyDescriptor(descriptor);
            this.optional = this.descriptor.getDependencyType() == Optional.class;
            this.beanName = beanName;
        }

        @Override // org.springframework.beans.factory.ObjectFactory
        public Object getObject() throws BeansException {
            if (this.optional) {
                return DefaultListableBeanFactory.this.createOptionalDependency(this.descriptor, this.beanName, new Object[0]);
            }
            Object result = DefaultListableBeanFactory.this.doResolveDependency(this.descriptor, this.beanName, null, null);
            if (result == null) {
                throw new NoSuchBeanDefinitionException(this.descriptor.getResolvableType());
            }
            return result;
        }

        @Override // org.springframework.beans.factory.ObjectProvider
        public Object getObject(final Object... args) throws BeansException {
            if (this.optional) {
                return DefaultListableBeanFactory.this.createOptionalDependency(this.descriptor, this.beanName, args);
            }
            DependencyDescriptor descriptorToUse = new DependencyDescriptor(this.descriptor) { // from class: org.springframework.beans.factory.support.DefaultListableBeanFactory.DependencyObjectProvider.1
                {
                    DependencyObjectProvider.this = this;
                }

                @Override // org.springframework.beans.factory.config.DependencyDescriptor
                public Object resolveCandidate(String beanName, Class<?> requiredType, BeanFactory beanFactory) {
                    return beanFactory.getBean(beanName, args);
                }
            };
            Object result = DefaultListableBeanFactory.this.doResolveDependency(descriptorToUse, this.beanName, null, null);
            if (result == null) {
                throw new NoSuchBeanDefinitionException(this.descriptor.getResolvableType());
            }
            return result;
        }

        @Override // org.springframework.beans.factory.ObjectProvider
        @Nullable
        public Object getIfAvailable() throws BeansException {
            if (this.optional) {
                return DefaultListableBeanFactory.this.createOptionalDependency(this.descriptor, this.beanName, new Object[0]);
            }
            DependencyDescriptor descriptorToUse = new DependencyDescriptor(this.descriptor) { // from class: org.springframework.beans.factory.support.DefaultListableBeanFactory.DependencyObjectProvider.2
                {
                    DependencyObjectProvider.this = this;
                }

                @Override // org.springframework.beans.factory.config.DependencyDescriptor
                public boolean isRequired() {
                    return false;
                }
            };
            return DefaultListableBeanFactory.this.doResolveDependency(descriptorToUse, this.beanName, null, null);
        }

        @Override // org.springframework.beans.factory.ObjectProvider
        @Nullable
        public Object getIfUnique() throws BeansException {
            DependencyDescriptor descriptorToUse = new DependencyDescriptor(this.descriptor) { // from class: org.springframework.beans.factory.support.DefaultListableBeanFactory.DependencyObjectProvider.3
                {
                    DependencyObjectProvider.this = this;
                }

                @Override // org.springframework.beans.factory.config.DependencyDescriptor
                public boolean isRequired() {
                    return false;
                }

                @Override // org.springframework.beans.factory.config.DependencyDescriptor
                @Nullable
                public Object resolveNotUnique(ResolvableType type, Map<String, Object> matchingBeans) {
                    return null;
                }
            };
            if (this.optional) {
                return DefaultListableBeanFactory.this.createOptionalDependency(descriptorToUse, this.beanName, new Object[0]);
            }
            return DefaultListableBeanFactory.this.doResolveDependency(descriptorToUse, this.beanName, null, null);
        }

        @Nullable
        protected Object getValue() throws BeansException {
            if (this.optional) {
                return DefaultListableBeanFactory.this.createOptionalDependency(this.descriptor, this.beanName, new Object[0]);
            }
            return DefaultListableBeanFactory.this.doResolveDependency(this.descriptor, this.beanName, null, null);
        }

        @Override // org.springframework.beans.factory.ObjectProvider
        public Stream<Object> stream() {
            return resolveStream(false);
        }

        @Override // org.springframework.beans.factory.ObjectProvider
        public Stream<Object> orderedStream() {
            return resolveStream(true);
        }

        private Stream<Object> resolveStream(boolean ordered) {
            DependencyDescriptor descriptorToUse = new StreamDependencyDescriptor(this.descriptor, ordered);
            Object result = DefaultListableBeanFactory.this.doResolveDependency(descriptorToUse, this.beanName, null, null);
            Assert.state(result instanceof Stream, "Stream expected");
            return (Stream) result;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/DefaultListableBeanFactory$Jsr330Factory.class */
    public class Jsr330Factory implements Serializable {
        private Jsr330Factory() {
            DefaultListableBeanFactory.this = r4;
        }

        public Object createDependencyProvider(DependencyDescriptor descriptor, @Nullable String beanName) {
            return new Jsr330Provider(descriptor, beanName);
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/DefaultListableBeanFactory$Jsr330Factory$Jsr330Provider.class */
        public class Jsr330Provider extends DependencyObjectProvider implements Provider<Object> {
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            public Jsr330Provider(DependencyDescriptor descriptor, @Nullable String beanName) {
                super(descriptor, beanName);
                Jsr330Factory.this = r6;
            }

            @Nullable
            public Object get() throws BeansException {
                return getValue();
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/DefaultListableBeanFactory$FactoryAwareOrderSourceProvider.class */
    public class FactoryAwareOrderSourceProvider implements OrderComparator.OrderSourceProvider {
        private final Map<Object, String> instancesToBeanNames;

        public FactoryAwareOrderSourceProvider(Map<Object, String> instancesToBeanNames) {
            DefaultListableBeanFactory.this = r4;
            this.instancesToBeanNames = instancesToBeanNames;
        }

        @Override // org.springframework.core.OrderComparator.OrderSourceProvider
        @Nullable
        public Object getOrderSource(Object obj) {
            RootBeanDefinition beanDefinition = getRootBeanDefinition(this.instancesToBeanNames.get(obj));
            if (beanDefinition == null) {
                return null;
            }
            List<Object> sources = new ArrayList<>(2);
            Method factoryMethod = beanDefinition.getResolvedFactoryMethod();
            if (factoryMethod != null) {
                sources.add(factoryMethod);
            }
            Class<?> targetType = beanDefinition.getTargetType();
            if (targetType != null && targetType != obj.getClass()) {
                sources.add(targetType);
            }
            return sources.toArray();
        }

        @Nullable
        private RootBeanDefinition getRootBeanDefinition(@Nullable String beanName) {
            if (beanName != null && DefaultListableBeanFactory.this.containsBeanDefinition(beanName)) {
                BeanDefinition bd = DefaultListableBeanFactory.this.getMergedBeanDefinition(beanName);
                if (bd instanceof RootBeanDefinition) {
                    return (RootBeanDefinition) bd;
                }
                return null;
            }
            return null;
        }
    }
}