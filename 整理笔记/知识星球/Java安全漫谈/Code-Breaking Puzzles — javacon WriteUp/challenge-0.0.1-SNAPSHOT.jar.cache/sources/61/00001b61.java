package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cache.Cache;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/interceptor/CacheOperationExpressionEvaluator.class */
public class CacheOperationExpressionEvaluator extends CachedExpressionEvaluator {
    public static final Object NO_RESULT = new Object();
    public static final Object RESULT_UNAVAILABLE = new Object();
    public static final String RESULT_VARIABLE = "result";
    private final Map<CachedExpressionEvaluator.ExpressionKey, Expression> keyCache = new ConcurrentHashMap(64);
    private final Map<CachedExpressionEvaluator.ExpressionKey, Expression> conditionCache = new ConcurrentHashMap(64);
    private final Map<CachedExpressionEvaluator.ExpressionKey, Expression> unlessCache = new ConcurrentHashMap(64);

    public EvaluationContext createEvaluationContext(Collection<? extends Cache> caches, Method method, Object[] args, Object target, Class<?> targetClass, Method targetMethod, @Nullable Object result, @Nullable BeanFactory beanFactory) {
        CacheExpressionRootObject rootObject = new CacheExpressionRootObject(caches, method, args, target, targetClass);
        CacheEvaluationContext evaluationContext = new CacheEvaluationContext(rootObject, targetMethod, args, getParameterNameDiscoverer());
        if (result == RESULT_UNAVAILABLE) {
            evaluationContext.addUnavailableVariable(RESULT_VARIABLE);
        } else if (result != NO_RESULT) {
            evaluationContext.setVariable(RESULT_VARIABLE, result);
        }
        if (beanFactory != null) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        return evaluationContext;
    }

    @Nullable
    public Object key(String keyExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        return getExpression(this.keyCache, methodKey, keyExpression).getValue(evalContext);
    }

    public boolean condition(String conditionExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        return Boolean.TRUE.equals(getExpression(this.conditionCache, methodKey, conditionExpression).getValue(evalContext, (Class<Object>) Boolean.class));
    }

    public boolean unless(String unlessExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        return Boolean.TRUE.equals(getExpression(this.unlessCache, methodKey, unlessExpression).getValue(evalContext, (Class<Object>) Boolean.class));
    }

    public void clear() {
        this.keyCache.clear();
        this.conditionCache.clear();
        this.unlessCache.clear();
    }
}