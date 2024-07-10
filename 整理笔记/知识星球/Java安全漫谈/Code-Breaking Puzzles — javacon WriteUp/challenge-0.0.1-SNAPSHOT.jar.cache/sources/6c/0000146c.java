package org.springframework.beans.factory.support;

import java.beans.ConstructorProperties;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.core.CollectionFactory;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MethodInvoker;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/ConstructorResolver.class */
public class ConstructorResolver {
    private static final Object[] EMPTY_ARGS = new Object[0];
    private static final NamedThreadLocal<InjectionPoint> currentInjectionPoint = new NamedThreadLocal<>("Current injection point");
    private final AbstractAutowireCapableBeanFactory beanFactory;
    private final Log logger;

    public ConstructorResolver(AbstractAutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.logger = beanFactory.getLogger();
    }

    public BeanWrapper autowireConstructor(String beanName, RootBeanDefinition mbd, @Nullable Constructor<?>[] chosenCtors, @Nullable Object[] explicitArgs) {
        int minNrOfArgs;
        Constructor<?>[] constructorArr;
        ArgumentsHolder argsHolder;
        ParameterNameDiscoverer pnd;
        BeanWrapperImpl bw = new BeanWrapperImpl();
        this.beanFactory.initBeanWrapper(bw);
        Constructor<?> constructorToUse = null;
        ArgumentsHolder argsHolderToUse = null;
        Object[] argsToUse = null;
        if (explicitArgs != null) {
            argsToUse = explicitArgs;
        } else {
            Object[] argsToResolve = null;
            synchronized (mbd.constructorArgumentLock) {
                constructorToUse = (Constructor) mbd.resolvedConstructorOrFactoryMethod;
                if (constructorToUse != null && mbd.constructorArgumentsResolved) {
                    argsToUse = mbd.resolvedConstructorArguments;
                    if (argsToUse == null) {
                        argsToResolve = mbd.preparedConstructorArguments;
                    }
                }
            }
            if (argsToResolve != null) {
                argsToUse = resolvePreparedArguments(beanName, mbd, bw, constructorToUse, argsToResolve, true);
            }
        }
        if (constructorToUse == null || argsToUse == null) {
            Constructor<?>[] candidates = chosenCtors;
            if (candidates == null) {
                Class<?> beanClass = mbd.getBeanClass();
                try {
                    candidates = mbd.isNonPublicAccessAllowed() ? beanClass.getDeclaredConstructors() : beanClass.getConstructors();
                } catch (Throwable ex) {
                    throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Resolution of declared constructors on bean Class [" + beanClass.getName() + "] from ClassLoader [" + beanClass.getClassLoader() + "] failed", ex);
                }
            }
            if (candidates.length == 1 && explicitArgs == null && !mbd.hasConstructorArgumentValues()) {
                Constructor<?> uniqueCandidate = candidates[0];
                if (uniqueCandidate.getParameterCount() == 0) {
                    synchronized (mbd.constructorArgumentLock) {
                        mbd.resolvedConstructorOrFactoryMethod = uniqueCandidate;
                        mbd.constructorArgumentsResolved = true;
                        mbd.resolvedConstructorArguments = EMPTY_ARGS;
                    }
                    bw.setBeanInstance(instantiate(beanName, mbd, uniqueCandidate, EMPTY_ARGS));
                    return bw;
                }
            }
            boolean autowiring = chosenCtors != null || mbd.getResolvedAutowireMode() == 3;
            ConstructorArgumentValues resolvedValues = null;
            if (explicitArgs != null) {
                minNrOfArgs = explicitArgs.length;
            } else {
                ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
                resolvedValues = new ConstructorArgumentValues();
                minNrOfArgs = resolveConstructorArguments(beanName, mbd, bw, cargs, resolvedValues);
            }
            AutowireUtils.sortConstructors(candidates);
            int minTypeDiffWeight = Integer.MAX_VALUE;
            Set<Constructor<?>> ambiguousConstructors = null;
            LinkedList<UnsatisfiedDependencyException> causes = null;
            for (Constructor<?> candidate : candidates) {
                Class<?>[] paramTypes = candidate.getParameterTypes();
                if (constructorToUse != null && argsToUse.length > paramTypes.length) {
                    break;
                }
                if (paramTypes.length >= minNrOfArgs) {
                    if (resolvedValues != null) {
                        try {
                            String[] paramNames = ConstructorPropertiesChecker.evaluate(candidate, paramTypes.length);
                            if (paramNames == null && (pnd = this.beanFactory.getParameterNameDiscoverer()) != null) {
                                paramNames = pnd.getParameterNames(candidate);
                            }
                            argsHolder = createArgumentArray(beanName, mbd, resolvedValues, bw, paramTypes, paramNames, getUserDeclaredConstructor(candidate), autowiring, candidates.length == 1);
                        } catch (UnsatisfiedDependencyException ex2) {
                            if (this.logger.isTraceEnabled()) {
                                this.logger.trace("Ignoring constructor [" + candidate + "] of bean '" + beanName + "': " + ex2);
                            }
                            if (causes == null) {
                                causes = new LinkedList<>();
                            }
                            causes.add(ex2);
                        }
                    } else if (paramTypes.length == explicitArgs.length) {
                        argsHolder = new ArgumentsHolder(explicitArgs);
                    }
                    int typeDiffWeight = mbd.isLenientConstructorResolution() ? argsHolder.getTypeDifferenceWeight(paramTypes) : argsHolder.getAssignabilityWeight(paramTypes);
                    if (typeDiffWeight < minTypeDiffWeight) {
                        constructorToUse = candidate;
                        argsHolderToUse = argsHolder;
                        argsToUse = argsHolder.arguments;
                        minTypeDiffWeight = typeDiffWeight;
                        ambiguousConstructors = null;
                    } else if (constructorToUse != null && typeDiffWeight == minTypeDiffWeight) {
                        if (ambiguousConstructors == null) {
                            ambiguousConstructors = new LinkedHashSet<>();
                            ambiguousConstructors.add(constructorToUse);
                        }
                        ambiguousConstructors.add(candidate);
                    }
                }
            }
            if (constructorToUse == null) {
                if (causes != null) {
                    UnsatisfiedDependencyException ex3 = causes.removeLast();
                    Iterator<UnsatisfiedDependencyException> it = causes.iterator();
                    while (it.hasNext()) {
                        UnsatisfiedDependencyException cause = it.next();
                        this.beanFactory.onSuppressedException(cause);
                    }
                    throw ex3;
                }
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Could not resolve matching constructor (hint: specify index/type/name arguments for simple parameters to avoid type ambiguities)");
            } else if (ambiguousConstructors != null && !mbd.isLenientConstructorResolution()) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Ambiguous constructor matches found in bean '" + beanName + "' (hint: specify index/type/name arguments for simple parameters to avoid type ambiguities): " + ambiguousConstructors);
            } else {
                if (explicitArgs == null) {
                    argsHolderToUse.storeCache(mbd, constructorToUse);
                }
            }
        }
        bw.setBeanInstance(instantiate(beanName, mbd, constructorToUse, argsToUse));
        return bw;
    }

    private Object instantiate(String beanName, RootBeanDefinition mbd, Constructor constructorToUse, Object[] argsToUse) {
        try {
            InstantiationStrategy strategy = this.beanFactory.getInstantiationStrategy();
            if (System.getSecurityManager() != null) {
                return AccessController.doPrivileged(() -> {
                    return strategy.instantiate(mbd, beanName, this.beanFactory, constructorToUse, argsToUse);
                }, this.beanFactory.getAccessControlContext());
            }
            return strategy.instantiate(mbd, beanName, this.beanFactory, constructorToUse, argsToUse);
        } catch (Throwable ex) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Bean instantiation via constructor failed", ex);
        }
    }

    public void resolveFactoryMethodIfPossible(RootBeanDefinition mbd) {
        Class<?> factoryClass;
        boolean isStatic;
        if (mbd.getFactoryBeanName() != null) {
            factoryClass = this.beanFactory.getType(mbd.getFactoryBeanName());
            isStatic = false;
        } else {
            factoryClass = mbd.getBeanClass();
            isStatic = true;
        }
        Assert.state(factoryClass != null, "Unresolvable factory class");
        Method[] candidates = getCandidateMethods(ClassUtils.getUserClass(factoryClass), mbd);
        Method uniqueCandidate = null;
        int length = candidates.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            Method candidate = candidates[i];
            if (Modifier.isStatic(candidate.getModifiers()) == isStatic && mbd.isFactoryMethod(candidate)) {
                if (uniqueCandidate == null) {
                    uniqueCandidate = candidate;
                } else if (!Arrays.equals(uniqueCandidate.getParameterTypes(), candidate.getParameterTypes())) {
                    uniqueCandidate = null;
                    break;
                }
            }
            i++;
        }
        mbd.factoryMethodToIntrospect = uniqueCandidate;
    }

    private Method[] getCandidateMethods(Class<?> factoryClass, RootBeanDefinition mbd) {
        if (System.getSecurityManager() != null) {
            return (Method[]) AccessController.doPrivileged(() -> {
                return mbd.isNonPublicAccessAllowed() ? ReflectionUtils.getAllDeclaredMethods(factoryClass) : factoryClass.getMethods();
            });
        }
        return mbd.isNonPublicAccessAllowed() ? ReflectionUtils.getAllDeclaredMethods(factoryClass) : factoryClass.getMethods();
    }

    public BeanWrapper instantiateUsingFactoryMethod(String beanName, RootBeanDefinition mbd, @Nullable Object[] explicitArgs) {
        Object factoryBean;
        Class<?> factoryClass;
        boolean isStatic;
        int minNrOfArgs;
        String simpleName;
        ArgumentsHolder argsHolder;
        BeanWrapperImpl bw = new BeanWrapperImpl();
        this.beanFactory.initBeanWrapper(bw);
        String factoryBeanName = mbd.getFactoryBeanName();
        if (factoryBeanName != null) {
            if (factoryBeanName.equals(beanName)) {
                throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName, "factory-bean reference points back to the same bean definition");
            }
            factoryBean = this.beanFactory.getBean(factoryBeanName);
            if (mbd.isSingleton() && this.beanFactory.containsSingleton(beanName)) {
                throw new ImplicitlyAppearedSingletonException();
            }
            factoryClass = factoryBean.getClass();
            isStatic = false;
        } else if (!mbd.hasBeanClass()) {
            throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName, "bean definition declares neither a bean class nor a factory-bean reference");
        } else {
            factoryBean = null;
            factoryClass = mbd.getBeanClass();
            isStatic = true;
        }
        Method factoryMethodToUse = null;
        ArgumentsHolder argsHolderToUse = null;
        Object[] argsToUse = null;
        if (explicitArgs != null) {
            argsToUse = explicitArgs;
        } else {
            Object[] argsToResolve = null;
            synchronized (mbd.constructorArgumentLock) {
                factoryMethodToUse = (Method) mbd.resolvedConstructorOrFactoryMethod;
                if (factoryMethodToUse != null && mbd.constructorArgumentsResolved) {
                    argsToUse = mbd.resolvedConstructorArguments;
                    if (argsToUse == null) {
                        argsToResolve = mbd.preparedConstructorArguments;
                    }
                }
            }
            if (argsToResolve != null) {
                argsToUse = resolvePreparedArguments(beanName, mbd, bw, factoryMethodToUse, argsToResolve, true);
            }
        }
        if (factoryMethodToUse == null || argsToUse == null) {
            Method[] rawCandidates = getCandidateMethods(ClassUtils.getUserClass(factoryClass), mbd);
            List<Method> candidateList = new ArrayList<>();
            for (Method candidate : rawCandidates) {
                if (Modifier.isStatic(candidate.getModifiers()) == isStatic && mbd.isFactoryMethod(candidate)) {
                    candidateList.add(candidate);
                }
            }
            if (candidateList.size() == 1 && explicitArgs == null && !mbd.hasConstructorArgumentValues()) {
                Method uniqueCandidate = candidateList.get(0);
                if (uniqueCandidate.getParameterCount() == 0) {
                    mbd.factoryMethodToIntrospect = uniqueCandidate;
                    synchronized (mbd.constructorArgumentLock) {
                        mbd.resolvedConstructorOrFactoryMethod = uniqueCandidate;
                        mbd.constructorArgumentsResolved = true;
                        mbd.resolvedConstructorArguments = EMPTY_ARGS;
                    }
                    bw.setBeanInstance(instantiate(beanName, mbd, factoryBean, uniqueCandidate, EMPTY_ARGS));
                    return bw;
                }
            }
            Method[] candidates = (Method[]) candidateList.toArray(new Method[0]);
            AutowireUtils.sortFactoryMethods(candidates);
            ConstructorArgumentValues resolvedValues = null;
            boolean autowiring = mbd.getResolvedAutowireMode() == 3;
            int minTypeDiffWeight = Integer.MAX_VALUE;
            Set<Method> ambiguousFactoryMethods = null;
            if (explicitArgs != null) {
                minNrOfArgs = explicitArgs.length;
            } else if (mbd.hasConstructorArgumentValues()) {
                ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
                resolvedValues = new ConstructorArgumentValues();
                minNrOfArgs = resolveConstructorArguments(beanName, mbd, bw, cargs, resolvedValues);
            } else {
                minNrOfArgs = 0;
            }
            LinkedList<UnsatisfiedDependencyException> causes = null;
            for (Method candidate2 : candidates) {
                Class<?>[] paramTypes = candidate2.getParameterTypes();
                if (paramTypes.length >= minNrOfArgs) {
                    if (explicitArgs != null) {
                        if (paramTypes.length == explicitArgs.length) {
                            argsHolder = new ArgumentsHolder(explicitArgs);
                        }
                    } else {
                        try {
                            String[] paramNames = null;
                            ParameterNameDiscoverer pnd = this.beanFactory.getParameterNameDiscoverer();
                            if (pnd != null) {
                                paramNames = pnd.getParameterNames(candidate2);
                            }
                            argsHolder = createArgumentArray(beanName, mbd, resolvedValues, bw, paramTypes, paramNames, candidate2, autowiring, candidates.length == 1);
                        } catch (UnsatisfiedDependencyException ex) {
                            if (this.logger.isTraceEnabled()) {
                                this.logger.trace("Ignoring factory method [" + candidate2 + "] of bean '" + beanName + "': " + ex);
                            }
                            if (causes == null) {
                                causes = new LinkedList<>();
                            }
                            causes.add(ex);
                        }
                    }
                    int typeDiffWeight = mbd.isLenientConstructorResolution() ? argsHolder.getTypeDifferenceWeight(paramTypes) : argsHolder.getAssignabilityWeight(paramTypes);
                    if (typeDiffWeight < minTypeDiffWeight) {
                        factoryMethodToUse = candidate2;
                        argsHolderToUse = argsHolder;
                        argsToUse = argsHolder.arguments;
                        minTypeDiffWeight = typeDiffWeight;
                        ambiguousFactoryMethods = null;
                    } else if (factoryMethodToUse != null && typeDiffWeight == minTypeDiffWeight && !mbd.isLenientConstructorResolution() && paramTypes.length == factoryMethodToUse.getParameterCount() && !Arrays.equals(paramTypes, factoryMethodToUse.getParameterTypes())) {
                        if (ambiguousFactoryMethods == null) {
                            ambiguousFactoryMethods = new LinkedHashSet<>();
                            ambiguousFactoryMethods.add(factoryMethodToUse);
                        }
                        ambiguousFactoryMethods.add(candidate2);
                    }
                }
            }
            if (factoryMethodToUse == null) {
                if (causes != null) {
                    UnsatisfiedDependencyException ex2 = causes.removeLast();
                    Iterator<UnsatisfiedDependencyException> it = causes.iterator();
                    while (it.hasNext()) {
                        UnsatisfiedDependencyException cause = it.next();
                        this.beanFactory.onSuppressedException(cause);
                    }
                    throw ex2;
                }
                List<String> argTypes = new ArrayList<>(minNrOfArgs);
                if (explicitArgs != null) {
                    int length = explicitArgs.length;
                    for (int i = 0; i < length; i++) {
                        Object arg = explicitArgs[i];
                        argTypes.add(arg != null ? arg.getClass().getSimpleName() : BeanDefinitionParserDelegate.NULL_ELEMENT);
                    }
                } else if (resolvedValues != null) {
                    Set<ConstructorArgumentValues.ValueHolder> valueHolders = new LinkedHashSet<>(resolvedValues.getArgumentCount());
                    valueHolders.addAll(resolvedValues.getIndexedArgumentValues().values());
                    valueHolders.addAll(resolvedValues.getGenericArgumentValues());
                    for (ConstructorArgumentValues.ValueHolder value : valueHolders) {
                        if (value.getType() != null) {
                            simpleName = ClassUtils.getShortName(value.getType());
                        } else {
                            simpleName = value.getValue() != null ? value.getValue().getClass().getSimpleName() : BeanDefinitionParserDelegate.NULL_ELEMENT;
                        }
                        String argType = simpleName;
                        argTypes.add(argType);
                    }
                }
                String argDesc = StringUtils.collectionToCommaDelimitedString(argTypes);
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "No matching factory method found: " + (mbd.getFactoryBeanName() != null ? "factory bean '" + mbd.getFactoryBeanName() + "'; " : "") + "factory method '" + mbd.getFactoryMethodName() + "(" + argDesc + ")'. Check that a method with the specified name " + (minNrOfArgs > 0 ? "and arguments " : "") + "exists and that it is " + (isStatic ? "static" : "non-static") + ".");
            } else if (Void.TYPE == factoryMethodToUse.getReturnType()) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid factory method '" + mbd.getFactoryMethodName() + "': needs to have a non-void return type!");
            } else {
                if (ambiguousFactoryMethods != null) {
                    throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Ambiguous factory method matches found in bean '" + beanName + "' (hint: specify index/type/name arguments for simple parameters to avoid type ambiguities): " + ambiguousFactoryMethods);
                }
                if (explicitArgs == null && argsHolderToUse != null) {
                    mbd.factoryMethodToIntrospect = factoryMethodToUse;
                    argsHolderToUse.storeCache(mbd, factoryMethodToUse);
                }
            }
        }
        bw.setBeanInstance(instantiate(beanName, mbd, factoryBean, factoryMethodToUse, argsToUse));
        return bw;
    }

    private Object instantiate(String beanName, RootBeanDefinition mbd, Object factoryBean, Method factoryMethod, Object[] args) {
        try {
            if (System.getSecurityManager() != null) {
                return AccessController.doPrivileged(() -> {
                    return this.beanFactory.getInstantiationStrategy().instantiate(mbd, beanName, this.beanFactory, factoryBean, factoryMethod, args);
                }, this.beanFactory.getAccessControlContext());
            }
            return this.beanFactory.getInstantiationStrategy().instantiate(mbd, beanName, this.beanFactory, factoryBean, factoryMethod, args);
        } catch (Throwable ex) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Bean instantiation via factory method failed", ex);
        }
    }

    private int resolveConstructorArguments(String beanName, RootBeanDefinition mbd, BeanWrapper bw, ConstructorArgumentValues cargs, ConstructorArgumentValues resolvedValues) {
        TypeConverter customConverter = this.beanFactory.getCustomTypeConverter();
        TypeConverter converter = customConverter != 0 ? customConverter : bw;
        BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this.beanFactory, beanName, mbd, converter);
        int minNrOfArgs = cargs.getArgumentCount();
        for (Map.Entry<Integer, ConstructorArgumentValues.ValueHolder> entry : cargs.getIndexedArgumentValues().entrySet()) {
            int index = entry.getKey().intValue();
            if (index < 0) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid constructor argument index: " + index);
            }
            if (index > minNrOfArgs) {
                minNrOfArgs = index + 1;
            }
            ConstructorArgumentValues.ValueHolder valueHolder = entry.getValue();
            if (valueHolder.isConverted()) {
                resolvedValues.addIndexedArgumentValue(index, valueHolder);
            } else {
                Object resolvedValue = valueResolver.resolveValueIfNecessary("constructor argument", valueHolder.getValue());
                ConstructorArgumentValues.ValueHolder resolvedValueHolder = new ConstructorArgumentValues.ValueHolder(resolvedValue, valueHolder.getType(), valueHolder.getName());
                resolvedValueHolder.setSource(valueHolder);
                resolvedValues.addIndexedArgumentValue(index, resolvedValueHolder);
            }
        }
        for (ConstructorArgumentValues.ValueHolder valueHolder2 : cargs.getGenericArgumentValues()) {
            if (valueHolder2.isConverted()) {
                resolvedValues.addGenericArgumentValue(valueHolder2);
            } else {
                Object resolvedValue2 = valueResolver.resolveValueIfNecessary("constructor argument", valueHolder2.getValue());
                ConstructorArgumentValues.ValueHolder resolvedValueHolder2 = new ConstructorArgumentValues.ValueHolder(resolvedValue2, valueHolder2.getType(), valueHolder2.getName());
                resolvedValueHolder2.setSource(valueHolder2);
                resolvedValues.addGenericArgumentValue(resolvedValueHolder2);
            }
        }
        return minNrOfArgs;
    }

    private ArgumentsHolder createArgumentArray(String beanName, RootBeanDefinition mbd, @Nullable ConstructorArgumentValues resolvedValues, BeanWrapper bw, Class<?>[] paramTypes, @Nullable String[] paramNames, Executable executable, boolean autowiring, boolean fallback) throws UnsatisfiedDependencyException {
        Object convertedValue;
        TypeConverter customConverter = this.beanFactory.getCustomTypeConverter();
        TypeConverter converter = customConverter != null ? customConverter : bw;
        ArgumentsHolder args = new ArgumentsHolder(paramTypes.length);
        Set<ConstructorArgumentValues.ValueHolder> usedValueHolders = new HashSet<>(paramTypes.length);
        Set<String> autowiredBeanNames = new LinkedHashSet<>(4);
        for (int paramIndex = 0; paramIndex < paramTypes.length; paramIndex++) {
            Class<?> paramType = paramTypes[paramIndex];
            String paramName = paramNames != null ? paramNames[paramIndex] : "";
            ConstructorArgumentValues.ValueHolder valueHolder = null;
            if (resolvedValues != null) {
                valueHolder = resolvedValues.getArgumentValue(paramIndex, paramType, paramName, usedValueHolders);
                if (valueHolder == null && (!autowiring || paramTypes.length == resolvedValues.getArgumentCount())) {
                    valueHolder = resolvedValues.getGenericArgumentValue(null, null, usedValueHolders);
                }
            }
            if (valueHolder != null) {
                usedValueHolders.add(valueHolder);
                Object originalValue = valueHolder.getValue();
                if (valueHolder.isConverted()) {
                    convertedValue = valueHolder.getConvertedValue();
                    args.preparedArguments[paramIndex] = convertedValue;
                } else {
                    MethodParameter methodParam = MethodParameter.forExecutable(executable, paramIndex);
                    try {
                        convertedValue = converter.convertIfNecessary(originalValue, paramType, methodParam);
                        Object sourceHolder = valueHolder.getSource();
                        if (sourceHolder instanceof ConstructorArgumentValues.ValueHolder) {
                            Object sourceValue = ((ConstructorArgumentValues.ValueHolder) sourceHolder).getValue();
                            args.resolveNecessary = true;
                            args.preparedArguments[paramIndex] = sourceValue;
                        }
                    } catch (TypeMismatchException ex) {
                        throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam), "Could not convert argument value of type [" + ObjectUtils.nullSafeClassName(valueHolder.getValue()) + "] to required type [" + paramType.getName() + "]: " + ex.getMessage());
                    }
                }
                args.arguments[paramIndex] = convertedValue;
                args.rawArguments[paramIndex] = originalValue;
            } else {
                MethodParameter methodParam2 = MethodParameter.forExecutable(executable, paramIndex);
                if (!autowiring) {
                    throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam2), "Ambiguous argument values for parameter of type [" + paramType.getName() + "] - did you specify the correct bean references as arguments?");
                }
                try {
                    Object autowiredArgument = resolveAutowiredArgument(methodParam2, beanName, autowiredBeanNames, converter, fallback);
                    args.rawArguments[paramIndex] = autowiredArgument;
                    args.arguments[paramIndex] = autowiredArgument;
                    args.preparedArguments[paramIndex] = new AutowiredArgumentMarker();
                    args.resolveNecessary = true;
                } catch (BeansException ex2) {
                    throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam2), ex2);
                }
            }
        }
        for (String autowiredBeanName : autowiredBeanNames) {
            this.beanFactory.registerDependentBean(autowiredBeanName, beanName);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Autowiring by type from bean name '" + beanName + "' via " + (executable instanceof Constructor ? BeanDefinitionParserDelegate.AUTOWIRE_CONSTRUCTOR_VALUE : "factory method") + " to bean named '" + autowiredBeanName + "'");
            }
        }
        return args;
    }

    private Object[] resolvePreparedArguments(String beanName, RootBeanDefinition mbd, BeanWrapper bw, Executable executable, Object[] argsToResolve, boolean fallback) {
        TypeConverter customConverter = this.beanFactory.getCustomTypeConverter();
        TypeConverter converter = customConverter != null ? customConverter : bw;
        BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this.beanFactory, beanName, mbd, converter);
        Class<?>[] paramTypes = executable.getParameterTypes();
        Object[] resolvedArgs = new Object[argsToResolve.length];
        for (int argIndex = 0; argIndex < argsToResolve.length; argIndex++) {
            Object argValue = argsToResolve[argIndex];
            MethodParameter methodParam = MethodParameter.forExecutable(executable, argIndex);
            GenericTypeResolver.resolveParameterType(methodParam, executable.getDeclaringClass());
            if (argValue instanceof AutowiredArgumentMarker) {
                argValue = resolveAutowiredArgument(methodParam, beanName, null, converter, fallback);
            } else if (argValue instanceof BeanMetadataElement) {
                argValue = valueResolver.resolveValueIfNecessary("constructor argument", argValue);
            } else if (argValue instanceof String) {
                argValue = this.beanFactory.evaluateBeanDefinitionString((String) argValue, mbd);
            }
            Class<?> paramType = paramTypes[argIndex];
            try {
                resolvedArgs[argIndex] = converter.convertIfNecessary(argValue, paramType, methodParam);
            } catch (TypeMismatchException ex) {
                throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam), "Could not convert argument value of type [" + ObjectUtils.nullSafeClassName(argValue) + "] to required type [" + paramType.getName() + "]: " + ex.getMessage());
            }
        }
        return resolvedArgs;
    }

    protected Constructor<?> getUserDeclaredConstructor(Constructor<?> constructor) {
        Class<?> declaringClass = constructor.getDeclaringClass();
        Class<?> userClass = ClassUtils.getUserClass(declaringClass);
        if (userClass != declaringClass) {
            try {
                return userClass.getDeclaredConstructor(constructor.getParameterTypes());
            } catch (NoSuchMethodException e) {
            }
        }
        return constructor;
    }

    @Nullable
    protected Object resolveAutowiredArgument(MethodParameter param, String beanName, @Nullable Set<String> autowiredBeanNames, TypeConverter typeConverter, boolean fallback) {
        Class<?> paramType = param.getParameterType();
        if (InjectionPoint.class.isAssignableFrom(paramType)) {
            InjectionPoint injectionPoint = currentInjectionPoint.get();
            if (injectionPoint == null) {
                throw new IllegalStateException("No current InjectionPoint available for " + param);
            }
            return injectionPoint;
        }
        try {
            return this.beanFactory.resolveDependency(new DependencyDescriptor(param, true), beanName, autowiredBeanNames, typeConverter);
        } catch (NoUniqueBeanDefinitionException ex) {
            throw ex;
        } catch (NoSuchBeanDefinitionException ex2) {
            if (fallback) {
                if (paramType.isArray()) {
                    return Array.newInstance(paramType.getComponentType(), 0);
                }
                if (CollectionFactory.isApproximableCollectionType(paramType)) {
                    return CollectionFactory.createCollection(paramType, 0);
                }
                if (CollectionFactory.isApproximableMapType(paramType)) {
                    return CollectionFactory.createMap(paramType, 0);
                }
            }
            throw ex2;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static InjectionPoint setCurrentInjectionPoint(@Nullable InjectionPoint injectionPoint) {
        InjectionPoint old = currentInjectionPoint.get();
        if (injectionPoint != null) {
            currentInjectionPoint.set(injectionPoint);
        } else {
            currentInjectionPoint.remove();
        }
        return old;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/ConstructorResolver$ArgumentsHolder.class */
    public static class ArgumentsHolder {
        public final Object[] rawArguments;
        public final Object[] arguments;
        public final Object[] preparedArguments;
        public boolean resolveNecessary = false;

        public ArgumentsHolder(int size) {
            this.rawArguments = new Object[size];
            this.arguments = new Object[size];
            this.preparedArguments = new Object[size];
        }

        public ArgumentsHolder(Object[] args) {
            this.rawArguments = args;
            this.arguments = args;
            this.preparedArguments = args;
        }

        public int getTypeDifferenceWeight(Class<?>[] paramTypes) {
            int typeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, this.arguments);
            int rawTypeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, this.rawArguments) - 1024;
            return rawTypeDiffWeight < typeDiffWeight ? rawTypeDiffWeight : typeDiffWeight;
        }

        public int getAssignabilityWeight(Class<?>[] paramTypes) {
            for (int i = 0; i < paramTypes.length; i++) {
                if (!ClassUtils.isAssignableValue(paramTypes[i], this.arguments[i])) {
                    return Integer.MAX_VALUE;
                }
            }
            for (int i2 = 0; i2 < paramTypes.length; i2++) {
                if (!ClassUtils.isAssignableValue(paramTypes[i2], this.rawArguments[i2])) {
                    return 2147483135;
                }
            }
            return 2147482623;
        }

        public void storeCache(RootBeanDefinition mbd, Executable constructorOrFactoryMethod) {
            synchronized (mbd.constructorArgumentLock) {
                mbd.resolvedConstructorOrFactoryMethod = constructorOrFactoryMethod;
                mbd.constructorArgumentsResolved = true;
                if (this.resolveNecessary) {
                    mbd.preparedConstructorArguments = this.preparedArguments;
                } else {
                    mbd.resolvedConstructorArguments = this.arguments;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/ConstructorResolver$AutowiredArgumentMarker.class */
    public static class AutowiredArgumentMarker {
        private AutowiredArgumentMarker() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/ConstructorResolver$ConstructorPropertiesChecker.class */
    public static class ConstructorPropertiesChecker {
        private ConstructorPropertiesChecker() {
        }

        @Nullable
        public static String[] evaluate(Constructor<?> candidate, int paramCount) {
            ConstructorProperties cp = candidate.getAnnotation(ConstructorProperties.class);
            if (cp != null) {
                String[] names = cp.value();
                if (names.length != paramCount) {
                    throw new IllegalStateException("Constructor annotated with @ConstructorProperties but not corresponding to actual number of parameters (" + paramCount + "): " + candidate);
                }
                return names;
            }
            return null;
        }
    }
}