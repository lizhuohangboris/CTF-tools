package org.springframework.context.event;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.lang.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/event/EventExpressionEvaluator.class */
public class EventExpressionEvaluator extends CachedExpressionEvaluator {
    private final Map<CachedExpressionEvaluator.ExpressionKey, Expression> conditionCache = new ConcurrentHashMap(64);

    public boolean condition(String conditionExpression, ApplicationEvent event, Method targetMethod, AnnotatedElementKey methodKey, Object[] args, @Nullable BeanFactory beanFactory) {
        EventExpressionRootObject root = new EventExpressionRootObject(event, args);
        MethodBasedEvaluationContext evaluationContext = new MethodBasedEvaluationContext(root, targetMethod, args, getParameterNameDiscoverer());
        if (beanFactory != null) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        return Boolean.TRUE.equals(getExpression(this.conditionCache, methodKey, conditionExpression).getValue((EvaluationContext) evaluationContext, (Class<Object>) Boolean.class));
    }
}