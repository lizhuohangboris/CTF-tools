package org.thymeleaf.spring5.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ConstructorResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.OperatorOverloader;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypeComparator;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.TypeLocator;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.standard.expression.RestrictedRequestAccessUtils;
import org.thymeleaf.standard.expression.StandardExpressionObjectFactory;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/expression/ThymeleafEvaluationContextWrapper.class */
public final class ThymeleafEvaluationContextWrapper implements IThymeleafEvaluationContext {
    private static final MapAccessor MAP_ACCESSOR_INSTANCE = new MapAccessor();
    private final EvaluationContext delegate;
    private final List<PropertyAccessor> propertyAccessors;
    private IExpressionObjects expressionObjects = null;
    private boolean requestParametersRestricted = false;
    private Map<String, Object> additionalVariables = null;

    public ThymeleafEvaluationContextWrapper(EvaluationContext delegate) {
        Validate.notNull(delegate, "Evaluation context delegate cannot be null");
        this.delegate = delegate;
        if (this.delegate instanceof ThymeleafEvaluationContext) {
            this.propertyAccessors = null;
        } else if (this.delegate instanceof StandardEvaluationContext) {
            ((StandardEvaluationContext) this.delegate).addPropertyAccessor(SPELContextPropertyAccessor.INSTANCE);
            ((StandardEvaluationContext) this.delegate).addPropertyAccessor(MAP_ACCESSOR_INSTANCE);
            this.propertyAccessors = null;
        } else {
            this.propertyAccessors = new ArrayList(5);
            this.propertyAccessors.addAll(this.delegate.getPropertyAccessors());
            this.propertyAccessors.add(SPELContextPropertyAccessor.INSTANCE);
            this.propertyAccessors.add(MAP_ACCESSOR_INSTANCE);
        }
    }

    @Override // org.springframework.expression.EvaluationContext
    public TypedValue getRootObject() {
        return this.delegate.getRootObject();
    }

    @Override // org.springframework.expression.EvaluationContext
    public List<ConstructorResolver> getConstructorResolvers() {
        return this.delegate.getConstructorResolvers();
    }

    @Override // org.springframework.expression.EvaluationContext
    public List<MethodResolver> getMethodResolvers() {
        return this.delegate.getMethodResolvers();
    }

    @Override // org.springframework.expression.EvaluationContext
    public List<PropertyAccessor> getPropertyAccessors() {
        return this.propertyAccessors == null ? this.delegate.getPropertyAccessors() : this.propertyAccessors;
    }

    @Override // org.springframework.expression.EvaluationContext
    public TypeLocator getTypeLocator() {
        return this.delegate.getTypeLocator();
    }

    @Override // org.springframework.expression.EvaluationContext
    public TypeConverter getTypeConverter() {
        return this.delegate.getTypeConverter();
    }

    @Override // org.springframework.expression.EvaluationContext
    public TypeComparator getTypeComparator() {
        return this.delegate.getTypeComparator();
    }

    @Override // org.springframework.expression.EvaluationContext
    public OperatorOverloader getOperatorOverloader() {
        return this.delegate.getOperatorOverloader();
    }

    @Override // org.springframework.expression.EvaluationContext
    public BeanResolver getBeanResolver() {
        return this.delegate.getBeanResolver();
    }

    @Override // org.springframework.expression.EvaluationContext
    public void setVariable(String name, Object value) {
        if (this.additionalVariables == null) {
            this.additionalVariables = new HashMap(5, 1.0f);
        }
        this.additionalVariables.put(name, value);
    }

    @Override // org.springframework.expression.EvaluationContext
    public Object lookupVariable(String name) {
        Object result;
        Object result2;
        if (this.expressionObjects != null && this.expressionObjects.containsObject(name) && (result2 = this.expressionObjects.getObject(name)) != null) {
            if (this.requestParametersRestricted && ("request".equals(name) || StandardExpressionObjectFactory.HTTP_SERVLET_REQUEST_EXPRESSION_OBJECT_NAME.equals(name))) {
                return RestrictedRequestAccessUtils.wrapRequestObject(result2);
            }
            return result2;
        } else if (this.additionalVariables != null && this.additionalVariables.containsKey(name) && (result = this.additionalVariables.get(name)) != null) {
            return result;
        } else {
            return this.delegate.lookupVariable(name);
        }
    }

    @Override // org.thymeleaf.spring5.expression.IThymeleafEvaluationContext
    public boolean isVariableAccessRestricted() {
        return this.requestParametersRestricted;
    }

    @Override // org.thymeleaf.spring5.expression.IThymeleafEvaluationContext
    public void setVariableAccessRestricted(boolean restricted) {
        this.requestParametersRestricted = restricted;
    }

    @Override // org.thymeleaf.spring5.expression.IThymeleafEvaluationContext
    public IExpressionObjects getExpressionObjects() {
        return this.expressionObjects;
    }

    @Override // org.thymeleaf.spring5.expression.IThymeleafEvaluationContext
    public void setExpressionObjects(IExpressionObjects expressionObjects) {
        this.expressionObjects = expressionObjects;
    }
}