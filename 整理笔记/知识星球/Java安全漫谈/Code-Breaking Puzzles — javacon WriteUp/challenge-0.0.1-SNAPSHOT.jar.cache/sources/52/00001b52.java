package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.function.SingletonSupplier;
import org.springframework.util.function.SupplierUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/interceptor/CacheAspectSupport.class */
public abstract class CacheAspectSupport extends AbstractCacheInvoker implements BeanFactoryAware, InitializingBean, SmartInitializingSingleton {
    @Nullable
    private CacheOperationSource cacheOperationSource;
    @Nullable
    private SingletonSupplier<CacheResolver> cacheResolver;
    @Nullable
    private BeanFactory beanFactory;
    protected final Log logger = LogFactory.getLog(getClass());
    private final Map<CacheOperationCacheKey, CacheOperationMetadata> metadataCache = new ConcurrentHashMap(1024);
    private final CacheOperationExpressionEvaluator evaluator = new CacheOperationExpressionEvaluator();
    private SingletonSupplier<KeyGenerator> keyGenerator = SingletonSupplier.of(SimpleKeyGenerator::new);
    private boolean initialized = false;

    public void configure(@Nullable Supplier<CacheErrorHandler> errorHandler, @Nullable Supplier<KeyGenerator> keyGenerator, @Nullable Supplier<CacheResolver> cacheResolver, @Nullable Supplier<CacheManager> cacheManager) {
        this.errorHandler = new SingletonSupplier<>(errorHandler, SimpleCacheErrorHandler::new);
        this.keyGenerator = new SingletonSupplier<>(keyGenerator, SimpleKeyGenerator::new);
        this.cacheResolver = new SingletonSupplier<>(cacheResolver, () -> {
            return SimpleCacheResolver.of((CacheManager) SupplierUtils.resolve(cacheManager));
        });
    }

    public void setCacheOperationSources(CacheOperationSource... cacheOperationSources) {
        Assert.notEmpty(cacheOperationSources, "At least 1 CacheOperationSource needs to be specified");
        this.cacheOperationSource = cacheOperationSources.length > 1 ? new CompositeCacheOperationSource(cacheOperationSources) : cacheOperationSources[0];
    }

    public void setCacheOperationSource(@Nullable CacheOperationSource cacheOperationSource) {
        this.cacheOperationSource = cacheOperationSource;
    }

    @Nullable
    public CacheOperationSource getCacheOperationSource() {
        return this.cacheOperationSource;
    }

    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = SingletonSupplier.of(keyGenerator);
    }

    public KeyGenerator getKeyGenerator() {
        return this.keyGenerator.obtain();
    }

    public void setCacheResolver(@Nullable CacheResolver cacheResolver) {
        this.cacheResolver = SingletonSupplier.ofNullable(cacheResolver);
    }

    @Nullable
    public CacheResolver getCacheResolver() {
        return (CacheResolver) SupplierUtils.resolve(this.cacheResolver);
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheResolver = SingletonSupplier.of(new SimpleCacheResolver(cacheManager));
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        Assert.state(getCacheOperationSource() != null, "The 'cacheOperationSources' property is required: If there are no cacheable methods, then don't use a cache aspect.");
    }

    @Override // org.springframework.beans.factory.SmartInitializingSingleton
    public void afterSingletonsInstantiated() {
        if (getCacheResolver() == null) {
            Assert.state(this.beanFactory != null, "CacheResolver or BeanFactory must be set on cache aspect");
            try {
                setCacheManager((CacheManager) this.beanFactory.getBean(CacheManager.class));
            } catch (NoUniqueBeanDefinitionException e) {
                throw new IllegalStateException("No CacheResolver specified, and no unique bean of type CacheManager found. Mark one as primary or declare a specific CacheManager to use.");
            } catch (NoSuchBeanDefinitionException e2) {
                throw new IllegalStateException("No CacheResolver specified, and no bean of type CacheManager found. Register a CacheManager bean or remove the @EnableCaching annotation from your configuration.");
            }
        }
        this.initialized = true;
    }

    protected String methodIdentification(Method method, Class<?> targetClass) {
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        return ClassUtils.getQualifiedMethodName(specificMethod);
    }

    protected Collection<? extends Cache> getCaches(CacheOperationInvocationContext<CacheOperation> context, CacheResolver cacheResolver) {
        Collection<? extends Cache> caches = cacheResolver.resolveCaches(context);
        if (caches.isEmpty()) {
            throw new IllegalStateException("No cache could be resolved for '" + context.getOperation() + "' using resolver '" + cacheResolver + "'. At least one cache should be provided per cache operation.");
        }
        return caches;
    }

    protected CacheOperationContext getOperationContext(CacheOperation operation, Method method, Object[] args, Object target, Class<?> targetClass) {
        CacheOperationMetadata metadata = getCacheOperationMetadata(operation, method, targetClass);
        return new CacheOperationContext(metadata, args, target);
    }

    protected CacheOperationMetadata getCacheOperationMetadata(CacheOperation operation, Method method, Class<?> targetClass) {
        KeyGenerator operationKeyGenerator;
        CacheResolver operationCacheResolver;
        CacheOperationCacheKey cacheKey = new CacheOperationCacheKey(operation, method, targetClass);
        CacheOperationMetadata metadata = this.metadataCache.get(cacheKey);
        if (metadata == null) {
            if (StringUtils.hasText(operation.getKeyGenerator())) {
                operationKeyGenerator = (KeyGenerator) getBean(operation.getKeyGenerator(), KeyGenerator.class);
            } else {
                operationKeyGenerator = getKeyGenerator();
            }
            if (StringUtils.hasText(operation.getCacheResolver())) {
                operationCacheResolver = (CacheResolver) getBean(operation.getCacheResolver(), CacheResolver.class);
            } else if (StringUtils.hasText(operation.getCacheManager())) {
                CacheManager cacheManager = (CacheManager) getBean(operation.getCacheManager(), CacheManager.class);
                operationCacheResolver = new SimpleCacheResolver(cacheManager);
            } else {
                operationCacheResolver = getCacheResolver();
                Assert.state(operationCacheResolver != null, "No CacheResolver/CacheManager set");
            }
            metadata = new CacheOperationMetadata(operation, method, targetClass, operationKeyGenerator, operationCacheResolver);
            this.metadataCache.put(cacheKey, metadata);
        }
        return metadata;
    }

    protected <T> T getBean(String beanName, Class<T> expectedType) {
        if (this.beanFactory == null) {
            throw new IllegalStateException("BeanFactory must be set on cache aspect for " + expectedType.getSimpleName() + " retrieval");
        }
        return (T) BeanFactoryAnnotationUtils.qualifiedBeanOfType(this.beanFactory, expectedType, beanName);
    }

    protected void clearMetadataCache() {
        this.metadataCache.clear();
        this.evaluator.clear();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Object execute(CacheOperationInvoker invoker, Object target, Method method, Object[] args) {
        if (this.initialized) {
            Class<?> targetClass = getTargetClass(target);
            CacheOperationSource cacheOperationSource = getCacheOperationSource();
            if (cacheOperationSource != null) {
                Collection<CacheOperation> operations = cacheOperationSource.getCacheOperations(method, targetClass);
                if (!CollectionUtils.isEmpty(operations)) {
                    return execute(invoker, method, new CacheOperationContexts(operations, method, args, target, targetClass));
                }
            }
        }
        return invoker.invoke();
    }

    protected Object invokeOperation(CacheOperationInvoker invoker) {
        return invoker.invoke();
    }

    private Class<?> getTargetClass(Object target) {
        return AopProxyUtils.ultimateTargetClass(target);
    }

    @Nullable
    private Object execute(CacheOperationInvoker invoker, Method method, CacheOperationContexts contexts) {
        Object returnValue;
        Object cacheValue;
        if (contexts.isSynchronized()) {
            CacheOperationContext context = contexts.get(CacheableOperation.class).iterator().next();
            if (isConditionPassing(context, CacheOperationExpressionEvaluator.NO_RESULT)) {
                Object key = generateKey(context, CacheOperationExpressionEvaluator.NO_RESULT);
                Cache cache = context.getCaches().iterator().next();
                try {
                    return wrapCacheValue(method, cache.get(key, () -> {
                        return unwrapReturnValue(invokeOperation(invoker));
                    }));
                } catch (Cache.ValueRetrievalException ex) {
                    throw ((CacheOperationInvoker.ThrowableWrapper) ex.getCause());
                }
            }
            return invokeOperation(invoker);
        }
        processCacheEvicts(contexts.get(CacheEvictOperation.class), true, CacheOperationExpressionEvaluator.NO_RESULT);
        Cache.ValueWrapper cacheHit = findCachedItem(contexts.get(CacheableOperation.class));
        List<CachePutRequest> cachePutRequests = new LinkedList<>();
        if (cacheHit == null) {
            collectPutRequests(contexts.get(CacheableOperation.class), CacheOperationExpressionEvaluator.NO_RESULT, cachePutRequests);
        }
        if (cacheHit != null && !hasCachePut(contexts)) {
            cacheValue = cacheHit.get();
            returnValue = wrapCacheValue(method, cacheValue);
        } else {
            returnValue = invokeOperation(invoker);
            cacheValue = unwrapReturnValue(returnValue);
        }
        collectPutRequests(contexts.get(CachePutOperation.class), cacheValue, cachePutRequests);
        for (CachePutRequest cachePutRequest : cachePutRequests) {
            cachePutRequest.apply(cacheValue);
        }
        processCacheEvicts(contexts.get(CacheEvictOperation.class), false, cacheValue);
        return returnValue;
    }

    @Nullable
    private Object wrapCacheValue(Method method, @Nullable Object cacheValue) {
        if (method.getReturnType() == Optional.class && (cacheValue == null || cacheValue.getClass() != Optional.class)) {
            return Optional.ofNullable(cacheValue);
        }
        return cacheValue;
    }

    @Nullable
    private Object unwrapReturnValue(Object returnValue) {
        return ObjectUtils.unwrapOptional(returnValue);
    }

    private boolean hasCachePut(CacheOperationContexts contexts) {
        Collection<CacheOperationContext> cachePutContexts = contexts.get(CachePutOperation.class);
        Collection<CacheOperationContext> excluded = new ArrayList<>();
        for (CacheOperationContext context : cachePutContexts) {
            try {
                if (!context.isConditionPassing(CacheOperationExpressionEvaluator.RESULT_UNAVAILABLE)) {
                    excluded.add(context);
                }
            } catch (VariableNotAvailableException e) {
            }
        }
        return cachePutContexts.size() != excluded.size();
    }

    private void processCacheEvicts(Collection<CacheOperationContext> contexts, boolean beforeInvocation, @Nullable Object result) {
        for (CacheOperationContext context : contexts) {
            CacheEvictOperation operation = (CacheEvictOperation) context.metadata.operation;
            if (beforeInvocation == operation.isBeforeInvocation() && isConditionPassing(context, result)) {
                performCacheEvict(context, operation, result);
            }
        }
    }

    private void performCacheEvict(CacheOperationContext context, CacheEvictOperation operation, @Nullable Object result) {
        Object key = null;
        for (Cache cache : context.getCaches()) {
            if (operation.isCacheWide()) {
                logInvalidating(context, operation, null);
                doClear(cache);
            } else {
                if (key == null) {
                    key = generateKey(context, result);
                }
                logInvalidating(context, operation, key);
                doEvict(cache, key);
            }
        }
    }

    private void logInvalidating(CacheOperationContext context, CacheEvictOperation operation, @Nullable Object key) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Invalidating " + (key != null ? "cache key [" + key + "]" : "entire cache") + " for operation " + operation + " on method " + context.metadata.method);
        }
    }

    @Nullable
    private Cache.ValueWrapper findCachedItem(Collection<CacheOperationContext> contexts) {
        Object result = CacheOperationExpressionEvaluator.NO_RESULT;
        for (CacheOperationContext context : contexts) {
            if (isConditionPassing(context, result)) {
                Object key = generateKey(context, result);
                Cache.ValueWrapper cached = findInCaches(context, key);
                if (cached != null) {
                    return cached;
                }
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("No cache entry for key '" + key + "' in cache(s) " + context.getCacheNames());
                }
            }
        }
        return null;
    }

    private void collectPutRequests(Collection<CacheOperationContext> contexts, @Nullable Object result, Collection<CachePutRequest> putRequests) {
        for (CacheOperationContext context : contexts) {
            if (isConditionPassing(context, result)) {
                Object key = generateKey(context, result);
                putRequests.add(new CachePutRequest(context, key));
            }
        }
    }

    @Nullable
    private Cache.ValueWrapper findInCaches(CacheOperationContext context, Object key) {
        for (Cache cache : context.getCaches()) {
            Cache.ValueWrapper wrapper = doGet(cache, key);
            if (wrapper != null) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Cache entry for key '" + key + "' found in cache '" + cache.getName() + "'");
                }
                return wrapper;
            }
        }
        return null;
    }

    private boolean isConditionPassing(CacheOperationContext context, @Nullable Object result) {
        boolean passing = context.isConditionPassing(result);
        if (!passing && this.logger.isTraceEnabled()) {
            this.logger.trace("Cache condition failed on method " + context.metadata.method + " for operation " + context.metadata.operation);
        }
        return passing;
    }

    private Object generateKey(CacheOperationContext context, @Nullable Object result) {
        Object key = context.generateKey(result);
        if (key == null) {
            throw new IllegalArgumentException("Null key returned for cache operation (maybe you are using named params on classes without debug info?) " + context.metadata.operation);
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Computed cache key '" + key + "' for operation " + context.metadata.operation);
        }
        return key;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/interceptor/CacheAspectSupport$CacheOperationContexts.class */
    public class CacheOperationContexts {
        private final MultiValueMap<Class<? extends CacheOperation>, CacheOperationContext> contexts;
        private final boolean sync;

        /* JADX WARN: Multi-variable type inference failed */
        public CacheOperationContexts(Collection<? extends CacheOperation> operations, Method method, Object[] args, Object target, Class<?> targetClass) {
            this.contexts = new LinkedMultiValueMap(operations.size());
            for (CacheOperation op : operations) {
                this.contexts.add(op.getClass(), CacheAspectSupport.this.getOperationContext(op, method, args, target, targetClass));
            }
            this.sync = determineSyncFlag(method);
        }

        public Collection<CacheOperationContext> get(Class<? extends CacheOperation> operationClass) {
            Collection<CacheOperationContext> result = (Collection) this.contexts.get(operationClass);
            return result != null ? result : Collections.emptyList();
        }

        public boolean isSynchronized() {
            return this.sync;
        }

        private boolean determineSyncFlag(Method method) {
            List<CacheOperationContext> cacheOperationContexts = (List) this.contexts.get(CacheableOperation.class);
            if (cacheOperationContexts == null) {
                return false;
            }
            boolean syncEnabled = false;
            Iterator<CacheOperationContext> it = cacheOperationContexts.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                } else if (((CacheableOperation) it.next().getOperation()).isSync()) {
                    syncEnabled = true;
                    break;
                }
            }
            if (syncEnabled) {
                if (this.contexts.size() > 1) {
                    throw new IllegalStateException("@Cacheable(sync=true) cannot be combined with other cache operations on '" + method + "'");
                }
                if (cacheOperationContexts.size() > 1) {
                    throw new IllegalStateException("Only one @Cacheable(sync=true) entry is allowed on '" + method + "'");
                }
                CacheOperationContext cacheOperationContext = cacheOperationContexts.iterator().next();
                CacheableOperation operation = (CacheableOperation) cacheOperationContext.getOperation();
                if (cacheOperationContext.getCaches().size() > 1) {
                    throw new IllegalStateException("@Cacheable(sync=true) only allows a single cache on '" + operation + "'");
                }
                if (StringUtils.hasText(operation.getUnless())) {
                    throw new IllegalStateException("@Cacheable(sync=true) does not support unless attribute on '" + operation + "'");
                }
                return true;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/interceptor/CacheAspectSupport$CacheOperationMetadata.class */
    public static class CacheOperationMetadata {
        private final CacheOperation operation;
        private final Method method;
        private final Class<?> targetClass;
        private final Method targetMethod;
        private final AnnotatedElementKey methodKey;
        private final KeyGenerator keyGenerator;
        private final CacheResolver cacheResolver;

        public CacheOperationMetadata(CacheOperation operation, Method method, Class<?> targetClass, KeyGenerator keyGenerator, CacheResolver cacheResolver) {
            this.operation = operation;
            this.method = BridgeMethodResolver.findBridgedMethod(method);
            this.targetClass = targetClass;
            this.targetMethod = !Proxy.isProxyClass(targetClass) ? AopUtils.getMostSpecificMethod(method, targetClass) : this.method;
            this.methodKey = new AnnotatedElementKey(this.targetMethod, targetClass);
            this.keyGenerator = keyGenerator;
            this.cacheResolver = cacheResolver;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/interceptor/CacheAspectSupport$CacheOperationContext.class */
    public class CacheOperationContext implements CacheOperationInvocationContext<CacheOperation> {
        private final CacheOperationMetadata metadata;
        private final Object[] args;
        private final Object target;
        private final Collection<? extends Cache> caches;
        private final Collection<String> cacheNames;
        @Nullable
        private Boolean conditionPassing;

        public CacheOperationContext(CacheOperationMetadata metadata, Object[] args, Object target) {
            this.metadata = metadata;
            this.args = extractArgs(metadata.method, args);
            this.target = target;
            this.caches = CacheAspectSupport.this.getCaches(this, metadata.cacheResolver);
            this.cacheNames = createCacheNames(this.caches);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.cache.interceptor.CacheOperationInvocationContext
        public CacheOperation getOperation() {
            return this.metadata.operation;
        }

        @Override // org.springframework.cache.interceptor.CacheOperationInvocationContext
        public Object getTarget() {
            return this.target;
        }

        @Override // org.springframework.cache.interceptor.CacheOperationInvocationContext
        public Method getMethod() {
            return this.metadata.method;
        }

        @Override // org.springframework.cache.interceptor.CacheOperationInvocationContext
        public Object[] getArgs() {
            return this.args;
        }

        private Object[] extractArgs(Method method, Object[] args) {
            if (!method.isVarArgs()) {
                return args;
            }
            Object[] varArgs = ObjectUtils.toObjectArray(args[args.length - 1]);
            Object[] combinedArgs = new Object[(args.length - 1) + varArgs.length];
            System.arraycopy(args, 0, combinedArgs, 0, args.length - 1);
            System.arraycopy(varArgs, 0, combinedArgs, args.length - 1, varArgs.length);
            return combinedArgs;
        }

        protected boolean isConditionPassing(@Nullable Object result) {
            if (this.conditionPassing == null) {
                if (StringUtils.hasText(this.metadata.operation.getCondition())) {
                    EvaluationContext evaluationContext = createEvaluationContext(result);
                    this.conditionPassing = Boolean.valueOf(CacheAspectSupport.this.evaluator.condition(this.metadata.operation.getCondition(), this.metadata.methodKey, evaluationContext));
                } else {
                    this.conditionPassing = true;
                }
            }
            return this.conditionPassing.booleanValue();
        }

        protected boolean canPutToCache(@Nullable Object value) {
            String unless = "";
            if (this.metadata.operation instanceof CacheableOperation) {
                unless = ((CacheableOperation) this.metadata.operation).getUnless();
            } else if (this.metadata.operation instanceof CachePutOperation) {
                unless = ((CachePutOperation) this.metadata.operation).getUnless();
            }
            if (StringUtils.hasText(unless)) {
                EvaluationContext evaluationContext = createEvaluationContext(value);
                return !CacheAspectSupport.this.evaluator.unless(unless, this.metadata.methodKey, evaluationContext);
            }
            return true;
        }

        @Nullable
        protected Object generateKey(@Nullable Object result) {
            if (!StringUtils.hasText(this.metadata.operation.getKey())) {
                return this.metadata.keyGenerator.generate(this.target, this.metadata.method, this.args);
            }
            EvaluationContext evaluationContext = createEvaluationContext(result);
            return CacheAspectSupport.this.evaluator.key(this.metadata.operation.getKey(), this.metadata.methodKey, evaluationContext);
        }

        private EvaluationContext createEvaluationContext(@Nullable Object result) {
            return CacheAspectSupport.this.evaluator.createEvaluationContext(this.caches, this.metadata.method, this.args, this.target, this.metadata.targetClass, this.metadata.targetMethod, result, CacheAspectSupport.this.beanFactory);
        }

        protected Collection<? extends Cache> getCaches() {
            return this.caches;
        }

        protected Collection<String> getCacheNames() {
            return this.cacheNames;
        }

        private Collection<String> createCacheNames(Collection<? extends Cache> caches) {
            Collection<String> names = new ArrayList<>();
            for (Cache cache : caches) {
                names.add(cache.getName());
            }
            return names;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/interceptor/CacheAspectSupport$CachePutRequest.class */
    public class CachePutRequest {
        private final CacheOperationContext context;
        private final Object key;

        public CachePutRequest(CacheOperationContext context, Object key) {
            this.context = context;
            this.key = key;
        }

        public void apply(@Nullable Object result) {
            if (this.context.canPutToCache(result)) {
                for (Cache cache : this.context.getCaches()) {
                    CacheAspectSupport.this.doPut(cache, this.key, result);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/interceptor/CacheAspectSupport$CacheOperationCacheKey.class */
    public static final class CacheOperationCacheKey implements Comparable<CacheOperationCacheKey> {
        private final CacheOperation cacheOperation;
        private final AnnotatedElementKey methodCacheKey;

        private CacheOperationCacheKey(CacheOperation cacheOperation, Method method, Class<?> targetClass) {
            this.cacheOperation = cacheOperation;
            this.methodCacheKey = new AnnotatedElementKey(method, targetClass);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CacheOperationCacheKey)) {
                return false;
            }
            CacheOperationCacheKey otherKey = (CacheOperationCacheKey) other;
            return this.cacheOperation.equals(otherKey.cacheOperation) && this.methodCacheKey.equals(otherKey.methodCacheKey);
        }

        public int hashCode() {
            return (this.cacheOperation.hashCode() * 31) + this.methodCacheKey.hashCode();
        }

        public String toString() {
            return this.cacheOperation + " on " + this.methodCacheKey;
        }

        @Override // java.lang.Comparable
        public int compareTo(CacheOperationCacheKey other) {
            int result = this.cacheOperation.getName().compareTo(other.cacheOperation.getName());
            if (result == 0) {
                result = this.methodCacheKey.compareTo(other.methodCacheKey);
            }
            return result;
        }
    }
}