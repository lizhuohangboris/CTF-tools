package org.springframework.aop.aspectj;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.NamePattern;
import org.aspectj.weaver.reflect.ReflectionWorld;
import org.aspectj.weaver.reflect.ShadowMatchImpl;
import org.aspectj.weaver.tools.ContextBasedMatcher;
import org.aspectj.weaver.tools.JoinPointMatch;
import org.aspectj.weaver.tools.MatchingContext;
import org.aspectj.weaver.tools.PointcutDesignatorHandler;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParameter;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;
import org.aspectj.weaver.tools.ShadowMatch;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.IntroductionAwareMethodMatcher;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.framework.autoproxy.ProxyCreationContext;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.aop.support.AbstractExpressionPointcut;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/AspectJExpressionPointcut.class */
public class AspectJExpressionPointcut extends AbstractExpressionPointcut implements ClassFilter, IntroductionAwareMethodMatcher, BeanFactoryAware {
    private static final Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet();
    private static final Log logger;
    @Nullable
    private Class<?> pointcutDeclarationScope;
    private String[] pointcutParameterNames;
    private Class<?>[] pointcutParameterTypes;
    @Nullable
    private BeanFactory beanFactory;
    @Nullable
    private transient ClassLoader pointcutClassLoader;
    @Nullable
    private transient PointcutExpression pointcutExpression;
    private transient Map<Method, ShadowMatch> shadowMatchCache;

    static {
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.ARGS);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.REFERENCE);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.THIS);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.TARGET);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.WITHIN);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ANNOTATION);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_WITHIN);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ARGS);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_TARGET);
        logger = LogFactory.getLog(AspectJExpressionPointcut.class);
    }

    public AspectJExpressionPointcut() {
        this.pointcutParameterNames = new String[0];
        this.pointcutParameterTypes = new Class[0];
        this.shadowMatchCache = new ConcurrentHashMap(32);
    }

    public AspectJExpressionPointcut(Class<?> declarationScope, String[] paramNames, Class<?>[] paramTypes) {
        this.pointcutParameterNames = new String[0];
        this.pointcutParameterTypes = new Class[0];
        this.shadowMatchCache = new ConcurrentHashMap(32);
        this.pointcutDeclarationScope = declarationScope;
        if (paramNames.length != paramTypes.length) {
            throw new IllegalStateException("Number of pointcut parameter names must match number of pointcut parameter types");
        }
        this.pointcutParameterNames = paramNames;
        this.pointcutParameterTypes = paramTypes;
    }

    public void setPointcutDeclarationScope(Class<?> pointcutDeclarationScope) {
        this.pointcutDeclarationScope = pointcutDeclarationScope;
    }

    public void setParameterNames(String... names) {
        this.pointcutParameterNames = names;
    }

    public void setParameterTypes(Class<?>... types) {
        this.pointcutParameterTypes = types;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override // org.springframework.aop.Pointcut
    public ClassFilter getClassFilter() {
        obtainPointcutExpression();
        return this;
    }

    @Override // org.springframework.aop.Pointcut
    public MethodMatcher getMethodMatcher() {
        obtainPointcutExpression();
        return this;
    }

    private PointcutExpression obtainPointcutExpression() {
        if (getExpression() == null) {
            throw new IllegalStateException("Must set property 'expression' before attempting to match");
        }
        if (this.pointcutExpression == null) {
            this.pointcutClassLoader = determinePointcutClassLoader();
            this.pointcutExpression = buildPointcutExpression(this.pointcutClassLoader);
        }
        return this.pointcutExpression;
    }

    @Nullable
    private ClassLoader determinePointcutClassLoader() {
        if (this.beanFactory instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory) this.beanFactory).getBeanClassLoader();
        }
        if (this.pointcutDeclarationScope != null) {
            return this.pointcutDeclarationScope.getClassLoader();
        }
        return ClassUtils.getDefaultClassLoader();
    }

    private PointcutExpression buildPointcutExpression(@Nullable ClassLoader classLoader) {
        PointcutParser parser = initializePointcutParser(classLoader);
        PointcutParameter[] pointcutParameters = new PointcutParameter[this.pointcutParameterNames.length];
        for (int i = 0; i < pointcutParameters.length; i++) {
            pointcutParameters[i] = parser.createPointcutParameter(this.pointcutParameterNames[i], this.pointcutParameterTypes[i]);
        }
        return parser.parsePointcutExpression(replaceBooleanOperators(resolveExpression()), this.pointcutDeclarationScope, pointcutParameters);
    }

    private String resolveExpression() {
        String expression = getExpression();
        Assert.state(expression != null, "No expression set");
        return expression;
    }

    private PointcutParser initializePointcutParser(@Nullable ClassLoader classLoader) {
        PointcutParser parser = PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(SUPPORTED_PRIMITIVES, classLoader);
        parser.registerPointcutDesignatorHandler(new BeanPointcutDesignatorHandler());
        return parser;
    }

    private String replaceBooleanOperators(String pcExpr) {
        String result = StringUtils.replace(pcExpr, " and ", " && ");
        return StringUtils.replace(StringUtils.replace(result, " or ", " || "), " not ", " ! ");
    }

    public PointcutExpression getPointcutExpression() {
        return obtainPointcutExpression();
    }

    @Override // org.springframework.aop.ClassFilter
    public boolean matches(Class<?> targetClass) {
        PointcutExpression pointcutExpression = obtainPointcutExpression();
        try {
            try {
                return pointcutExpression.couldMatchJoinPointsInType(targetClass);
            } catch (ReflectionWorld.ReflectionWorldException e) {
                logger.debug("PointcutExpression matching rejected target class - trying fallback expression", e);
                PointcutExpression fallbackExpression = getFallbackPointcutExpression(targetClass);
                if (fallbackExpression != null) {
                    return fallbackExpression.couldMatchJoinPointsInType(targetClass);
                }
                return false;
            }
        } catch (Throwable ex) {
            logger.debug("PointcutExpression matching rejected target class", ex);
            return false;
        }
    }

    @Override // org.springframework.aop.IntroductionAwareMethodMatcher
    public boolean matches(Method method, Class<?> targetClass, boolean hasIntroductions) {
        obtainPointcutExpression();
        ShadowMatch shadowMatch = getTargetShadowMatch(method, targetClass);
        if (shadowMatch.alwaysMatches()) {
            return true;
        }
        if (shadowMatch.neverMatches()) {
            return false;
        }
        if (hasIntroductions) {
            return true;
        }
        RuntimeTestWalker walker = getRuntimeTestWalker(shadowMatch);
        return !walker.testsSubtypeSensitiveVars() || walker.testTargetInstanceOfResidue(targetClass);
    }

    @Override // org.springframework.aop.MethodMatcher
    public boolean matches(Method method, Class<?> targetClass) {
        return matches(method, targetClass, false);
    }

    @Override // org.springframework.aop.MethodMatcher
    public boolean isRuntime() {
        return obtainPointcutExpression().mayNeedDynamicTest();
    }

    @Override // org.springframework.aop.MethodMatcher
    public boolean matches(Method method, Class<?> targetClass, Object... args) {
        MethodInvocation mi;
        obtainPointcutExpression();
        ShadowMatch shadowMatch = getTargetShadowMatch(method, targetClass);
        ProxyMethodInvocation pmi = null;
        Object targetObject = null;
        Object thisObject = null;
        try {
            mi = ExposeInvocationInterceptor.currentInvocation();
            targetObject = mi.getThis();
        } catch (IllegalStateException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Could not access current invocation - matching with limited context: " + ex);
            }
        }
        if (!(mi instanceof ProxyMethodInvocation)) {
            throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
        }
        pmi = (ProxyMethodInvocation) mi;
        thisObject = pmi.getProxy();
        try {
            JoinPointMatch joinPointMatch = shadowMatch.matchesJoinPoint(thisObject, targetObject, args);
            if (pmi != null && thisObject != null) {
                RuntimeTestWalker originalMethodResidueTest = getRuntimeTestWalker(getShadowMatch(method, method));
                if (!originalMethodResidueTest.testThisInstanceOfResidue(thisObject.getClass())) {
                    return false;
                }
                if (joinPointMatch.matches()) {
                    bindParameters(pmi, joinPointMatch);
                }
            }
            return joinPointMatch.matches();
        } catch (Throwable ex2) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to evaluate join point for arguments " + Arrays.asList(args) + " - falling back to non-match", ex2);
                return false;
            }
            return false;
        }
    }

    @Nullable
    protected String getCurrentProxiedBeanName() {
        return ProxyCreationContext.getCurrentProxiedBeanName();
    }

    @Nullable
    private PointcutExpression getFallbackPointcutExpression(Class<?> targetClass) {
        try {
            ClassLoader classLoader = targetClass.getClassLoader();
            if (classLoader != null && classLoader != this.pointcutClassLoader) {
                return buildPointcutExpression(classLoader);
            }
            return null;
        } catch (Throwable ex) {
            logger.debug("Failed to create fallback PointcutExpression", ex);
            return null;
        }
    }

    private RuntimeTestWalker getRuntimeTestWalker(ShadowMatch shadowMatch) {
        if (shadowMatch instanceof DefensiveShadowMatch) {
            return new RuntimeTestWalker(((DefensiveShadowMatch) shadowMatch).primary);
        }
        return new RuntimeTestWalker(shadowMatch);
    }

    private void bindParameters(ProxyMethodInvocation invocation, JoinPointMatch jpm) {
        invocation.setUserAttribute(resolveExpression(), jpm);
    }

    private ShadowMatch getTargetShadowMatch(Method method, Class<?> targetClass) {
        Method targetMethod = AopUtils.getMostSpecificMethod(method, targetClass);
        if (targetMethod.getDeclaringClass().isInterface()) {
            Set<Class<?>> ifcs = ClassUtils.getAllInterfacesForClassAsSet(targetClass);
            if (ifcs.size() > 1) {
                try {
                    Class<?> compositeInterface = ClassUtils.createCompositeInterface(ClassUtils.toClassArray(ifcs), targetClass.getClassLoader());
                    targetMethod = ClassUtils.getMostSpecificMethod(targetMethod, compositeInterface);
                } catch (IllegalArgumentException e) {
                }
            }
        }
        return getShadowMatch(targetMethod, method);
    }

    private ShadowMatch getShadowMatch(Method targetMethod, Method originalMethod) {
        ShadowMatch shadowMatch = this.shadowMatchCache.get(targetMethod);
        if (shadowMatch == null) {
            synchronized (this.shadowMatchCache) {
                PointcutExpression fallbackExpression = null;
                shadowMatch = this.shadowMatchCache.get(targetMethod);
                if (shadowMatch == null) {
                    Method methodToMatch = targetMethod;
                    try {
                        shadowMatch = obtainPointcutExpression().matchesMethodExecution(methodToMatch);
                    } catch (ReflectionWorld.ReflectionWorldException e) {
                        try {
                            fallbackExpression = getFallbackPointcutExpression(methodToMatch.getDeclaringClass());
                            if (fallbackExpression != null) {
                                shadowMatch = fallbackExpression.matchesMethodExecution(methodToMatch);
                            }
                        } catch (ReflectionWorld.ReflectionWorldException e2) {
                            fallbackExpression = null;
                        }
                    }
                    if (targetMethod != originalMethod && (shadowMatch == null || (shadowMatch.neverMatches() && Proxy.isProxyClass(targetMethod.getDeclaringClass())))) {
                        methodToMatch = originalMethod;
                        try {
                            shadowMatch = obtainPointcutExpression().matchesMethodExecution(methodToMatch);
                        } catch (ReflectionWorld.ReflectionWorldException e3) {
                            try {
                                fallbackExpression = getFallbackPointcutExpression(methodToMatch.getDeclaringClass());
                                if (fallbackExpression != null) {
                                    shadowMatch = fallbackExpression.matchesMethodExecution(methodToMatch);
                                }
                            } catch (ReflectionWorld.ReflectionWorldException e4) {
                                fallbackExpression = null;
                            }
                        }
                    }
                    if (shadowMatch == null) {
                        shadowMatch = new ShadowMatchImpl(FuzzyBoolean.NO, (Test) null, (ExposedState) null, (PointcutParameter[]) null);
                    } else if (shadowMatch.maybeMatches() && fallbackExpression != null) {
                        shadowMatch = new DefensiveShadowMatch(shadowMatch, fallbackExpression.matchesMethodExecution(methodToMatch));
                    }
                    this.shadowMatchCache.put(targetMethod, shadowMatch);
                }
            }
        }
        return shadowMatch;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AspectJExpressionPointcut)) {
            return false;
        }
        AspectJExpressionPointcut otherPc = (AspectJExpressionPointcut) other;
        return ObjectUtils.nullSafeEquals(getExpression(), otherPc.getExpression()) && ObjectUtils.nullSafeEquals(this.pointcutDeclarationScope, otherPc.pointcutDeclarationScope) && ObjectUtils.nullSafeEquals(this.pointcutParameterNames, otherPc.pointcutParameterNames) && ObjectUtils.nullSafeEquals(this.pointcutParameterTypes, otherPc.pointcutParameterTypes);
    }

    public int hashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode(getExpression());
        return (31 * ((31 * ((31 * hashCode) + ObjectUtils.nullSafeHashCode(this.pointcutDeclarationScope))) + ObjectUtils.nullSafeHashCode((Object[]) this.pointcutParameterNames))) + ObjectUtils.nullSafeHashCode((Object[]) this.pointcutParameterTypes);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AspectJExpressionPointcut: ");
        sb.append("(");
        for (int i = 0; i < this.pointcutParameterTypes.length; i++) {
            sb.append(this.pointcutParameterTypes[i].getName());
            sb.append(" ");
            sb.append(this.pointcutParameterNames[i]);
            if (i + 1 < this.pointcutParameterTypes.length) {
                sb.append(", ");
            }
        }
        sb.append(")");
        sb.append(" ");
        if (getExpression() != null) {
            sb.append(getExpression());
        } else {
            sb.append("<pointcut expression not set>");
        }
        return sb.toString();
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.shadowMatchCache = new ConcurrentHashMap(32);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/AspectJExpressionPointcut$BeanPointcutDesignatorHandler.class */
    public class BeanPointcutDesignatorHandler implements PointcutDesignatorHandler {
        private static final String BEAN_DESIGNATOR_NAME = "bean";

        private BeanPointcutDesignatorHandler() {
            AspectJExpressionPointcut.this = r4;
        }

        public String getDesignatorName() {
            return "bean";
        }

        public ContextBasedMatcher parse(String expression) {
            return new BeanContextMatcher(expression);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/AspectJExpressionPointcut$BeanContextMatcher.class */
    private class BeanContextMatcher implements ContextBasedMatcher {
        private final NamePattern expressionPattern;

        public BeanContextMatcher(String expression) {
            AspectJExpressionPointcut.this = r6;
            this.expressionPattern = new NamePattern(expression);
        }

        @Deprecated
        public boolean couldMatchJoinPointsInType(Class someClass) {
            return contextMatch(someClass) == org.aspectj.weaver.tools.FuzzyBoolean.YES;
        }

        @Deprecated
        public boolean couldMatchJoinPointsInType(Class someClass, MatchingContext context) {
            return contextMatch(someClass) == org.aspectj.weaver.tools.FuzzyBoolean.YES;
        }

        public boolean matchesDynamically(MatchingContext context) {
            return true;
        }

        public org.aspectj.weaver.tools.FuzzyBoolean matchesStatically(MatchingContext context) {
            return contextMatch(null);
        }

        public boolean mayNeedDynamicTest() {
            return false;
        }

        private org.aspectj.weaver.tools.FuzzyBoolean contextMatch(@Nullable Class<?> targetType) {
            String advisedBeanName = AspectJExpressionPointcut.this.getCurrentProxiedBeanName();
            if (advisedBeanName == null) {
                return org.aspectj.weaver.tools.FuzzyBoolean.MAYBE;
            }
            if (BeanFactoryUtils.isGeneratedBeanName(advisedBeanName)) {
                return org.aspectj.weaver.tools.FuzzyBoolean.NO;
            }
            if (targetType != null) {
                boolean isFactory = FactoryBean.class.isAssignableFrom(targetType);
                return org.aspectj.weaver.tools.FuzzyBoolean.fromBoolean(matchesBean(isFactory ? BeanFactory.FACTORY_BEAN_PREFIX + advisedBeanName : advisedBeanName));
            }
            return org.aspectj.weaver.tools.FuzzyBoolean.fromBoolean(matchesBean(advisedBeanName) || matchesBean(new StringBuilder().append(BeanFactory.FACTORY_BEAN_PREFIX).append(advisedBeanName).toString()));
        }

        private boolean matchesBean(String advisedBeanName) {
            NamePattern namePattern = this.expressionPattern;
            namePattern.getClass();
            return BeanFactoryAnnotationUtils.isQualifierMatch(this::matches, advisedBeanName, AspectJExpressionPointcut.this.beanFactory);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/AspectJExpressionPointcut$DefensiveShadowMatch.class */
    public static class DefensiveShadowMatch implements ShadowMatch {
        private final ShadowMatch primary;
        private final ShadowMatch other;

        public DefensiveShadowMatch(ShadowMatch primary, ShadowMatch other) {
            this.primary = primary;
            this.other = other;
        }

        public boolean alwaysMatches() {
            return this.primary.alwaysMatches();
        }

        public boolean maybeMatches() {
            return this.primary.maybeMatches();
        }

        public boolean neverMatches() {
            return this.primary.neverMatches();
        }

        public JoinPointMatch matchesJoinPoint(Object thisObject, Object targetObject, Object[] args) {
            try {
                return this.primary.matchesJoinPoint(thisObject, targetObject, args);
            } catch (ReflectionWorld.ReflectionWorldException e) {
                return this.other.matchesJoinPoint(thisObject, targetObject, args);
            }
        }

        public void setMatchingContext(MatchingContext aMatchContext) {
            this.primary.setMatchingContext(aMatchContext);
            this.other.setMatchingContext(aMatchContext);
        }
    }
}