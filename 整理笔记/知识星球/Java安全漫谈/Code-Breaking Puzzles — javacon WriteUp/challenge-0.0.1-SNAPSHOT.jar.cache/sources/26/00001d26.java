package org.springframework.context.expression;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/expression/BeanFactoryAccessor.class */
public class BeanFactoryAccessor implements PropertyAccessor {
    @Override // org.springframework.expression.PropertyAccessor
    public Class<?>[] getSpecificTargetClasses() {
        return new Class[]{BeanFactory.class};
    }

    @Override // org.springframework.expression.PropertyAccessor
    public boolean canRead(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
        return (target instanceof BeanFactory) && ((BeanFactory) target).containsBean(name);
    }

    @Override // org.springframework.expression.PropertyAccessor
    public TypedValue read(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
        Assert.state(target instanceof BeanFactory, "Target must be of type BeanFactory");
        return new TypedValue(((BeanFactory) target).getBean(name));
    }

    @Override // org.springframework.expression.PropertyAccessor
    public boolean canWrite(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
        return false;
    }

    @Override // org.springframework.expression.PropertyAccessor
    public void write(EvaluationContext context, @Nullable Object target, String name, @Nullable Object newValue) throws AccessException {
        throw new AccessException("Beans in a BeanFactory are read-only");
    }
}